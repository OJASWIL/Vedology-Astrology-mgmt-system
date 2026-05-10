<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, java.time.format.DateTimeFormatter" %>
<%
    User admin  = (User) session.getAttribute("user");
    User client = (User) request.getAttribute("client");
    String error   = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");

    String defaultImg = request.getContextPath() + "/images/profiles/default.png";
    String headerImg  = (admin != null && admin.getProfileImage() != null && !admin.getProfileImage().isEmpty())
        ? request.getContextPath() + "/images/profiles/" + admin.getProfileImage()
        : defaultImg;

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Edit Client – Vedology Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminDashboard.css">
    <style>
        .edit-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.07);
            overflow: hidden;
        }
        .edit-card-header {
            background: linear-gradient(135deg, #2c3e50, #3d5166);
            padding: 24px 28px;
            display: flex;
            align-items: center;
            gap: 16px;
        }
        .edit-card-avatar {
            width: 56px; height: 56px; border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex; align-items: center; justify-content: center;
            font-size: 22px; font-weight: 700; color: #fff;
            border: 2px solid rgba(255,255,255,0.4);
            flex-shrink: 0;
        }
        .edit-card-header-info h4 {
            margin: 0 0 4px; color: #fff; font-size: 17px;
        }
        .edit-card-header-info p {
            margin: 0; color: rgba(255,255,255,0.7); font-size: 13px;
        }
        .edit-card-body { padding: 28px; }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0 20px;
        }
        @media (max-width: 600px) { .form-row { grid-template-columns: 1fr; } }
        .form-actions {
            display: flex; gap: 10px; margin-top: 20px; flex-wrap: wrap;
            padding-top: 20px; border-top: 1px solid #eef0f3;
        }
        .field-hint { font-size: 11px; color: #aaa; margin-top: 3px; }
    </style>
</head>
<body>

<header>
    <div class="logo">Vedology Astrology</div>
    <div class="top-nav">
        <img class="header-avatar" src="<%= headerImg %>" alt="Profile"
             onerror="this.onerror=null; this.src='<%= defaultImg %>';">
        <span><%= admin != null ? admin.getFullName() : "Admin" %></span>
        <span>Notifications</span>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>
</header>

<div class="dashboard">
    <aside class="sidebar">
        <nav><ul>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=dashboard">Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=editProfile">Edit Profile</a></li>
            <li class="active"><a href="<%= request.getContextPath() %>/admin/clients">Manage Clients</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=astrologers">Astrologer List</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=horoscope">View Horoscopes</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=payments">Payment History</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=book">Book Appointment</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/manage?tab=reports">View Reports</a></li>
        </ul></nav>
    </aside>

    <main class="content">

        <div class="page-header" style="margin-bottom:20px; background:#fff; padding:14px 20px; border-radius:10px; box-shadow:0 1px 6px rgba(0,0,0,0.06);">
            <h3 style="margin:0; font-size:17px;">Edit Client</h3>
            <a href="<%= request.getContextPath() %>/admin/clients" class="btn secondary" style="font-size:12px; padding:7px 14px;">← Back to List</a>
        </div>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert error" style="margin-bottom:16px;"><%= error %></div>
        <% } %>
        <% if (message != null && !message.isEmpty()) { %>
            <div class="alert success" style="margin-bottom:16px;"><%= message %></div>
        <% } %>

        <% if (client == null) { %>
            <div class="alert error">
                Client not found. <a href="<%= request.getContextPath() %>/admin/clients">Return to list</a>.
            </div>
        <% } else {
            String nm = client.getFullName() != null ? client.getFullName().trim() : "";
            String initials = "?";
            if (!nm.isEmpty()) {
                String[] parts = nm.split("\\s+");
                initials = parts.length >= 2
                    ? ("" + parts[0].charAt(0) + parts[parts.length-1].charAt(0)).toUpperCase()
                    : String.valueOf(parts[0].charAt(0)).toUpperCase();
            }
        %>
        <div class="edit-card">
            <div class="edit-card-header">
                <div class="edit-card-avatar"><%= initials %></div>
                <div class="edit-card-header-info">
                    <h4><%= nm.isEmpty() ? "Client" : nm %></h4>
                    <p>ID #<%= client.getUserId() %> &nbsp;·&nbsp; <%= client.getEmail() != null ? client.getEmail() : "" %></p>
                </div>
            </div>
            <div class="edit-card-body">
                <form action="<%= request.getContextPath() %>/admin/clients/edit" method="post">
                    <input type="hidden" name="userId" value="<%= client.getUserId() %>">

                    <div class="form-row">
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName"
                                   value="<%= client.getFullName() != null ? client.getFullName() : "" %>"
                                   placeholder="Enter full name" required>
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone Number</label>
                            <input type="text" id="phone" name="phone"
                                   value="<%= client.getPhone() != null ? client.getPhone() : "" %>"
                                   placeholder="e.g. +977-9800000000">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" name="email"
                                   value="<%= client.getEmail() != null ? client.getEmail() : "" %>"
                                   required>
                        </div>
                        <div class="form-group">
                            <label for="timeOfBirth">Time of Birth</label>
                            <input type="time" id="timeOfBirth" name="timeOfBirth"
                                   value="<%= client.getTimeOfBirth() != null ? client.getTimeOfBirth().format(timeFmt) : "" %>">
                            <div class="field-hint">Used for astrological calculations</div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn">Update Client</button>
                        <a href="<%= request.getContextPath() %>/admin/clients" class="btn secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
        <% } %>

    </main>
</div>

</body>
</html>
