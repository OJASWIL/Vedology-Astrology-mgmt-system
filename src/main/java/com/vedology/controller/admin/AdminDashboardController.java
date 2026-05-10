package com.vedology.controller.admin;

import com.vedology.model.Astrologer;
import com.vedology.model.User;
import com.vedology.service.ClientDashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-dashboard")
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Always reload fresh profile from DB so image/name are current
        try {
            ClientDashboardService profileService = new ClientDashboardService();
            User freshUser = profileService.getUserProfile(user.getEmail());
            if (freshUser != null) {
                if (freshUser.getRole() == null || freshUser.getRole().trim().isEmpty()) {
                    freshUser.setRole(user.getRole());
                }
                if (freshUser.getProfileImage() != null && freshUser.getProfileImage().trim().isEmpty()) {
                    freshUser.setProfileImage(null);
                }
                session.setAttribute("user", freshUser);
            }
        } catch (Exception e) {
            System.out.println("AdminDashboardController: could not refresh profile – " + e.getMessage());
        }

        // Forward to the dashboard JSP (tab defaults to "dashboard")
        req.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(req, resp);
    }
}
