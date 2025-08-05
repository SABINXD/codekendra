<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Log the raw POST data for debugging
error_log("=== DELETE POST DEBUG ===");
error_log("Raw POST data: " . file_get_contents('php://input'));
error_log("POST array: " . print_r($_POST, true));

include __DIR__ . '/config/db.php';

try {
    // Get database connection
    $conn = getDbConnection();
    
    // Handle both POST data and JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Get and validate parameters from POST or JSON
    $post_id = 0;
    $user_id = 0;
    
    if (isset($_POST['post_id'])) {
        $post_id = (int)$_POST['post_id'];
        $user_id = (int)$_POST['user_id'];
    } elseif (isset($input['post_id'])) {
        $post_id = (int)$input['post_id'];
        $user_id = (int)$input['user_id'];
    }
    
    error_log("Processing delete request - Post ID: $post_id, User ID: $user_id");
    
    // Validate parameters
    if ($post_id <= 0) {
        error_log("Invalid post_id: $post_id");
        echo json_encode([
            'status' => false, 
            'error' => 'Invalid post ID',
            'debug' => ['post_id' => $post_id, 'user_id' => $user_id]
        ]);
        exit;
    }
    
    if ($user_id <= 0) {
        error_log("Invalid user_id: $user_id");
        echo json_encode([
            'status' => false, 
            'error' => 'Invalid user ID',
            'debug' => ['post_id' => $post_id, 'user_id' => $user_id]
        ]);
        exit;
    }
    
    // First, check if the post exists and belongs to the user
    $checkQuery = "SELECT post_img FROM posts WHERE id = ? AND user_id = ?";
    $checkStmt = mysqli_prepare($conn, $checkQuery);
    
    if (!$checkStmt) {
        error_log("Prepare statement failed: " . mysqli_error($conn));
        echo json_encode([
            'status' => false, 
            'error' => 'Database error preparing statement',
            'debug' => ['mysqli_error' => mysqli_error($conn)]
        ]);
        exit;
    }
    
    mysqli_stmt_bind_param($checkStmt, "ii", $post_id, $user_id);
    
    if (!mysqli_stmt_execute($checkStmt)) {
        error_log("Execute check statement failed: " . mysqli_stmt_error($checkStmt));
        echo json_encode([
            'status' => false, 
            'error' => 'Database error checking post',
            'debug' => ['mysqli_error' => mysqli_stmt_error($checkStmt)]
        ]);
        mysqli_stmt_close($checkStmt);
        exit;
    }
    
    mysqli_stmt_store_result($checkStmt);
    
    if (mysqli_stmt_num_rows($checkStmt) === 0) {
        error_log("Post not found or user doesn't have permission");
        echo json_encode([
            'status' => false, 
            'error' => 'Post not found or you do not have permission to delete it',
            'debug' => [
                'post_id' => $post_id, 
                'user_id' => $user_id,
                'rows_found' => mysqli_stmt_num_rows($checkStmt)
            ]
        ]);
        mysqli_stmt_close($checkStmt);
        exit;
    }
    
    // Get the post image path
    $image_path = '';
    mysqli_stmt_bind_result($checkStmt, $image_path);
    mysqli_stmt_fetch($checkStmt);
    mysqli_stmt_close($checkStmt);
    
    error_log("Post found. Image path: " . ($image_path ? $image_path : 'NULL'));
    
    // Start transaction for data consistency
    mysqli_begin_transaction($conn);
    
    try {
        // Delete related data first (foreign key constraints)
        
        // Delete likes
        $deleteLikesQuery = "DELETE FROM likes WHERE post_id = ?";
        $deleteLikesStmt = mysqli_prepare($conn, $deleteLikesQuery);
        mysqli_stmt_bind_param($deleteLikesStmt, "i", $post_id);
        mysqli_stmt_execute($deleteLikesStmt);
        mysqli_stmt_close($deleteLikesStmt);
        
        // Delete comments
        $deleteCommentsQuery = "DELETE FROM comments WHERE post_id = ?";
        $deleteCommentsStmt = mysqli_prepare($conn, $deleteCommentsQuery);
        mysqli_stmt_bind_param($deleteCommentsStmt, "i", $post_id);
        mysqli_stmt_execute($deleteCommentsStmt);
        mysqli_stmt_close($deleteCommentsStmt);
        
        // Delete notifications related to this post
        $deleteNotificationsQuery = "DELETE FROM notifications WHERE post_id = ?";
        $deleteNotificationsStmt = mysqli_prepare($conn, $deleteNotificationsQuery);
        mysqli_stmt_bind_param($deleteNotificationsStmt, "s", (string)$post_id);
        mysqli_stmt_execute($deleteNotificationsStmt);
        mysqli_stmt_close($deleteNotificationsStmt);
        
        // Now delete the post
        $deleteQuery = "DELETE FROM posts WHERE id = ? AND user_id = ?";
        $deleteStmt = mysqli_prepare($conn, $deleteQuery);
        
        if (!$deleteStmt) {
            throw new Exception("Prepare delete statement failed: " . mysqli_error($conn));
        }
        
        mysqli_stmt_bind_param($deleteStmt, "ii", $post_id, $user_id);
        
        if (!mysqli_stmt_execute($deleteStmt)) {
            throw new Exception("Execute delete statement failed: " . mysqli_stmt_error($deleteStmt));
        }
        
        $affected_rows = mysqli_stmt_affected_rows($deleteStmt);
        mysqli_stmt_close($deleteStmt);
        
        if ($affected_rows > 0) {
            // Commit transaction
            mysqli_commit($conn);
            
            // Delete the post image if it exists
            if (!empty($image_path)) {
                $fullImagePath = __DIR__ . "/../web/assets/img/posts/$image_path";
                error_log("Checking image file: $fullImagePath");
                
                if (file_exists($fullImagePath)) {
                    if (unlink($fullImagePath)) {
                        error_log("Successfully deleted image: $fullImagePath");
                    } else {
                        error_log("Failed to delete image: $fullImagePath");
                    }
                } else {
                    error_log("Image file does not exist: $fullImagePath");
                }
            }
            
            error_log("Successfully deleted post ID: $post_id");
            echo json_encode([
                'status' => true, 
                'message' => 'Post deleted successfully',
                'debug' => [
                    'post_id' => $post_id,
                    'affected_rows' => $affected_rows
                ]
            ]);
        } else {
            throw new Exception("No rows affected when deleting post");
        }
        
    } catch (Exception $e) {
        // Rollback transaction on error
        mysqli_rollback($conn);
        throw $e;
    }
    
    mysqli_close($conn);
    
} catch (Exception $e) {
    error_log("Exception in delete_post.php: " . $e->getMessage());
    echo json_encode([
        'status' => false, 
        'error' => 'Server error: ' . $e->getMessage(),
        'debug' => ['exception' => $e->getMessage()]
    ]);
}
?>
