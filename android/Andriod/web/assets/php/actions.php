<?php
require_once 'function.php';
require_once 'send_code.php';




//php for signup validating and help user to signup in CodeKendra
if (isset($_GET['signup'])) {
    $response = validateSignupForm($_POST);
    if ($response['status']) {
        if (createUser($_POST)) {
            header('Location: ../../?login');
        } else {
            echo "<script>alert('Something is wrong')</script>";
        }
    } else {
        $_SESSION['error'] = $response;
        $_SESSION['formdata'] = $_POST;
        header("location:../../?signup");
    }
}

//php for login validation for user
if (isset($_GET['login'])) {

    $response = validateLoginForm($_POST);
    if ($response['status']) {
        $_SESSION['Auth'] = true;
        $_SESSION['userdata'] = $response['user'];
        if ($response['user']['ac_status'] == 0) {
            $_SESSION['code'] = $code = rand(111111, 999999);
            sendCode($response['user']['email'], 'Verify Your Email', $code);
        }
        header("location:../../");
    } else {
        $_SESSION['error'] = $response;
        $_SESSION['formdata'] = $_POST;
        header("location:../../?login");
    }
}
if (isset($_GET['resend_code'])) {
    $_SESSION['code'] = $code = rand(111111, 999999);
    sendCode($_SESSION['userdata']['email'], 'Verify Your Email', $code);
    header("location:../../?codesent");
}
if (isset($_GET['verify_email'])) {
    $user_code = $_POST['code'];
    $code = $_SESSION['code'];
    if ($user_code == $code) {
        if (verifyEmail($_SESSION['userdata']['email'])) {
            header("location:../../");
        } else {
            echo "<script>alert('Something is wrong')</script>";
        }
    } else {
        $response['msg'] = 'Incorrect Verification Code';
        if (!$_POST['code']) {
            $response['msg'] = 'Enter 6 digit Code';
        }
        $response['field'] = 'email_verify';
        $_SESSION['error'] = $response;
        header("location:../../");
    }
}
// for forgotting password
if (isset($_GET['forgotpassword'])) {
    if (!$_POST['email']) {
        $response['msg'] = "Please Enter Email Id";
        $response['field'] = 'email';
        $_SESSION['error'] = $response;
        header("location:../../?forgotpassword");
    } else if (!isEmailRegistered($_POST['email'])) {
        $response['msg'] = "Email id is not  registered";
        $response['field'] = 'email';
        $_SESSION['error'] = $response;
        header("location:../../?forgotpassword");
    } else {
        $_SESSION['forgot_email'] = $_POST['email'];
        $_SESSION['forgot_email'] = $_POST['email'];
        $_SESSION['forgot_code'] = $code = rand(111111, 999999);
        sendCode($_POST['email'], 'forgot Your Password', $code);
        header("location:../../?forgotpassword&codesent");
    }
}

// for user forgot code verify 
if (isset($_GET['verifycode'])) {
    $user_code = $_POST['code'];
    $code = $_SESSION['forgot_code'];
    if ($user_code == $code) {
        $_SESSION['auth_temp'] = true;
        header("location:../../?forgotpassword");
    } else {
        $response['msg'] = 'Incorrect Verification Code';
        if (!$_POST['code']) {
            $response['msg'] = 'Enter 6 digit Code';
        }
        $response['field'] = 'email_verify';
        $_SESSION['error'] = $response;
        header("location:../../?forgotpassword");
    }
}
//now function to eset password
if (isset($_GET['changepassword'])) {
    if (!$_POST['password']) {
        $response['msg'] = "enter your new password";
        $response['field'] = 'password';
        $_SESSION['error'] = $response;
        header('location:../../?forgotpassword');
    } else {
        resetPassword($_SESSION['forgot_email'], $_POST['password']);
        session_destroy();
        header('location:../../?reseted');
    }
}

//edit profile 
if (isset($_GET['updateprofile'])) {

    $response = validateUpdateForm($_POST, $_FILES['profile_pic']);

    if ($response['status']) {

        if (updateProfile($_POST, $_FILES['profile_pic'])) {
            header("location:../../?editprofile&success");
        } else {
            echo "something is wrong";
        }
    } else {
        $_SESSION['error'] = $response;
        header("location:../../?editprofile");
    }
}
// managing add post 
if (isset($_GET['addpost'])) {
    $response = validatePostImage($_FILES['post_img']);

    if ($response['status']) {
        if (createPost($_POST, $_FILES['post_img'])) {
            header("location:../../?new_post_added");
        } else {
            echo 'something went wrong';
        }
    } else {
        $_SESSION['error'] = $response;
        header("location:../../");
    }
}
//ajax for delete post
if (isset($_GET['deletepost'])) {
    $post_id = $_GET['deletepost'];
    if (deletePost($post_id)) {
        header("location:{$_SERVER['HTTP_REFERER']}");
    } else {
        echo "something went wrong";
    }
}
// blocking user
if (isset($_GET['block'])) {
    $user_id = $_GET['block'];
    $user = $_GET['username'];
    if (blockUser($user_id)) {
        header("location:../../?u=$user");
    } else {
        echo "something went wrong";
    }
}
