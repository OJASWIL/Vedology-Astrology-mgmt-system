<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/home.css">
<title>Home</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 20px;
        background-color: #e9ecef;
        color: #333;
    }
    .container {
        max-width: 800px;
        margin: 0 auto;
        text-align: center;
    }
    .buttons {
        margin: 20px 0;
    }
    .buttons a {
        padding: 10px 20px;
        margin: 0 10px;
        background-color: #007bff;
        color: white;
        text-decoration: none;
        border-radius: 5px;
    }
    .buttons a:hover {
        background-color: #0056b3;
    }
    .description {
        text-align: left;
        line-height: 1.6;
        background-color: white;
        padding: 20px;
        border-radius: 5px;
        box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }
</style>
</head>
<body>
    <div class="container">
        <h1>Welcome to Vedology</h1>
        <div class="buttons">
            <a href="<%= request.getContextPath() %>/login">Login</a>
            <a href="<%= request.getContextPath() %>/register">Signup</a>
        </div>
        <div class="description">
            <p>Our project is all about Astrology Management System. Name of the project is Vedology which means related to vedas. There are total four vedas and astrology is part of the three vedas they are: Rig Veda, Yajur Veda have few sections and Atharva Veda has sections in it that deals with astrology. This platform is designed to deliver astrological services. It can be health related, relationship related, career related and many more with the help of positions of the stars and the planets.</p>
            
            <p>This platform is user friendly and effective for astrologers to manage their clients and it is also make easy for clients to find out the best astrologer whom they can choose. In this project logo represent the father of the astrology who is Shiv wearing moon as an ornament, sun as a divine thrown and stars in his heart shows universe is within him. Moon and Sun in the logo shows two powerful planet for astrology because Sun is the atma-karaka planet and moon is the man-karaka planet which means sun is power of mind and moon is power of emotions. Both plays vital role in astrological world. Other planets are also equally important but these two are the major.</p>
            
            <p>Blue color in this project represent astrology is one of the deep and secret knowledge because it can tell us about our past, present as well as future. So, if anybody is interested to know about their past, present and future they can choose this platform.</p>
        </div>
    </div>
</body>
</html>