<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . "/config/db.php");


try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $uid = $_POST['uid'] ?? '';
    $profile_img = $_POST['profile_img'] ?? '';
    
    if (!$uid || !is_numeric($uid)) {
        echo json_encode(['status' => 'fail', 'message' => 'Missing or invalid UID']);
        exit;
    }
    
    if (empty($profile_img)) {
        echo json_encode(['status' => 'fail', 'message' => 'No image data provided']);
        exit;
    }
    
    // Decode base64 image
    $image_data = base64_decode($profile_img);
    if ($image_data === false) {
        echo json_encode(['status' => 'fail', 'message' => 'Invalid image data']);
        exit;
    }
    
    // Create filename
    $filename = 'profile_' . $uid . '_' . time() . '.jpg';
    $upload_dir = '../web/assets/img/profile/';
    
    // Create directory if it doesn't exist
    if (!is_dir($upload_dir)) {
        mkdir($upload_dir, 0755, true);
    }
    
    $upload_path = $upload_dir . $filename;
    
    // Save image
    if (file_put_contents($upload_path, $image_data) === false) {
        echo json_encode(['status' => 'fail', 'message' => 'Failed to save image']);
        exit;
    }
    
    // Update database
    $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE id = ?");
    $stmt->bind_param("si", $filename, $uid);
    
    if ($stmt->execute()) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Profile image updated successfully',
            'filename' => $filename
        ]);
    } else {
        echo json_encode(['status' => 'fail', 'message' => 'Database update failed']);
    }
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Upload error: " . $e->getMessage());
    echo json_encode([
        'status' => 'fail', 
        'message' => 'Upload error: ' . $e->getMessage()
    ]);
}
?>