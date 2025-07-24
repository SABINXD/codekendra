<?php
header('Content-Type: application/json; charset=UTF-8');
$conn = new mysqli('localhost','root','','codekendra');

$email    = trim($_POST['email']    ?? '');
$password = trim($_POST['password'] ?? '');

if ($email === '' || $password === '') {
  echo json_encode(['status'=>'fail','error'=>'Missing email or password']);
  exit;
}


$hash = md5($password);

$stmt = $conn->prepare("UPDATE users SET password = ? WHERE email = ?");
$stmt->bind_param('ss', $hash, $email);

if ($stmt->execute()) {
  echo json_encode(['status'=>'success']);
} else {
  echo json_encode(['status'=>'fail','error'=>'DB update failed']);
}
$stmt->close();
$conn->close();
exit;
