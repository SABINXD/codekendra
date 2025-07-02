-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 01, 2025 at 09:40 AM
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
-- Database: `connectify`
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
(43, 21, 27, 'Haha this is good meme', '2025-02-20 14:24:20'),
(50, 21, 34, 'haha', '2025-03-07 02:19:16');

-- --------------------------------------------------------

--
-- Table structure for table `follow_list`
--

CREATE TABLE `follow_list` (
  `id` int(11) NOT NULL,
  `follower_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `follow_list`
--

INSERT INTO `follow_list` (`id`, `follower_id`, `user_id`) VALUES
(39, 18, 21),
(41, 21, 16),
(44, 16, 21),
(46, 28, 1),
(47, 2, 1),
(49, 29, 1),
(50, 30, 25),
(51, 25, 30),
(52, 31, 30),
(53, 31, 33),
(54, 34, 31),
(55, 34, 27),
(56, 27, 34),
(57, 31, 34);

-- --------------------------------------------------------

--
-- Table structure for table `likes`
--

CREATE TABLE `likes` (
  `id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `likes`
--

INSERT INTO `likes` (`id`, `post_id`, `user_id`) VALUES
(103, 20, 2),
(106, 21, 2),
(108, 21, 27),
(111, 20, 27),
(113, 21, 28),
(116, 22, 29),
(117, 21, 29),
(119, 23, 29),
(125, 21, 34),
(126, 25, 34),
(128, 25, 31),
(129, 25, 27),
(130, 25, 35);

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
(43, 35, 28, 'hlo uncle', 1, '2025-06-30 11:34:34'),
(44, 28, 35, 'k xw akhanxa kati gpa aayo?', 0, '2025-06-30 11:34:50');

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
(25, 21, 'Your post has been deleted by admin', '2025-02-20 08:44:01', 1, 1, '13'),
(26, 21, 'has  warned you for post !', '2025-02-20 08:53:18', 1, 2, '14'),
(27, 21, 'has warned you for post !', '2025-02-20 08:57:11', 1, 2, '14'),
(28, 21, 'has warned you for post !', '2025-02-20 08:58:08', 1, 2, '14'),
(29, 21, 'has warned you for post !', '2025-02-20 08:58:34', 1, 2, '14'),
(30, 21, 'has warned you for post !', '2025-02-20 09:02:16', 1, 2, '14'),
(31, 21, 'has warned you for post !', '2025-02-20 09:04:09', 1, 2, '14'),
(32, 21, 'has warned you for post !', '2025-02-20 09:04:29', 1, 2, '14'),
(33, 21, 'commented on your post', '2025-02-20 09:05:42', 23, 2, '14'),
(34, 21, 'liked your post !', '2025-02-20 09:05:44', 23, 2, '14'),
(35, 25, 'liked your post !', '2025-02-20 10:12:34', 2, 1, '15'),
(36, 25, 'commented on your post', '2025-02-20 10:12:39', 2, 1, '15'),
(37, 25, 'has  deleted Your post !', '2025-02-20 10:17:28', 1, 1, '15'),
(38, 1, 'started following you !', '2025-02-20 12:50:44', 28, 0, '0'),
(39, 2, 'has  deleted Your post !', '2025-02-20 14:22:11', 1, 1, '19'),
(40, 1, 'started following you !', '2025-02-20 14:22:40', 2, 0, '0'),
(41, 27, 'liked your post !', '2025-02-20 14:23:02', 2, 1, '17'),
(42, 25, 'liked your post !', '2025-02-20 14:23:06', 2, 1, '16'),
(43, 27, 'liked your post !', '2025-02-20 14:23:07', 2, 1, '18'),
(44, 28, 'liked your post !', '2025-02-20 14:23:10', 2, 0, '21'),
(45, 27, 'commented on your post', '2025-02-20 14:23:19', 2, 1, '18'),
(46, 28, 'liked your post !', '2025-02-20 14:24:11', 27, 0, '21'),
(47, 28, 'commented on your post', '2025-02-20 14:24:20', 27, 0, '21'),
(48, 25, 'liked your post !', '2025-02-20 14:24:25', 27, 1, '16'),
(49, 2, 'liked your post !', '2025-02-20 14:24:27', 27, 0, '20'),
(50, 27, 'liked your post !', '2025-02-21 09:34:17', 28, 1, '17'),
(51, 27, 'commented on your post', '2025-02-21 12:50:11', 28, 1, '17'),
(52, 25, 'liked your post !', '2025-02-21 12:50:14', 28, 1, '16'),
(53, 27, 'liked your post !', '2025-02-22 01:34:26', 29, 1, '17'),
(54, 27, 'commented on your post', '2025-02-22 01:34:34', 29, 1, '17'),
(55, 25, 'started following you !', '2025-02-22 01:35:37', 29, 1, '0'),
(56, 25, 'commented on your post', '2025-02-22 01:35:52', 29, 1, '16'),
(57, 25, 'has warned you for post !', '2025-02-22 01:40:18', 1, 1, '16'),
(58, 25, 'has  deleted Your post !', '2025-02-22 01:40:53', 1, 1, '16'),
(59, 27, 'has warned you for post !', '2025-02-22 03:04:22', 1, 1, '17'),
(60, 28, 'liked your post !', '2025-02-22 03:07:23', 29, 0, '21'),
(61, 28, 'commented on your post', '2025-02-22 03:07:28', 29, 0, '21'),
(62, 1, 'started following you !', '2025-02-22 03:07:38', 29, 0, '0'),
(63, 27, 'liked your post !', '2025-02-22 03:07:46', 29, 1, '18'),
(64, 27, 'liked your post !', '2025-03-06 07:01:02', 30, 1, '17'),
(65, 27, 'liked your post !', '2025-03-06 07:03:53', 25, 1, '18'),
(66, 27, 'liked your post !', '2025-03-06 07:03:59', 30, 1, '18'),
(67, 27, 'commented on your post', '2025-03-06 07:07:43', 30, 1, '17'),
(68, 25, 'started following you !', '2025-03-06 07:08:15', 30, 1, '0'),
(69, 30, 'started following you !', '2025-03-06 07:09:31', 25, 0, '0'),
(70, 30, 'started following you !', '2025-03-06 07:15:34', 31, 0, '0'),
(71, 27, 'has warned you for post !', '2025-03-06 07:18:24', 1, 1, '18'),
(72, 27, 'liked your post !', '2025-03-07 01:54:24', 31, 1, '17'),
(73, 33, 'started following you !', '2025-03-07 02:11:41', 31, 0, '0'),
(74, 27, 'liked your post !', '2025-03-07 02:18:57', 34, 1, '17'),
(75, 27, 'commented on your post', '2025-03-07 02:19:02', 34, 1, '17'),
(76, 28, 'commented on your post', '2025-03-07 02:19:15', 34, 0, '21'),
(77, 28, 'liked your post !', '2025-03-07 02:19:18', 34, 0, '21'),
(78, 31, 'started following you !', '2025-03-07 02:20:00', 34, 1, '0'),
(79, 27, 'liked your post !', '2025-03-07 08:06:12', 34, 1, '18'),
(80, 27, 'started following you !', '2025-03-07 08:06:38', 34, 1, '0'),
(81, 27, 'has warned you for post !', '2025-03-07 08:11:16', 1, 1, '18'),
(82, 34, 'started following you !', '2025-03-07 08:12:16', 27, 1, '0'),
(83, 27, 'has  deleted Your post !', '2025-03-07 08:12:36', 1, 1, '18'),
(84, 27, 'has  deleted Your post !', '2025-03-07 08:12:48', 1, 1, '17'),
(85, 34, 'started following you !', '2025-06-06 08:56:40', 31, 1, '0'),
(86, 34, 'liked your post !', '2025-06-06 08:56:54', 31, 1, '25'),
(87, 34, 'liked your post !', '2025-06-06 09:08:41', 27, 1, '25'),
(88, 28, 'started following you !', '2025-06-30 11:29:12', 35, 0, '0'),
(89, 34, 'liked your post !', '2025-06-30 11:34:13', 35, 0, '25');

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_img` text NOT NULL,
  `post_text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `user_id`, `post_img`, `post_text`, `created_at`) VALUES
(21, 28, '1740055825472395308_1264370404802223_8387227532795261538_n.jpg', '🤣', '2025-02-20 12:50:25'),
(24, 33, '1741313405Flowchart.png', 'flowchart', '2025-03-07 02:10:05'),
(25, 34, '1741313970Flowchart.png', 'flowchart', '2025-03-07 02:19:30');

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
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `profile_pic` text NOT NULL DEFAULT 'default_profile.jpg',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ac_status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `gender`, `email`, `username`, `password`, `profile_pic`, `created_at`, `updated_at`, `ac_status`) VALUES
(1, 'Admin', '', 1, 'admin@admin.com', 'Admin', '95a52f6386fb42264be28503d6c2d5da', '1740044415circle-icon.png', '2024-12-11 00:14:20', '2025-02-20 09:40:30', 1),
(25, 'Basu Dev', 'Poudel', 1, 'basupdl123@gmail.com', 'basudev', '4157322794cc730ba645cfb46f5c534d', '17400468561734051765php-svgrepo-com.png', '2025-02-20 09:38:31', '2025-02-20 10:20:56', 1),
(26, 'Tesing', 'Person', 1, 'tester123@gmail.com', 'tester', '5030924da380a999067adbd78af06bd9', '17400468011734000183Character.png', '2025-02-20 09:39:04', '2025-02-20 10:20:01', 1),
(27, 'Neymar', 'Junior', 1, 'neymar123@gmail.com', 'neymarJunior', '592495852c2a694723a465a4a2d37fcc', '1740055267images.jpg', '2025-02-20 12:40:09', '2025-02-20 12:41:07', 1),
(28, 'Xavier', 'Gurung', 1, 'xavier123@gmail.com', 'xavier', '240ad41058154bbdea1a594130cfff15', '1740055801images (1).jpg', '2025-02-20 12:49:41', '2025-02-20 12:50:01', 1),
(31, 'hari', 'prassad', 1, 'hariprassad@gmail.com', 'haeiprassad', '0b9d378627978ba5d1ece8beca3208b0', 'default_profile.jpg', '2025-03-06 07:14:21', '2025-03-06 07:15:25', 1),
(32, '-2', '-51', 1, '123@123.com', '-2', '3a5f12925265accd0792c2ccc612050e', 'default_profile.jpg', '2025-03-07 02:06:40', '2025-03-07 02:06:40', 0),
(34, 'sabin', 'dhungana', 1, 'hwaukff@gmail.com', 'sabin12', '95a52f6386fb42264be28503d6c2d5da', 'default_profile.jpg', '2025-03-07 02:18:20', '2025-06-06 07:46:23', 1),
(35, 'sabin', 'dhungana', 1, 'hwaukffde@gmail.com', 'sabin12#@', '6583cc50bd9030ba629fcae18ebf3721', 'default_profile.jpg', '2025-06-06 08:14:25', '2025-06-30 11:29:04', 1);

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
  ADD PRIMARY KEY (`id`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `follow_list`
--
ALTER TABLE `follow_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT for table `likes`
--
ALTER TABLE `likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=131;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=90;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
