<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, com.vedology.model.Astrologer, java.util.List, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminDashboard.css">
   
</head>
<body>
    <header>
        <div class="logo">Vedology Astrology</div>
        <div class="top-nav">
            <% 
                User user = (User) session.getAttribute("user");
                out.print("<br>");
                if (user != null) { 
                    out.print(user.getFullName()); 
                } else { 
                    out.print("Unknown User"); 
                } 
            %>
            <span>Notifications</span>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </header>

    <div class="dashboard">
        <aside class="sidebar">
            <nav>
                <ul>
                    <li <%= "dashboard".equals(request.getParameter("tab")) || request.getAttribute("defaultTab") != null && "dashboard".equals(request.getAttribute("defaultTab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=dashboard">Dashboard</a>
                    </li>
                    <li <%= "edit".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=edit">Manage Clients</a>
                    </li>
                    <li <%= "search".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=search">Search Services</a>
                    </li>
                    <li <%= "astrologers".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=astrologers">Astrologer List</a>
                    </li>
                    <li <%= "horoscope".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=horoscope">View Horoscopes</a>
                    </li>
                    <li <%= "payments".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=payments">Payment History</a>
                    </li>
                    <li <%= "book".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=book">Book Appointment</a>
                    </li>
                    <li <%= "reports".equals(request.getParameter("tab")) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=reports">View Reports</a>
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
                    if (tab == null || tab.isEmpty() || "dashboard".equals(tab)) {
                        user = (User) session.getAttribute("user");
                %>
                    <h2>Welcome, <%= user != null ? user.getFullName() : "Unknown User" %>!</h2>
                    <div class="user-info">
                        <p>Email: <%= user != null ? user.getEmail() : "N/A" %></p>
                        <p>Phone: <%= user != null && user.getPhone() != null ? user.getPhone() : "N/A" %></p>
                        <p>Role: <%= user != null ? user.getRole() : "N/A" %></p>
                    </div>
                    <p>Manage clients, services, appointments, and reports from the sidebar.</p>
                <% } else if ("edit".equals(tab)) { %>
                    <h3>Manage Clients</h3>
                    <% 
                        List<User> clients = null;
                        Object clientsObj = request.getAttribute("clients");
                        if (clientsObj instanceof List) {
                            clients = (List<User>) clientsObj;
                        }
                        if (clients != null && !clients.isEmpty()) { 
                    %>
                        <table>
                            <tr>
                                <th>ID</th>
                                <th>Email</th>
                                <th>Name</th>
                                <th>Time of Birth</th>
                                <th>Phone</th>
                                <th>Actions</th>
                            </tr>
                            <% for (User u : clients) { %>
                            <tr>
                                <td><%= u.getUserId() %></td>
                                <td><%= u.getEmail() %></td>
                                <td><%= u.getFullName() %></td>
                                <td><%= u.getTimeOfBirth() != null ? u.getTimeOfBirth().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A" %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "N/A" %></td>
                                <td>
                                    <form action="<%= request.getContextPath() %>/admin/manage?tab=edit" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                        <input type="email" name="email" value="<%= u.getEmail() %>" required>
                                        <input type="text" name="fullName" value="<%= u.getFullName() %>" required>
                                        <input type="time" name="timeOfBirth" value="<%= u.getTimeOfBirth() != null ? u.getTimeOfBirth().format(DateTimeFormatter.ofPattern("HH:mm")) : "" %>" required>
                                        <input type="text" name="phone" value="<%= u.getPhone() != null ? u.getPhone() : "" %>">
                                        <button type="submit">Update</button>
                                    </form>
                                    <a href="<%= request.getContextPath() %>/admin/manage?action=delete&userId=<%= u.getUserId() %>&tab=edit" onclick="return confirm('Are you sure?')">Delete</a>
                                </td>
                            </tr>
                            <% } %>
                        </table>
                        <h4>Add New Client</h4>
                        <form action="<%= request.getContextPath() %>/admin/manage?tab=edit" method="post">
                            <input type="hidden" name="action" value="create">
                            <div class="form-group">
                                <label for="email">Email</label>
                                <input type="email" id="email" name="email" required>
                            </div>
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" name="fullName" required>
                            </div>
                            <div class="form-group">
                                <label for="timeOfBirth">Time of Birth</label>
                                <input type="time" id="timeOfBirth" name="timeOfBirth" required>
                            </div>
                            <div class="form-group">
                                <label for="phone">Phone</label>
                                <input type="text" id="phone" name="phone">
                            </div>
                            <button type="submit" class="btn">Add Client</button>
                        </form>
                    <% } else { %>
                        <p>No clients found.</p>
                    <% } %>
                <% } else if ("search".equals(tab)) { %>
                    <h3>Search Astrological Services</h3>
                    <form action="<%= request.getContextPath() %>/admin/manage?tab=search" method="post" class="search-form">
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
                        <h4>Results</h4>
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
                    <h3>View Horoscopes</h3>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<String> horoscopes = (List<String>) request.getAttribute("horoscopes");
                        if (horoscopes != null && !horoscopes.isEmpty()) { 
                    %>
                        <h4>Horoscope List</h4>
                        <ul class="report-list">
                            <% for (String horoscope : horoscopes) { %>
                                <li><%= horoscope %></li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        <p>No horoscopes available.</p>
                    <% } %>
                <% } else if ("payments".equals(tab)) { %>
                    <h3>Payment History</h3>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<String> payments = (List<String>) request.getAttribute("payments");
                        if (payments != null && !payments.isEmpty()) { 
                    %>
                        <ul class="payment-list">
                            <% for (String payment : payments) { %>
                                <li><%= payment %></li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        <p>No payments found.</p>
                    <% } %>
                <% } else if ("book".equals(tab)) { %>
                    <h3>Book Appointment</h3>
                    <form action="<%= request.getContextPath() %>/admin/manage?tab=book" method="post" class="booking-form">
                        <input type="hidden" name="action" value="bookAppointment">
                        <div class="form-group">
                            <label for="clientEmail">Client Email</label>
                            <input type="email" id="clientEmail" name="clientEmail" required>
                        </div>
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
                <% } else if ("reports".equals(tab)) { %>
                    <h3>View Reports</h3>
                    <% 
                        @SuppressWarnings("unchecked")
                        List<String> reports = (List<String>) request.getAttribute("reports");
                        if (reports != null && !reports.isEmpty()) { 
                    %>
                        <ul class="report-list">
                            <% for (String report : reports) { %>
                                <li><%= report %></li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        <p>No reports found.</p>
                    <% } %>
                <% } else { %>
                    <p>Invalid tab selected. Please use the sidebar to navigate.</p>
                <% } %>
            </div>
        </main>
    </div>
</body>
</html>