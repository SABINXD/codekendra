-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 20, 2025 at 12:57 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `codekendra`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `full_name` varchar(250) NOT NULL,
  `email` varchar(250) NOT NULL,
  `password` text NOT NULL,
  `password_text` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id`, `full_name`, `email`, `password`, `password_text`) VALUES
(1, 'admin', 'admin@admin.com', 'savin12#@', 'savin12#@');

-- --------------------------------------------------------

--
-- Table structure for table `block_list`
--

CREATE TABLE `block_list` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `blocked_user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `comment` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `post_id`, `user_id`, `comment`, `created_at`) VALUES
(68, 137, 167, 'hello', '2025-08-07 02:04:07');

-- --------------------------------------------------------

--
-- Table structure for table `follow_list`
--

CREATE TABLE `follow_list` (
  `id` int(11) NOT NULL,
  `follower_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `likes`
--

CREATE TABLE `likes` (
  `id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `from_user_id` int(11) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `read_status` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `from_user_id`, `to_user_id`, `message`, `read_status`, `created_at`) VALUES
(45, 157, 154, 'hello', 1, '2025-08-04 13:12:41'),
(46, 154, 157, 'yes', 0, '2025-08-04 13:13:16'),
(47, 154, 157, 'nice', 0, '2025-08-06 16:23:08'),
(48, 154, 157, 'best design', 0, '2025-08-06 16:23:20');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `from_user_id` int(11) NOT NULL,
  `read_status` int(11) NOT NULL,
  `post_id` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `to_user_id`, `message`, `created_at`, `from_user_id`, `read_status`, `post_id`) VALUES
(122, 157, 'commented on your post', '2025-08-06 16:12:11', 154, 1, '128'),
(123, 154, 'started following you', '2025-08-07 00:31:11', 157, 0, ''),
(124, 167, 'liked your post !', '2025-08-07 01:16:06', 168, 1, '137'),
(125, 168, 'started following you !', '2025-08-07 02:16:11', 167, 0, '0'),
(126, 167, 'has warned you for post !', '2025-08-15 16:33:45', 1, 1, '138');

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_img` text NOT NULL,
  `post_text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `code_content` text NOT NULL,
  `code_language` text NOT NULL,
  `tags` text NOT NULL,
  `code_status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `user_id`, `post_img`, `post_text`, `created_at`, `code_content`, `code_language`, `tags`, `code_status`) VALUES
(136, 167, '1754529190images (4).png', 'Here is a java script function code', '2025-08-07 01:13:11', 'function greet(name) {\r\n  // This function takes a name and returns a greeting message\r\n  return \"Hello, \" + name + \"!\";\r\n}\r\n', 'javascript', 'java script', 1),
(137, 167, '1754529286sabindhungana-web.png', 'here is a basic portfolio website', '2025-08-07 01:14:46', '<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n  <meta charset=\"UTF-8\">\r\n  <title>Sabin\'s Portfolio</title>\r\n  <script src=\"https://cdn.tailwindcss.com\"></script>\r\n</head>\r\n<body class=\"bg-gray-100 text-gray-800\">\r\n\r\n  <!-- Main Container -->\r\n  <div class=\"min-h-screen flex flex-col justify-center items-center px-4 text-center\">\r\n\r\n    <!-- Name and Title -->\r\n    <h1 class=\"text-4xl md:text-5xl font-bold mb-4\">Hi, I\'m <span class=\"text-blue-600\">Sabin Dhungana</span></h1>\r\n    <p class=\"text-lg md:text-xl mb-6\">A passionate Web Developer from Nepal.</p>\r\n\r\n    <!-- Skills -->\r\n    <div class=\"flex flex-wrap justify-center gap-4 mb-8\">\r\n      <span class=\"bg-blue-100 text-blue-700 px-4 py-2 rounded-full\">HTML</span>\r\n      <span class=\"bg-green-100 text-green-700 px-4 py-2 rounded-full\">CSS</span>\r\n      <span class=\"bg-yellow-100 text-yellow-700 px-4 py-2 rounded-full\">JavaScript</span>\r\n      <span class=\"bg-purple-100 text-purple-700 px-4 py-2 rounded-full\">PHP</span>\r\n    </div>\r\n\r\n    <!-- Contact Button -->\r\n    <button \r\n      onclick=\"showMessage()\" \r\n      class=\"bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition\">\r\n      Contact Me\r\n    </button>\r\n\r\n    <!-- JS Message -->\r\n    <p id=\"message\" class=\"mt-6 text-green-600 font-semibold hidden\">Thanks for your interest! Email me at: sabin@example.com</p>\r\n\r\n  </div>\r\n\r\n  <!-- JavaScript -->\r\n  <script>\r\n    function showMessage() {\r\n      document.getElementById(\"message\").classList.remove(\"hidden\");\r\n    }\r\n  </script>\r\n</body>\r\n</html>\r\n', 'html', 'html,js', 1),
(138, 167, '1755275519download (1).jpg', 'yesto kinw vayeko ho kei reson xw', '2025-08-15 16:31:59', '', '', '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `gender` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `password` text NOT NULL,
  `profile_pic` text NOT NULL DEFAULT 'default_profile.jpg',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ac_status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `gender`, `email`, `username`, `bio`, `password`, `profile_pic`, `created_at`, `updated_at`, `ac_status`) VALUES
(167, 'Sabin', 'Dhungana', 0, 'hwaukff@gmail.com', 'sabin12', '👋', '5030924da380a999067adbd78af06bd9', '1754529029Untitled design (16).png', '2025-08-07 01:08:31', '2025-08-07 01:53:20', 1),
(168, 'basu ', 'poudel', 1, 'pdlbasu7@gmail.com', 'poudelbasu', NULL, 'adbdefa33bd2370dea10e97777ce27eb', 'default_profile.jpg', '2025-08-07 01:15:17', '2025-08-07 01:15:38', 1);

-- --------------------------------------------------------

--
-- Table structure for table `verification_codes`
--

CREATE TABLE `verification_codes` (
  `email` varchar(255) NOT NULL,
  `code` int(6) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `purpose` varchar(20) NOT NULL DEFAULT 'verify',
  `expires_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `verification_codes`
--

INSERT INTO `verification_codes` (`email`, `code`, `created_at`, `purpose`, `expires_at`) VALUES
('2738383@gmail.com', 705718, '2025-08-06 05:43:04', 'verify', NULL),
('53s83837@gmail.com', 580114, '2025-08-06 05:48:21', 'verify', NULL),
('sabin263848@gmail.com', 153887, '2025-08-06 05:50:21', 'verify', NULL),
('sajjan@gmail.com', 293924, '2025-07-30 10:12:28', 'verify', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `block_list`
--
ALTER TABLE `block_list`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `follow_list`
--
ALTER TABLE `follow_list`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `likes`
--
ALTER TABLE `likes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `verification_codes`
--
ALTER TABLE `verification_codes`
  ADD PRIMARY KEY (`email`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `block_list`
--
ALTER TABLE `block_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `follow_list`
--
ALTER TABLE `follow_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `likes`
--
ALTER TABLE `likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=253;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=127;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=139;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=170;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
