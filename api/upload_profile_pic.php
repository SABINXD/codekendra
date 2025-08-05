<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Log all errors to help debug
ini_set('log_errors', 1);
ini_set('error_log', '/tmp/php_errors.log');

include __DIR__ . '/config/db.php';

try {
    // Check if request method is POST
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Only POST method allowed');
    }

    // Check if user ID is provided
    if (!isset($_POST['uid']) || empty($_POST['uid'])) {
        throw new Exception('Missing or invalid user ID');
    }

    $user_id = (int)$_POST['uid'];
    if ($user_id <= 0) {
        throw new Exception('Invalid user ID format');
    }

    // Check if file was uploaded
    if (!isset($_FILES['profile_pic']) || $_FILES['profile_pic']['error'] !== UPLOAD_ERR_OK) {
        $error_msg = 'File upload error';
        if (isset($_FILES['profile_pic']['error'])) {
            switch ($_FILES['profile_pic']['error']) {
                case UPLOAD_ERR_INI_SIZE:
                case UPLOAD_ERR_FORM_SIZE:
                    $error_msg = 'File too large';
                    break;
                case UPLOAD_ERR_PARTIAL:
                    $error_msg = 'File upload incomplete';
                    break;
                case UPLOAD_ERR_NO_FILE:
                    $error_msg = 'No file uploaded';
                    break;
                case UPLOAD_ERR_NO_TMP_DIR:
                    $error_msg = 'Server configuration error (no temp dir)';
                    break;
                case UPLOAD_ERR_CANT_WRITE:
                    $error_msg = 'Server write error';
                    break;
                default:
                    $error_msg = 'Unknown upload error';
            }
        }
        throw new Exception($error_msg);
    }

    $uploaded_file = $_FILES['profile_pic'];
    
    // Validate file type
    $allowed_types = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    $file_type = $uploaded_file['type'];
    
    if (!in_array($file_type, $allowed_types)) {
        throw new Exception('Invalid file type. Only JPEG, PNG, and GIF allowed');
    }

    // Validate file size (5MB max)
    $max_size = 5 * 1024 * 1024; // 5MB
    if ($uploaded_file['size'] > $max_size) {
        throw new Exception('File too large. Maximum size is 5MB');
    }

    // Create upload directory if it doesn't exist
    $upload_dir = __DIR__ . '/../web/assets/img/profile/';
    if (!is_dir($upload_dir)) {
        if (!mkdir($upload_dir, 0755, true)) {
            throw new Exception('Failed to create upload directory');
        }
    }

    // Check if directory is writable
    if (!is_writable($upload_dir)) {
        throw new Exception('Upload directory is not writable');
    }

    // Generate unique filename
    $file_extension = pathinfo($uploaded_file['name'], PATHINFO_EXTENSION);
    $new_filename = 'profile_' . $user_id . '_' . time() . '.' . $file_extension;
    $upload_path = $upload_dir . $new_filename;

    // Move uploaded file
    if (!move_uploaded_file($uploaded_file['tmp_name'], $upload_path)) {
        throw new Exception('Failed to save uploaded file');
    }

    // Connect to database
    $conn = getDbConnection();
    
    // Verify user exists
    $check_user_sql = "SELECT id, profile_pic FROM users WHERE id = ?";
    $check_stmt = $conn->prepare($check_user_sql);
    if (!$check_stmt) {
        throw new Exception('Database prepare error: ' . $conn->error);
    }
    
    $check_stmt->bind_param("i", $user_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    if ($result->num_rows === 0) {
        // Clean up uploaded file
        unlink($upload_path);
        throw new Exception('User not found');
    }
    
    $user_data = $result->fetch_assoc();
    $old_profile_pic = $user_data['profile_pic'];

    // Update database with new profile picture
    $update_sql = "UPDATE users SET profile_pic = ? WHERE id = ?";
    $update_stmt = $conn->prepare($update_sql);
    if (!$update_stmt) {
        // Clean up uploaded file
        unlink($upload_path);
        throw new Exception('Database prepare error: ' . $conn->error);
    }
    
    $update_stmt->bind_param("si", $new_filename, $user_id);
    
    if (!$update_stmt->execute()) {
        // Clean up uploaded file
        unlink($upload_path);
        throw new Exception('Failed to update database: ' . $update_stmt->error);
    }

    // Delete old profile picture (if not default)
    if ($old_profile_pic && $old_profile_pic !== 'default_profile.jpg' && $old_profile_pic !== $new_filename) {
        $old_file_path = $upload_dir . $old_profile_pic;
        if (file_exists($old_file_path)) {
            unlink($old_file_path);
        }
    }

    // Close database connections
    $check_stmt->close();
    $update_stmt->close();
    $conn->close();

    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Profile picture updated successfully',
        'filename' => $new_filename,
        'url' => '/codekendra/web/assets/img/profile/' . $new_filename
    ]);

} catch (Exception $e) {
    // Log the error
    error_log('Profile upload error: ' . $e->getMessage());
    
    // Return error response
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
