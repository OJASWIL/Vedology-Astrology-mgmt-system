package com.vedology.controller.admin;

import com.vedology.model.Astrologer;
import com.vedology.model.User;
import com.vedology.service.AdminService;
import com.vedology.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize      = 1024 * 1024 * 5,
    maxRequestSize   = 1024 * 1024 * 10
)
@WebServlet(urlPatterns = {
    "/admin/manage",
    "/admin/clients",
    "/admin/clients/*"
})
public class AdminController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "images" + File.separator + "profiles";

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri = req.getRequestURI();
        String pathInfo = req.getPathInfo();

        try {
            // ===== CLIENT ROUTES =====
            if (uri.contains("/admin/clients")) {

                if (pathInfo == null || "/".equals(pathInfo) || "/list".equals(pathInfo)) {
                    // LIST
                    req.setAttribute("clients", getAllClients());
                    req.getRequestDispatcher("/WEB-INF/pages/admin/clients/list.jsp").forward(req, resp);

                } else if ("/add".equals(pathInfo)) {
                    // SHOW ADD FORM
                    req.getRequestDispatcher("/WEB-INF/pages/admin/clients/add.jsp").forward(req, resp);

                } else if ("/edit".equals(pathInfo)) {
                    // SHOW EDIT FORM (pre-filled)
                    String userIdStr = req.getParameter("userId");
                    if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                        User client = getClientById(Integer.parseInt(userIdStr));
                        req.setAttribute("client", client);
                    }
                    req.getRequestDispatcher("/WEB-INF/pages/admin/clients/edit.jsp").forward(req, resp);

                } else if ("/delete".equals(pathInfo)) {
                    // DELETE
                    String userIdStr = req.getParameter("userId");
                    if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                        boolean ok = deleteClient(Integer.parseInt(userIdStr));
                        session.setAttribute("clientMessage", ok
                            ? "Client deleted successfully."
                            : "Failed to delete client.");
                    }
                    resp.sendRedirect(req.getContextPath() + "/admin/clients");
                }
                return;
            }

            // ===== MAIN DASHBOARD TABS =====
            String tab = req.getParameter("tab");

            // Always reload admin profile from DB
            com.vedology.service.ClientDashboardService profileService =
                    new com.vedology.service.ClientDashboardService();
            User freshUser = profileService.getUserProfile(user.getEmail());
            if (freshUser != null) {
                if (freshUser.getRole() == null) freshUser.setRole(user.getRole());
                session.setAttribute("user", freshUser);
            }

            if (tab == null || tab.isEmpty() || "dashboard".equals(tab)) {
                // nothing extra needed
            } else if ("edit".equals(tab)) {
                req.setAttribute("clients", getAllClients());
            } else if ("astrologers".equals(tab)) {
                req.setAttribute("astrologers", getAllAstrologers());
            } else if ("book".equals(tab)) {
                req.setAttribute("astrologers", getAllAstrologers());
                req.setAttribute("clients", getAllClients());
            } else if ("payments".equals(tab)) {
                req.setAttribute("payments", getAllPayments());
            } else if ("reports".equals(tab)) {
                req.setAttribute("reports", getAllReports());
            } else if ("horoscope".equals(tab)) {
                req.setAttribute("horoscopes", getAllHoroscopes());
            }

            req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);
        }
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri = req.getRequestURI();
        String pathInfo = req.getPathInfo();

        try {
            // ===== CLIENT POST ROUTES =====
            if (uri.contains("/admin/clients")) {

                if ("/add".equals(pathInfo)) {
                    User newUser = new User();
                    newUser.setFullName(req.getParameter("fullName"));
                    newUser.setEmail(req.getParameter("email"));
                    newUser.setPhone(req.getParameter("phone"));
                    String tob = req.getParameter("timeOfBirth");
                    if (tob != null && !tob.isEmpty()) newUser.setTimeOfBirth(LocalTime.parse(tob));
                    String pass = req.getParameter("password");
                    newUser.setPassword(pass != null && !pass.isEmpty()
                        ? PasswordUtil.encrypt(newUser.getEmail(), pass) : "defaultPassword");

                    if (createClient(newUser)) {
                        session.setAttribute("clientMessage", "Client added successfully!");
                        resp.sendRedirect(req.getContextPath() + "/admin/clients");
                    } else {
                        req.setAttribute("error", "Failed to add client. Email may already exist.");
                        req.getRequestDispatcher("/WEB-INF/pages/admin/clients/add.jsp").forward(req, resp);
                    }
                    return;
                }

                if ("/edit".equals(pathInfo)) {
                    User updatedUser = new User();
                    updatedUser.setUserId(Integer.parseInt(req.getParameter("userId")));
                    updatedUser.setFullName(req.getParameter("fullName"));
                    updatedUser.setEmail(req.getParameter("email"));
                    updatedUser.setPhone(req.getParameter("phone"));
                    String tob = req.getParameter("timeOfBirth");
                    if (tob != null && !tob.isEmpty()) updatedUser.setTimeOfBirth(LocalTime.parse(tob));

                    if (updateClient(updatedUser)) {
                        session.setAttribute("clientMessage", "Client updated successfully!");
                        resp.sendRedirect(req.getContextPath() + "/admin/clients");
                    } else {
                        req.setAttribute("error", "Failed to update client.");
                        req.setAttribute("client", updatedUser);
                        req.getRequestDispatcher("/WEB-INF/pages/admin/clients/edit.jsp").forward(req, resp);
                    }
                    return;
                }
            }

            // ===== ADMIN EDIT PROFILE (with image upload) =====
            String action = req.getParameter("action");
            if ("editProfile".equals(action)) {
                com.vedology.service.ClientDashboardService profileService =
                        new com.vedology.service.ClientDashboardService();

                User updatedAdmin = new User();
                updatedAdmin.setUserId(user.getUserId());
                updatedAdmin.setRole(user.getRole());
                updatedAdmin.setFullName(req.getParameter("fullName"));
                updatedAdmin.setPhone(req.getParameter("phone"));
                String email = req.getParameter("email");
                updatedAdmin.setEmail(email != null && !email.isEmpty() ? email : user.getEmail());
                String tob = req.getParameter("timeOfBirth");
                if (tob != null && !tob.isEmpty()) updatedAdmin.setTimeOfBirth(LocalTime.parse(tob));

                // Image upload
                String savedImageName = user.getProfileImage();
                try {
                    Part filePart = req.getPart("profileImage");
                    if (filePart != null && filePart.getSize() > 0) {
                        String originalName = getSubmittedFileName(filePart);
                        if (originalName != null && !originalName.isEmpty()) {
                            String lower = originalName.toLowerCase();
                            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
                                String ext = originalName.substring(originalName.lastIndexOf("."));
                                String newFileName = "user_" + user.getUserId() + "_"
                                        + UUID.randomUUID().toString().substring(0, 8) + ext;
                                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                                Path dir = Paths.get(uploadPath);
                                if (!Files.exists(dir)) Files.createDirectories(dir);
                                filePart.write(uploadPath + File.separator + newFileName);
                                savedImageName = newFileName;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Admin image upload error: " + e.getMessage());
                }
                updatedAdmin.setProfileImage(savedImageName);

                // Password
                String newPass = req.getParameter("newPassword");
                String confirmPass = req.getParameter("confirmPassword");
                if (newPass != null && !newPass.trim().isEmpty()) {
                    if (newPass.equals(confirmPass)) {
                        updatedAdmin.setPassword(PasswordUtil.encrypt(updatedAdmin.getEmail(), newPass));
                    } else {
                        session.setAttribute("profileError", "Passwords do not match.");
                        resp.sendRedirect(req.getContextPath() + "/admin/manage?tab=editProfile");
                        return;
                    }
                } else {
                    User freshProfile = profileService.getUserProfile(user.getEmail());
                    updatedAdmin.setPassword(freshProfile != null ? freshProfile.getPassword() : user.getPassword());
                }

                boolean ok = profileService.updateUserProfile(updatedAdmin);
                if (ok) {
                    session.setAttribute("user", updatedAdmin);
                    session.setAttribute("profileSuccess", "Profile updated successfully!");
                } else {
                    session.setAttribute("profileError", "Failed to update profile.");
                }
                resp.sendRedirect(req.getContextPath() + "/admin/manage?tab=editProfile");
                return;
            }

            // ===== ADMIN BOOK APPOINTMENT =====
            if ("bookAppointment".equals(action)) {
                String clientEmail = req.getParameter("clientEmail");
                String astrologerIdStr = req.getParameter("astrologer");
                String date = req.getParameter("date");
                String time = req.getParameter("time");

                if (clientEmail != null && !clientEmail.isEmpty()
                        && astrologerIdStr != null && !astrologerIdStr.isEmpty()
                        && date != null && !date.isEmpty()
                        && time != null && !time.isEmpty()) {
                    try {
                        int astrologerId = Integer.parseInt(astrologerIdStr);
                        com.vedology.service.ClientDashboardService svc =
                                new com.vedology.service.ClientDashboardService();
                        boolean ok = svc.bookAppointment(clientEmail, astrologerId, date, time);
                        if (ok) {
                            session.setAttribute("profileSuccess", "Appointment booked successfully for " + clientEmail + "!");
                        } else {
                            session.setAttribute("profileError", "Failed to book appointment. Check client email and try again.");
                        }
                    } catch (NumberFormatException nfe) {
                        session.setAttribute("profileError", "Invalid astrologer selection.");
                    }
                } else {
                    session.setAttribute("profileError", "All booking fields are required.");
                }
                resp.sendRedirect(req.getContextPath() + "/admin/manage?tab=book");
                return;
            }

            req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/clients");
        }
    }

    // ===================== HELPERS =====================
    private User getClientById(int userId) {
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT UserId, Email, Role, FullName, TimeOfBirth, Phone FROM users WHERE UserId = ? AND Role = 'client'")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserId"));
                    u.setEmail(rs.getString("Email"));
                    u.setRole(rs.getString("Role"));
                    u.setFullName(rs.getString("FullName"));
                    String tob = rs.getString("TimeOfBirth");
                    if (tob != null) u.setTimeOfBirth(LocalTime.parse(tob));
                    u.setPhone(rs.getString("Phone"));
                    return u;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private List<User> getAllClients() {
        List<User> clients = new ArrayList<>();
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT UserId, Email, Role, FullName, TimeOfBirth, Phone FROM users WHERE Role = 'client'");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserId"));
                u.setEmail(rs.getString("Email"));
                u.setRole(rs.getString("Role"));
                u.setFullName(rs.getString("FullName"));
                String tob = rs.getString("TimeOfBirth");
                if (tob != null) u.setTimeOfBirth(LocalTime.parse(tob));
                u.setPhone(rs.getString("Phone"));
                clients.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return clients;
    }

    private List<Astrologer> getAllAstrologers() {
        List<Astrologer> list = new ArrayList<>();
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT AstrologerId, AvailableDays, Address, ContactNumber, ExperienceYear, Specialization FROM astrologer");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Astrologer(
                    rs.getInt("AstrologerId"),
                    rs.getString("AvailableDays"),
                    rs.getString("Address"),
                    rs.getString("ContactNumber"),
                    rs.getInt("ExperienceYear"),
                    rs.getString("Specialization")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private boolean createClient(User user) {
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (Email, Password, Role, FullName, TimeOfBirth, Phone) VALUES (?, ?, 'client', ?, ?, ?)")) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(5, user.getPhone());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private boolean updateClient(User user) {
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET Email=?, FullName=?, TimeOfBirth=?, Phone=? WHERE UserId=?")) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(4, user.getPhone());
            stmt.setInt(5, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private boolean deleteClient(int userId) {
        try (Connection conn = com.vedology.config.DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM users WHERE UserId=? AND Role='client'")) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private String getSubmittedFileName(Part part) {
        for (String c : part.getHeader("content-disposition").split(";")) {
            if (c.trim().startsWith("filename")) {
                return c.substring(c.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private List<String> getAllPayments() {
        List<String> p = new ArrayList<>();
        p.add("Payment #P001: Rs 8,500 on 2025-07-03 for Vedic Astrology Consultation");
        p.add("Payment #P002: Rs 5,000 on 2025-07-06 for Numerology Analysis");
        p.add("Payment #P003: Rs 7,000 on 2025-07-09 for Vastu Shastra Assessment");
        return p;
    }

    private List<String> getAllReports() {
        List<String> r = new ArrayList<>();
        r.add("Report #R001: Natal Chart - generated on 2025-07-02");
        r.add("Report #R002: Yearly Horoscope - generated on 2025-07-05");
        return r;
    }

    private List<String> getAllHoroscopes() {
        List<String> h = new ArrayList<>();
        h.add("Horoscope #H001: Aries Forecast - generated on 2025-07-02");
        h.add("Horoscope #H002: Taurus Forecast - generated on 2025-07-05");
        return h;
    }
}
