//package com.vedology.service;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import com.vedology.config.DbConfig;
//import com.vedology.model.AstroDetailModel;
//
///**
// * RegisterService handles the registration of new students. It manages database
// * interactions for student registration.
// */
//public class RegisterService {
//
//	private Connection dbConn;
//
//	/**
//	 * Constructor initializes the database connection.
//	 */
//	public RegisterService() {
//		try {
//			this.dbConn = DbConfig.getDbConnection();
//		} catch (SQLException | ClassNotFoundException ex) {
//			System.err.println("Database connection error: " + ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
//
//	/**
//	 * Registers a new student in the database.
//	 *
//	 * @param studentModel the student details to be registered
//	 * @return Boolean indicating the success of the operation
//	 */
//	public Boolean addAstro(AstroDetailModel AstroDetailModel) {
//		if (dbConn == null) {
//			System.err.println("Database connection is not available.");
//			return null;
//		}
//
//		String programQuery = "SELECT program_id FROM program WHERE name = ?";
//		String insertQuery = "INSERT INTO student (first_name, last_name, username, dob, gender, email, number, password, program_id, image_path) "
//				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//		try (PreparedStatement programStmt = dbConn.prepareStatement(programQuery);
//				PreparedStatement insertStmt = dbConn.prepareStatement(insertQuery)) {
//
//			// Fetch program ID
////			programStmt.setString(1, AstroDetailModel.getProgram().getName());
//			ResultSet result = programStmt.executeQuery();
//			int programId = result.next() ? result.getInt("program_id") : 1;
//
//			// Insert student details
//			insertStmt.setString(1, AstroDetailModel.getFirstName());
//			insertStmt.setString(2, AstroDetailModel.getLastName());
//			insertStmt.setString(3, AstroDetailModel.getUserName());
//			insertStmt.setDate(4, Date.valueOf(AstroDetailModel.getDob()));
//			insertStmt.setString(5, AstroDetailModel.getGender());
//			insertStmt.setString(6, AstroDetailModel.getEmail());
//			insertStmt.setString(7, AstroDetailModel.getNumber());
//			insertStmt.setString(8, AstroDetailModel.getPassword());
//			insertStmt.setInt(9, programId);
//			insertStmt.setString(10, AstroDetailModel.getImageUrl());
//
//			return insertStmt.executeUpdate() > 0;
//		} catch (SQLException e) {
//			System.err.println("Error during student registration: " + e.getMessage());
//			e.printStackTrace();
//			return null;
//		}
//	}
//}
