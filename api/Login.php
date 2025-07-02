<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = 'localhost';
$db = 'codekendra';
$user = 'root';
$pass = '';
$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Missing email or password"]);
    exit;
}

$query = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo json_encode(["status" => "error", "message" => "No user found with this email"]);
    exit;
}

$user = $result->fetch_assoc();

if (password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => "success",
        "message" => "Login successful",
        "user" => [
            "id" => $user['id'],
            "email" => $user['email'],
            "username" => $user['username'],
            "firstName" => $user['first_name'],
            "lastName" => $user['last_name'],
            "gender" => $user['gender']
        ]
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid password"]);
}

$stmt->close();
$conn->close();
?>
