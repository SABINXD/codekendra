<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    error_log("DB connection failed: " . $conn->connect_error);
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$caption = $_POST['caption'] ?? '';
$image   = $_FILES['post_img'] ?? null;

if ($user_id === '' || $caption === '' || !$image || $image['error'] !== UPLOAD_ERR_OK) {
    error_log("Missing fields or image upload error");
    echo json_encode(['status'=>'fail', 'error'=>'Missing fields']);
    exit;
}

// Save image
$ext         = pathinfo($image['name'], PATHINFO_EXTENSION);
$uniqueName  = uniqid('img_', true) . '.' . $ext;
$uploadPath  = '../web/assets/img/posts/' . $uniqueName;
$imagePath   = 'web/assets/img/posts/' . $uniqueName;

if (!is_dir('../web/assets/img/posts/')) {
    mkdir('../web/assets/img/posts/', 0755, true);
}
if (!move_uploaded_file($image['tmp_name'], $uploadPath)) {
    error_log("Upload failed to: " . $uploadPath);
    echo json_encode(['status'=>'fail', 'error'=>'Image save failed']);
    exit;
}

// Insert post
$stmt = $conn->prepare("INSERT INTO posts (user_id, post_text, post_img) VALUES (?, ?, ?)");
$stmt->bind_param("iss", $user_id, $caption, $imagePath);

echo $stmt->execute()
    ? json_encode(['status'=>'success'])
    : json_encode(['status'=>'fail', 'error'=>'Insert failed']);

$stmt->close();
$conn->close();
?>