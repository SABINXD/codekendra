<?php
require_once 'function.php';

if (isset($_GET['sendmessage'])) {
    if (sendMessage($_POST['user_id'], $_POST['msg'])) {
        $response['status'] = true;
    } else {
        $response['status'] = false;
    }
    echo json_encode($response);
}


$response = array();
// for follow user ajax
if (isset($_GET['follow'])) {
    $user_id = $_POST['user_id'];


    if (followUser($user_id)) {
        global $db;
        // if(false){
        $response['status'] = true;
    } else {
        $response['status'] = false;
    }
    echo json_encode($response);
}
//for unfollow user
if (isset($_GET['unfollow'])) {
    $user_id = $_POST['user_id'];


    if (unfollowUser($user_id)) {
        global $db;
        // if(false){
        $response['status'] = true;
    } else {
        $response['status'] = false;
    }
    echo json_encode($response);
}
// for liking the post 
if (isset($_GET['like'])) {
    $post_id = $_POST['post_id'];
    if (!checkLiked($post_id)) {
        if (like($post_id)) {


            $response['status'] = true;
        } else {
            $response['status'] = false;
        }
        echo json_encode($response);
    }
}



// for unliking the post 
if (isset($_GET['unlike'])) {
    $post_id = $_POST['post_id'];
    if (checkLiked($post_id)) {
        if (unLike($post_id)) {


            $response['status'] = true;
        } else {
            $response['status'] = false;
        }
        echo json_encode($response);
    }
}
// live like count using jax 
if (isset($_GET['get_like_count'])) {
    $post_id = $_POST['post_id'];
    $like_count = getLikesCount($post_id);
    $response = array('like_count' => count($like_count));
    echo json_encode($response);
}

// for commenting in any post 

if (isset($_GET['addcomment'])) {
    $post_id = $_POST['post_id'];
    $comment = $_POST['comment'];

    if (addComment($post_id, $comment)) {
        $cuser = getUser($_SESSION['userdata']['id']);

        $response['status'] = true;
        $response['comment'] = '<div class="flex items-start gap-3 p-3 border-b border-gray-200">
        <div class="flex-shrink-0">
            <img src="assets/img/profile/' . htmlspecialchars($cuser['profile_pic']) . '" alt="" class="w-10 h-10 rounded-full border border-gray-300 object-cover">
        </div>
        <div class="flex flex-col">
            <h6 class="text-sm font-semibold text-gray-700 mb-1">
                <a href="?u=' . htmlspecialchars($cuser['username']) . '" class="hover:underline">@' . htmlspecialchars($cuser['username']) . '</a> - ' . htmlspecialchars($comment) . '
            </h6>
            <p class="text-xs text-gray-400">(just now)</p>
        </div>
    </div>';
    
    } else {
        $response['status'] = false;
    }

    echo json_encode($response);
}


// count the comment using ajax
if (isset($_GET['get_comment_count'])) {
    $post_id = $_POST['post_id'];
    $comment_count = count(getComments($post_id)); // Get the actual comment count
    $response = array('comment_count' => $comment_count);
    echo json_encode($response);
}
//unblock user using ajax
if (isset($_GET['unblock'])) {
    $user_id = $_POST['user_id'];
    if (unblockUser($user_id)) {
        $response['status'] = true;
    } else {
        $response['status'] = false;
    }

    echo json_encode($response);
}
// searching user
if (isset($_GET['search'])) {
    $keyword = $_POST['keyword'];
    $data = searchUser($keyword);
    $users = "";
    if (count($data) > 0) {
        $response['status'] = true;

        foreach ($data as $fuser) {
            $fbtn = ''; // You can fill follow/unfollow button logic here with Tailwind classes if needed

            $users .= '
            <div class="flex justify-between items-center p-3 bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition duration-200 mb-2">
                <!-- Left Side: Profile -->
                <div class="flex items-center space-x-3">
                    <img src="assets/img/profile/' . htmlspecialchars($fuser['profile_pic']) . '" alt="Profile"
                         class="h-10 w-10 rounded-full border border-gray-300 object-cover" />
        
                    <div class="leading-tight">
                        <a href="?u=' . htmlspecialchars($fuser['username']) . '" class="text-gray-900 font-semibold text-sm hover:underline">
                            ' . htmlspecialchars($fuser['first_name']) . ' ' . htmlspecialchars($fuser['last_name']) . '
                        </a>
                        <p class="text-gray-500 text-xs">@' . htmlspecialchars($fuser['username']) . '</p>
                    </div>
                </div>
    
                <!-- Right Side: Follow Button -->
                <div class="flex items-center space-x-2">
                    ' . $fbtn . '
                </div>
            </div>';
        }

        $response['users'] = $users;
    } else {
        $response['status'] = false;
    }


    echo json_encode($response);
}
//notification ajax reuqest 

if (isset($_GET['notread'])) {



    if (setNotificationStatusAsRead()) {
        $response['status'] = true;
    } else {
        $response['status'] = false;
    }

    echo json_encode($response);
}
// to chek messages
if (isset($_GET['getmessages'])) {
    $chats = getAllMessages();
    // echo "<pre>";
    // print_r($chats);
    $chatlist = " ";
    foreach ($chats as $chat) {
        $ch_user = getUser($chat['user_id']);

        $seen = false;



        if ($chat['messages'][0]['read_status'] == 0 && $chat['messages'][0]['from_user_id'] == $_SESSION['userdata']['id']) {
            $seen = true;
        }





        $chatlist .= '
                    <div class="flex justify-between items-center border-b border-gray-200 p-3 cursor-pointer hover:bg-gray-100 chatlist_item" data-bs-toggle="modal" data-bs-target="#chatbox" onclick="popchat(' . $chat['user_id'] . ')">
                        <div class="flex items-center space-x-3">
                            <img src="assets/img/profile/' . htmlspecialchars($ch_user['profile_pic']) . '" alt="" class="w-10 h-10 rounded-full border border-gray-300 object-cover">
                            <div>
                                <a href="?u=' . htmlspecialchars($ch_user['username']) . '" class="text-gray-900 font-semibold text-sm hover:underline">
                                    ' . htmlspecialchars($ch_user['first_name'] . ' ' . $ch_user['last_name']) . '
                                </a>
                                <p class="text-gray-600 text-xs truncate max-w-xs">' . htmlspecialchars($chat['messages'][0]['message']) . '</p>
                                <time class="text-gray-400 text-xs">' . gettime($chat['messages'][0]['created_at']) . '</time>
                            </div>
                        </div>
                        <div>
                            <span class="inline-block w-3 h-3 rounded-full bg-blue-600 ' . ($seen ? 'hidden' : '') . '"></span>
                        </div>
                    </div>';
    }
    // echo json_encode(['chatlist' => $chatlist]);
    $json['chatlist'] = $chatlist;
    if (isset($_POST['chatter_id']) && $_POST['chatter_id'] != 0) {
        $messages = getMessages($_POST['chatter_id']);
        $chatmsg = "";
        if (checkBS($_POST['chatter_id'])) {
            $json['blocked'] = true;
        } else {
            $json['blocked'] = false;
        }
        foreach ($messages as $cm) {
            if ($cm['from_user_id'] == $_SESSION['userdata']['id']) {
                $class1 = 'self-end bg-blue-600 text-white';
                $class2 = 'text-white';
            } else {
                $class1 = 'self-start bg-gray-100 text-gray-800';
                $class2 = 'text-gray-500';
            }
            $chatmsg .= '<div class="max-w-xs px-4 py-2 rounded-lg shadow mb-2 ' . $class1 . '">
            ' . htmlspecialchars($cm['message']) . '<br>
            <span class="text-xs ' . $class2 . '">' . gettime($cm['created_at']) . '</span>
        </div>';
        }

        $json['chat']['msgs'] = $chatmsg;
        $json['chat']['userdata'] = getUser($_POST['chatter_id']);
    } else {
        $json['chat']['msgs'] = '<div class="spinner-border" role="status">
  <span class="sr-only"></span>
</div>';
    }
    $json['newmsgcount'] = newMsgCount();
    echo json_encode($json);
}
