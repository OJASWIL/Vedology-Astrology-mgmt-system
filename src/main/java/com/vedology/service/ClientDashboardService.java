package com.vedology.service;

import com.vedology.model.Astrologer;
import com.vedology.model.User;
import com.vedology.config.DbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClientDashboardService {
    private Connection dbConn;
    private boolean isConnectionError = false;

    public ClientDashboardService() {
        try {
            dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            isConnectionError = true;
        }
    }

    public List<Astrologer> getAstrologerList() {
        List<Astrologer> astrologers = new ArrayList<>();
        if (isConnectionError) {
            System.out.println("Connection Error!");
            return astrologers;
        }

        String query = "SELECT AstrologerId, AvailableDays, Address, ContactNumber, ExperienceYear, Specialization FROM astrologers";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Astrologer astrologer = new Astrologer(
                    rs.getInt("AstrologerId"),
                    rs.getString("AvailableDays"),
                    rs.getString("Address"),
                    rs.getString("ContactNumber"),
                    rs.getInt("ExperienceYear"),
                    rs.getString("Specialization")
                );
                astrologers.add(astrologer);
            }
            System.out.println("getAstrologerList: Successfully loaded " + astrologers.size() + " astrologers");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getAstrologerList: Failed to load astrologers: " + e.getMessage());
        }
        return astrologers;
    }

    public User getUserProfile(String email) {
        if (isConnectionError) {
            System.out.println("Connection Error!");
            return null;
        }

        User user = null;
        String query = "SELECT UserId, Email, Password, FullName, TimeOfBirth, Phone FROM users WHERE Email = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setFullName(rs.getString("FullName"));
                String timeOfBirthStr = rs.getString("TimeOfBirth");
                if (timeOfBirthStr != null) {
                    user.setTimeOfBirth(LocalTime.parse(timeOfBirthStr));
                }
                user.setPhone(rs.getString("Phone"));
                System.out.println("getUserProfile: Successfully loaded profile for email " + email);
            } else {
                System.out.println("getUserProfile: No profile found for email " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getUserProfile failed for email " + email + ": " + e.getMessage());
        }
        return user;
    }

    public boolean updateUserProfile(User user) {
        if (isConnectionError) {
            System.out.println("Connection Error!");
            return false;
        }

        String query = "UPDATE users SET FullName = ?, Phone = ?, TimeOfBirth = ?, Password = ? WHERE UserId = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(4, user.getPassword());
            stmt.setInt(5, user.getUserId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("updateUserProfile: Rows affected for UserId " + user.getUserId() + ": " + rowsAffected);
            if (rowsAffected == 0) {
                System.out.println("updateUserProfile: No rows updated. Check if UserId " + user.getUserId() + " exists.");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("updateUserProfile failed for UserId " + user.getUserId() + ": " + e.getMessage() +
                               ", SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
            return false;
        }
    }

    public List<String> searchServices(String keyword) {
        // Existing implementation
        return null; // Placeholder
    }

    public boolean bookAppointment(String email, String astrologer, String date, String time) {
        // Existing implementation
        return false; // Placeholder
    }
}