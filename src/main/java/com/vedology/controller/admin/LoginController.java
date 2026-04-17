package com.vedology.controller.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vedology.config.DbConfig;
import com.vedology.model.User;
import com.vedology.service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LoginController handles user login requests and processes form submissions
 * for the Vedology Astrology Management System.
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/login" })
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET request received for /login");
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST request received for /login");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        System.out.println("Login attempt - Email: " + email + ", Password: [HIDDEN]");

        LoginService loginService = new LoginService();
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        Boolean isValid = loginService.loginUser(user);
        System.out.println("LoginService result: " + isValid);

        if (isValid == null) {
            System.out.println("Database connection error during login for email: " + email);
            req.setAttribute("error", "Database connection error. Please try again later.");
            req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
        } else if (isValid) {
            User loggedInUser = getUserDetails(email);
            if (loggedInUser != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", loggedInUser);
                System.out.println("Login successful - Email: " + loggedInUser.getEmail() + ", Role: " + loggedInUser.getRole() + ", Session ID: " + session.getId());
                if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
                    System.out.println("Redirecting admin to /admin-dashboard");
                    resp.sendRedirect(req.getContextPath() + "/admin-dashboard");
                } else if ("client".equalsIgnoreCase(loggedInUser.getRole())) {
                    System.out.println("Redirecting client to /client-dashboard");
                    resp.sendRedirect(req.getContextPath() + "/client-dashboard");
                } else {
                    System.out.println("Unknown role: " + loggedInUser.getRole());
                    req.setAttribute("error", "Invalid user role.");
                    req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
                }
            } else {
                System.out.println("Error retrieving user details for email: " + email);
                req.setAttribute("error", "Error retrieving user details.");
                req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
            }
        } else {
            System.out.println("Invalid credentials for email: " + email);
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
        }
    }

    private User getUserDetails(String email) {
        try (Connection dbConn = DbConfig.getDbConnection();
             PreparedStatement stmt = dbConn.prepareStatement("SELECT UserId, Email, Role, FullName, Phone, TimeOfBirth FROM users WHERE Email = ?")) {
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                User user = new User();
                user.setUserId(result.getInt("UserId"));
                user.setEmail(result.getString("Email"));
                String role = result.getString("Role");
                user.setRole(role != null ? role.trim() : null);
                user.setFullName(result.getString("FullName"));
                user.setPhone(result.getString("Phone"));
                user.setTimeOfBirth(result.getTime("timeOfBirth").toLocalTime());
                System.out.println("Fetched user details - UserId: " + user.getUserId() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
                return user;
            } else {
                System.out.println("No user found for email: " + email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error fetching user details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}