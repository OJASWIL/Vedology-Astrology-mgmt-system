package com.vedology.controller.admin;

import com.vedology.model.Astrologer;
import com.vedology.model.User;
import com.vedology.service.ClientDashboardService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet(asyncSupported = true, urlPatterns = { "/client-dashboard" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize       = 1024 * 1024 * 5,
    maxRequestSize    = 1024 * 1024 * 10
)
public class ClientDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "images" + File.separator + "profiles";

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ClientDashboardService service = new ClientDashboardService();

        // Load astrologers for all tabs
        List<Astrologer> astrologers = service.getAstrologerList();
        req.setAttribute("astrologers", astrologers);

        // Load fresh profile
        User profile = service.getUserProfile(user.getEmail());
        if (profile != null) {
            if (profile.getRole() == null) profile.setRole(user.getRole());
            if (profile.getProfileImage() != null && profile.getProfileImage().trim().isEmpty()) {
                profile.setProfileImage(null);
            }
            req.setAttribute("userProfile", profile);
            session.setAttribute("user", profile);

            // FIXED: Pass email (String) to match service method
            List<Map<String, String>> appointments = service.getAppointmentsForUser(profile.getEmail());
            req.setAttribute("appointments", appointments);
        } else {
            req.setAttribute("userProfile", user);
            req.setAttribute("error", "Unable to load profile from database.");

            List<Map<String, String>> appointments = service.getAppointmentsForUser(user.getEmail());
            req.setAttribute("appointments", appointments);
        }

        req.getRequestDispatcher("/WEB-INF/pages/clientDashboard.jsp").forward(req, resp);
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        ClientDashboardService service = new ClientDashboardService();

        if ("updateProfile".equals(action)) {

            User updatedUser = new User();
            updatedUser.setUserId(currentUser.getUserId());
            updatedUser.setEmail(currentUser.getEmail());
            updatedUser.setRole(currentUser.getRole());
            updatedUser.setFullName(req.getParameter("fullName"));
            updatedUser.setPhone(req.getParameter("phone"));

            // Time of Birth
            String timeStr = req.getParameter("timeOfBirth");
            try {
                if (timeStr != null && !timeStr.trim().isEmpty()) {
                    updatedUser.setTimeOfBirth(LocalTime.parse(timeStr));
                } else {
                    updatedUser.setTimeOfBirth(currentUser.getTimeOfBirth());
                }
            } catch (Exception e) {
                session.setAttribute("profileError", "Invalid time format. Use HH:MM.");
                resp.sendRedirect(req.getContextPath() + "/client-dashboard?tab=edit");
                return;
            }

            // Profile Image Upload
            String savedImageName = currentUser.getProfileImage(); // keep existing by default
            try {
                Part filePart = req.getPart("profileImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String originalName = getSubmittedFileName(filePart);
                    if (originalName != null && !originalName.isEmpty()) {
                        String lower = originalName.toLowerCase();
                        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
                            String ext = originalName.substring(originalName.lastIndexOf("."));
                            String newFileName = "user_" + currentUser.getUserId() + "_"
                                    + UUID.randomUUID().toString().substring(0, 8) + ext;

                            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                            Path dir = Paths.get(uploadPath);
                            if (!Files.exists(dir)) Files.createDirectories(dir);

                            filePart.write(uploadPath + File.separator + newFileName);
                            savedImageName = newFileName;
                            System.out.println("Profile image saved: " + uploadPath + File.separator + newFileName);
                        } else {
                            session.setAttribute("profileError", "Only JPG and PNG images are allowed.");
                            resp.sendRedirect(req.getContextPath() + "/client-dashboard?tab=edit");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Image upload error: " + e.getMessage());
                // Keep existing image on upload error
            }
            // Normalise empty to null
            updatedUser.setProfileImage((savedImageName != null && !savedImageName.trim().isEmpty())
                    ? savedImageName.trim() : null);

            // Password
            String newPass     = req.getParameter("newPassword");
            String confirmPass = req.getParameter("confirmPassword");
            if (newPass != null && !newPass.trim().isEmpty()) {
                if (!newPass.equals(confirmPass)) {
                    session.setAttribute("profileError", "Passwords do not match.");
                    resp.sendRedirect(req.getContextPath() + "/client-dashboard?tab=edit");
                    return;
                }
                updatedUser.setPassword(PasswordUtil.encrypt(updatedUser.getEmail(), newPass));
            } else {
                // Preserve existing password
                User freshProfile = service.getUserProfile(currentUser.getEmail());
                updatedUser.setPassword(freshProfile != null ? freshProfile.getPassword() : currentUser.getPassword());
            }

            // Save
            boolean success = service.updateUserProfile(updatedUser);
            if (success) {
                // Update session with the saved data (including new image name)
                session.setAttribute("user", updatedUser);
                session.setAttribute("profileSuccess", "Profile updated successfully!");
            } else {
                session.setAttribute("profileError", "Failed to update profile. Please try again.");
            }

            // Redirect back to edit tab (PRG pattern — prevents double-submit on refresh)
            resp.sendRedirect(req.getContextPath() + "/client-dashboard?tab=edit");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/client-dashboard");
    }

    private String getSubmittedFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
