<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
header('Content-Type: application/json');

$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$email    = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

if ($email === '' || $password === '') {
    echo json_encode(["status" => "error", "message" => "Missing credentials"]);
    exit;
}

$hashed = md5($password); // Replace later with password_verify

$stmt = $conn->prepare("SELECT * FROM users WHERE email = ? AND password = ? AND ac_status = 1");
$stmt->bind_param('ss', $email, $hashed);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid credentials or account not verified"
    ]);
    exit;
}

$user = $result->fetch_assoc();
echo json_encode([
    "status" => "success",
    "message" => "Login successful",
    "user" => [
        "user_id"   => $user['id'],
        "email"     => $user['email'],
        "username"  => $user['username'],
        "firstName" => $user['first_name'],
        "lastName"  => $user['last_name'],
        "gender"    => $user['gender']
    ]
]);

$stmt->close();
$conn->close();
?>
