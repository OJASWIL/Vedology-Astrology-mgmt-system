<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Manage Astrologers</title>
 <link rel="stylesheet" type= "text/css" href="../css/manageAstrologer.css">
</head>
<body>
  <div class="container">
    <h1>Manage Astrologers</h1>

    <div class="search-bar">
      <input type="text" placeholder="Search" />
    </div>

    <h2>Astrologer List Table</h2>

    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Phone Number</th>
          <th>Specialization</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Namuna Sharma</td>
          <td>Namuna @gmail.com</td>
          <td>9846758392</td>
          <td>Vedic Astrologer</td>
          <td>Approve/Reject</td>
        </tr>
        <tr>
          <td>Hari Lamichhane</td>
          <td>Hari@gmail.com</td>
          <td>9835674927</td>
          <td>Tarot Reader</td>
          <td>Approve/Reject</td>
        </tr>
        <tr>
          <td>Pratibha Pokhrel</td>
          <td>Pratibha@gmail.com</td>
          <td>9813264785</td>
          <td>Numerologist</td>
          <td>Approve/Reject</td>
        </tr>
      </tbody>
    </table>

    <div class="add-button">
      <button>Add a New Astrologer</button>
    </div>
  </div>
</body>
</html>