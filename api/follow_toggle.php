<?php
require_once('config/db.php'); 
$follower_id = $_POST['follower_id'];
$user_id = $_POST['user_id'];

// Check if already following
$check = mysqli_query($conn, "SELECT * FROM follows WHERE follower_id='$follower_id' AND user_id='$user_id'");
if (mysqli_num_rows($check) > 0) {
    // Unfollow
    mysqli_query($conn, "DELETE FROM follows WHERE follower_id='$follower_id' AND user_id='$user_id'");
    echo json_encode(["status" => "unfollowed"]);
} else {
    // Follow
    mysqli_query($conn, "INSERT INTO follows(follower_id, user_id) VALUES('$follower_id', '$user_id')");
    echo json_encode(["status" => "followed"]);
}
?>
