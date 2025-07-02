<?php
require_once 'assets/php/function.php';

if (isset($_GET['newfp'])) {
    unset($_SESSION['auth_temp']);
    unset($_SESSION['forgot_email']);
    unset($_SESSION['forgot_code']);
}

if (isset($_SESSION['Auth'])) {
    $user = getUser($_SESSION['userdata']['id']);
    $posts = filterPosts();
    $follow_sugesstions = filterFollowSuggestion();
}
$pagecount = count($_GET);





//manage pages

if (isset($_SESSION['Auth']) && $user['ac_status'] == 1 && !$pagecount) {
    showPage('header', ["page_title" => 'CodeKendra - Connect  all over the world']);
    showPage('navbar');
    showPage('main');
} else if (isset($_SESSION['Auth']) && $user['ac_status'] == 0 && !$pagecount) {
    showPage('header', ["page_title" => 'verify you email']);
    showPage('verify_email');
} else if (isset($_SESSION['Auth']) && $user['ac_status'] == 2 && !$pagecount) {
    showPage('header', ["page_title" => 'Something is Unavailable']);
    showPage('blocked');
} elseif (isset($_SESSION['Auth']) && isset($_GET['editprofile']) && $user['ac_status'] == 1) {

    showPage('header', ["page_title" => 'Edit your profile deatils']);
    showPage('navbar');
    showPage('edit_profile');
} elseif (isset($_SESSION['Auth']) && isset($_GET['u']) && $user['ac_status'] == 1) {

    $profile = getUserByUsername($_GET['u']);



    if (!$profile) {


        showPage('header', ["page_title" => 'No user found']);
        showPage('navbar');
        showPage('user_not_found');
    } else {
        $profile_post = getPostById($profile['id']);
        $profile['followers'] = getFollowersCount($profile['id']);
        $profile['following'] = getFollowingCount($profile['id']);

        showPage('header', ["page_title" => $profile['first_name'] . ' ' . $profile['last_name'] . ' ' . 'Profile']);
        showPage('navbar');
        showPage('profile');
    }
} elseif (isset($_GET['signup'])) {
    showPage('header', ["page_title" => 'CodeKendra - Signup page CodeKendra']);
    showPage('signup');
} elseif (isset($_GET['login'])) {
    showPage('header', ["page_title" => 'CodeKendra - login page CodeKendra']);
    showPage('login');
} elseif (isset($_GET['forgotpassword'])) {
    showPage('header', ["page_title" => 'forgot Password -CodeKendra']);
    showPage('forgot_password');
} else {
    if (isset($_SESSION['Auth']) && $user['ac_status'] == 1) {
        showPage('header', ['page_title' => 'Home']);
        showPage('navbar');
        showPage('main');
    } elseif (isset($_SESSION['Auth']) && $user['ac_status'] == 0) {

        showPage('header', ['page_title' => 'Verify Your Email']);
        showPage('verify_email');
    } elseif (isset($_SESSION['Auth']) && $user['ac_status'] == 2) {
        showPage('header', ['page_title' => 'Blocked']);
        showPage('blocked');
    } else {
        showPage('header', ['page_title' => 'CodeKendra - Login']);
        showPage('login');
    }
}





showPage('footer');
unset($_SESSION['error']);
unset($_SESSION['formdata']);
