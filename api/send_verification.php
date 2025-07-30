<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/PHPMailer.php';
require 'PHPMailer/SMTP.php';
require 'PHPMailer/Exception.php';

header('Content-Type: application/json');

$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'DB connection failed']);
    exit;
}

$email   = $_POST['email'] ?? '';
$purpose = 'verify';
$code    = rand(100000, 999999);

if (!$email) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing email']);
    exit;
}

$userCheck = $conn->prepare("SELECT id FROM users WHERE email=?");
$userCheck->bind_param("s", $email);
$userCheck->execute();
$userCheck->store_result();
if ($userCheck->num_rows === 0) {
    echo json_encode(['status' => 'fail', 'error' => 'User not registered']);
    exit;
}
$userCheck->close();

$stmt = $conn->prepare("REPLACE INTO verification_codes (email, code, purpose, created_at) VALUES (?, ?, ?, NOW())");
$stmt->bind_param("sis", $email, $code, $purpose);
$stmt->execute();
$stmt->close();

$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = 't32337817@gmail.com';          // Move to env later
    $mail->Password   = 'pbmbbsbykwcokuja';             // SECURITY: Don't commit this!
    $mail->SMTPSecure = 'tls';
    $mail->Port       = 587;

    $mail->setFrom('t32337817@gmail.com', 'Code Kendra');
    $mail->addAddress($email);
    $mail->Subject = 'Verify your Code Kendra account';
    $mail->Body    = "Your verification code is: $code";

    $mail->send();
    echo json_encode(['status' => 'sent']);
} catch (Exception $e) {
    echo json_encode(['status' => 'fail', 'error' => $mail->ErrorInfo]);
}
?>
