<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = 'localhost';
$db = 'codekendra';
$user = 'root';
$pass = '';
$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error){
    die("Connection failed: " . $conn->connect_error);
}

// Retrieve POST data
$email     = $_POST['email'] ?? '';
$password  = $_POST['password'] ?? '';
$firstName = $_POST['firstName'] ?? '';
$lastName  = $_POST['lastName'] ?? '';
$username  = $_POST['username'] ?? '';
$gender    = $_POST['gender'] ?? '';

// Check email presence
if (empty($email)) {
    echo "Missing email";
    exit;
}

// Check if this is just an email availability check
if (empty($firstName)) {
    $checkQuery = "SELECT * FROM users WHERE email = ?";
    $stmt = $conn->prepare($checkQuery);
    if (!$stmt) {
        echo "Prepare failed: (" . $conn->errno . ") " . $conn->error;
        exit;
    }
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    echo ($result->num_rows > 0) ? "exists" : "available";

    $stmt->close();
    $conn->close();
    exit;
}

// Validate all required fields
if (empty($password) || empty($lastName) || empty($username) || empty($gender)) {
    echo "Missing required fields for signup";
    exit;
}

// Optional: Hash the password (recommended)
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

// Insert user into database
$insertQuery = "INSERT INTO users (email, password, first_name, last_name, username, gender) VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($insertQuery);
if (!$stmt) {
    echo "Insert Prepare failed: (" . $conn->errno . ") " . $conn->error;
    exit;
}
$stmt->bind_param("ssssss", $email, $hashedPassword, $firstName, $lastName, $username, $gender);

if ($stmt->execute()) {
    echo "User registered successfully";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>
