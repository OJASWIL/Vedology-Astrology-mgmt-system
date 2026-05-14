<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.vedology.model.User, com.vedology.model.Astrologer, java.util.List, java.util.Map, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Client Dashboard – Vedology</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/clientDashboard.css">
</head>
<body>
<%
    User sessionUser = (User) session.getAttribute("user");
    User userProfile = (User) request.getAttribute("userProfile");
    String defaultImg = request.getContextPath() + "/images/profiles/default.png";

    String headerImgSrc = defaultImg;
    if (sessionUser != null && sessionUser.getProfileImage() != null
            && !sessionUser.getProfileImage().trim().isEmpty()) {
        headerImgSrc = request.getContextPath() + "/images/profiles/" + sessionUser.getProfileImage().trim();
    }

    String profileImgSrc = defaultImg;
    if (userProfile != null && userProfile.getProfileImage() != null
            && !userProfile.getProfileImage().trim().isEmpty()) {
        profileImgSrc = request.getContextPath() + "/images/profiles/" + userProfile.getProfileImage().trim();
    }

    String currentTab = request.getParameter("tab");
    if (currentTab == null || currentTab.trim().isEmpty()) currentTab = "dashboard";

    String error   = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
    String profileError   = (String) session.getAttribute("profileError");
    String profileSuccess = (String) session.getAttribute("profileSuccess");
    session.removeAttribute("profileError");
    session.removeAttribute("profileSuccess");
    if (profileError   != null) error   = profileError;
    if (profileSuccess != null) message = profileSuccess;

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

   
%>

    <header>
        <div class="logo">Vedology Astrology</div>
        <div class="top-nav">
            <img class="header-avatar"
                 src="<%= headerImgSrc %>"
                 alt="Profile"
                 onerror="this.onerror=null; this.src='<%= defaultImg %>';">
            <span><%= (sessionUser != null && sessionUser.getFullName() != null) ? sessionUser.getFullName() : "User" %></span>
            <span>Notifications</span>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </header>

    <div class="dashboard">
        <aside class="sidebar">
            <nav>
                <ul>
                <li id="nav-dashboard"><a href="#" onclick="switchTab('dashboard'); return false;">Dashboard</a></li>
                <li id="nav-edit"><a href="#" onclick="switchTab('edit'); return false;">Edit Profile</a></li>
                <li id="nav-search"><a href="#" onclick="switchTab('search'); return false;">Search Services</a></li>
                <li id="nav-astrologers"><a href="#" onclick="switchTab('astrologers'); return false;">Astrologer List</a></li>
                <li id="nav-horoscope"><a href="#" onclick="switchTab('horoscope'); return false;">View Horoscope</a></li>
                <li id="nav-payments"><a href="#" onclick="switchTab('payments'); return false;">Payment History</a></li>
                <li id="nav-appointments"><a href="#" onclick="switchTab('appointments'); return false;">My Appointments</a></li>
                <li id="nav-reports"><a href="#" onclick="switchTab('reports'); return false;">View Reports</a></li>
                <li id="nav-about"><a href="#" onclick="switchTab('about'); return false;">About Us</a></li>
                <li id="nav-contact"><a href="#" onclick="switchTab('contact'); return false;">Contact Us</a></li>
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

            <div class="dashboard-container" id="dashboardContainer">

                <%-- === DASHBOARD TAB === --%>
                <div id="panel-dashboard" class="tab-panel">
                    <div class="profile-card">
                        <div class="profile-img-circle">
                            <img src="<%= profileImgSrc %>"
                                 alt="Profile Image"
                                 onerror="this.onerror=null; this.src='<%= defaultImg %>';">
                        </div>
                        <h2><%= (userProfile != null && userProfile.getFullName() != null) ? userProfile.getFullName() : "User" %></h2>
                        <span class="badge confirmed">Client</span>
                    </div>

                    <div class="user-info">
                        <p><strong>Email:</strong> <%= (userProfile != null && userProfile.getEmail() != null) ? userProfile.getEmail() : "N/A" %></p>
                        <p><strong>Phone:</strong> <%= (userProfile != null && userProfile.getPhone() != null) ? userProfile.getPhone() : "N/A" %></p>
                        <% if (userProfile != null && userProfile.getTimeOfBirth() != null) { %>
                        <p><strong>Time of Birth:</strong> <%= userProfile.getTimeOfBirth().format(timeFmt) %></p>
                        <% } %>
                        <p><strong>Role:</strong> <%= (userProfile != null && userProfile.getRole() != null) ? userProfile.getRole() : "client" %></p>
                    </div>

                    <p style="text-align:center; color:#888; margin-top:20px; font-size:14px;">
                        View your appointments, astrologers, and more from the sidebar.
                    </p>
                </div>

                <%-- === EDIT PROFILE TAB === --%>
                <div id="panel-edit" class="tab-panel">
                    <h3>Edit Profile</h3>
                    <div style="text-align:center; margin-bottom:24px;">
                        <div class="profile-img-circle" style="margin:0 auto;">
                            <img id="profilePreview"
                                 src="<%= profileImgSrc %>"
                                 alt="Profile Picture"
                                 onerror="this.onerror=null; this.src='<%= defaultImg %>';">
                        </div>
                        <br><small style="color:#888;">Select a JPG/PNG to update your photo</small>
                    </div>

                    <form action="<%= request.getContextPath() %>/client-dashboard" method="post"
                          enctype="multipart/form-data" class="profile-form">
                        <input type="hidden" name="action" value="updateProfile">

                        <div class="form-group">
                            <label for="profileImage">Profile Picture (JPG/PNG, max 5MB)</label>
                            <input type="file" id="profileImage" name="profileImage"
                                   accept="image/jpeg,image/png" onchange="previewImage(this)">
                        </div>
                        <div class="form-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" name="email"
                                   value="<%= (userProfile != null && userProfile.getEmail() != null) ? userProfile.getEmail() : "" %>"
                                   readonly>
                        </div>
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName"
                                   value="<%= (userProfile != null && userProfile.getFullName() != null) ? userProfile.getFullName() : "" %>"
                                   required>
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone Number</label>
                            <input type="text" id="phone" name="phone"
                                   value="<%= (userProfile != null && userProfile.getPhone() != null) ? userProfile.getPhone() : "" %>">
                        </div>
                        <div class="form-group">
                            <label for="timeOfBirth">Time of Birth</label>
                            <input type="time" id="timeOfBirth" name="timeOfBirth"
                                   value="<%= (userProfile != null && userProfile.getTimeOfBirth() != null) ? userProfile.getTimeOfBirth().format(timeFmt) : "" %>">
                        </div>
                        <div class="form-group">
                            <label for="newPassword">New Password <small style="color:#888;">(leave blank to keep current)</small></label>
                            <input type="password" id="newPassword" name="newPassword" placeholder="Enter new password">
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">Confirm New Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm new password">
                        </div>
                        <button type="submit" class="btn">Save Changes</button>
                    </form>
                </div>

                <%-- === SEARCH TAB === --%>
                <div id="panel-search" class="tab-panel">
                    <h3>Search Astrological Services</h3>
                    <div class="search-form">
                        <div class="search-group">
                            <input type="text" placeholder="Search for services...">
                            <button class="btn">Search</button>
                        </div>
                    </div>
                    <p style="margin-top:16px; color:#888; font-size:14px;">Search functionality coming soon.</p>
                </div>

                <%-- === ASTROLOGERS TAB === --%>
                <div id="panel-astrologers" class="tab-panel">
                    <h3>Astrologer List</h3>
                    <%
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
                                <% for (Astrologer a : astrologers) { %>
                                <tr>
                                    <td><%= a.getAstrologerId() %></td>
                                    <td><%= a.getSpecialization() != null ? a.getSpecialization() : "N/A" %></td>
                                    <td><%= a.getExperienceYear() %></td>
                                    <td><%= a.getAvailableDays() != null ? a.getAvailableDays() : "N/A" %></td>
                                    <td><%= a.getContactNumber() != null ? a.getContactNumber() : "N/A" %></td>
                                    <td><%= a.getAddress() != null ? a.getAddress() : "N/A" %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        </div>
                    <% } else { %>
                        <p style="color:#888; font-size:14px;">No astrologers available at the moment.</p>
                    <% } %>
                </div>

                <%-- === HOROSCOPE TAB === --%>
                <div id="panel-horoscope" class="tab-panel">
                    <h3>View Horoscope</h3>
                    <p style="color:#888; font-size:14px;">Horoscope content coming soon.</p>
                </div>

                <%-- === PAYMENTS TAB === --%>
                <div id="panel-payments" class="tab-panel">
                    <h3>Payment History</h3>
                    <p style="color:#888; font-size:14px;">No payment records found.</p>
                </div>

                <%-- === MY APPOINTMENTS TAB === --%>
                
<div id="panel-appointments" class="tab-panel">
    <h3>My Appointments</h3>
    <%
        List<Map<String, String>> appointments = (List<Map<String, String>>) request.getAttribute("appointments");
    %>
    <% if (appointments != null && !appointments.isEmpty()) { %>
        <div style="overflow-x:auto;">
        <table class="appointment-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Astrologer ID</th>
                    <th>Specialization</th>
                    <th>Contact</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
            <% for (Map<String, String> appt : appointments) { %>
                <tr>
                    <td><%= appt.get("date") %></td>
                    <td><%= appt.get("time") %></td>
                    <td><%= appt.get("astrologer_id") %></td>
                    <td><%= appt.get("specialization") %></td>
                    <td><%= appt.get("contact") %></td>
                    <td><span class="badge <%= appt.get("status") %>"><%= appt.get("status") %></span></td>
                </tr>
            <% } %>
            </tbody>
        </table>
        </div>
    <% } else { %>
        <div style="text-align:center; padding:60px 20px; color:#7f8c8d;">
            <div style="font-size:48px; margin-bottom:16px; opacity:0.3;">📅</div>
            <h3>No appointments booked yet.</h3>
            <p>Your appointments will appear here once the admin books one for you.</p>
        </div>
    <% } %>
</div>

<%-- === ABOUT US TAB === --%>
            <div id="panel-about" class="tab-panel">
                <h3>About Vedology</h3>
                <div style="line-height:1.7; color:#333;">
                    <p><strong>Vedology</strong> is a modern Astrology Management System inspired by the ancient wisdom of the Vedas.</p>
                    <p>The name "Vedology" comes from the four Vedas — Rig Veda, Yajur Veda, and Atharva Veda contain significant references to astrology. This platform bridges traditional Vedic knowledge with modern technology.</p>
                    <p>Vedology provides various astrological services related to health, career, relationships, and more by analyzing planetary positions. It offers a user-friendly experience for clients to find the right astrologer and helps astrologers manage client records efficiently.</p>
                    <p>The logo features Lord Shiva, regarded as the father of astrology in mythology, with the Sun (Atma Karaka) and Moon (Mana Karaka) symbolizing soul and emotions. The deep blue color represents mystery, depth, and hidden cosmic knowledge.</p>
                    <p>Built using MVC architecture, ArrayList for data handling, proper form validation, and full CRUD operations, Vedology seamlessly combines ancient astrology with contemporary software development.</p>
                </div>
            </div>

            <%-- === CONTACT US TAB === --%>
            <div id="panel-contact" class="tab-panel">
                <h3>Contact Us</h3>
                <div style="text-align:center; padding:40px 20px; background:#f8f9fa; border-radius:12px; max-width:500px; margin:30px auto;">
                    <div style="font-size:48px; margin-bottom:20px;">📞</div>
                    <h2>Get in Touch</h2>
                    <p style="font-size:18px; margin:20px 0;">
                        <strong>Phone:</strong> <a href="tel:9779768522395" style="color:#007bff; text-decoration:none;">977-9768522395</a>
                    </p>
                    <p style="font-size:18px; margin:20px 0;">
                        <strong>Email:</strong> <a href="mailto:ojaswi0607@gmail.com" style="color:#007bff; text-decoration:none;">ojaswi0607@gmail.com</a>
                    </p>
                    <p style="margin-top:30px; color:#666;">
                        We are here to help you connect with the cosmic wisdom.<br>
                        Feel free to reach out anytime!
                    </p>
                </div>
            </div>

                <%-- === REPORTS TAB === --%>
                <div id="panel-reports" class="tab-panel">
                    <h3>View Reports</h3>
                    <p style="color:#888; font-size:14px;">No reports available.</p>
                </div>

            </div><%-- /.dashboard-container --%>
        </main>
    </div>

    <script>
    var TABS = ['dashboard','edit','search','astrologers','horoscope','payments','appointments','reports','about','contact'];
    function switchTab(tabName) {
        TABS.forEach(function(t) {
            var panel = document.getElementById('panel-' + t);
            var nav   = document.getElementById('nav-' + t);
            if (panel) panel.classList.toggle('active', t === tabName);
            if (nav)   nav.classList.toggle('active',  t === tabName);
        });
        if (history.pushState) {
            history.pushState(null, '', '?tab=' + tabName);
        }
    }

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

    // Init: activate correct tab immediately, then reveal to avoid flash
    (function() {
        switchTab('<%= currentTab %>');
        document.getElementById('dashboardContainer').classList.add('ready');
    })();

    window.addEventListener('popstate', function() {
        var params = new URLSearchParams(window.location.search);
        switchTab(params.get('tab') || 'dashboard');
    });
    </script>
</body>
</html>
