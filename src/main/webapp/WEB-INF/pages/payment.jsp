<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Payments and History</title>
<link rel="stylesheet" type= "text/css" href="../css/payment.css">
</head>
<body>
  <div class="container">
    <h1>Payments and History</h1>

    <section class="balance">
      <h2>Account Balance</h2>
      <p><strong>Current Balance:</strong> $300</p>
    </section>

    <section class="recent-payments">
      <h2>Recent Payments</h2>
      <div class="payment-row">
        <span>Date: MM/DD/YYYY</span>
        <span>Amount: $....</span>
        <span>Status: <span class="status success">[✓]</span></span>
      </div>
      <div class="payment-row">
        <span>Date: MM/DD/YYYY</span>
        <span>Amount: $....</span>
        <span>Status: <span class="status failed">[✗]</span></span>
      </div>
      <button class="button">View All Payments</button>
    </section>

    <section class="payment-methods">
      <h2>Payment Methods</h2>
      <div class="methods">
        <button class="button">Credit Card</button>
        <button class="button">PayPal</button>
        <button class="button">Bank Transfer</button>
      </div>
      <button class="button">Make a Payment</button>
    </section>

    <section class="payment-history">	
      <h2>Payment History</h2>
      <p>Show <a href="#">Previous</a> Payments</p>
    </section>
  </div>
</body>
</html>
