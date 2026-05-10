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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (isConnectionError) return astrologers;

        // FIXED: Use correct table name 'astrologer'
        String query = "SELECT AstrologerId, AvailableDays, Address, ContactNumber, ExperienceYear, Specialization FROM astrologer";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return astrologers;
    }

    public User getUserProfile(String email) {
        if (isConnectionError) return null;

        User user = null;
        String query = """
            SELECT UserId, Email, Password, Role, FullName, Phone,
                   TimeOfBirth, ProfileImage
            FROM users WHERE Email = ?
            """;

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setRole(rs.getString("Role"));
                user.setFullName(rs.getString("FullName"));
                user.setPhone(rs.getString("Phone"));

                String img = rs.getString("ProfileImage");
                user.setProfileImage((img != null && !img.trim().isEmpty()) ? img.trim() : null);

                String timeStr = rs.getString("TimeOfBirth");
                if (timeStr != null && !timeStr.trim().isEmpty()) {
                    try {
                        user.setTimeOfBirth(LocalTime.parse(timeStr));
                    } catch (Exception e) {
                        System.out.println("Could not parse TimeOfBirth: " + timeStr);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateUserProfile(User user) {
        if (isConnectionError) return false;

        String query = """
            UPDATE users
            SET FullName = ?, Phone = ?, TimeOfBirth = ?,
                ProfileImage = ?, Password = ?
            WHERE UserId = ?
            """;

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getTimeOfBirth() != null ? user.getTimeOfBirth().toString() : null);
            stmt.setString(4, user.getProfileImage());
            stmt.setString(5, user.getPassword());
            stmt.setInt(6, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> searchServices(String keyword) {
        return null;
    }

    public boolean bookAppointment(String clientEmail, int astrologerId, String date, String time) {
        if (isConnectionError) return false;

        ensureAppointmentsTable();

        int clientId = getUserIdByEmail(clientEmail);
        if (clientId <= 0) {
            System.out.println("bookAppointment: client not found for email " + clientEmail);
            return false;
        }

        String sql = "INSERT INTO appointments (client_id, astrologer_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, 'confirmed')";
        try (PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, astrologerId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            int rows = stmt.executeUpdate();
            System.out.println("bookAppointment: inserted " + rows + " row(s) for clientId=" + clientId);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> getAppointmentsForUser(String email) {
        List<Map<String, String>> appointments = new ArrayList<>();
        if (isConnectionError || email == null) return appointments;

        String query = """
            SELECT a.id, a.appointment_date, a.appointment_time, a.status,
                   ast.AstrologerId as astrologer_id,
                   ast.Specialization,
                   ast.ContactNumber as contact
            FROM appointments a
            JOIN astrologer ast ON a.astrologer_id = ast.AstrologerId
            WHERE a.client_id = (SELECT UserId FROM users WHERE Email = ?)
            ORDER BY a.appointment_date DESC, a.appointment_time DESC
            """;

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> appt = new HashMap<>();
                appt.put("id", rs.getString("id"));
                appt.put("date", rs.getString("appointment_date"));
                appt.put("time", rs.getString("appointment_time"));
                appt.put("status", rs.getString("status"));
                appt.put("astrologer_id", rs.getString("astrologer_id"));
                appt.put("specialization", rs.getString("Specialization"));
                appt.put("contact", rs.getString("contact"));
                appointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private int getUserIdByEmail(String email) {
        String sql = "SELECT UserId FROM users WHERE Email = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("UserId");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void ensureAppointmentsTable() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS appointments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                client_id INT NOT NULL,
                astrologer_id INT NOT NULL,
                appointment_date DATE NOT NULL,
                appointment_time TIME NOT NULL,
                status VARCHAR(50) DEFAULT 'confirmed',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (PreparedStatement stmt = dbConn.prepareStatement(ddl)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}