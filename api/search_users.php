<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error logging
error_reporting(E_ALL);
ini_set('display_errors', 1);
error_log("🔍 Search API called - Method: " . $_SERVER['REQUEST_METHOD'] . " at " . date('Y-m-d H:i:s'));

// Log all received data
error_log("📥 GET data: " . json_encode($_GET));
error_log("📥 POST data: " . json_encode($_POST));
error_log("📥 Raw input: " . file_get_contents('php://input'));
include(__DIR__ . "/config/db.php");

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    // Get query from multiple sources
    $query = '';
    
    // Try GET first
    if (isset($_GET['query']) && !empty($_GET['query'])) {
        $query = trim($_GET['query']);
        error_log("✅ Got query from GET: '" . $query . "'");
    }
    // Try POST
    elseif (isset($_POST['query']) && !empty($_POST['query'])) {
        $query = trim($_POST['query']);
        error_log("✅ Got query from POST: '" . $query . "'");
    }
    // Try JSON input
    else {
        $input = json_decode(file_get_contents('php://input'), true);
        if (isset($input['query']) && !empty($input['query'])) {
            $query = trim($input['query']);
            error_log("✅ Got query from JSON: '" . $query . "'");
        }
    }
    
    if (empty($query)) {
        error_log("❌ No query received");
        echo json_encode([
            'status' => 'fail',
            'message' => 'Search query is required',
            'debug' => [
                'get' => $_GET,
                'post' => $_POST,
                'method' => $_SERVER['REQUEST_METHOD']
            ]
        ]);
        exit;
    }
    
    if (strlen($query) < 2) {
        echo json_encode([
            'status' => 'fail',
            'message' => 'Search query must be at least 2 characters'
        ]);
        exit;
    }
    
    error_log("🔍 Searching for: '" . $query . "'");
    
    // Search users by username, first name, or last name
    $searchTerm = '%' . $query . '%';
    $sql = "SELECT id, first_name, last_name, username, email, bio, profile_pic 
            FROM users 
            WHERE username LIKE ? 
               OR first_name LIKE ? 
               OR last_name LIKE ? 
               OR CONCAT(first_name, ' ', last_name) LIKE ?
            ORDER BY 
                CASE 
                    WHEN username = ? THEN 1
                    WHEN username LIKE ? THEN 2
                    WHEN first_name LIKE ? OR last_name LIKE ? THEN 3
                    ELSE 4
                END,
                username ASC
            LIMIT 20";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    
    $exactMatch = $query;
    $startsWith = $query . '%';
    
    $stmt->bind_param("ssssssss", 
        $searchTerm, $searchTerm, $searchTerm, $searchTerm,
        $exactMatch, $startsWith, $searchTerm, $searchTerm
    );
    
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }
    
    $result = $stmt->get_result();
    
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => intval($row['id']),
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'username' => $row['username'],
            'email' => $row['email'],
            'bio' => $row['bio'] ?: '',
            'profile_pic' => $row['profile_pic'] ?: 'default_profile.jpg'
        ];
    }
    
    error_log("✅ Found " . count($users) . " users for query: '" . $query . "'");
    
    echo json_encode([
        'status' => 'success',
        'users' => $users,
        'count' => count($users),
        'query' => $query
    ]);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("❌ Search error: " . $e->getMessage());
    echo json_encode([
        'status' => 'fail',
        'message' => 'Search error: ' . $e->getMessage()
    ]);
}
?>