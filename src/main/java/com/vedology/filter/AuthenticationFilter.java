package com.vedology.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.vedology.model.User;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    // Public routes (no login required)
    private static final String LOGIN           = "/login";
    private static final String REGISTER        = "/register";
    private static final String LOGOUT          = "/logout";
    private static final String FORGOT_PW       = "/forgot-password";
    private static final String ROOT            = "/";

    // Protected routes by role
    private static final String ADMIN_DASHBOARD  = "/admin-dashboard";
    private static final String ADMIN_MANAGE     = "/admin/manage";
    private static final String CLIENT_DASHBOARD = "/client-dashboard";
    private static final String CHANGE_PASSWORD  = "/change-password";

    // Error pages
    private static final String UNAUTHORIZED     = "/unauthorized";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest)  request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI().substring(req.getContextPath().length());

        // Always allow static resources
        if (uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg")
                || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        // Always allow the unauthorized / error page itself
        if (uri.equals(UNAUTHORIZED)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user   = (session != null) ? (User) session.getAttribute("user") : null;
        String role = (user != null) ? user.getRole() : null;

        System.out.println("Filter - URI: " + uri
                + " | User: " + (user != null ? user.getEmail() : "guest")
                + " | Role: " + role);

        // Public routes: accessible by everyone
        if (isPublicRoute(uri)) {
            // Already logged in — bounce away from login / register
            if (user != null && (uri.endsWith(LOGIN) || uri.endsWith(REGISTER))) {
                redirectToDashboard(req, res, role);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // Not logged in — send to login
        if (user == null) {
            System.out.println("No session, redirecting to login for URI: " + uri);
            res.sendRedirect(req.getContextPath() + LOGIN);
            return;
        }

        // Logged in — enforce role-based access
        if ("admin".equalsIgnoreCase(role)) {
            if (isAdminAllowed(uri)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Admin not allowed for: " + uri);
                res.sendRedirect(req.getContextPath() + ADMIN_DASHBOARD);
            }

        } else if ("client".equalsIgnoreCase(role)) {
            if (isClientAllowed(uri)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Client not allowed for: " + uri);
                res.sendRedirect(req.getContextPath() + CLIENT_DASHBOARD);
            }

        } else {
            // Unknown role — invalidate and send to login
            System.out.println("Unknown role [" + role + "], invalidating session");
            session.invalidate();
            res.sendRedirect(req.getContextPath() + LOGIN);
        }
    }

    @Override
    public void destroy() { }

    // Route helpers

    private boolean isPublicRoute(String uri) {
        return uri.equals(ROOT)
                || uri.endsWith(LOGIN)
                || uri.endsWith(REGISTER)
                || uri.endsWith(LOGOUT)
                || uri.endsWith(FORGOT_PW);
    }

    private boolean isAdminAllowed(String uri) {
        return uri.equals(ADMIN_DASHBOARD)
                || uri.equals(ADMIN_MANAGE)
                || uri.startsWith(ADMIN_MANAGE + "?")
                || uri.equals(CHANGE_PASSWORD)
                || uri.equals(ROOT);
    }

    private boolean isClientAllowed(String uri) {
        return uri.equals(CLIENT_DASHBOARD)
                || uri.startsWith(CLIENT_DASHBOARD + "?")
                || uri.equals(CHANGE_PASSWORD)
                || uri.equals(ROOT);
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse res, String role)
            throws IOException {
        if ("admin".equalsIgnoreCase(role)) {
            res.sendRedirect(req.getContextPath() + ADMIN_DASHBOARD);
        } else {
            res.sendRedirect(req.getContextPath() + CLIENT_DASHBOARD);
        }
    }
}