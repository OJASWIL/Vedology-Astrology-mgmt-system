<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User" %>
<%
    User admin  = (User) session.getAttribute("user");
    String error = (String) request.getAttribute("error");

    String defaultImg = request.getContextPath() + "/images/profiles/default.png";
    String headerImg  = (admin != null && admin.getProfileImage() != null && !admin.getProfileImage().isEmpty())
        ? request.getContextPath() + "/images/profiles/" + admin.getProfileImage()
        : defaultImg;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Add Client – Vedology Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminDashboard.css">
</head>
<body>

<header>
    <div class="logo">Vedology Astrology</div>
    <div class="top-nav">
        <img class="header-avatar"
             src="<%= headerImg %>"
             alt="Profile"
             onerror="this.onerror=null; this.src='<%= defaultImg %>';">
        <span><%= admin != null ? admin.getFullName() : "Admin" %></span>
        <span>Notifications</span>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>
</header>

<div class="dashboard">
    <aside class="sidebar">
        <nav>
            <ul>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=dashboard">Dashboard</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=editProfile">Edit Profile</a></li>
                <li class="active"><a href="<%= request.getContextPath() %>/admin/clients">Manage Clients</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=astrologers">Astrologer List</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=horoscope">View Horoscopes</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=payments">Payment History</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=book">Book Appointment</a></li>
                <li><a href="<%= request.getContextPath() %>/admin/manage?tab=reports">View Reports</a></li>
            </ul>
        </nav>
    </aside>

    <main class="content">
        <div class="dashboard-container">

            <div class="page-header">
                <h3>Add New Client</h3>
                <a href="<%= request.getContextPath() %>/admin/clients" class="btn secondary">← Back to List</a>
            </div>

            <% if (error != null && !error.isEmpty()) { %>
                <div class="alert error"><%= error %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/admin/clients/add" method="post" class="profile-form">
                <div class="form-group">
                    <label for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" required placeholder="Enter full name">
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required placeholder="Enter email address">
                </div>
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" placeholder="e.g. 9812345678">
                </div>
                <div class="form-group">
                    <label for="timeOfBirth">Time of Birth</label>
                    <input type="time" id="timeOfBirth" name="timeOfBirth" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Set initial password">
                </div>

                <div style="display:flex; gap:10px; margin-top:8px;">
                    <button type="submit" class="btn">Add Client</button>
                    <a href="<%= request.getContextPath() %>/admin/clients" class="btn secondary">Cancel</a>
                </div>
            </form>

        </div>
    </main>
</div>

</body>
</html>
