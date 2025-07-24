<?php

header('Content-Type: application/json; charset=UTF-8');
ini_set('display_errors', 0);
error_reporting(E_ALL);

$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status'=>'fail','error'=>'DB connection failed']);
    exit;
}


$email = trim($_POST['email'] ?? '');
$code  = trim($_POST['code']  ?? '');

if ($email === '' || $code === '') {
    echo json_encode(['status'=>'fail','error'=>'Missing email or code']);
    exit;
}


$stmt = $conn->prepare("
    SELECT code, expires_at
      FROM verification_codes
     WHERE email = ? AND purpose = 'reset'
    LIMIT 1
");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($db_code, $expires_at);


if (! $stmt->fetch()) {
   
    echo json_encode(['status'=>'invalid']);
    exit;
}

$stmt->close();
$conn->close();

$entered = (int) $code;
$stored  = (int) $db_code;

if (strtotime($expires_at) < time()) {
    echo json_encode(['status'=>'expired']);
    exit;
}


if ($entered === $stored) {
    echo json_encode(['status'=>'verified']);
} else {
    echo json_encode(['status'=>'invalid']);
}
exit;
