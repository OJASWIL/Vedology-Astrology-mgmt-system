<%--  <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/register.css">
</head>
<body>
  <div class="form-container">
    <h2>Registration Form</h2>
    <p>Please fill in your details below</p>
    <form>
      <label for="fullname">Full Name</label>
      <input type="text" id="fullname" name="fullname">

      <label for="email">Email Address</label>
      <input type="email" id="email" name="email">

      <label for="phone">Phone Number</label>
      <input type="tel" id="phone" name="phone">

      <label for="password">Password</label>
      <input type="password" id="password" name="password">

      <label for="confirm-password">Confirm Password</label>
      <input type="password" id="confirm-password" name="confirm-password">

      <div class="checkbox-container">
        <input type="checkbox" id="terms">
        <label for="terms">I agree to the term and conditions</label>
      </div>

      <button type="submit" >Register</button>
      <p class="login-text">
        Already have an account? <a href="#">Login</a>
      </p>
    </form>
  </div>
</body>
</html>
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register - Vedology</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/register.css">
</head>
<body>
  <div class="form-container">
    <h2>Registration Form</h2>
    <p>Please fill in your details below</p>
    <form action="register" method="post">
      <label for="fullname">Full Name</label>
      <input type="text" id="fullname" name="fullname" value="${param.fullname}" required><br>
      <label for="email">Email Address</label>
      <input type="email" id="email" name="email" value="${param.email}" required><br>
      <label for="timeOfBirth">Time of Birth (HH:MM)</label>
      <input type="time" id="timeOfBirth" name="timeOfBirth" value="${param.timeOfBirth}" required><br>
      <label for="phone">Phone Number</label>
      <input type="tel" id="phone" name="phone" value="${param.phone}" required><br>
      <label for="password">Password</label>
      <input type="password" id="password" name="password" required><br>
      <label for="confirm-password">Confirm Password</label>
      <input type="password" id="confirm-password" name="confirm-password" required><br>
      <div class="checkbox-container">
        <input type="checkbox" id="terms" name="terms" required>
        <label for="terms">I agree to the terms and conditions</label>
      </div>
      <button type="submit">Register</button>
      <p class="login-text">
        Already have an account? <a href="login.jsp">Login</a>
      </p>
      <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;">${error}</p>
      <% } else if (request.getAttribute("success") != null) { %>
        <p style="color:green;">${success}</p>
      <% } %>
    </form>
  </div>
</body>
</html>