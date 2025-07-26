<?php
header('Content-Type: application/json');
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$username = $_POST['username'] ?? '';
if (!$username) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing username']);
    exit;
}

$stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$stmt->store_result();

$isAvailable = $stmt->num_rows === 0;
echo json_encode(['available' => $isAvailable]);

$stmt->close();
$conn->close();
?>
 