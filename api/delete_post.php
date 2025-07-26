<?php
header('Content-Type: application/json');
include __DIR__ . '/config/db.php';

$post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : null;
$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : null;

if (!$post_id || !$user_id) {
    echo json_encode(['status' => false, 'error' => 'Missing post_id or user_id']);
    exit;
}

// ✅ Fetch image path first
$image_path = '';
$imgQuery = "SELECT post_img FROM posts WHERE id = ? AND user_id = ?";
$imgStmt = mysqli_prepare($db, $imgQuery);
mysqli_stmt_bind_param($imgStmt, "ii", $post_id, $user_id);
mysqli_stmt_execute($imgStmt);
mysqli_stmt_bind_result($imgStmt, $image_path);
mysqli_stmt_fetch($imgStmt);
mysqli_stmt_close($imgStmt);

// ✅ Try to delete image file
if (!empty($image_path)) {
    $fullImagePath = __DIR__ . "/../../../web/assets/img/posts/$image_path";
    if (file_exists($fullImagePath)) {
        if (!unlink($fullImagePath)) {
            // Optional logging: unable to delete file
        }
    }
}

$deleteQuery = "DELETE FROM posts WHERE id = ? AND user_id = ?";
$deleteStmt = mysqli_prepare($db, $deleteQuery);
mysqli_stmt_bind_param($deleteStmt, "ii", $post_id, $user_id);
$result = mysqli_stmt_execute($deleteStmt);

$response = [];

if ($result && mysqli_stmt_affected_rows($deleteStmt) > 0) {
    $response = ['status' => true, 'message' => 'Post deleted successfully'];
} else {
    $response = ['status' => false, 'error' => 'Post not found or delete failed'];
}

mysqli_stmt_close($deleteStmt);
mysqli_close($db);

echo json_encode($response);
?>
