<?php


use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require __DIR__ . '/PHPMailer/Exception.php';
require __DIR__ . '/PHPMailer/PHPMailer.php';
require __DIR__ . '/PHPMailer/SMTP.php';


header('Content-Type: application/json; charset=UTF-8');


$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status'=>'fail','error'=>'DB connection failed']);
    exit;
}


$email = trim($_POST['email'] ?? '');
if ($email === '') {
    echo json_encode(['status'=>'fail','error'=>'Missing email']);
    exit;
}


$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows === 0) {
    echo json_encode(['status'=>'fail','error'=>'Email not registered']);
    exit;
}
$stmt->close();


$code    = random_int(100000, 999999);
$purpose = 'reset';
$stmt = $conn->prepare("
    REPLACE INTO verification_codes
      (email, code, purpose, created_at, expires_at)
    VALUES
      (?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE))
");

$stmt->bind_param('sis', $email, $code, $purpose);
$stmt->execute();
$stmt->close();
$conn->close();


try {
    $mail = new PHPMailer(true);
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = 't32337817@gmail.com';
    $mail->Password   = 'pbmbbsbykwcokuja';
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    $mail->setFrom('t32337817@gmail.com','Code Kendra');
    $mail->addAddress($email);
    $mail->Subject = 'Your Code Kendra reset code';
    $mail->Body    = "Your password reset code is: $code";
    $mail->send();
} catch (Exception $e) {
    error_log("PHPMailer error: " . $mail->ErrorInfo);
}


echo json_encode(['status'=>'reset_sent']);
exit;
