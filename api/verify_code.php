<?php
header('Content-Type: application/json');

// Connect to the database
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$email = $_POST['email'] ?? '';
$input_code = $_POST['code'] ?? '';
$purpose = 'verify';  

if (!$email || !$input_code) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing email or code']);
    exit;
}


$stmt = $conn->prepare("SELECT code FROM verification_codes WHERE email = ? AND purpose = ?");
$stmt->bind_param("ss", $email, $purpose);
$stmt->execute();
$stmt->bind_result($db_code);
$stmt->fetch();
$stmt->close();


if (trim($input_code) === trim($db_code)) {
    
    $stmt = $conn->prepare("UPDATE users SET ac_status = 1 WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->close();

    echo json_encode(['status' => 'verified']);
} else {
    echo json_encode(['status' => 'invalid', 'debug_input' => $input_code, 'debug_expected' => $db_code]);
}
?>
