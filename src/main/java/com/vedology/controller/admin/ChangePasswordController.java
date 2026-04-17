package com.vedology.controller.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vedology.config.DbConfig;
import com.vedology.model.User;
import com.vedology.util.PasswordUtil;
import com.vedology.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(asyncSupported = true, urlPatterns = { "/change-password" })
public class ChangePasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String dashboard = "admin".equalsIgnoreCase(user.getRole())
                ? "/admin/manage?tab=changePassword"
                : "/client-dashboard?tab=changePassword";
        resp.sendRedirect(req.getContextPath() + dashboard);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String currentPassword = req.getParameter("currentPassword");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        String redirectTab = "admin".equalsIgnoreCase(user.getRole())
                ? "/admin/manage?tab=changePassword"
                : "/client-dashboard?tab=changePassword";

        // Validation
        if (ValidationUtil.isNullOrEmpty(currentPassword)) {
            sendBack(req, resp, redirectTab, "Current password is required.");
            return;
        }
        if (ValidationUtil.isNullOrEmpty(newPassword)) {
            sendBack(req, resp, redirectTab, "New password is required.");
            return;
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            sendBack(req, resp, redirectTab, "New password must be at least 8 characters long.");
            return;
        }
        if (!ValidationUtil.doPasswordsMatch(newPassword, confirmPassword)) {
            sendBack(req, resp, redirectTab, "New passwords do not match.");
            return;
        }
        if (currentPassword.equals(newPassword)) {
            sendBack(req, resp, redirectTab, "New password must be different from the current password.");
            return;
        }

        // Verify current password
        String storedEncrypted = getStoredPassword(user.getEmail());
        if (storedEncrypted == null) {
            sendBack(req, resp, redirectTab, "Unable to retrieve current password. Please try again.");
            return;
        }

        String decrypted = PasswordUtil.decrypt(storedEncrypted, user.getEmail());
        if (decrypted == null || !decrypted.equals(currentPassword)) {
            sendBack(req, resp, redirectTab, "Current password is incorrect.");
            return;
        }

        // Update password
        String encrypted = PasswordUtil.encrypt(user.getEmail(), newPassword);
        if (encrypted == null) {
            sendBack(req, resp, redirectTab, "Password encryption failed. Please try again.");
            return;
        }

        boolean updated = updatePasswordInDb(user.getEmail(), encrypted);
        if (!updated) {
            sendBack(req, resp, redirectTab, "Failed to update password. Please try again.");
            return;
        }

        // Success
        user.setPassword(encrypted);
        session.setAttribute("user", user);
        System.out.println("ChangePasswordController: password updated for " + user.getEmail());

        req.getSession().setAttribute("passwordChangeSuccess", "Password changed successfully!");
        resp.sendRedirect(req.getContextPath() + redirectTab + "&pwChanged=1");
    }

    private String getStoredPassword(String email) {
        String query = "SELECT Password FROM users WHERE Email = ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("Password");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ChangePasswordController.getStoredPassword error: " + e.getMessage());
        }
        return null;
    }

    private boolean updatePasswordInDb(String email, String encryptedPassword) {
        String query = "UPDATE users SET Password = ? WHERE Email = ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, encryptedPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ChangePasswordController.updatePasswordInDb error: " + e.getMessage());
            return false;
        }
    }

    private void sendBack(HttpServletRequest req, HttpServletResponse resp,
                          String path, String error) throws IOException {
        req.getSession().setAttribute("changePasswordError", error);
        resp.sendRedirect(req.getContextPath() + path);
    }
}