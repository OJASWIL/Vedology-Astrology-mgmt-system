package com.vedology.controller.admin;

import com.vedology.model.User;
import com.vedology.model.Astrologer;
import com.vedology.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.vedology.config.DbConfig;

@WebServlet(asyncSupported = true, urlPatterns = { "/admin/manage" })
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        System.out.println("AdminController doGet - Session ID: " + (session != null ? session.getId() : "null") + 
                           ", User: " + (user != null ? user.getEmail() + ", Role=" + user.getRole() : "null"));

        if (user == null || !"admin".equals(user.getRole())) {
            System.out.println("Redirecting to login due to null user or invalid role: " + 
                               (user != null ? "Email=" + user.getEmail() + ", Role=" + user.getRole() : "null"));
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        AdminService service = new AdminService();
        String tab = req.getParameter("tab");

        System.out.println("Processing tab: " + tab);

        if (tab == null || tab.isEmpty()) {
            req.setAttribute("defaultTab", "dashboard");
        } else if ("edit".equals(tab)) {
            List<User> clients = getAllClients();
            req.setAttribute("clients", clients);
            System.out.println("Clients loaded: " + (clients != null ? clients.size() : 0));
        } else if ("astrologers".equals(tab) || "book".equals(tab)) {
            List<Astrologer> astrologers = getAllAstrologers();
            req.setAttribute("astrologers", astrologers);
            System.out.println("Astrologers loaded: " + (astrologers != null ? astrologers.size() : 0));
        } else if ("search".equals(tab)) {
            req.setAttribute("services", new ArrayList<>());
        } else if ("payments".equals(tab)) {
            List<String> payments = getAllPayments();
            req.setAttribute("payments", payments);
            System.out.println("Payments loaded: " + (payments != null ? payments.size() : 0));
        } else if ("reports".equals(tab)) {
            List<String> reports = getAllReports();
            req.setAttribute("reports", reports);
            System.out.println("Reports loaded: " + (reports != null ? reports.size() : 0));
        } else if ("horoscope".equals(tab)) {
            req.setAttribute("horoscopes", getAllHoroscopes()); 
        } else if ("changePassword".equals(tab)) {
            // Just render the form — flash messages come from session
        }

        // Flash messages from ChangePasswordController (stored in session)
        HttpSession session2 = req.getSession(false);
        if (session2 != null) {
            String pwError = (String) session2.getAttribute("changePasswordError");
            if (pwError != null) {
                req.setAttribute("error", pwError);
                session2.removeAttribute("changePasswordError");
            }
            String pwSuccess = (String) session2.getAttribute("passwordChangeSuccess");
            if (pwSuccess != null) {
                req.setAttribute("message", pwSuccess);
                session2.removeAttribute("passwordChangeSuccess");
            }
        }

        req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        System.out.println("AdminController doPost - Session ID: " + (session != null ? session.getId() : "null") + 
                           ", User: " + (user != null ? user.getEmail() + ", Role=" + user.getRole() : "null"));

        if (user == null || !"admin".equals(user.getRole())) {
            System.out.println("Redirecting to login in post due to null user or invalid role: " + 
                               (user != null ? "Email=" + user.getEmail() + ", Role=" + user.getRole() : "null"));
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        AdminService service = new AdminService();
        String action = req.getParameter("action");
        String tab = req.getParameter("tab");

        if ("create".equals(action)) {
            User newUser = new User();
            newUser.setEmail(req.getParameter("email"));
            newUser.setFullName(req.getParameter("fullName"));
            newUser.setTimeOfBirth(LocalTime.parse(req.getParameter("timeOfBirth")));
            newUser.setPhone(req.getParameter("phone"));
            newUser.setPassword("defaultPassword");
            if (createClient(newUser)) {
                req.setAttribute("message", "Client created successfully.");
            } else {
                req.setAttribute("error", "Failed to create client.");
            }
            req.setAttribute("clients", getAllClients());
        } else if ("update".equals(action)) {
            User updatedUser = new User();
            updatedUser.setUserId(Integer.parseInt(req.getParameter("userId")));
            updatedUser.setEmail(req.getParameter("email"));
            updatedUser.setFullName(req.getParameter("fullName"));
            updatedUser.setTimeOfBirth(LocalTime.parse(req.getParameter("timeOfBirth")));
            updatedUser.setPhone(req.getParameter("phone"));
            if (updateClient(updatedUser)) {
                req.setAttribute("message", "Client updated successfully.");
            } else {
                req.setAttribute("error", "Failed to update client.");
            }
            req.setAttribute("clients", getAllClients());
        } else if ("delete".equals(action)) {
            int userId = Integer.parseInt(req.getParameter("userId"));
            if (deleteClient(userId)) {
                req.setAttribute("message", "Client deleted successfully.");
            } else {
                req.setAttribute("error", "Failed to delete client.");
            }
            req.setAttribute("clients", getAllClients());
        } else if ("search".equals(action)) {
            String keyword = req.getParameter("keyword").toLowerCase();
            List<String> services = getAllServices();
            List<String> filteredServices = new ArrayList<>();
            for (String allService : services) {
                if (allService.toLowerCase().contains(keyword)) {
                    filteredServices.add(allService);
                }
            }
            req.setAttribute("services", filteredServices);
        } else if ("bookAppointment".equals(action)) {
            String clientEmail = req.getParameter("clientEmail");
            String astrologerId = req.getParameter("astrologer");
            String date = req.getParameter("date");
            String time = req.getParameter("time");
            if (service.bookAppointment(clientEmail, astrologerId, date, time)) {
                req.setAttribute("message", "Appointment booked successfully.");
            } else {
                req.setAttribute("error", "Failed to book appointment.");
            }
            req.setAttribute("astrologers", getAllAstrologers());
        } else if ("viewHoroscope".equals(action)) {
            req.setAttribute("horoscopes", getAllHoroscopes()); // Use getAllHoroscopes instead of getHoroscopesForClient
        }

        req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);
    }

    private List<User> getAllClients() {
        List<User> clients = new ArrayList<>();
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT UserId, Email, Role, FullName, TimeOfBirth, Phone FROM users WHERE Role = 'client'");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setEmail(rs.getString("Email"));
                user.setRole(rs.getString("Role"));
                user.setFullName(rs.getString("FullName"));
                String timeOfBirthStr = rs.getString("TimeOfBirth");
                user.setTimeOfBirth(timeOfBirthStr != null ? LocalTime.parse(timeOfBirthStr) : null);
                user.setPhone(rs.getString("Phone"));
                clients.add(user);
            }
            System.out.println("getAllClients: Successfully loaded " + clients.size() + " clients");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("getAllClients: Failed to load clients: " + e.getMessage());
        }
        return clients;
    }

    private List<Astrologer> getAllAstrologers() {
        List<Astrologer> astrologers = new ArrayList<>();
        String query = "SELECT AstrologerId, AvailableDays, Address, ContactNumber, ExperienceYear, Specialization FROM astrologer";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int astrologerId = rs.getInt("AstrologerId");
                String availableDays = rs.getString("AvailableDays");
                String address = rs.getString("Address");
                String contactNumber = rs.getString("ContactNumber");
                int experienceYear = rs.getInt("ExperienceYear");
                String specialization = rs.getString("Specialization");

                Astrologer astrologer = new Astrologer(astrologerId, availableDays, address, 
                                                      contactNumber, experienceYear, specialization);
                astrologers.add(astrologer);
            }
            System.out.println("getAllAstrologers: Successfully loaded " + astrologers.size() + " astrologers");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("getAllAstrologers: Failed to load astrologers: " + e.getMessage());
        }
        return astrologers;
    }

    private List<String> getAllServices() {
        List<String> services = new ArrayList<>();
        services.add("Vedic Astrology Consultation");
        services.add("Numerology Analysis");
        services.add("Vastu Shastra Assessment");
        services.add("Palm Reading Session");
        services.add("Tarot Card Reading");
        services.add("Daily Horoscope Analysis");
        services.add("Yearly Horoscope Analysis");
        services.add("Kundli Matching");
        services.add("Muhurta Selection");
        services.add("Gemstone Recommendation");
        services.add("Panchang Analysis");
        services.add("Astrological Remedies");
        return services;
    }

    private List<String> getAllPayments() {
        List<String> payments = new ArrayList<>();
        payments.add("Payment #P001: Rs 8,500.00 on 2025-07-03 for Vedic Astrology Consultation (Client: oj.thapa@example.com)");
        payments.add("Payment #P002: Rs 5,000.00 on 2025-07-06 for Numerology Analysis (Client: prabuddha.adhikari@example.com)");
        payments.add("Payment #P003: Rs 7,000.00 on 2025-07-09 for Vastu Shastra Assessment (Client: rahul.lamichhane@example.com)");
        payments.add("Payment #P004: Rs 4,500.00 on 2025-07-12 for Kundli Matching (Client: anu.sharma@example.com)");
        payments.add("Payment #P005: Rs 6,000.00 on 2025-07-15 for Gemstone Recommendation (Client: garima.shrestha@example.com)");
        payments.add("Payment #P006: Rs 3,500.00 on 2025-07-18 for Daily Horoscope Analysis (Client: suraj.kc@example.com)");
        payments.add("Payment #P007: Rs 9,000.00 on 2025-07-21 for Muhurta Selection (Client: laxmi.poudel@example.com)");
        return payments;
    }

    private List<String> getAllReports() {
        List<String> reports = new ArrayList<>();
        reports.add("Report #R001: Natal Chart for oj.thapa@example.com, generated on 2025-07-02");
        reports.add("Report #R002: Yearly Horoscope for prabuddha.adhikari@example.com, generated on 2025-07-05");
        reports.add("Report #R003: Kundli Matching for rahul.lamichhane@example.com, generated on 2025-07-08");
        reports.add("Report #R004: Vastu Analysis for anu.sharma@example.com, generated on 2025-07-10");
        reports.add("Report #R005: Gemstone Recommendation for garima.shrestha@example.com, generated on 2025-07-13");
        reports.add("Report #R006: Astrological Remedies for suraj.kc@example.com, generated on 2025-07-16");
        reports.add("Report #R007: Panchang Analysis for laxmi.poudel@example.com, generated on 2025-07-20");
        return reports;
    }

    private List<String> getAllHoroscopes() {
        List<String> horoscopes = new ArrayList<>();
        horoscopes.add("Horoscope #H001: Aries Forecast for oj.thapa@example.com, generated on 2025-07-02");
        horoscopes.add("Horoscope #H002: Taurus Forecast for prabuddha.adhikari@example.com, generated on 2025-07-05");
        horoscopes.add("Horoscope #H003: Gemini Forecast for rahul.lamichhane@example.com, generated on 2025-07-08");
        horoscopes.add("Horoscope #H004: Cancer Forecast for anu.sharma@example.com, generated on 2025-07-10");
        horoscopes.add("Horoscope #H005: Leo Forecast for garima.shrestha@example.com, generated on 2025-07-13");
        horoscopes.add("Horoscope #H006: Virgo Forecast for suraj.kc@example.com, generated on 2025-07-16");
        horoscopes.add("Horoscope #H007: Libra Forecast for laxmi.poudel@example.com, generated on 2025-07-20");
        System.out.println("getAllHoroscopes: Loaded " + horoscopes.size() + " horoscopes");
        return horoscopes;
    }

    private boolean createClient(User user) {
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (Email, Password, Role, FullName, TimeOfBirth, Phone) VALUES (?, ?, 'client', ?, ?, ?)")) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(5, user.getPhone());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateClient(User user) {
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET Email = ?, FullName = ?, TimeOfBirth = ?, Phone = ? WHERE UserId = ?")) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(4, user.getPhone());
            stmt.setInt(5, user.getUserId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteClient(int userId) {
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE UserId = ? AND Role = 'client'")) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}