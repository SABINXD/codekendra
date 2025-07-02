<?php
require_once 'config.php';
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASS, DB_NAME) or die("database is not connected");

// Function to show pages 
function showPage($page, $data = "")
{
    $safePage = basename($page); // Prevent directory traversal
    include("./assets/pages/$safePage.php");
}

// Function to show error 
function showError($field)
{
    if (isset($_SESSION['error'])) {
        $error = $_SESSION['error'];
        if (isset($error['field']) && $field == $error['field']) {
?>
            <div class="alert alert-danger my-2" role="alert">
                <?= $error['msg'] ?>
            </div>
<?php
        }
    }
}

// Function to show previous form data 
function showFormData($field)
{
    if (isset($_SESSION['formdata'])) {
        $formdata = $_SESSION['formdata'];
        return $formdata[$field] ?? null;
    }
}

// for checking dublicate email 
function isEmailRegistered($email)
{
    global $db;
    $query = "SELECT count(*) as row FROM users WHERE email='$email'";
    $run = mysqli_query($db, $query);
    $return_data = mysqli_fetch_assoc($run);
    return $return_data['row'];
}


//for checking dublicate username
function isUsernameRegistered($username)
{
    global $db;
    $query = "SELECT count(*) as row FROM users WHERE username='$username'";
    $run = mysqli_query($db, $query);
    $return_data = mysqli_fetch_assoc($run);
    return $return_data['row'];
}
//for checking  username regsitered by other
function isUsernameRegisteredByOther($username)
{
    global $db;
    $user_id = $_SESSION['userdata']['id'];
    $query = "SELECT count(*) as row FROM users WHERE username='$username' && id!=$user_id";
    $run = mysqli_query($db, $query);
    $return_data = mysqli_fetch_assoc($run);
    return $return_data['row'];
}


// Validating signup form
function validateSignupForm($form_data)
{
    $response = array('status' => true, 'msg' => '');

    if (!isset($form_data['password']) || !$form_data['password']) {
        $response['msg'] = "Password is not provided";
        $response['status'] = false;
        $response['field'] = 'password';
    } else if (strlen($form_data['password']) < 8) {
        $response['msg'] = "Password must be at least 8 characters long";
        $response['status'] = false;
        $response['field'] = 'password';
    } else if (!preg_match('/[!@#$%^&*(),.?":{}|<>]/', $form_data['password'])) {
        $response['msg'] = "Password must include at least one special character (!@#$%^&*(),.?\":{}|<>)";
        $response['status'] = false;
        $response['field'] = 'password';
    }

    if (!isset($form_data['username']) || !$form_data['username']) {
        $response['msg'] = "Username is not provided";
        $response['status'] = false;
        $response['field'] = 'username';
    }

    if (!isset($form_data['email']) || !$form_data['email']) {
        $response['msg'] = "Email is not provided";
        $response['status'] = false;
        $response['field'] = 'email';
    }

    if (!isset($form_data['last_name']) || !$form_data['last_name']) {
        $response['msg'] = "Last name is not provided";
        $response['status'] = false;
        $response['field'] = 'last_name';
    }

    if (!isset($form_data['first_name']) || !$form_data['first_name']) {
        $response['msg'] = "First name is not provided";
        $response['status'] = false;
        $response['field'] = 'first_name';
    }

    if (isEmailRegistered($form_data['email'])) {
        $response['msg'] = "Email is already registered";
        $response['status'] = false;
        $response['field'] = 'email';
    }

    if (isUsernameRegistered($form_data['username'])) {
        $response['msg'] = "Username is already registered";
        $response['status'] = false;
        $response['field'] = 'username';
    }


    return $response;
}


// validationg login in php
function validateLoginForm($form_data)
{
    $response = array();
    $response['status'] = true;
    $blank = false;


    if (!$form_data['password']) {
        $response['msg'] = "Password is not provided";
        $response['status'] = false;
        $response['field'] = 'password';
        $blank = true;
    }
    if (!$form_data['username_email']) {
        $response['msg'] = "Username/email is not provided";
        $response['status'] = false;
        $response['field'] = 'username_email';
        $blank = true;
    }
    if (!$blank && !checkUser($form_data)['status']) {
        $response['msg'] = "Something is incorrect we cannot find you";
        $response['status'] = false;
        $response['field'] = 'checkuser';
    } else {
        $response['user'] = checkUser($form_data)['user'];
    }
    return $response;
}
// fro checking code
function validateVerify($form_data)
{
    $response = array();
    $response['status'] = true;
    $blank = false;


    if (!$form_data['verify_code']) {
        $response['msg'] = "please Enter code";
        $response['status'] = false;
        $response['field'] = 'verify_code';
        $blank = true;
    }

    return $response;
}

// for checking user 
function checkUser($login_data)
{
    global $db;
    $username_email = $login_data['username_email'];
    $password = md5($login_data['password']);
    $query = "SELECT * FROM users WHERE (email='$username_email' || username ='$username_email') && password = '$password'";
    $run = mysqli_query($db, $query);
    $data['user'] = mysqli_fetch_assoc($run) ?? array();
    if (count($data['user']) > 0) {
        $data['status'] = true;
    } else {
        $data['satus'] = false;
    }
    return $data;
}

//for geeting userr data by id
function getUser($user_id)
{
    global $db;

    $query = "SELECT * FROM users WHERE id=$user_id ";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run);
}
//for geting user by username
function getUserByUsername($username)
{
    global $db;

    $query = "SELECT * FROM users WHERE username= '$username'";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run);
}

// for creating a new user 
function createUser($data)
{
    global $db;
    $first_name = mysqli_real_escape_string($db, $data['first_name']);
    $last_name = mysqli_real_escape_string($db, $data['last_name']);
    $gender = $data['gender'];
    $email = mysqli_real_escape_string($db, $data['email']);
    $username = mysqli_real_escape_string($db, $data['username']);
    $password = mysqli_real_escape_string($db, $data['password']);
    $password = md5($password);
    $query = "INSERT INTO users(first_name,last_name,gender,email,username,password)";
    $query .= "VALUES ('$first_name','$last_name',$gender,'$email','$username','$password')";
    return mysqli_query($db, $query);
}
//function to verify user email
function verifyEmail($email)
{
    global $db;
    $query = "UPDATE users SET ac_status=1 WHERE email='$email'";
    return mysqli_query($db, $query);
}
//loging out the user
if (isset($_GET['logout'])) {
    session_destroy();
    header("location:../../");
}
// function to change password
function resetPassword($email, $password)
{
    global $db;
    $password = md5($password);
    $query = "UPDATE users SET password='$password' WHERE email='$email'";
    return mysqli_query($db, $query);
}
//function fir validating update form
//for validating update form
function validateUpdateForm($form_data, $image_data)
{
    $response = array();
    $response['status'] = true;


    if (!$form_data['username']) {
        $response['msg'] = "username is not given";
        $response['status'] = false;
        $response['field'] = 'username';
    }

    if (!$form_data['last_name']) {
        $response['msg'] = "last name is not given";
        $response['status'] = false;
        $response['field'] = 'last_name';
    }
    if (!$form_data['first_name']) {
        $response['msg'] = "first name is not given";
        $response['status'] = false;
        $response['field'] = 'first_name';
    }
    if (isUsernameRegisteredByOther($form_data['username'])) {
        $response['msg'] = $form_data['username'] . " is already registered";
        $response['status'] = false;
        $response['field'] = 'username';
    }

    if ($image_data['name']) {
        $image = basename($image_data['name']);
        $type = strtolower(pathinfo($image, PATHINFO_EXTENSION));
        $size = $image_data['size'] / 1024;

        if ($type != 'jpg' && $type != 'jpeg' && $type != 'png') {
            $response['msg'] = "only jpg,jpeg,png images are allowed";
            $response['status'] = false;
            $response['field'] = 'profile_pic';
        }

        if ($size > 2048) {
            $response['msg'] = "upload image less then 2  mb";
            $response['status'] = false;
            $response['field'] = 'profile_pic';
        }
    }
    return $response;
}

// function for updating profile
function updateProfile($data, $imagedata)
{
    global $db;
    $first_name = mysqli_real_escape_string($db, $data['first_name']);
    $last_name = mysqli_real_escape_string($db, $data['last_name']);
    $username = mysqli_real_escape_string($db, $data['username']);
    $password = mysqli_real_escape_string($db, $data['password']);

    if (!$data['password']) {
        $password = $_SESSION['userdata']['password'];
    } else {
        $password = md5($password);
        $_SESSION['userdata']['password'] = $password;
    }

    $profile_pic = "";
    if ($imagedata['name']) {
        $image_name = time() . basename($imagedata['name']);
        $image_dir = "../img/profile/$image_name";
        move_uploaded_file($imagedata['tmp_name'], $image_dir);
        $profile_pic = ", profile_pic='$image_name'";
    }



    $query = "UPDATE users SET first_name = '$first_name', last_name='$last_name', username='$username', password='$password' $profile_pic WHERE id=" . $_SESSION['userdata']['id'];
    $result = mysqli_query($db, $query);
    if (!$result) {
        die("Database error: " . mysqli_error($db));
    }
    return $result;
}
//function to validate post uploaded picture form
function validatePostImage($image_data)
{
    $response = array();
    $response['status'] = true;

    if (!$image_data['name']) {
        $response['msg'] = "None Image is Selected";
        $response['status'] = false;
        $response['field'] = 'post_img';
    }


    if ($image_data['name']) {
        $image = basename($image_data['name']);
        $type = strtolower(pathinfo($image, PATHINFO_EXTENSION));
        $size = $image_data['size'] / 1024;

        if ($type != 'jpg' && $type != 'jpeg' && $type != 'png') {
            $response['msg'] = "only jpg,jpeg,png images are allowed";
            $response['status'] = false;
            $response['field'] = 'post_img';
        }

        if ($size > 2048) {
            $response['msg'] = "upload image less then 2  mb";
            $response['status'] = false;
            $response['field'] = 'post_img';
        }
    }
    return $response;
}
//function to create post
function createPost($text, $image)
{
    global $db;
    $post_text = mysqli_real_escape_string($db, $text['post_text']);
    $user_id = $_SESSION['userdata']['id'];

    $image_name = time() . basename($image['name']);
    $image_dir = "../img/posts/$image_name";
    move_uploaded_file($image['tmp_name'], $image_dir);


    $query = "INSERT INTO posts(user_id,post_text,post_img)";
    $query .= "VALUES ($user_id,'$post_text','$image_name')";

    return mysqli_query($db, $query);
}
// for geeting post dynamically

function getPost()
{
    global $db;
    $query = "SELECT 
                 users.id as uid, 
                 posts.id, 
                 posts.user_id, 
                 posts.post_img, 
                 posts.post_text, 
                 posts.created_at, 
                 users.first_name, 
                 users.last_name, 
                 users.username, 
                 users.profile_pic 
              FROM posts 
              JOIN users ON users.id = posts.user_id 
              ORDER BY RAND()"; // Use RAND() to fetch posts in random order

    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, MYSQLI_ASSOC); // Corrected to MYSQLI_ASSOC for associative array
}

//get post by id 
function getPostById($user_id)
{
    global $db;
    $query = "SELECT * FROM posts WHERE user_id =$user_id ";

    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}
//get poster id
//for getting post
function getPosterId($post_id)
{
    global $db;
    $query = "SELECT user_id FROM posts WHERE id=$post_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['user_id'];
}

// for getting user for follow sugesstion
function getFollowSuggestions()
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "SELECT * FROM users WHERE id != $current_user LIMIT 7";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}

//function filter sugesstion list
function filterFollowSuggestion()
{
    $list = getFollowSuggestions();
    $filter_list  = array();
    foreach ($list as $user) {
        if (!checkFollowed($user['id'])) {
            $filter_list[] = $user;
        }
    }

    return $filter_list;
}

// for checkinh the user  follwed by logined user 
function checkFollowed($user_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "SELECT count(*) as row FROM follow_list WHERE follower_id=$current_user && user_id=$user_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['row'];
}
//function for follow user 
function followUser($user_id)
{
    global $db;
    $cu = getUser($_SESSION['userdata']['id']);

    $current_user = $_SESSION['userdata']['id'];
    $query = "INSERT INTO follow_list(follower_id,user_id) VALUES ($current_user,$user_id)";
    createNotification($cu['id'], $user_id, "started following you !");
    return mysqli_query($db, $query);
}
//function for unfollow user
function unfollowUser($user_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "DELETE FROM  follow_list WHERE follower_id=$current_user && user_id=$user_id";
    return mysqli_query($db, $query);
}
//For getting follwer count
function getFollowersCount($user_id)
{
    global $db;
    $query = "SELECT * FROM follow_list WHERE user_id=$user_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}

// get follwing list
function getFollowingCount($user_id)
{
    global $db;
    $query = "SELECT * FROM follow_list WHERE follower_id=$user_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}
//function to check liked or not

function checkLiked($post_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "SELECT count(*) as row FROM likes WHERE user_id=$current_user && post_id=$post_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['row'];
}

//function for like post 
function like($post_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "INSERT INTO likes(post_id,user_id) VALUES ($post_id,$current_user)";
    $poster_id = getPosterId($post_id);
    if ($poster_id != $current_user) {
        createNotification($current_user, $poster_id, "liked your post !", $post_id);
    }

    return mysqli_query($db, $query);
}
//function for unlike post 
function unLike($post_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "DELETE FROM  likes WHERE user_id=$current_user && post_id=$post_id ";
    $poster_id = getPosterId($post_id);
    if ($poster_id != $current_user) {
        createNotification($current_user, $poster_id, "unliked your post !", $post_id);
    }
    return mysqli_query($db, $query);
}
//For getting like count
function getLikesCount($post_id)
{
    global $db;
    $query = "SELECT * FROM likes WHERE post_id=$post_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}

// function for  add comment in the post
function addComment($post_id, $comment)
{
    global $db;
    $comment = mysqli_real_escape_string($db, $comment);

    $current_user = $_SESSION['userdata']['id'];
    $query = "INSERT INTO comments(post_id,user_id,comment) VALUES ($post_id,$current_user,'$comment')";
    $poster_id = getPosterId($post_id);

    if ($poster_id != $current_user) {
        createNotification($current_user, $poster_id, "commented on your post", $post_id);
    }
    return mysqli_query($db, $query);
}
//For getting comment  count
function getComments($post_id)
{
    global $db;
    $query = "SELECT * FROM comments WHERE post_id=$post_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}
// function to show time
function show_time($time)
{
    return '<time style="font-size:small" class="timeago text-muted text-small" datetime="' . $time . '"></time>';
}
//function to delete post
function deletePost($post_id)
{
    global $db;
    $user_id = $_SESSION['userdata']['id'];
    $dellike = "DELETE FROM likes WHERE post_id=$post_id && user_id=$user_id";
    mysqli_query($db, $dellike);
    $delcom = "DELETE FROM comments WHERE post_id=$post_id && user_id=$user_id";
    mysqli_query($db, $delcom);
    $not = "UPDATE notifications SET read_status=2 WHERE post_id=$post_id && to_user_id=$user_id";
    mysqli_query($db, $not);



    $query = "DELETE FROM posts WHERE id=$post_id";
    return mysqli_query($db, $query);
}
//function to get time

function gettime($date){
    return date('H:i - (F jS, Y )', strtotime($date));
}



// function for block user
function blockUser($blocked_user_id)
{
    global $db;
    $cu = getUser($_SESSION['userdata']['id']);
    $current_user = $_SESSION['userdata']['id'];
    $query = "INSERT INTO block_list(user_id,blocked_user_id) VALUES($current_user,$blocked_user_id)";


    $query2 = "DELETE FROM follow_list WHERE follower_id=$current_user && user_id=$blocked_user_id";
    mysqli_query($db, $query2);
    $query3 = "DELETE FROM follow_list WHERE follower_id=$blocked_user_id && user_id=$current_user";
    mysqli_query($db, $query3);


    return mysqli_query($db, $query);
}
//check block tatus
function checkBS($user_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "SELECT count(*) as row FROM block_list WHERE (user_id=$current_user && blocked_user_id=$user_id) || (user_id=$user_id && blocked_user_id=$current_user)";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['row'];
}
//check block statu
function checkBlockStatus($current_user, $user_id)
{
    global $db;

    $query = "SELECT count(*) as row FROM block_list WHERE user_id=$current_user && blocked_user_id=$user_id";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['row'];
}
//for unblocking the user
function unblockUser($user_id)
{
    global $db;
    $current_user = $_SESSION['userdata']['id'];
    $query = "DELETE FROM block_list WHERE user_id=$current_user && blocked_user_id=$user_id";
    return mysqli_query($db, $query);
}
//function to filter post
function filterPosts()
{
    global $db;

    // Fetch all posts
    $list = getPost(); // Assuming `getPost()` fetches all posts as an array

    // Initialize the filtered list
    $filter_list = array();

    // Current user ID
    $current_user = $_SESSION['userdata']['id'];

    // Iterate through each post and check block status
    foreach ($list as $post) {
        $post_user_id = $post['user_id']; // Assuming each post has a `user_id` field
        if (!checkBS($post_user_id)) { // Check block status
            $filter_list[] = $post;
        }
    }

    return $filter_list;
}
// function to search user
function searchUser($keyword)
{
    global $db;
    $query = "SELECT * FROM users WHERE username LIKE '%" . $keyword . "%' || (first_name LIKE '%" . $keyword . "%' || last_name LIKE '%" . $keyword . "%') LIMIT 5";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}
// functioins to created noptification
function createNotification($from_user_id, $to_user_id, $msg, $post_id = 0)
{
    global $db;
    $query = "INSERT INTO notifications(from_user_id,to_user_id,message,post_id) VALUES($from_user_id,$to_user_id,'$msg',$post_id)";
    mysqli_query($db, $query);
}
// functions to get notification
function getNotifications()
{
    $cu_user_id = $_SESSION['userdata']['id'];

    global $db;
    $query = "SELECT * FROM notifications WHERE to_user_id=$cu_user_id ORDER BY id DESC";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_all($run, true);
}

//function
function getUnreadNotificationsCount()
{
    $cu_user_id = $_SESSION['userdata']['id'];

    global $db;
    $query = "SELECT count(*) as row FROM notifications WHERE to_user_id=$cu_user_id && read_status=0 ORDER BY id DESC";
    $run = mysqli_query($db, $query);
    return mysqli_fetch_assoc($run)['row'];
};
//  function to make notification as read 
function setNotificationStatusAsRead()
{
    $cu_user_id = $_SESSION['userdata']['id'];
    global $db;
    $query = "UPDATE notifications SET read_status=1 WHERE to_user_id=$cu_user_id";
    return mysqli_query($db, $query);
}

//function to get id of chat user
function getActiveChatUserId()
{
    global $db;
    $current_user_id = $_SESSION['userdata']['id'];
    $query = "SELECT from_user_id,to_user_id FROM messages WHERE to_user_id=$current_user_id || from_user_id=$current_user_id ORDER BY id DESC";
    $run = mysqli_query($db, $query);
    $data = mysqli_fetch_all($run, true);
    $ids = array();
    foreach ($data as $ch) {
        if ($ch['from_user_id'] != $current_user_id && !in_array($ch['from_user_id'], $ids)) {
            $ids[] = $ch['from_user_id'];
        }
        if ($ch['to_user_id'] != $current_user_id && !in_array($ch['to_user_id'], $ids)) {
            $ids[] = $ch['to_user_id'];
        }
    }
    return $ids;
}

// function to get messages 

function getMessages($user_id){
    global $db;
    $current_user_id = $_SESSION['userdata']['id'];
    $query = "SELECT  * FROM  messages WHERE (to_user_id=$current_user_id && from_user_id=$user_id) || (from_user_id=$current_user_id && to_user_id=$user_id) ORDER BY id DESC";
    $run = mysqli_query($db, $query);
  return mysqli_fetch_all($run, true);


}
//FUNCTIOn to get all messages

function getAllMessages(){
    $active_chat_ids = getActiveChatUserId();
$conversation = array();
    foreach($active_chat_ids as $index=>$id){
        $conversation[$index]['user_id']=$id;
       
        $conversation[$index]['messages']=getMessages($id);
    }
    return $conversation;

}
//function to read message stautus
function updateReadMessageStatus($user_id){
    global $db;
    $current_user_id = $_SESSION['userdata']['id'];
    $query = "UPDATE messages SET read_status=1 WHERE to_user_id=$current_user_id && from_user_id=$user_id";
    return mysqli_query($db,$query);
}
//function to send message
function sendMessage($user_id,$msg){
    global $db;
    $current_user_id = $_SESSION['userdata']['id'];
    $msg = mysqli_real_escape_string($db,$msg);
    $query = "INSERT INTO messages(from_user_id,to_user_id,message) VALUES($current_user_id,$user_id,'$msg')";
    updateReadMessageStatus($user_id);
    return mysqli_query($db,$query);
}

//function to get new msg count
function newMsgCount(){
    global $db;
    $current_user_id = $_SESSION['userdata']['id'];
    $query = "SELECT count(*) as row FROM messages WHERE to_user_id=$current_user_id && read_status=0";
    $run = mysqli_query($db,$query);
    return mysqli_fetch_assoc($run)['row'];
}


?>