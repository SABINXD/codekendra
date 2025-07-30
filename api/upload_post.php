<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("connection.php");

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = $_POST['user_id'];
    $caption = $_POST['caption'];
    $image = $_FILES['post_img'];

    $target_dir = "../web/assets/img/posts/";
    $unique_name = uniqid("img_") . "_" . basename($image["name"]);
    $target_path = $target_dir . $unique_name;

    $image_url = "http://192.168.1.5/codekendra/web/assets/img/posts/" . $unique_name;

    // Move uploaded file
    if (move_uploaded_file($image["tmp_name"], $target_path)) {
        // Insert into database
        $sql = "INSERT INTO posts (user_id, post_text, post_img) VALUES ('$user_id', '$caption', '$image_url')";
        if (mysqli_query($conn, $sql)) {
            echo json_encode(["status" => "success", "message" => "Post uploaded."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Database error: " . mysqli_error($conn)]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Image upload failed."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request."]);
}
?>
