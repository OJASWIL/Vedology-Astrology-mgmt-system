-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: astrologydb
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.28-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `AppointmentId` int(11) NOT NULL,
  `ClientId` int(11) DEFAULT NULL,
  `AstrologerId` int(11) DEFAULT NULL,
  `AppointmentDate` date DEFAULT NULL,
  `Mode` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`AppointmentId`),
  KEY `fk_client` (`ClientId`),
  KEY `fk_astrologer` (`AstrologerId`),
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `client` (`ClientId`),
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`AstrologerId`) REFERENCES `astrologer` (`AstrologerId`),
  CONSTRAINT `fk_astrologer` FOREIGN KEY (`AstrologerId`) REFERENCES `astrologer` (`AstrologerId`),
  CONSTRAINT `fk_client` FOREIGN KEY (`ClientId`) REFERENCES `client` (`ClientId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentdetails`
--

DROP TABLE IF EXISTS `appointmentdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointmentdetails` (
  `AppointmentId` int(11) NOT NULL,
  `TimeFrameId` int(11) DEFAULT NULL,
  PRIMARY KEY (`AppointmentId`),
  KEY `fk_timeframe` (`TimeFrameId`),
  CONSTRAINT `appointmentdetails_ibfk_1` FOREIGN KEY (`AppointmentId`) REFERENCES `appointment` (`AppointmentId`),
  CONSTRAINT `appointmentdetails_ibfk_2` FOREIGN KEY (`TimeFrameId`) REFERENCES `timeframe` (`TimeFrameId`),
  CONSTRAINT `fk_appointment` FOREIGN KEY (`AppointmentId`) REFERENCES `appointment` (`AppointmentId`),
  CONSTRAINT `fk_timeframe` FOREIGN KEY (`TimeFrameId`) REFERENCES `timeframe` (`TimeFrameId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` int(11) NOT NULL,
  `astrologer_id` int(11) NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `status` varchar(50) DEFAULT 'confirmed',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `astrologer`
--

DROP TABLE IF EXISTS `astrologer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `astrologer` (
  `AstrologerId` int(11) NOT NULL,
  `AvailableDays` varchar(100) DEFAULT NULL,
  `Address` varchar(255) DEFAULT NULL,
  `ContactNumber` varchar(20) DEFAULT NULL,
  `ExperienceYear` int(11) DEFAULT NULL,
  `Specialization` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`AstrologerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `ClientId` int(11) NOT NULL,
  `ClientName` varchar(100) DEFAULT NULL,
  `TimeOfBirth` time DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `ContactNumber` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ClientId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `timeframe`
--

DROP TABLE IF EXISTS `timeframe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timeframe` (
  `TimeFrameId` int(11) NOT NULL,
  `StartTime` time DEFAULT NULL,
  `Duration` int(11) DEFAULT NULL,
  PRIMARY KEY (`TimeFrameId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `UserId` int(11) NOT NULL AUTO_INCREMENT,
  `Email` varchar(100) NOT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `Role` enum('admin','client') DEFAULT 'client',
  `CreatedAt` timestamp NOT NULL DEFAULT current_timestamp(),
  `FullName` varchar(100) NOT NULL,
  `TimeOfBirth` time NOT NULL,
  `Phone` varchar(15) NOT NULL,
  `ProfileImage` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UserId`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-14 15:35:52
