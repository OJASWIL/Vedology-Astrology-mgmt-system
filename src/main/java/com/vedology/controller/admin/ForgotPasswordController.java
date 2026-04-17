package com.vedology.controller.admin;

import java.io.IOException;

import com.vedology.service.ForgotPasswordService;
import com.vedology.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the forgot-password flow:
 *   Step 1 (GET)           — show the verify email+phone form
 *   Step 1 (POST verify)   — validate email+phone against DB
 *   Step 2 (POST reset)    — update the password in DB
 *   Step 3                 — show success message
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/forgot-password" })
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ForgotPasswordService forgotPasswordService = new ForgotPasswordService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Show the first step of the form
        req.setAttribute("step", "1");
        req.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String formStep = req.getParameter("formStep");

        if ("verify".equals(formStep)) {
            handleVerify(req, resp);
        } else if ("reset".equals(formStep)) {
            handleReset(req, resp);
        } else {
            // Unknown step — restart
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
        }
    }

    // ─── Step 1: Verify email + phone ────────────────────────────────────────

    private void handleVerify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // Basic presence check
        if (ValidationUtil.isNullOrEmpty(email) || ValidationUtil.isNullOrEmpty(phone)) {
            forwardStep1(req, resp, email, phone, "Email and phone number are required.");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            forwardStep1(req, resp, email, phone, "Invalid email format.");
            return;
        }

        if (!ValidationUtil.isValidPhoneNumber(phone)) {
            forwardStep1(req, resp, email, phone, "Phone number must be 10 digits and start with 98.");
            return;
        }

        Boolean match = forgotPasswordService.verifyEmailAndPhone(email, phone);

        if (match == null) {
            forwardStep1(req, resp, email, phone, "Database error. Please try again later.");
        } else if (!match) {
            forwardStep1(req, resp, email, phone, "No account found with that email and phone combination.");
        } else {
            // Verification passed — show reset form (step 2)
            req.setAttribute("step", "2");
            req.setAttribute("verifiedEmail", email);
            req.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(req, resp);
        }
    }

    // ─── Step 2: Reset password ───────────────────────────────────────────────

    private void handleReset(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (ValidationUtil.isNullOrEmpty(email)) {
            // Session tampered — restart
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        if (ValidationUtil.isNullOrEmpty(newPassword) || ValidationUtil.isNullOrEmpty(confirmPassword)) {
            forwardStep2(req, resp, email, "Both password fields are required.");
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            forwardStep2(req, resp, email, "Password must be at least 8 characters long.");
            return;
        }

        if (!ValidationUtil.doPasswordsMatch(newPassword, confirmPassword)) {
            forwardStep2(req, resp, email, "Passwords do not match.");
            return;
        }

        Boolean result = forgotPasswordService.resetPassword(email, newPassword);

        if (result == null) {
            forwardStep2(req, resp, email, "Database error. Please try again later.");
        } else if (!result) {
            forwardStep2(req, resp, email, "Could not update password. Please try again.");
        } else {
            // Success — show step 3
            req.setAttribute("step", "3");
            req.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(req, resp);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void forwardStep1(HttpServletRequest req, HttpServletResponse resp,
                              String email, String phone, String errorMsg)
            throws ServletException, IOException {
        req.setAttribute("step", "1");
        req.setAttribute("error", errorMsg);
        req.setAttribute("email", email);
        req.setAttribute("phone", phone);
        req.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(req, resp);
    }

    private void forwardStep2(HttpServletRequest req, HttpServletResponse resp,
                              String email, String errorMsg)
            throws ServletException, IOException {
        req.setAttribute("step", "2");
        req.setAttribute("error", errorMsg);
        req.setAttribute("verifiedEmail", email);
        req.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(req, resp);
    }
}