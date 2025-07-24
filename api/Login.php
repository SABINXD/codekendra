<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

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

$hashed = md5($password);

$stmt = $conn->prepare("SELECT * FROM users WHERE email = ? AND password = ?");
$stmt->bind_param('ss', $email, $hashed);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Invalid email or password"]);
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
