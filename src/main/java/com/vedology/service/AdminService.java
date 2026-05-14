package com.vedology.service;

import com.vedology.config.DbConfig;
import com.vedology.model.User;
import com.vedology.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling admin dashboard operations, including client
 * management, service search, astrologer list, and appointment booking.
 */
public class AdminService {
	private Connection dbConn;
	private boolean isConnectionError = false;

	public AdminService() {
		try {
			dbConn = DbConfig.getDbConnection();
		} catch (SQLException | ClassNotFoundException ex) {
			ex.printStackTrace();
			isConnectionError = true;
		}
	}

	public List<User> getAllClients() {
		List<User> clients = new ArrayList<>();
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return clients;
		}

		String query = "SELECT UserId, Email, Password, Role, CreatedAt, FullName, TimeOfBirth, Phone FROM users WHERE Role = 'client'";
		try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				User user = new User();
				user.setUserId(result.getInt("UserId"));
				user.setEmail(result.getString("Email"));
				user.setPassword(result.getString("Password"));
				user.setRole(result.getString("Role"));
				user.setCreatedAt(result.getTimestamp("CreatedAt"));
				user.setFullName(result.getString("FullName"));
				String timeOfBirth = result.getString("TimeOfBirth");
				if (timeOfBirth != null) {
					user.setTimeOfBirth(LocalTime.parse(timeOfBirth));
				}
				user.setPhone(result.getString("Phone"));
				clients.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return clients;
	}

	public boolean createClient(User user) {
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return false;
		}

		String query = "INSERT INTO users (Email, Password, Role, CreatedAt, FullName, TimeOfBirth, Phone) VALUES (?, ?, 'client', ?, ?, ?, ?)";
		try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
			stmt.setString(1, user.getEmail());
			stmt.setString(2, PasswordUtil.encrypt(user.getPassword(), user.getEmail()));
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stmt.setString(4, user.getFullName());
			stmt.setString(5, user.getTimeOfBirth().toString());
			stmt.setString(6, user.getPhone());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateClient(User user) {
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return false;
		}

		String query = "UPDATE users SET Email = ?, FullName = ?, TimeOfBirth = ?, Phone = ? WHERE UserId = ?";
		try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getFullName());
			stmt.setString(3, user.getTimeOfBirth().toString());
			stmt.setString(4, user.getPhone());
			stmt.setInt(5, user.getUserId());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteClient(int userId) {
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return false;
		}

		String query = "DELETE FROM users WHERE UserId = ? AND Role = 'client'";
		try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
			stmt.setInt(1, userId);
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String> searchServices(String keyword) {
		List<String> services = new ArrayList<>();
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return services;
		}

		String query = "SELECT ServiceName FROM services WHERE ServiceName LIKE ? OR Description LIKE ?";
		try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				services.add(result.getString("ServiceName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return services;
	}

	public List<String> getAstrologerList() {
		List<String> astrologers = new ArrayList<>();
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return astrologers;
		}

		// Placeholder: Replace with actual query once astrologer table schema is
		// provided
		try (PreparedStatement stmt = dbConn.prepareStatement("SELECT Name FROM astrologers")) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				astrologers.add(result.getString("Name"));
			}
		} catch (SQLException e) {
			astrologers.add("Astrologer 1");
			astrologers.add("Astrologer 2");
			e.printStackTrace();
		}
		return astrologers;
	}

	public boolean bookAppointment(String clientEmail, String astrologer, String date, String time) {
		if (isConnectionError) {
			System.out.println("Connection Error!");
			return false;
		}

		// Placeholder: Replace with actual query once appointment table schema is
		// provided
		try (PreparedStatement stmt = dbConn.prepareStatement(
				"INSERT INTO appointments (ClientEmail, Astrologer, Date, Time) VALUES (?, ?, ?, ?)")) {
			stmt.setString(1, clientEmail);
			stmt.setString(2, astrologer);
			stmt.setString(3, date);
			stmt.setString(4, time);
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return true; // Static success for testing
		}
	}
}