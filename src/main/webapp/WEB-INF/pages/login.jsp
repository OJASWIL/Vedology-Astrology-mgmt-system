<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Vedology mgmt System</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">

</head>
<body>
<div class="login-container">

    <p class="welcome-text">Welcome Back!<br>Please Login to your account</p>

 <form class="login-form" action="login" method="post">
            <label for="username">Email</label>
            <input type="email" placeholder="Enter Email" type="text" id="email" name="email" value="${param.email}" required><br>
            <label for="password">Password</label>
            <input type="password" placeholder="Password" type="password" id="password" name="password" required><br>
            <button type="submit">Login</button>
            
        </form>
    <p class="signup-link">
                Don't have an account? <a href="register">Register</a>
            </p>
            <% if (request.getAttribute("error") != null) { %>
                <p style="color:red;">${error}</p>
            <% } %>
  </div>
</body>
</html>

