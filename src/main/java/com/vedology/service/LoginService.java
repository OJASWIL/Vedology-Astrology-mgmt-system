package com.vedology.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vedology.config.DbConfig;
import com.vedology.model.User;
import com.vedology.util.PasswordUtil;

/**
 * Service class for handling login operations. Connects to the database,
 * verifies user credentials, and returns login status with user details.
 */
public class LoginService {

    private Connection dbConn;
    private boolean isConnectionError = false;

    /**
     * Constructor initializes the database connection. Sets the connection error
     * flag if the connection fails.
     */
    public LoginService() {
        try {
            dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            isConnectionError = true;
        }
    }

    /**
     * Validates the user credentials against the database records.
     *
     * @param astroDetailModel the User object containing user credentials
     * @return true if the user credentials are valid, false otherwise; null if a
     *         connection error occurs
     */
    public Boolean loginUser(User astroDetailModel) {
        if (isConnectionError) {
            System.out.println("Connection Error!");
            return null;
        }

        String query = "SELECT Email, Password, Role, FullName FROM users WHERE Email = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, astroDetailModel.getEmail()); // Map userName to Email
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return validatePassword(result, astroDetailModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return false;
    }

    /**
     * Validates the password retrieved from the database and updates model with user details.
     *
     * @param result           the ResultSet containing the email, password, role, and full name
     * @param astroDetailModel the User object containing user credentials
     * @return true if the passwords match, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean validatePassword(ResultSet result, User astroDetailModel) throws SQLException {
        String dbEmail = result.getString("Email");
        String dbPassword = result.getString("Password");
        String enteredPassword = astroDetailModel.getPassword();

        if (dbEmail == null || !dbEmail.equals(astroDetailModel.getEmail())) {
            return false;
        }

        String decryptedPassword = PasswordUtil.decrypt(dbPassword, dbEmail);
        if (decryptedPassword == null) {
            System.out.println("Decryption failed for email: " + dbEmail);
            return false; // Handle decryption failure gracefully
        }

        return enteredPassword != null && decryptedPassword.equals(enteredPassword);
    }
}