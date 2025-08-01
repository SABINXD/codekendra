<?php

include(__DIR__ . "/config/db.php");
if ($conn->connect_error){
    die("Connection failed: " . $conn->connect_error);
}

$email = $_POST['email'] ?? '';

if (empty($email)) {
    echo "Missing email";
    exit;
}

$stmt = $conn->prepare("SELECT * FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo "exists";
} else {
    echo "available";
}

$stmt->close();
$conn->close();
?>
