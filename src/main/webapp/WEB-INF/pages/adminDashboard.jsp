<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, com.vedology.model.Astrologer, java.util.List, java.time.format.DateTimeFormatter" %>
<%
    User user = (User) session.getAttribute("user");
    String tab = request.getParameter("tab");
    String defaultImg = request.getContextPath() + "/images/profiles/default.svg";

    String headerImg = (user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty())
        ? request.getContextPath() + "/images/profiles/" + user.getProfileImage()
        : defaultImg;

    String profileImgSrc = (user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty())
        ? request.getContextPath() + "/images/profiles/" + user.getProfileImage()
        : defaultImg;

    String error   = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");

    String profileError   = (String) session.getAttribute("profileError");
    String profileSuccess = (String) session.getAttribute("profileSuccess");
    session.removeAttribute("profileError");
    session.removeAttribute("profileSuccess");
    if (profileError   != null) error   = profileError;
    if (profileSuccess != null) message = profileSuccess;

    String pwError   = (String) session.getAttribute("changePasswordError");
    String pwSuccess = (String) session.getAttribute("passwordChangeSuccess");
    session.removeAttribute("changePasswordError");
    session.removeAttribute("passwordChangeSuccess");
    if (pwError   != null) error   = pwError;
    if (pwSuccess != null) message = pwSuccess;

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Admin Dashboard – Vedology</title>
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
            <span><%= user != null ? user.getFullName() : "Admin" %></span>
            <span>Notifications</span>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </header>

    <div class="dashboard">
        <aside class="sidebar">
            <nav>
                <ul>
                    <li <%= (tab == null || tab.isEmpty() || "dashboard".equals(tab)) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=dashboard">Dashboard</a>
                    </li>
                    <li <%= "editProfile".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=editProfile">Edit Profile</a>
                    </li>
                    <li <%= "edit".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/clients">Manage Clients</a>
                    </li>
                    <li <%= "astrologers".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=astrologers">Astrologer List</a>
                    </li>
                    <li <%= "horoscope".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=horoscope">View Horoscopes</a>
                    </li>
                    <li <%= "payments".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=payments">Payment History</a>
                    </li>
                    <li <%= "book".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=book">Book Appointment</a>
                    </li>
                    <li <%= "reports".equals(tab) ? "class=\"active\"" : "" %>>
                        <a href="<%= request.getContextPath() %>/admin/manage?tab=reports">View Reports</a>
                    </li>
                </ul>
            </nav>
        </aside>

        <main class="content">
            <% if (error != null && !error.isEmpty()) { %>
                <div class="alert error"><%= error %></div>
            <% } %>
            <% if (message != null && !message.isEmpty()) { %>
                <div class="alert success"><%= message %></div>
            <% } %>

            <div class="dashboard-container">

                <%-- ===================== DASHBOARD TAB ===================== --%>
                <% if (tab == null || tab.isEmpty() || "dashboard".equals(tab)) { %>
                    <div class="profile-card" style="max-width:700px;margin:0 auto;">
                        <div class="profile-img-circle">
                            <img src="<%= profileImgSrc %>"
                                 alt="Profile Photo"
                                 onerror="this.onerror=null; this.src='<%= defaultImg %>';">
                        </div>
                        <h2><%= user != null ? user.getFullName() : "Administrator" %></h2>
                        <span class="badge confirmed">Admin</span>
                    </div>

                    <div class="user-info">
                        <p><strong>Email:</strong> <%= user != null ? user.getEmail() : "N/A" %></p>
                        <p><strong>Phone:</strong> <%= user != null && user.getPhone() != null ? user.getPhone() : "N/A" %></p>
                        <p><strong>Role:</strong> <%= user != null ? user.getRole() : "N/A" %></p>
                        <% if (user != null && user.getTimeOfBirth() != null) { %>
                        <p><strong>Time of Birth:</strong> <%= user.getTimeOfBirth().format(timeFmt) %></p>
                        <% } %>
                    </div>

                    <p style="text-align:center; color:#888; margin-top:20px; font-size:14px;">
                        Manage clients, services, appointments, and reports from the sidebar.
                    </p>

                <%-- ===================== EDIT PROFILE TAB ===================== --%>
                <% } else if ("editProfile".equals(tab)) { %>
                    <h3>Edit Profile</h3>

                    <div style="text-align:center; margin-bottom:24px;">
                        <div class="profile-img-circle" style="margin:0 auto;">
                            <img id="profilePreview"
                                 src="<%= profileImgSrc %>"
                                 alt="Profile Picture"
                                 onerror="this.onerror=null; this.src='<%= defaultImg %>';">
                        </div>
                        <br><small style="color:#888;">Select a JPG/PNG to change your photo</small>
                    </div>

                    <form action="<%= request.getContextPath() %>/admin/manage" method="post"
                          enctype="multipart/form-data" class="profile-form">
                        <input type="hidden" name="action" value="editProfile">

                        <div class="form-group">
                            <label for="profileImage">Profile Picture (JPG/PNG, max 5MB)</label>
                            <input type="file" id="profileImage" name="profileImage"
                                   accept="image/jpeg,image/png" onchange="previewImage(this)">
                        </div>
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName"
                                   value="<%= user != null && user.getFullName() != null ? user.getFullName() : "" %>"
                                   required>
                        </div>
                        <div class="form-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" name="email"
                                   value="<%= user != null ? user.getEmail() : "" %>"
                                   required>
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone Number</label>
                            <input type="text" id="phone" name="phone"
                                   value="<%= user != null && user.getPhone() != null ? user.getPhone() : "" %>">
                        </div>
                        <div class="form-group">
                            <label for="timeOfBirth">Time of Birth</label>
                            <input type="time" id="timeOfBirth" name="timeOfBirth"
                                   value="<%= user != null && user.getTimeOfBirth() != null ? user.getTimeOfBirth().format(timeFmt) : "" %>">
                        </div>
                        <div class="form-group">
                            <label for="newPassword">New Password
                                <small style="color:#888;">(leave blank to keep current)</small>
                            </label>
                            <input type="password" id="newPassword" name="newPassword"
                                   placeholder="Enter new password">
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">Confirm New Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword"
                                   placeholder="Confirm new password">
                        </div>
                        <button type="submit" class="btn">Save Changes</button>
                    </form>

                <%-- ===================== MANAGE CLIENTS ===================== --%>
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
                        <div style="overflow-x:auto;">
                        <table class="client-table">
                            <thead>
                                <tr>
                                    <th>ID</th><th>Name</th><th>Email</th>
                                    <th>Phone</th><th>Time of Birth</th><th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% for (User u : clients) { %>
                            <tr>
                                <td><%= u.getUserId() %></td>
                                <td><%= u.getFullName() %></td>
                                <td><%= u.getEmail() %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "N/A" %></td>
                                <td><%= u.getTimeOfBirth() != null ? u.getTimeOfBirth().format(timeFmt) : "N/A" %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/admin/clients/edit?userId=<%= u.getUserId() %>"
                                       class="btn" style="font-size:12px;padding:5px 12px;">Edit</a>
                                    <a href="<%= request.getContextPath() %>/admin/clients/delete?userId=<%= u.getUserId() %>"
                                       class="btn danger" style="font-size:12px;padding:5px 12px;margin-left:6px;"
                                       onclick="return confirm('Delete this client?')">Delete</a>
                                </td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                        </div>
                    <% } else { %>
                        <p style="color:#888;">No clients found.</p>
                    <% } %>

                <%-- ===================== ASTROLOGERS ===================== --%>
                <% } else if ("astrologers".equals(tab)) { %>
                    <h3>Astrologer List</h3>
                    <%
                        @SuppressWarnings("unchecked")
                        List<Astrologer> astrologers = (List<Astrologer>) request.getAttribute("astrologers");
                        if (astrologers != null && !astrologers.isEmpty()) {
                    %>
                        <div style="overflow-x:auto;">
                        <table class="astrologer-table">
                            <thead>
                                <tr>
                                    <th>ID</th><th>Specialization</th><th>Experience (yrs)</th>
                                    <th>Available Days</th><th>Contact</th><th>Address</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% for (Astrologer astrologer : astrologers) { %>
                                <tr>
                                    <td><%= astrologer.getAstrologerId() %></td>
                                    <td><%= astrologer.getSpecialization() != null ? astrologer.getSpecialization() : "N/A" %></td>
                                    <td><%= astrologer.getExperienceYear() >= 0 ? astrologer.getExperienceYear() : "N/A" %></td>
                                    <td><%= astrologer.getAvailableDays() != null ? astrologer.getAvailableDays() : "N/A" %></td>
                                    <td><%= astrologer.getContactNumber() != null ? astrologer.getContactNumber() : "N/A" %></td>
                                    <td><%= astrologer.getAddress() != null ? astrologer.getAddress() : "N/A" %></td>
                                </tr>
                            <% } %>
                            </tbody>
                        </table>
                        </div>
                    <% } else { %>
                        <p style="color:#888;">No astrologers available.</p>
                    <% } %>

                <%-- ===================== HOROSCOPE ===================== --%>
                <% } else if ("horoscope".equals(tab)) { %>
                    <h3>View Horoscopes</h3>
                    <%
                        @SuppressWarnings("unchecked")
                        List<String> horoscopes = (List<String>) request.getAttribute("horoscopes");
                        if (horoscopes != null && !horoscopes.isEmpty()) {
                    %>
                        <ul class="report-list">
                            <% for (String horoscope : horoscopes) { %>
                                <li><%= horoscope %></li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        <p style="color:#888;">No horoscopes available.</p>
                    <% } %>

                <%-- ===================== PAYMENTS ===================== --%>
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
                        <p style="color:#888;">No payments found.</p>
                    <% } %>

                <%-- ===================== BOOK APPOINTMENT ===================== --%>
                <% } else if ("book".equals(tab)) { %>
                    <div class="page-header">
                        <h3>Book Appointment</h3>
                    </div>
                    <form action="<%= request.getContextPath() %>/admin/manage?tab=book" method="post"
                          class="booking-form">
                        <input type="hidden" name="action" value="bookAppointment">

                        <div class="form-group">
                            <label for="clientEmail">Select Client</label>
                            <select id="clientEmail" name="clientEmail" required>
                                <option value="">-- Select a client --</option>
                                <%
                                    @SuppressWarnings("unchecked")
                                    List<User> bookClients = (List<User>) request.getAttribute("clients");
                                    if (bookClients != null && !bookClients.isEmpty()) {
                                        for (User bc : bookClients) {
                                %>
                                    <option value="<%= bc.getEmail() %>">
                                        <%= bc.getFullName() != null ? bc.getFullName() : "N/A" %> — <%= bc.getEmail() %>
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="astrologer">Astrologer</label>
                            <select id="astrologer" name="astrologer" required>
                                <option value="">-- Select an astrologer --</option>
                                <%
                                    @SuppressWarnings("unchecked")
                                    List<Astrologer> bookAstros = (List<Astrologer>) request.getAttribute("astrologers");
                                    if (bookAstros != null && !bookAstros.isEmpty()) {
                                        for (Astrologer astrologer : bookAstros) {
                                %>
                                    <option value="<%= astrologer.getAstrologerId() %>">
                                        <%= astrologer.getSpecialization() != null ? astrologer.getSpecialization() : "Astrologer" %>
                                        (ID <%= astrologer.getAstrologerId() %>, <%= astrologer.getExperienceYear() %> yrs exp)
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="date">Appointment Date</label>
                            <input type="date" id="date" name="date" required
                                   min="<%= java.time.LocalDate.now().toString() %>">
                        </div>
                        <div class="form-group">
                            <label for="time">Appointment Time</label>
                            <input type="time" id="time" name="time" required>
                        </div>
                        <div style="display:flex; gap:10px; margin-top:8px;">
                            <button type="submit" class="btn">Book Appointment</button>
                            <button type="reset" class="btn secondary">Clear</button>
                        </div>
                    </form>

                <%-- ===================== REPORTS ===================== --%>
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
                        <p style="color:#888;">No reports found.</p>
                    <% } %>

                <% } else { %>
                    <p style="color:#888;">Invalid tab. Use the sidebar to navigate.</p>
                <% } %>

            </div><%-- /.dashboard-container --%>
        </main>
    </div>

    <script>
    function previewImage(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var prev = document.getElementById('profilePreview');
                if (prev) prev.src = e.target.result;
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
    </script>
</body>
</html>
