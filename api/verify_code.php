<?php
header('Content-Type: application/json');

// ✅ Connect to DB
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$email      = $_POST['email'] ?? '';
$input_code = $_POST['code'] ?? '';
$purpose    = 'verify';

// 🔍 Check for required fields
if (!$email || !$input_code) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing email or code']);
    exit;
}

// 🔎 Fetch code from DB
$stmt = $conn->prepare("SELECT code FROM verification_codes WHERE email = ? AND purpose = ?");
$stmt->bind_param("ss", $email, $purpose);
$stmt->execute();
$stmt->bind_result($db_code);
$stmt->fetch();
$stmt->close();

if (!$db_code) {
    echo json_encode(['status' => 'fail', 'error' => 'No code found for this email']);
    exit;
}

// ✅ Compare codes after trimming
if (trim((string)$input_code) === trim((string)$db_code)) {
    // 🔓 Activate account
    $stmt = $conn->prepare("UPDATE users SET ac_status = 1 WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->close();

    // 🧼 Clean up used verification code
    $stmt = $conn->prepare("DELETE FROM verification_codes WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->close();

    echo json_encode(['status' => 'verified', 'message' => 'Account verified successfully']);
} else {
    // ❌ Wrong code
    echo json_encode([
        'status' => 'invalid',
        'message' => 'Incorrect verification code',
        'debug_input' => $input_code,
        'debug_expected' => $db_code
    ]);
}

$conn->close();
?>
