-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 03, 2025 at 05:32 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pdf_converter`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT 'USER',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `role`, `created_at`) VALUES
(100, 'ydragn', '$2a$10$boLkws0dpOIuL9TIBkWUke6UdCtIn8eEJvhtiPeH4Ciw2YLY2R/Pu', 'ydragn@gmail.com', 'USER', '2025-11-03 02:56:50'),
(101, 'admin', '$2a$10$T4MStw9P1EqpYW/hRhzQ3OGgpcRxgkqwbHEXEKQsNB3JJNsF.KR4W', 'admin@gmail.com', 'ADMIN', '2025-11-03 03:04:06'),
(102, 'ydragn12', '$2a$10$vDFbO.tNXltUW7hBq6JL6eaBrCyG.xNrSq4CAiRtHGNLKyGEwWf9O', 'ydragn12@gmail.com', 'USER', '2025-11-03 04:04:22'),
(103, 'ydragn1210', '$2a$10$Ch6bRlEwAS1oS1tjZwoFq.mHEKT4fPcyQroMgVhnpefX7eJSH.TpC', 'ydragn1210@gmail.com', 'USER', '2025-11-03 04:29:49');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
