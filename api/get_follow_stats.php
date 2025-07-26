<?php
include 'db.php';

$uid = $_POST['uid'];

$followers = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM follows WHERE user_id='$uid'"));
$following = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM follows WHERE follower_id='$uid'"));

echo json_encode([
    "followers" => $followers,
    "following" => $following
]);
?>
