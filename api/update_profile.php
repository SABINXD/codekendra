<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "codekendra";
$ip_address = "192.168.1.6"; // Change to your server IP

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $conn->connect_error]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    $conn->close();
    exit;
}

$user_id = isset($_POST['uid']) ? (int)$_POST['uid'] : 0;
$encoded_image = $_POST['profile_img'] ?? '';

if ($user_id <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user ID.']);
    $conn->close();
    exit;
}

if (empty($encoded_image)) {
    echo json_encode(['status' => 'error', 'message' => 'No profile image data provided.']);
    $conn->close();
    exit;
}

$uploadDir = __DIR__ . '/../web/assets/img/profile/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$decoded_image = base64_decode($encoded_image);
if ($decoded_image === false) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to decode image data.']);
    $conn->close();
    exit;
}

// Save to temp file for GD
$tempFilePath = tempnam(sys_get_temp_dir(), 'profile_img_');
file_put_contents($tempFilePath, $decoded_image);

list($originalWidth, $originalHeight, $imageType) = getimagesize($tempFilePath);
if ($originalWidth === 0 || $originalHeight === 0) {
    unlink($tempFilePath);
    echo json_encode(['status' => 'error', 'message' => 'Uploaded image is invalid.']);
    $conn->close();
    exit;
}

$maxWidth = 400; $maxHeight = 400; $quality = 80;
switch ($imageType) {
    case IMAGETYPE_JPEG: $sourceImage = imagecreatefromjpeg($tempFilePath); break;
    case IMAGETYPE_PNG: $sourceImage = imagecreatefrompng($tempFilePath); break;
    case IMAGETYPE_GIF: $sourceImage = imagecreatefromgif($tempFilePath); break;
    default:
        unlink($tempFilePath);
        echo json_encode(['status' => 'error', 'message' => 'Unsupported image type.']);
        $conn->close();
        exit;
}

if (!$sourceImage) {
    unlink($tempFilePath);
    echo json_encode(['status' => 'error', 'message' => 'Failed to load image for processing.']);
    $conn->close();
    exit;
}

$ratio = min($maxWidth / $originalWidth, $maxHeight / $originalHeight, 1);
$newWidth = (int)($originalWidth * $ratio);
$newHeight = (int)($originalHeight * $ratio);

$newImage = imagecreatetruecolor($newWidth, $newHeight);

if ($imageType == IMAGETYPE_PNG || $imageType == IMAGETYPE_GIF) {
    imagealphablending($newImage, false);
    imagesavealpha($newImage, true);
    $transparent = imagecolorallocatealpha($newImage, 0, 0, 0, 127);
    imagefilledrectangle($newImage, 0, 0, $newWidth, $newHeight, $transparent);
}

imagecopyresampled($newImage, $sourceImage, 0, 0, 0, 0, $newWidth, $newHeight, $originalWidth, $originalHeight);

$profilePicFileName = 'profile_' . $user_id . '_' . time() . '.jpg';
$processedDestPath = $uploadDir . $profilePicFileName;
imagejpeg($newImage, $processedDestPath, $quality);

imagedestroy($sourceImage);
imagedestroy($newImage);
unlink($tempFilePath);

// Update user's profile_pic in the database
$stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE id = ?");
$stmt->bind_param("si", $profilePicFileName, $user_id);
if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Profile picture updated successfully.",
        "profile_pic_url" => 'http://' . $ip_address . '/codekendra/web/assets/img/profile/' . $profilePicFileName
    ]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Failed to update database: ' . $stmt->error]);
}
$stmt->close();
$conn->close();
?>