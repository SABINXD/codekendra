<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "codekendra";
$ip_address = "192.168.1.6";

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    $caption = isset($_POST['caption']) ? trim($_POST['caption']) : '';
    
    if ($user_id <= 0) {
        throw new Exception("Invalid user ID");
    }
    
    if (empty($caption)) {
        throw new Exception("Caption is required");
    }
    
    if (!isset($_FILES['post_img']) || $_FILES['post_img']['error'] !== UPLOAD_ERR_OK) {
        throw new Exception("No image uploaded or upload error");
    }
    
    // Handle file upload
    $uploadDir = __DIR__ . '/../web/assets/img/posts/';
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0755, true);
    }
    
    $fileExtension = strtolower(pathinfo($_FILES['post_img']['name'], PATHINFO_EXTENSION));
    $allowedTypes = ['jpg', 'jpeg', 'png', 'gif'];
    
    if (!in_array($fileExtension, $allowedTypes)) {
        throw new Exception("Invalid file type");
    }
    
    $fileName = 'post_' . $user_id . '_' . time() . '.' . $fileExtension;
    $uploadPath = $uploadDir . $fileName;
    
    if (!move_uploaded_file($_FILES['post_img']['tmp_name'], $uploadPath)) {
        throw new Exception("Failed to save uploaded file");
    }
    
    // Insert into database using correct column name
    $stmt = $conn->prepare("INSERT INTO posts (user_id, post_text, post_img, created_at) VALUES (?, ?, ?, NOW())");
    $stmt->bind_param("iss", $user_id, $caption, $fileName);
    
    if ($stmt->execute()) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Post created successfully',
            'post_id' => $conn->insert_id,
            'filename' => $fileName
        ]);
    } else {
        throw new Exception("Database insert failed: " . $stmt->error);
    }
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Create post error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>