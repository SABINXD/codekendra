<?php
require_once('admin_functions.php');
require_once '../../assets/php/send_code.php';

if(isset($_GET['login'])){
    $response = checkAdminUser($_POST);

    if($response['status']){
        $_SESSION['admin_auth'] = $response['user_id'];

        // Set cookie for 15 days (60 * 60 * 24 * 15 seconds)
        setcookie("admin_auth", $response['user_id'], time() + (60 * 60 * 24 * 15), "/");

        header('Location:../');
    } else {
        $_SESSION['error'] = [
            "field" => "useraccess",
            "msg" => "Incorrect email/password",
        ];
        header('Location:../');
    }
}

if(isset($_GET['logout'])){
    session_destroy();
    
    // Expire the cookie
    setcookie("admin_auth", "", time() - 3600, "/");

    header('Location:../');
}

if(isset($_GET['updateprofile'])){
    if(updateAdmin($_POST)){
        $_SESSION['error']=[
            "field"=>"adminprofile",
            "msg"=>"profile update successfully !",
        ];
     header('Location:../?edit_profile');
    }else{
        $_SESSION['error']=[
            "field"=>"adminprofile",
            "msg"=>"something went wrong, try again later",
        ];
     header('Location:../?edit_profile');
    }
}

if(isset($_GET['userlogin']) && isset($_SESSION['admin_auth'])){

  
    $response=loginUserByAdmin($_GET['userlogin']);
    
  
    if($response['status']){
     $_SESSION['Auth'] = true;
     $_SESSION['userdata'] = $response['user'];

     if($response['user']['ac_status']==0){
     $_SESSION['code']=$code = rand(111111,999999);
     sendCode($response['user']['email'],'Verify Your Email',$code);
     }

     header("location:../../");

    }
        
    }
    if(isset($_GET['allposts'])){
       
        header('Location:../?allposts');
     


    }


?>