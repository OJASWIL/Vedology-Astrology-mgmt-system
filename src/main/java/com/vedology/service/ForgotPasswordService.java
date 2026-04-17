package com.vedology.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vedology.config.DbConfig;
import com.vedology.util.PasswordUtil;

/**
 * Service for password reset operations.
 * Verifies that the email + phone combination exists in the DB,
 * then allows updating the password.
 */
public class ForgotPasswordService {

    /**
     * Verifies whether the given email and phone number match a record in the DB.
     *
     * @param email the user's email
     * @param phone the user's phone number
     * @return true if the combination exists, false if not, null on DB error
     */
    public Boolean verifyEmailAndPhone(String email, String phone) {
        String query = "SELECT Email FROM users WHERE Email = ? AND Phone = ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if a matching row exists

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ForgotPasswordService.verifyEmailAndPhone error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the password for the given email.
     * Encrypts the new password using AES-GCM (same scheme as registration).
     *
     * @param email       the user's email (also used as the encryption key)
     * @param newPassword the plain-text new password
     * @return true on success, false on failure, null on DB error
     */
    public Boolean resetPassword(String email, String newPassword) {
        // Encrypt password using email as the key (matches PasswordUtil.encrypt scheme)
        String encryptedPassword = PasswordUtil.encrypt(email, newPassword);
        if (encryptedPassword == null) {
            System.out.println("ForgotPasswordService: password encryption failed for email: " + email);
            return false;
        }

        String query = "UPDATE users SET Password = ? WHERE Email = ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, encryptedPassword);
            stmt.setString(2, email);
            int rows = stmt.executeUpdate();
            System.out.println("ForgotPasswordService.resetPassword: rows updated = " + rows + " for email: " + email);
            return rows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ForgotPasswordService.resetPassword error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}