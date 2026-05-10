package com.vedology.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Serves uploaded profile images from the stable upload directory.
 * URL pattern: /profile-images/filename.jpg
 *
 * This is needed because getRealPath() saves to a Tomcat temp/work directory
 * that does NOT match the static-file serving path. This servlet bridges that gap.
 */
@WebServlet(urlPatterns = "/profile-images/*")
public class ImageServlet extends HttpServlet {

    // Must match UPLOAD_BASE_DIR in ClientDashboardController
    public static final String UPLOAD_BASE_DIR =
            System.getProperty("user.home") + File.separator + "vedology_uploads" + File.separator + "profiles";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Extract just the filename from the URL  e.g. /profile-images/user_3_abc123.jpg → user_3_abc123.jpg
        String pathInfo = req.getPathInfo(); // "/user_3_abc123.jpg"
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Strip leading slash and prevent path traversal
        String fileName = pathInfo.substring(1).replaceAll("[/\\\\]", "");
        if (fileName.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File imageFile = new File(UPLOAD_BASE_DIR, fileName);

        if (!imageFile.exists() || !imageFile.isFile()) {
            // Serve the default.png from webapp instead
            String defaultPath = getServletContext().getRealPath("/images/profiles/default.png");
            File defaultFile = new File(defaultPath);
            if (defaultFile.exists()) {
                resp.setContentType("image/png");
                resp.setContentLength((int) defaultFile.length());
                try (FileInputStream fis = new FileInputStream(defaultFile);
                     OutputStream os = resp.getOutputStream()) {
                    byte[] buf = new byte[4096];
                    int read;
                    while ((read = fis.read(buf)) != -1) os.write(buf, 0, read);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }

        // Detect content type
        String contentType = Files.probeContentType(imageFile.toPath());
        if (contentType == null) contentType = "image/jpeg";
        resp.setContentType(contentType);
        resp.setContentLength((int) imageFile.length());

        // Cache for 1 hour
        resp.setHeader("Cache-Control", "max-age=3600");

        try (FileInputStream fis = new FileInputStream(imageFile);
             OutputStream os = resp.getOutputStream()) {
            byte[] buf = new byte[4096];
            int read;
            while ((read = fis.read(buf)) != -1) os.write(buf, 0, read);
        }
    }
}