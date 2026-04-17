package com.vedology.controller.admin;

import com.vedology.model.Astrologer;
import com.vedology.model.User;
import com.vedology.service.ClientDashboardService;
import com.vedology.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * ClientDashboardController handles requests for the client dash board, including
 * profile editing, service search, astrologer list, and other features.
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/client-dashboard" })
public class ClientDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("doGet - Session ID: " + (session != null ? session.getId() : "null") +
                           ", User: " + (user != null ? user.getEmail() + ", UserId=" + user.getUserId() : "null"));

        if (user == null) {
            System.out.println("User session not found, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ClientDashboardService service = new ClientDashboardService();
        String tab = req.getParameter("tab");

        if ("astrologers".equals(tab)) {
            List<Astrologer> astrologers = service.getAstrologerList();
            req.setAttribute("astrologers", astrologers);
            System.out.println("Astrologers loaded for client: " + (astrologers != null ? astrologers.size() : 0));
        }

        // Flash messages from ChangePasswordController (stored in session)
        String pwError = (String) session.getAttribute("changePasswordError");
        if (pwError != null) {
            req.setAttribute("error", pwError);
            session.removeAttribute("changePasswordError");
        }
        String pwSuccess = (String) session.getAttribute("passwordChangeSuccess");
        if (pwSuccess != null) {
            req.setAttribute("message", pwSuccess);
            session.removeAttribute("passwordChangeSuccess");
        }

        User profile = service.getUserProfile(user.getEmail());
        if (profile != null) {
            req.setAttribute("userProfile", profile);
        } else {
            req.setAttribute("error", "Unable to load profile.");
        }

        req.getRequestDispatcher("/WEB-INF/pages/clientDashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("doPost - Session ID: " + (session != null ? session.getId() : "null") +
                           ", User: " + (user != null ? user.getEmail() + ", UserId=" + user.getUserId() : "null"));

        if (user == null) {
            System.out.println("User session lost during post, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ClientDashboardService service = new ClientDashboardService();
        String action = req.getParameter("action");
        String tab = req.getParameter("tab");

        if ("updateProfile".equals(action)) {
            User updatedUser = new User();
            updatedUser.setUserId(user.getUserId());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setFullName(req.getParameter("fullName"));
            updatedUser.setPhone(req.getParameter("phone"));
            String timeOfBirthStr = req.getParameter("timeOfBirth");
            LocalTime timeOfBirth = null;
            try {
                timeOfBirth = LocalTime.parse(timeOfBirthStr);
                updatedUser.setTimeOfBirth(timeOfBirth);
            } catch (DateTimeParseException e) {
                req.setAttribute("error", "Invalid time of birth format. Use HH:mm (e.g., 14:30).");
                req.setAttribute("userProfile", user);
                req.getRequestDispatcher("/WEB-INF/pages/clientDashboard.jsp").forward(req, resp);
                return;
            }
            String newPassword = req.getParameter("password");

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // encrypt(email_as_key, password) — matches PasswordUtil scheme
                updatedUser.setPassword(PasswordUtil.encrypt(updatedUser.getEmail(), newPassword));
            } else {
                updatedUser.setPassword(user.getPassword());
            }

            boolean updateSuccess = service.updateUserProfile(updatedUser);
            if (updateSuccess) {
                session.setAttribute("user", updatedUser);
                req.setAttribute("message", "Profile updated successfully.");
                System.out.println("Profile updated for UserId: " + updatedUser.getUserId());
            } else {
                req.setAttribute("error", "Failed to update profile. Check logs for details.");
                System.out.println("Profile update failed for UserId: " + user.getUserId());
            }
            req.setAttribute("userProfile", updatedUser);
        } else if ("search".equals(action)) {
            String keyword = req.getParameter("keyword");
            List<String> services = service.searchServices(keyword);
            req.setAttribute("services", services);
            req.setAttribute("userProfile", service.getUserProfile(user.getEmail()));
        } else if ("bookAppointment".equals(action)) {
            String astrologer = req.getParameter("astrologer");
            String date = req.getParameter("date");
            String time = req.getParameter("time");
            boolean booked = service.bookAppointment(user.getEmail(), astrologer, date, time);
            if (booked) {
                req.setAttribute("message", "Appointment booked successfully.");
            } else {
                req.setAttribute("error", "Failed to book appointment.");
            }
            req.setAttribute("userProfile", service.getUserProfile(user.getEmail()));
        }

        req.getRequestDispatcher("/WEB-INF/pages/clientDashboard.jsp").forward(req, resp);
    }
}