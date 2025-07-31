<?php
// Enable error logging to a file instead of displaying
error_reporting(E_ALL);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/upload_errors.log');
ini_set('display_errors', 0); // Don't display errors in response

// Force JSON response
header('Content-Type: application/json');

// Log all incoming data for debugging
file_put_contents(__DIR__ . '/debug.log', 
    "=== UPLOAD DEBUG " . date('Y-m-d H:i:s') . " ===\n" .
    "POST: " . print_r($_POST, true) . "\n" .
    "FILES: " . print_r($_FILES, true) . "\n" .
    "SERVER: " . print_r($_SERVER, true) . "\n\n", 
    FILE_APPEND
);

try {
    // Database connection
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "codekendra";
    
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        throw new Exception("Database connection failed: " . $conn->connect_error);
    }
    
    // Check request method
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Invalid request method: ' . $_SERVER['REQUEST_METHOD']);
    }
    
    // Get and validate UID
    $uid = isset($_POST['uid']) ? (int)$_POST['uid'] : 0;
    if ($uid <= 0) {
        throw new Exception('Missing or invalid UID. Received: ' . print_r($_POST, true));
    }
    
    // Check if user exists
    $stmt = $conn->prepare("SELECT id FROM users WHERE id = ?");
    if (!$stmt) {
        throw new Exception('Database prepare failed: ' . $conn->error);
    }
    
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        throw new Exception('User not found with UID: ' . $uid);
    }
    $stmt->close();
    
    // Check upload directory
    $uploadDir = dirname(__DIR__) . '/web/assets/img/profile/';
    
    if (!is_dir($uploadDir)) {
        if (!mkdir($uploadDir, 0755, true)) {
            throw new Exception('Failed to create upload directory: ' . $uploadDir);
        }
    }
    
    if (!is_writable($uploadDir)) {
        throw new Exception('Upload directory not writable: ' . $uploadDir);
    }
    
    // Check file upload
    if (!isset($_FILES['profile_pic'])) {
        throw new Exception('No file uploaded. FILES array: ' . print_r($_FILES, true));
    }
    
    $file = $_FILES['profile_pic'];
    
    if ($file['error'] !== UPLOAD_ERR_OK) {
        $errorMessages = [
            UPLOAD_ERR_INI_SIZE => 'File exceeds upload_max_filesize',
            UPLOAD_ERR_FORM_SIZE => 'File exceeds MAX_FILE_SIZE',
            UPLOAD_ERR_PARTIAL => 'File upload incomplete',
            UPLOAD_ERR_NO_FILE => 'No file uploaded',
            UPLOAD_ERR_NO_TMP_DIR => 'Missing temporary folder',
            UPLOAD_ERR_CANT_WRITE => 'Failed to write file to disk',
            UPLOAD_ERR_EXTENSION => 'Upload stopped by extension'
        ];
        
        $errorMsg = isset($errorMessages[$file['error']]) 
            ? $errorMessages[$file['error']] 
            : 'Unknown upload error: ' . $file['error'];
            
        throw new Exception($errorMsg);
    }
    
    // Validate file
    $fileTmpPath = $file['tmp_name'];
    $fileExtension = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $allowedTypes = ['jpg', 'jpeg', 'png', 'gif'];
    
    if (!in_array($fileExtension, $allowedTypes)) {
        throw new Exception('Invalid file type: ' . $fileExtension . '. Allowed: ' . implode(', ', $allowedTypes));
    }
    
    // Validate image
    $imageInfo = getimagesize($fileTmpPath);
    if ($imageInfo === false) {
        throw new Exception('Invalid image file');
    }
    
    // Process image (resize)
    list($originalWidth, $originalHeight) = $imageInfo;
    $maxWidth = 400;
    $maxHeight = 400;
    $ratio = min($maxWidth/$originalWidth, $maxHeight/$originalHeight, 1);
    $newWidth = (int)($originalWidth * $ratio);
    $newHeight = (int)($originalHeight * $ratio);
    
    // Create source image
    $sourceImage = null;
    switch ($fileExtension) {
        case 'jpg':
        case 'jpeg':
            $sourceImage = imagecreatefromjpeg($fileTmpPath);
            break;
        case 'png':
            $sourceImage = imagecreatefrompng($fileTmpPath);
            break;
        case 'gif':
            $sourceImage = imagecreatefromgif($fileTmpPath);
            break;
    }
    
    if (!$sourceImage) {
        throw new Exception('Failed to create image resource from: ' . $fileExtension);
    }
    
    // Create new image
    $newImage = imagecreatetruecolor($newWidth, $newHeight);
    
    // Handle transparency for PNG/GIF
    if (in_array($fileExtension, ['png', 'gif'])) {
        imagealphablending($newImage, false);
        imagesavealpha($newImage, true);
        $transparent = imagecolorallocatealpha($newImage, 0, 0, 0, 127);
        imagefilledrectangle($newImage, 0, 0, $newWidth, $newHeight, $transparent);
    }
    
    // Resize image
    imagecopyresampled($newImage, $sourceImage, 0, 0, 0, 0,
        $newWidth, $newHeight, $originalWidth, $originalHeight);
    
    // Save image
    $fileName = 'profile_' . $uid . '_' . time() . '.jpg';
    $destPath = $uploadDir . $fileName;
    
    if (!imagejpeg($newImage, $destPath, 80)) {
        throw new Exception('Failed to save resized image to: ' . $destPath);
    }
    
    // Clean up memory
    imagedestroy($sourceImage);
    imagedestroy($newImage);
    
    // Update database
    $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE id = ?");
    if (!$stmt) {
        throw new Exception('Database prepare failed: ' . $conn->error);
    }
    
    $stmt->bind_param("si", $fileName, $uid);
    
    if (!$stmt->execute()) {
        throw new Exception('Database update failed: ' . $stmt->error);
    }
    
    $stmt->close();
    $conn->close();
    
    // Success response
    echo json_encode([
        "status" => "success",
        "message" => "Profile picture updated successfully",
        "filename" => $fileName,
        "upload_dir" => $uploadDir,
        "file_size" => filesize($destPath)
    ]);
    
} catch (Exception $e) {
    // Log the error
    file_put_contents(__DIR__ . '/upload_errors.log', 
        date('Y-m-d H:i:s') . " ERROR: " . $e->getMessage() . "\n", 
        FILE_APPEND
    );
    
    // Return error response
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
