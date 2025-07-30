<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);
require_once('config/db.php'); 
// Database connection
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    error_log("❌ DB connection failed: " . $conn->connect_error);
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$caption = $_POST['caption'] ?? '';
$image   = $_FILES['post_img'] ?? null;

if ($user_id === '' || $caption === '' || !$image || $image['error'] !== UPLOAD_ERR_OK) {
    error_log("❌ Missing fields or image upload error");
    echo json_encode(['status'=>'fail', 'error'=>'Missing fields or image']);
    exit;
}

// Save image
$ext = pathinfo($image['name'], PATHINFO_EXTENSION);
$uniqueName = uniqid('img_', true) . '.' . $ext;
$uploadFolder = '../web/assets/img/posts/';
$uploadPath = $uploadFolder . $uniqueName;
$imagePath = 'web/assets/img/posts/' . $uniqueName;

if (!is_dir($uploadFolder)) {
    mkdir($uploadFolder, 0755, true);
}
if (!move_uploaded_file($image['tmp_name'], $uploadPath)) {
    error_log("❌ Upload failed to: " . $uploadPath);
    echo json_encode(['status'=>'fail', 'error'=>'Image save failed']);
    exit;
}

// Insert post
$createdAt = date("Y-m-d H:i:s");
$stmt = $conn->prepare("INSERT INTO posts (user_id, post_text, post_img, created_at) VALUES (?, ?, ?, ?)");
$stmt->bind_param("isss", $user_id, $caption, $imagePath, $createdAt);

if ($stmt->execute()) {
    $postId = $stmt->insert_id;

    // ✅ Real-time broadcast
    require_once 'PieSocketPublisher.php';

    $eventData = [
        "post_id" => $postId,
        "user_id" => $user_id,
        "caption" => $caption,
        "post_img" => $imagePath,
        "created_at" => $createdAt
    ];

    $pushResult = PieSocketPublisher::publish("new-post", $eventData);
    error_log("🚀 PieSocket broadcast response: " . $pushResult);

    echo json_encode(['status'=>'success']);
} else {
    error_log("❌ DB insert failed: " . $stmt->error);
    echo json_encode(['status'=>'fail', 'error'=>'Insert failed']);
}

$stmt->close();
$conn->close();
?>
