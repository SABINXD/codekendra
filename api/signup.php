<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/PHPMailer.php';
require 'PHPMailer/SMTP.php';
require 'PHPMailer/Exception.php';

header('Content-Type: application/json');
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

// Get POST values safely
$email     = $_POST['email'] ?? '';
$password  = $_POST['password'] ?? '';
$firstName = $_POST['firstName'] ?? '';
$lastName  = $_POST['lastName'] ?? '';
$username  = $_POST['username'] ?? '';
$gender    = $_POST['gender'] ?? '';

// Early check for email availability (first phase)
if (!isset($_POST['firstName'])) {
    if (!$email) {
        echo json_encode(['status' => 'fail', 'error' => 'Missing email']);
        exit;
    }

    $stmt = $conn->prepare("SELECT id FROM users WHERE email=?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    $result = $stmt->num_rows > 0 ? "exists" : "available";
    $stmt->close();

    echo $result;
    exit;
}

// Full form validation (second phase)
if (!$email || !$password || !$firstName || !$lastName || !$username || !$gender) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing one or more required fields']);
    exit;
}

// ✅ NEW: Check if username already exists
$stmt = $conn->prepare("SELECT id FROM users WHERE username=?");
$stmt->bind_param("s", $username);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    echo json_encode(['status' => 'fail', 'error' => 'Username already exists']);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

// Insert user
$hashedPassword = md5($password); // consider switching to password_hash() later
$stmt = $conn->prepare("INSERT INTO users (email, password, first_name, last_name, username, gender, ac_status) VALUES (?, ?, ?, ?, ?, ?, 0)");
$stmt->bind_param("ssssss", $email, $hashedPassword, $firstName, $lastName, $username, $gender);
if (!$stmt->execute()) {
    echo json_encode(['status' => 'fail', 'error' => 'Signup failed: ' . $stmt->error]);
    exit;
}
$stmt->close();

// Generate code and save
$code = rand(100000, 999999);
$stmt = $conn->prepare("REPLACE INTO verification_codes (email, code, purpose, created_at) VALUES (?, ?, 'verify', NOW())");
$stmt->bind_param("si", $email, $code);
$stmt->execute();
$stmt->close();

// Send email
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 't32337817@gmail.com';
    $mail->Password = 'pbmbbsbykwcokuja';
    $mail->SMTPSecure = 'tls';
    $mail->Port = 587;

    $mail->setFrom('t32337817@gmail.com', 'Code Kendra');
    $mail->addAddress($email);
    $mail->Subject = 'Verify your Code Kendra account';
    $mail->Body = "Welcome to Code Kendra!\n\nYour verification code is: $code";

    $mail->send();
    echo json_encode(['status' => 'verify_sent']);
} catch (Exception $e) {
    echo json_encode(['status' => 'fail', 'error' => $mail->ErrorInfo]);
}

$conn->close();
?>
