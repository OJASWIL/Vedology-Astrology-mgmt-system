 	package com.vedology.controller.admin;
	
	import java.io.IOException;
	import java.time.LocalTime;
	
	import com.vedology.config.DbConfig;
	import com.vedology.model.User;
	import com.vedology.util.PasswordUtil;
	import com.vedology.util.ValidationUtil;
	
	import jakarta.servlet.ServletException;
	import jakarta.servlet.annotation.WebServlet;
	import jakarta.servlet.http.HttpServlet;
	import jakarta.servlet.http.HttpServletRequest;
	import jakarta.servlet.http.HttpServletResponse;
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.SQLException;
	
	/**
	 * RegisterController handles user registration requests and processes form
	 */
	@WebServlet(asyncSupported = true, urlPatterns = { "/register" })
	public class RegisterController extends HttpServlet {
	    private static final long serialVersionUID = 1L;
	
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        System.out.println("GET request received for /register");
	        req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
	    }
	
	    @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        System.out.println("POST request received for /register");
	        try {
	            String validationMessage = validateRegistrationForm(req);
	            if (validationMessage != null) {
	                System.out.println("Validation failed: " + validationMessage);
	                handleError(req, resp, validationMessage);
	                return;
	            }
	
	            User user = extractUserModel(req);
	            System.out.println("Extracted user: Email=" + user.getEmail() + ", FullName=" + user.getFullName());
	            boolean isAdded = addUserToDatabase(user);
	            System.out.println("Database operation result: " + isAdded);
	
	            if (isAdded) {
	                handleSuccess(req, resp, "Your account is successfully created!", "/WEB-INF/pages/login.jsp");
	            } else {
	                handleError(req, resp, "Could not register your account. Please try again later!");
	            }
	        } catch (SQLException e) {
	            System.out.println("SQL Exception: " + e.getMessage());
	            e.printStackTrace();
	            handleError(req, resp, "Database error: " + e.getMessage());
	        } catch (ClassNotFoundException e) {
	            System.out.println("ClassNotFoundException: " + e.getMessage());
	            e.printStackTrace();
	            handleError(req, resp, "JDBC driver not found. Please ensure the MySQL JDBC driver is included.");
	        } catch (Exception e) {
	            System.out.println("Unexpected Exception: " + e.getMessage());
	            e.printStackTrace();
	            handleError(req, resp, "An unexpected error occurred: " + e.getMessage());
	        }
	    }
	
	    private String validateRegistrationForm(HttpServletRequest req) {
	        String fullname = req.getParameter("fullname");
	        String email = req.getParameter("email");
	        String timeOfBirth = req.getParameter("timeOfBirth");
	        String phone = req.getParameter("phone");
	        String password = req.getParameter("password");
	        String confirmPassword = req.getParameter("confirm-password");
	        String terms = req.getParameter("terms");
	
	        System.out.println("Validating: fullname=" + fullname + ", email=" + email);
	        if (ValidationUtil.isNullOrEmpty(fullname))
	            return "Full name is required.";
	        if (!ValidationUtil.isValidFullName(fullname))
	            return "Full name must contain only letters and spaces.";
	        if (ValidationUtil.isNullOrEmpty(email))
	            return "Email is required.";
	        if (ValidationUtil.isNullOrEmpty(timeOfBirth))
	            return "Time of birth is required.";
	        if (!ValidationUtil.isValidTimeFormat(timeOfBirth))
	            return "Invalid time format. Use HH:MM.";
	        if (ValidationUtil.isNullOrEmpty(phone))
	            return "Phone number is required.";
	        if (ValidationUtil.isNullOrEmpty(password))
	            return "Password is required.";
	        if (ValidationUtil.isNullOrEmpty(confirmPassword))
	            return "Confirm password is required.";
	        if (terms == null)
	            return "You must agree to the terms and conditions.";
	
	        if (!ValidationUtil.isValidEmail(email))
	            return "Invalid email format.";
	        if (!ValidationUtil.isValidPhoneNumber(phone))
	            return "Phone number must be 10 digits and start with 98.";
	        if (!ValidationUtil.isValidPassword(password))
	            return "Password must be at least 8 characters long";
	        if (!ValidationUtil.doPasswordsMatch(password, confirmPassword))
	            return "Passwords do not match.";
	
	        return null;
	    }
	
	    private User extractUserModel(HttpServletRequest req) {
	        String fullname = req.getParameter("fullname");
	        String email = req.getParameter("email");
	        String timeOfBirth = req.getParameter("timeOfBirth");
	        String phone = req.getParameter("phone");
	        String password = req.getParameter("password");
	        String role = "client";
	
	        // Encrypt password using email as the key
	        String encryptedPassword = PasswordUtil.encrypt(email, password);
	        if (encryptedPassword == null) {
	            throw new IllegalStateException("Password encryption failed.");
	        }
	
	        User user = new User();
	        user.setEmail(email);
	        user.setPassword(encryptedPassword);
	        user.setRole(role);
	        user.setFullName(fullname);
	        user.setTimeOfBirth(LocalTime.parse(timeOfBirth));
	        user.setPhone(phone);
	        return user;
	    }
	
	    private boolean addUserToDatabase(User user) throws SQLException, ClassNotFoundException {
	        String sql = "INSERT INTO users (Email, Password, Role, FullName, TimeOfBirth, Phone) VALUES (?, ?, ?, ?, ?, ?)";
	        System.out.println("Executing SQL: " + sql);
	        try (Connection conn = DbConfig.getDbConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, user.getEmail());
	            pstmt.setString(2, user.getPassword());
	            pstmt.setString(3, user.getRole());
	            pstmt.setString(4, user.getFullName());
	            pstmt.setString(5, user.getTimeOfBirth().toString());
	            pstmt.setString(6, user.getPhone());
	            int rowsAffected = pstmt.executeUpdate();
	            System.out.println("Rows affected: " + rowsAffected);
	            return rowsAffected > 0;
	        }
	    }
	
	    private void handleSuccess(HttpServletRequest req, HttpServletResponse resp, String message, String redirectPage)
	            throws ServletException, IOException {
	        req.setAttribute("success", message);
	        req.getRequestDispatcher(redirectPage).forward(req, resp);
	    }
	
	    private void handleError(HttpServletRequest req, HttpServletResponse resp, String message)
	            throws ServletException, IOException {
	        req.setAttribute("error", message);
	        req.setAttribute("fullname", req.getParameter("fullname"));
	        req.setAttribute("email", req.getParameter("email"));
	        req.setAttribute("timeOfBirth", req.getParameter("timeOfBirth"));
	        req.setAttribute("phone", req.getParameter("phone"));
	        req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
	    }
	}