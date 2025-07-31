<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
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
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    
    if ($post_id <= 0) {
        throw new Exception("Invalid post ID");
    }
    
    // Get comments with user info
    $sql = "SELECT 
                c.id,
                c.user_id,
                c.comment_text,
                c.created_at,
                u.username,
                COALESCE(u.first_name, '') as first_name,
                COALESCE(u.last_name, '') as last_name,
                COALESCE(u.profile_pic, 'default_profile.jpg') as profile_pic
            FROM comments c
            LEFT JOIN users u ON c.user_id = u.id
            WHERE c.post_id = ?
            ORDER BY c.created_at ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $post_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $comments = array();
    
    while ($row = $result->fetch_assoc()) {
        // Create display name
        $display_name = trim($row['first_name'] . ' ' . $row['last_name']);
        if (empty($display_name)) {
            $display_name = $row['username'] ?: 'Unknown User';
        }
        
        // Handle profile picture
        $profile_pic_filename = null;
        if (!empty($row['profile_pic']) && $row['profile_pic'] !== 'default_profile.jpg') {
            $profile_pic_filename = $row['profile_pic'];
        }
        
        $comments[] = array(
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'user_name' => $display_name,
            'comment_text' => $row['comment_text'],
            'created_at' => $row['created_at'],
            'profile_pic' => $profile_pic_filename
        );
    }
    
    echo json_encode([
        'status' => true,
        'comments' => $comments,
        'total_comments' => count($comments)
    ]);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Get comments error: " . $e->getMessage());
    echo json_encode([
        'status' => false,
        'message' => $e->getMessage()
    ]);
}
?>