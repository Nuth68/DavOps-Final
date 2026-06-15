-- MySQL dump 10.13  Distrib 8.0.44, for Linux (aarch64)
--
-- Host: localhost    Database: PRAVE_Vinuth-db
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `status` enum('approved','cancelled','completed','pending','rejected') NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `renter_id` bigint NOT NULL,
  `terrain_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhi6uutgints5fj9cj244ecysh` (`renter_id`),
  KEY `FKgjx5ojtmm0c8qrgfuw0oim64i` (`terrain_id`),
  CONSTRAINT `FKgjx5ojtmm0c8qrgfuw0oim64i` FOREIGN KEY (`terrain_id`) REFERENCES `terrains` (`id`),
  CONSTRAINT `FKhi6uutgints5fj9cj244ecysh` FOREIGN KEY (`renter_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'2026-06-15 08:14:40.809983','2026-06-18 08:14:40.810021','2026-06-17 08:14:40.810014','approved',150.00,'2026-06-15 08:14:40.809987',3,1),(2,'2026-06-15 08:14:40.812831','2026-06-21 08:14:40.812840','2026-06-20 08:14:40.812837','pending',100.00,'2026-06-15 08:14:40.812835',3,2),(3,'2026-06-15 08:14:40.814606','2026-06-27 08:14:40.814614','2026-06-25 08:14:40.814612','approved',400.00,'2026-06-15 08:14:40.814610',4,3);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `terrain_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtkwsfcto4f50snyjodt15y218` (`user_id`,`terrain_id`),
  KEY `FKkfs6lmurovif34cq5430xcojn` (`terrain_id`),
  CONSTRAINT `FKk7du8b8ewipawnnpg76d55fus` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkfs6lmurovif34cq5430xcojn` FOREIGN KEY (`terrain_id`) REFERENCES `terrains` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (1,'2026-06-15 08:14:40.833101','2026-06-15 08:14:40.833105',1,3),(2,'2026-06-15 08:14:40.835148','2026-06-15 08:14:40.835153',3,3),(3,'2026-06-15 08:14:40.836880','2026-06-15 08:14:40.836906',2,4);
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount_paid` decimal(10,2) NOT NULL,
  `payment_date` datetime(6) NOT NULL,
  `payment_method` varchar(255) NOT NULL,
  `status` enum('failed','paid','refunded') NOT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,150.00,'2026-06-15 08:14:40.816518','credit_card','paid','TXN-001',1),(2,400.00,'2026-06-15 08:14:40.818577','aba_pay','paid','TXN-002',3);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `terrain_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKodvnh2ymx313jcpxb9vf93yg5` (`terrain_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKodvnh2ymx313jcpxb9vf93yg5` FOREIGN KEY (`terrain_id`) REFERENCES `terrains` (`id`),
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` <= 5) and (`rating` >= 1)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'Amazing field! Well maintained and great facilities.','2026-06-15 08:14:40.821946',5,'2026-06-15 08:14:40.821949',1,3),(2,'Good pitch, but changing rooms could be cleaner.','2026-06-15 08:14:40.829553',4,'2026-06-15 08:14:40.829569',2,3),(3,'Beautiful location right by the river. Will book again!','2026-06-15 08:14:40.831362',5,'2026-06-15 08:14:40.831377',3,4);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terrain_images`
--

DROP TABLE IF EXISTS `terrain_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terrain_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_path` varchar(255) NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `terrain_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkhnnxy1fi8ovis756av1y2r5f` (`terrain_id`),
  CONSTRAINT `FKkhnnxy1fi8ovis756av1y2r5f` FOREIGN KEY (`terrain_id`) REFERENCES `terrains` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terrain_images`
--

LOCK TABLES `terrain_images` WRITE;
/*!40000 ALTER TABLE `terrain_images` DISABLE KEYS */;
INSERT INTO `terrain_images` VALUES (1,'/uploads/terrain1_main.jpg','2026-06-15 08:14:40.770367',1),(2,'/uploads/terrain1_side.jpg','2026-06-15 08:14:40.775594',1),(3,'/uploads/terrain2_main.jpg','2026-06-15 08:14:40.779559',2),(4,'/uploads/terrain3_main.jpg','2026-06-15 08:14:40.782951',3);
/*!40000 ALTER TABLE `terrain_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terrains`
--

DROP TABLE IF EXISTS `terrains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terrains` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_size` decimal(10,2) DEFAULT NULL,
  `available_from` datetime(6) DEFAULT NULL,
  `available_to` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `is_available` bit(1) NOT NULL,
  `location` varchar(255) NOT NULL,
  `price_per_day` decimal(10,2) NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `main_image_id` bigint DEFAULT NULL,
  `owner_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmv68g47othx5j6acnjyx7jhrx` (`main_image_id`),
  KEY `FK2ifeakh4thflbujirh24u0j74` (`owner_id`),
  CONSTRAINT `FK2ifeakh4thflbujirh24u0j74` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmv68g47othx5j6acnjyx7jhrx` FOREIGN KEY (`main_image_id`) REFERENCES `terrain_images` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terrains`
--

LOCK TABLES `terrains` WRITE;
/*!40000 ALTER TABLE `terrains` DISABLE KEYS */;
INSERT INTO `terrains` VALUES (1,500.00,'2026-06-05 08:14:40.758595','2026-12-15 08:14:40.758607','2026-06-15 08:14:40.758572','Beautiful natural grass field with floodlights, suitable for 11-a-side matches.',_binary '','Phnom Penh',150.00,'Green Field Arena','2026-06-15 08:14:40.797667',1,1),(2,300.00,'2026-06-10 08:14:40.762814','2026-10-15 08:14:40.762817','2026-06-15 08:14:40.762797','Artificial turf, perfect for 7-a-side games. Changing rooms available.',_binary '','Siem Reap',100.00,'Stadium X Pitch','2026-06-15 08:14:40.804884',3,1),(3,600.00,'2026-06-12 08:14:40.766070','2026-09-15 08:14:40.766073','2026-06-15 08:14:40.766052','Scenic riverside location with standard 11-a-side pitch. Parking available.',_binary '','Phnom Penh',200.00,'Riverside Football Ground','2026-06-15 08:14:40.808075',4,2);
/*!40000 ALTER TABLE `terrains` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-06-15 08:14:40.731799','owner1@example.com','password123','2026-06-15 08:14:40.731803','owner1'),(2,'2026-06-15 08:14:40.752149','owner2@example.com','password123','2026-06-15 08:14:40.752159','owner2'),(3,'2026-06-15 08:14:40.754462','renter1@example.com','password123','2026-06-15 08:14:40.754469','renter1'),(4,'2026-06-15 08:14:40.756510','renter2@example.com','password123','2026-06-15 08:14:40.756515','renter2');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'PRAVE_Vinuth-db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-15  9:19:07
