<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);
require_once('config/db.php'); 

const IP_ADDRESS = "192.168.1.5";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = $_POST['user_id'];
    $caption = $_POST['caption'];
    $image = $_FILES['post_img'];

    $target_dir = "../web/assets/img/posts/";
    $unique_name = uniqid("img_") . "_" . basename($image["name"]);
    $target_path = $target_dir . $unique_name;

    $image_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/posts/" . $unique_name;

    if (move_uploaded_file($image["tmp_name"], $target_path)) {
        $stmt = $conn->prepare("INSERT INTO posts (user_id, post_text, post_img) VALUES (?, ?, ?)");
        $stmt->bind_param("iss", $user_id, $caption, $image_url);
        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Post uploaded."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Database error: " . $stmt->error]);
        }
        $stmt->close();
    } else {
        echo json_encode(["status" => "error", "message" => "Image upload failed."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request."]);
}
?>
