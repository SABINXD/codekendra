<?php
require_once('admin_functions.php');
require_once '../../assets/php/send_code.php';

if(isset($_GET['verify_user'])){
    $user = getUser($_POST['user_id']);
    if(verifyEmail($user['email'])){
    
        $response['status']=true;

    }else{
        $response['status']=false;
    }

    echo json_encode($response);
}

if(isset($_GET['block_user'])){
   
    if(blockUserByAdmin($_POST['user_id'])){
    
        $response['status']=true;

    }else{
        $response['status']=false;
    }

    echo json_encode($response);
}



if(isset($_GET['unblock_user'])){
   
    if(unblockUserByAdmin($_POST['user_id'])){
    
        $response['status']=true;

    }else{
        $response['status']=false;
    }

    echo json_encode($response);
}
if(isset($_GET['delete_post'])){
    
     if(deletePostByAdmin($_POST['post_id'])){
     
          $response['status']=true;
    
     }else{
          $response['status']=false;
     }
    
     echo json_encode($response);
}
if (isset($_GET['warn_post'])) {
    $userId = getUserByPostId($_POST['post_id']);

    if ($userId !== false) { // Check if getUserByPostId was successful
        if (createNotification(1, $userId, "has warned you for post !", $_POST['post_id'])) {
            $response['status'] = true;
        } else {
            $response['status'] = true; // Notification creation failed
        }
    } else {
        $response['status'] = false; // Getting the user ID failed
    }

    header('Content-Type: application/json'); // Set the content type
    echo json_encode($response);
}