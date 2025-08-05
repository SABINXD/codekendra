<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . "/config/db.php");

try {
    $conn = getDbConnection();
    
    // Get parameters
    $user_id = $_POST['user_id'] ?? '';
    $caption = $_POST['caption'] ?? $_POST['post_description'] ?? '';
    $code_content = $_POST['code_content'] ?? '';
    $code_language = $_POST['code_language'] ?? '';
    $tags = $_POST['tags'] ?? '';
    
    error_log("=== CREATE POST DEBUG ===");
    error_log("User ID: " . $user_id);
    error_log("Caption: " . $caption);
    error_log("Code content length: " . strlen($code_content));
    error_log("Code language: " . $code_language);
    error_log("Tags: " . $tags);
    
    // Validate inputs
    if (empty($user_id) || !is_numeric($user_id)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid user ID']);
        exit;
    }
    
    if (empty($caption)) {
        echo json_encode(['status' => 'error', 'message' => 'Caption is required']);
        exit;
    }
    
    // Check if image was uploaded
    $image_field = null;
    if (isset($_FILES['post_img']) && $_FILES['post_img']['error'] === UPLOAD_ERR_OK) {
        $image_field = 'post_img';
    } elseif (isset($_FILES['post_image']) && $_FILES['post_image']['error'] === UPLOAD_ERR_OK) {
        $image_field = 'post_image';
    }
    
    // At least one of image or code is required
    if (!$image_field && empty($code_content)) {
        echo json_encode(['status' => 'error', 'message' => 'Image or code is required']);
        exit;
    }
    
    // Determine code_status based on whether code content exists
    $code_status = (!empty($code_content) && !empty($code_language)) ? 1 : 0;
    
    $post_img = '';
    if ($image_field) {
        $uploaded_file = $_FILES[$image_field];
        
        // Validate image
        $allowed_types = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
        if (!in_array($uploaded_file['type'], $allowed_types)) {
            echo json_encode(['status' => 'error', 'message' => 'Invalid image type']);
            exit;
        }
        
        // Create upload directory if it doesn't exist
        $upload_dir = __DIR__ . '/../web/assets/img/posts/';
        if (!file_exists($upload_dir)) {
            mkdir($upload_dir, 0755, true);
        }
        
        // Generate unique filename
        $file_extension = pathinfo($uploaded_file['name'], PATHINFO_EXTENSION);
        if (empty($file_extension)) {
            $file_extension = 'jpg';
        }
        $filename = 'post_' . $user_id . '_' . time() . '_' . uniqid() . '.' . $file_extension;
        $file_path = $upload_dir . $filename;
        
        // Move uploaded file
        if (!move_uploaded_file($uploaded_file['tmp_name'], $file_path)) {
            echo json_encode(['status' => 'error', 'message' => 'Failed to save image']);
            exit;
        }
        
        $post_img = $filename;
    }
    
    // FIXED: Insert without specifying ID (let AUTO_INCREMENT handle it)
    $stmt = $conn->prepare("INSERT INTO posts (user_id, post_text, post_img, code_content, code_language, tags, code_status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())");
    $stmt->bind_param("isssssi", $user_id, $caption, $post_img, $code_content, $code_language, $tags, $code_status);
    
    if ($stmt->execute()) {
        $post_id = $conn->insert_id; // Get the actual generated ID
        
        error_log("✅ Post created successfully - ID: " . $post_id . ", Code Status: " . $code_status);
        
        // Return full URL for immediate display
        $image_url = '';
        if (!empty($post_img)) {
            $image_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/posts/" . $post_img;
        }
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Post created successfully',
            'post_id' => $post_id, // Return the actual generated ID
            'image_url' => $image_url,
            'code_status' => $code_status
        ]);
    } else {
        error_log("❌ Database insert failed: " . $stmt->error);
        echo json_encode(['status' => 'error', 'message' => 'Failed to create post: ' . $stmt->error]);
    }
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("❌ Create post error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?>