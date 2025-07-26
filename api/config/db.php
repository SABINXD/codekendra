<?php
$host = "localhost";
$user = "root";
$password = ""; 
$dbname = "codekendra";

$db = mysqli_connect($host, $user, $password, $dbname);

if (!$db) {
    die(json_encode(['status' => false, 'error' => mysqli_connect_error()]));
}
?>
