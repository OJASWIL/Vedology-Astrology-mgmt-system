<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, java.util.List, java.time.format.DateTimeFormatter" %>
<%
    User admin = (User) session.getAttribute("user");
    String error = (String) request.getAttribute("error");
    String message = (String) session.getAttribute("clientMessage");
    session.removeAttribute("clientMessage");

    @SuppressWarnings("unchecked")
    List<User> clients = (List<User>) request.getAttribute("clients");

    String defaultImg = request.getContextPath() + "/images/profiles/default.png";
    String headerImg = (admin != null && admin.getProfileImage() != null && !admin.getProfileImage().isEmpty())
        ? request.getContextPath() + "/images/profiles/" + admin.getProfileImage()
        : defaultImg;

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Manage Clients – Vedology Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminDashboard.css">
    
    <style>
        .search-container {
            display: flex;
            gap: 10px;
            margin: 15px 0 20px 0;
            max-width: 500px;
        }
        .search-container input {
            flex: 1;
            padding: 12px 16px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 15px;
        }
        .search-container button {
            padding: 12px 24px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 15px;
        }
        .search-container button:hover {
            background-color: #2980b9;
        }
        .no-results {
            text-align: center;
            padding: 40px 20px;
            color: #666;
            font-size: 16px;
            background: #f9f9f9;
            border-radius: 8px;
            margin-top: 20px;
        }
    </style>
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
        <div class="manage-container">
            <div class="page-header">
                <h2>Manage Clients</h2>
                <a href="<%= request.getContextPath() %>/admin/clients/add" class="btn primary">+ Add New Client</a>
            </div>

            <!-- Search Bar with Button -->
            <div class="search-container">
                <input type="text" 
                       id="clientSearch" 
                       placeholder="Search by Name, Email or Phone...">
                <button onclick="filterClients()">Search</button>
            </div>

            <% if (error != null && !error.isEmpty()) { %>
                <div class="alert error"><%= error %></div>
            <% } %>
            <% if (message != null && !message.isEmpty()) { %>
                <div class="alert success"><%= message %></div>
            <% } %>

            <% if (clients != null && !clients.isEmpty()) { %>
                <table class="client-table" id="clientsTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Client</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Time of Birth</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (User u : clients) { %>
                        <tr>
                            <td><%= u.getUserId() %></td>
                            <td><strong><%= u.getFullName() != null ? u.getFullName() : "N/A" %></strong></td>
                            <td><%= u.getEmail() %></td>
                            <td><%= u.getPhone() != null ? u.getPhone() : "N/A" %></td>
                            <td><%= u.getTimeOfBirth() != null ? u.getTimeOfBirth().format(timeFmt) : "N/A" %></td>
                            <td class="actions-cell">
                                <a href="<%= request.getContextPath() %>/admin/clients/edit?userId=<%= u.getUserId() %>"
                                   class="btn primary">Edit</a>
                                <a href="<%= request.getContextPath() %>/admin/clients/delete?userId=<%= u.getUserId() %>"
                                   class="btn danger"
                                   onclick="return confirm('Are you sure you want to delete this client?')">Delete</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="no-clients">
                    No clients found. Add your first client.
                </div>
            <% } %>
        </div>
    </main>
</div>

<script>
function filterClients() {
    const input = document.getElementById('clientSearch').value.toLowerCase().trim();
    const rows = document.querySelectorAll('#clientsTable tbody tr');
    let visibleCount = 0;

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(input)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    // Show "No results" message if nothing matches
    let noResultsMsg = document.getElementById('noResults');
    if (visibleCount === 0 && input !== '') {
        if (!noResultsMsg) {
            noResultsMsg = document.createElement('div');
            noResultsMsg.id = 'noResults';
            noResultsMsg.className = 'no-results';
            noResultsMsg.innerHTML = `<strong>No clients found.</strong><br>Try different search terms.`;
            document.querySelector('.manage-container').appendChild(noResultsMsg);
        } else {
            noResultsMsg.style.display = 'block';
        }
    } else if (noResultsMsg) {
        noResultsMsg.style.display = 'none';
    }
}
</script>

</body>
</html>