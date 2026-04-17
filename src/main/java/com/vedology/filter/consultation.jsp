<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Book a Consultation</title>
 <link rel="stylesheet" type= "text/css" href="../css/consultation.css">
</head>
<body>
  <div class="form-container">
    <h1>Book a Consultation</h1>
    <form>
      <label>Name:</label>
      <input type="text" disabled />

      <label>Email:</label>
      <input type="email" disabled />

      <label>Phone Number:</label>
      <input type="tel" disabled />

      <label>Service Type:</label>
      <input type="text" disabled />

      <label>Preferred Date:</label>
      <input type="date" disabled />

      <label>Preferred Time:</label>
      <input type="time" disabled />

      <label class="notes-label"><a href="#">Additional Notes</a></label>
      <textarea rows="5" disabled></textarea>

      <div class="checkbox-container">
        <input type="checkbox" checked disabled />
        <label>I agree to the Terms and Conditions</label>
      </div>

      <button type="submit" disabled>Book Consultation</button>

      <p class="login-link">Already have an appointment? <a href="#">Login</a></p>
    </form>
  </div>
</body>
</html>