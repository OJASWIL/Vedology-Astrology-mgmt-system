<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, com.vedology.model.Astrologer, java.util.List, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Client Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/clientDashboard.css">
</head>
<body>
    <header>
        <div class="logo">Vedology Astrology</div>
        <div class="top-nav">
            <span><%= ((User) session.getAttribute("user")).getFullName() %></span> 
            <span>Notifications</span>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </header>

    <div class="dashboard">
        <aside class="sidebar">
            <nav>
                <ul>
                    <li <%= "dashboard".equals(request.getParameter("tab")) || request.getParameter("tab") == null ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=dashboard">Dashboard</a>
                    </li>
                    <li <%= "edit".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=edit">Edit Profile</a>
                    </li>
                    <li <%= "search".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=search">Search Services</a>
                    </li>
                    <li <%= "astrologers".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=astrologers">Astrologer List</a>
                    </li>
                    <li <%= "horoscope".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=horoscope">View Horoscope</a>
                    </li>
                    <li <%= "payments".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=payments">Payment History</a>
                    </li>
                    <li <%= "book".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=book">Book Appointment</a>
                    </li>
                    <li <%= "reports".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/client-dashboard?tab=reports">View Reports</a>
                    </li>
                </ul>
            </nav>
        </aside>

        <main class="content">
            <% 
                String error = (String) request.getAttribute("error");
                String message = (String) request.getAttribute("message");
                if (error != null && !error.isEmpty()) { 
            %>
                <div class="alert error"><%= error %></div>
            <% } %>
            <% if (message != null && !message.isEmpty()) { %>
                <div class="alert success"><%= message %></div>
            <% } %>

            <div class="dashboard-container">
                <% 
                    String tab = request.getParameter("tab");
                    User userProfile = (User) request.getAttribute("userProfile");
                    if (tab == null || "dashboard".equals(tab)) {
                %>
                    <h2>Welcome, <%= (userProfile != null && userProfile.getFullName() != null) ? userProfile.getFullName() : "Unknown User" %>!</h2>
                    <p>This is your dashboard. Use the sidebar to navigate to different features.</p>
                <% } else if ("edit".equals(tab)) { %>
                    <h3>Edit Profile</h3>
                    <form action="<%= request.getContextPath() %>/client-dashboard" method="post" class="profile-form">
                        <input type="hidden" name="action" value="updateProfile">
                        <div class="form-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" name="email" value="<%= (userProfile != null) ? userProfile.getEmail() : "" %>" readonly>
                        </div>
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName" value="<%= (userProfile != null && userProfile.getFullName() != null) ? userProfile.getFullName() : "" %>" required>
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone</label>
                            <input type="text" id="phone" name="phone" value="<%= (userProfile != null && userProfile.getPhone() != null) ? userProfile.getPhone() : "" %>">
                        </div>
                        <div class="form-group">
                            <label for="timeOfBirth">Time of Birth</label>
                            <input type="time" id="timeOfBirth" name="timeOfBirth" value="<%= (userProfile != null && userProfile.getTimeOfBirth() != null) ? userProfile.getTimeOfBirth().format(DateTimeFormatter.ofPattern("HH:mm")) : "" %>" required>
                        </div>
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" placeholder="Enter new password (leave blank to keep current)">
                        </div>
                        <button type="submit" class="btn">Update Profile</button>
                    </form>
                <% } else if ("search".equals(tab)) { %>
                    <h3>Search Astrological Services</h3>
                    <form action="<%= request.getContextPath() %>/client-dashboard?tab=search" method="post" class="search-form">
                        <input type="hidden" name="action" value="search">
                        <div class="search-group">
                            <input type="text" name="keyword" placeholder="Search services..." required>
                            <button type="submit" class="btn">Search</button>
                        </div>
                    </form>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<String> services = (List<String>) request.getAttribute("services");
                        if (services != null && !services.isEmpty()) { 
                    %>
                        <h4>Search Results</h4>
                        <ul class="service-list">
                            <% for (String service : services) { %>
                                <li><%= service %></li>
                            <% } %>
                        </ul>
                    <% } %>
                <% } else if ("astrologers".equals(tab)) { %>
                    <h3>Astrologer List</h3>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<Astrologer> astrologers = (List<Astrologer>) request.getAttribute("astrologers");
                        if (astrologers != null && !astrologers.isEmpty()) { 
                    %>
                        <table class="astrologer-table">
                            <tr>
                                <th>ID</th>
                                <th>Available Days</th>
                                <th>Address</th>
                                <th>Contact</th>
                                <th>Experience (Years)</th>
                                <th>Specializations</th>
                            </tr>
                            <% for (Astrologer astrologer : astrologers) { %>
                                <tr>
                                    <td><%= astrologer.getAstrologerId() %></td>
                                    <td><%= astrologer.getAvailableDays() != null ? astrologer.getAvailableDays() : "N/A" %></td>
                                    <td><%= astrologer.getAddress() != null ? astrologer.getAddress() : "N/A" %></td>
                                    <td><%= astrologer.getContactNumber() != null ? astrologer.getContactNumber() : "N/A" %></td>
                                    <td><%= astrologer.getExperienceYear() >= 0 ? astrologer.getExperienceYear() : "N/A" %></td>
                                    <td><%= astrologer.getSpecialization() != null ? astrologer.getSpecialization() : "N/A" %></td>
                                </tr>
                            <% } %>
                        </table>
                    <% } else { %>
                        <p>No astrologers available.</p>
                    <% } %>
                <% } else if ("horoscope".equals(tab)) { %>
                    <h3>View Horoscope</h3>
                    <p>This is a placeholder for your daily horoscope. (Dynamic content requires horoscope data.)</p>
                <% } else if ("payments".equals(tab)) { %>
                    <h3>Payment History</h3>
                    <ul class="payment-list">
                        <li>Payment #1: $50 on 2025-07-01 for Consultation</li>
                        <li>Payment #2: $30 on 2025-07-15 for Horoscope Reading</li>
                    </ul>
                    <p>(Dynamic payment history requires payment table schema.)</p>
                <% } else if ("book".equals(tab)) { %>
                    <h3>Book Appointment</h3>
                    <form action="<%= request.getContextPath() %>/client-dashboard?tab=book" method="post" class="booking-form">
                        <input type="hidden" name="action" value="bookAppointment">
                        <div class="form-group">
                            <label for="astrologer">Astrologer</label>
                            <select id="astrologer" name="astrologer" required>
                                <option value="">Select an astrologer</option>
                                <% 
                                    @SuppressWarnings("unchecked")
                                    List<Astrologer> astrologers = (List<Astrologer>) request.getAttribute("astrologers");
                                    if (astrologers != null && !astrologers.isEmpty()) {
                                        for (Astrologer astrologer : astrologers) {
                                %>
                                    <option value="<%= astrologer.getAstrologerId() %>">
                                        ID <%= astrologer.getAstrologerId() %> - <%= astrologer.getSpecialization() != null ? astrologer.getSpecialization() : "N/A" %>
                                    </option>
                                <% 
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="date">Date</label>
                            <input type="date" id="date" name="date" required>
                        </div>
                        <div class="form-group">
                            <label for="time">Time</label>
                            <input type="time" id="time" name="time" required>
                        </div>
                        <button type="submit" class="btn">Book</button>
                    </form>
                    <p>(Dynamic booking requires appointment table schema.)</p>
                <% } else if ("reports".equals(tab)) { %>
                    <h3>View Reports</h3>
                    <ul class="report-list">
                        <li>Report #1: Natal Chart, generated on 2025-07-01</li>
                        <li>Report #2: Yearly Horoscope, generated on 2025-07-10</li>
                    </ul>
                    <p>(Dynamic reports require report table schema.)</p>
                <% } %>
            </div>
        </main>
    </div>
</body>
</html>