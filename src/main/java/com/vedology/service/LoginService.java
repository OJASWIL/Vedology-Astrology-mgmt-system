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

    /**
     * Validates the user credentials against the database records.
     * Connection is opened and closed within this method to prevent connection leaks.
     *
     * @param astroDetailModel the User object containing user credentials
     * @return true if the user credentials are valid, false if not found or password mismatch;
     *         null if a connection/database error occurs
     */
    public Boolean loginUser(User astroDetailModel) {
        String query = "SELECT Email, Password, Role, FullName FROM users WHERE Email = ?";

        try (Connection dbConn = DbConfig.getDbConnection();
             PreparedStatement stmt = dbConn.prepareStatement(query)) {

            stmt.setString(1, astroDetailModel.getEmail());
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return validatePassword(result, astroDetailModel);
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database connection error during login for email: "
                    + astroDetailModel.getEmail());
            e.printStackTrace();
            return null; // signals connection/DB error to the caller
        }

        return false; // email not found in database
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
            return false;
        }

        return enteredPassword != null && decryptedPassword.equals(enteredPassword);
    }
}