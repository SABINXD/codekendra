<?php
global $profile;
global $profile_post;
global $user;
?>
<div>
    <link rel="stylesheet" href="./assets/css/profile.css">
    <div class="profile-container">
        <div class="profile-header">
            <div class="profile-picture-container">
                <img src="assets/img/profile/<?= $profile['profile_pic'] ?>" alt="Profile picture"
                    class="profile-picture">
            </div>
            <div class="profile-info">
                <div class="profile-username-row">
                    <h2 style="font-weight:500; text-transform:capitalize;" class="profile-username">
                        <?= $profile['first_name'] ?> <?= $profile['last_name'] ?></h2>
                    <div class="profile-actions">
                        <?php


                        if ($user['id'] != $profile['id']  &&   (!checkBS($profile['id']))) {
                        ?>
                            <?php

                            if (checkFollowed($profile['id'])) {
                            ?>
                                <button style="background:transparent; color:black; font-weight:bold; border:2px solid black; " class="profile-edit-btn unfollowbtn" data-user-id='<?= $profile['id'] ?>'>UnFollow</button>

                            <?php
                            } else {


                            ?>
                                <button class="profile-edit-btn followbtn" data-user-id='<?= $profile['id'] ?>'>Follow</button>

                        <?php
                            }
                        }



                        ?>
                        <?php
                        if ($user['id'] != $profile['id']  &&   (!checkBS($profile['id']))) {
                        ?>

                            <span class="" style="font-size:xx-large" type="button" id="dropdownMenuButton1"
                                data-bs-toggle="dropdown" aria-expanded="false"><i class="fa-solid fa-ellipsis"></i></span>
                            <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
                                <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#chatbox" onclick="popchat(<?= $profile['id'] ?>)"><i class="fa-solid fa-comment"></i> Message</a></li>
                                <li><a class="dropdown-item " href="assets/php/actions.php?block=<?= $profile['id'] ?>&username=<?= $profile['username'] ?>"><i class="fa-solid fa-user-ninja"></i>Block</a></li>


                            </ul>


                        <?php
                        }



                        ?>

                    </div>
                </div>
                <div class="profile-stats">
                    <span class="profile-stat-item"><span class="profile-stat-number"><?= count($profile_post) ?></span> posts</span>
                    <span style="cursor: pointer;" data-bs-toggle="modal" data-bs-target="#follower_list" class="profile-stat-item"><span class="profile-stat-number"><?= count($profile['followers']) ?></span> followers</span>
                    <span style="cursor: pointer;" data-bs-toggle="modal" data-bs-target="#following_list" class="profile-stat-item"><span class="profile-stat-number"><?= count($profile['following']) ?></span> following</span>
                </div>
                <div class="profile-bio">
                    <div>@<?= $profile['username'] ?></div>
                </div>
                <?php
                if (checkBlockStatus($user['id'], $profile['id'])) {
                ?>
                    <button class="btn btn-sm btn-danger unblockbtn" data-user-id='<?= $profile['id'] ?>'>Unblock</button>
                <?php
                }
                ?>
            </div>
        </div>

        <nav class="profile-nav">
            <h2 class="profile-nav-item">POSTS</h2>
        </nav>

        <!-- Post Image Grid -->
        <div class="post-image-grid container mt-4">
            <div class="row g-3">
                <?php
                if (checkBS($profile['id'])) {
                    $profile_post = array();

                ?>
                    <div class="alert alert-secondary text-center" role="alert">
                        <i class="bi bi-x-octagon-fill"></i> You are not allowed to see posts !
                    </div>
                    <?php

                } else if (count($profile_post) < 1 && $user['id'] == $profile['id']) {
                    // Empty state when there are no posts
                    echo '<div class="profile-empty-state">
            <h2 class="profile-empty-title">Share Photos</h2>
            <p class="profile-empty-text">When you share photos, they will appear on your profile.</p>
            <label style="cursor: pointer;" data-bs-toggle="modal" data-bs-target="#addpost" class="profile-share-btn">Share your first photo</label>
          </div>';
                }else if(count($profile_post) < 1){
                    ?>
                    <div class="alert alert-secondary text-center" role="alert">
                        <i class="bi bi-x-octagon-fill"></i> The user has no post !
                    </div>
                    <?php
                } else {
                    // Loop through all posts
                    foreach ($profile_post as $post) {
                        $likes = getLikesCount($post['id']);
                        $comments = getComments($post['id']);
                        // Generate a unique ID for each modal
                    ?>
                        <div class="col-12 col-md-4">
                            <div class="rounded overflow-hidden" style="aspect-ratio: 16/9;">
                                <!-- Post Image -->
                                <img class="w-100 h-100 object-fit-cover" src="assets/img/posts/<?= $post['post_img'] ?>"
                                    data-bs-toggle="modal" data-bs-target="#postview<?= $post['id'] ?>" alt="Post Image">

                                <!-- Modal for Viewing Post -->
                                <!-- Modal for post popup  -->

                                <div class="modal fade" id="postview<?= $post['id'] ?>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                    <div class="modal-dialog modal-xl modal-dialog-centered">
                                        <div class="modal-content">

                                            <div class="modal-body d-md-flex p-0">
                                                <div class="col-md-8 col-sm-12">
                                                    <img src="assets/img/posts/<?= $post['post_img'] ?>" style="max-height:90vh" class="w-100 overflow:hidden">
                                                </div>



                                                <div class="col-md-4 col-sm-12 d-flex flex-column">
                                                    <div class="d-flex align-items-center p-2 border-bottom">
                                                        <div><img src="assets/img/profile/<?= $profile['profile_pic'] ?>" alt="" height="50" width="50" class="rounded-circle border">
                                                        </div>
                                                        <div>&nbsp;&nbsp;&nbsp;</div>
                                                        <div class="d-flex flex-column justify-content-start">
                                                            <h6 style="margin: 0px;"><?= $profile['first_name'] ?> <?= $profile['last_name'] ?></h6>
                                                            <p style="margin:0px;" class="text-muted">@<?= $profile['username'] ?></p>
                                                        </div>
                                                        <div class="d-flex flex-column align-items-end flex-fill">
                                                            <div class=""></div>
                                                            <div class="dropdown">
                                                                <span style="font-weight: 500; cursor:pointer;" class="<?= count($likes) < 1 ? 'disabled' : '' ?>" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
                                                                    <?= count($likes) ?> likes
                                                                </span>
                                                                <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
                                                                    <?php
                                                                    foreach ($likes as $like) {
                                                                        $lu = getUser($like['user_id']);
                                                                    ?>
                                                                        <li><a class="dropdown-item" href="?u=<?= $lu['username'] ?>"><?= $lu['first_name'] . ' ' . $lu['last_name'] ?> (@<?= $lu['username'] ?>)</a></li>

                                                                    <?php
                                                                    }
                                                                    ?>

                                                                </ul>
                                                            </div>
                                                            <div style="font-size:small" class="text-muted">Posted <?= show_time($profile['created_at']) ?> </div>

                                                        </div>
                                                    </div>


                                                    <div class="flex-fill align-self-stretch overflow-auto" id="comment-section<?= $post['id'] ?>" style="height: 100px;">

                                                        <?php
                                                        if (count($comments) < 1) {
                                                        ?>
                                                            <p class="p-3 text-center my-2 nce">no comments</p>
                                                        <?php
                                                        }
                                                        foreach ($comments as $comment) {
                                                            $cuser = getUser($comment['user_id']);
                                                        ?>
                                                            <div class="d-flex align-items-center p-2">
                                                                <div><img src="assets/img/profile/<?= $cuser['profile_pic'] ?>" alt="" height="40" width="40" class="rounded-circle border">
                                                                </div>
                                                                <div>&nbsp;&nbsp;&nbsp;</div>
                                                                <div class="d-flex flex-column justify-content-start align-items-start">
                                                                    <h6 style="margin: 0px;"><a href="?u=<?= $cuser['username'] ?>" class="text-decoration-none text-dark text-small text-muted">@<?= $cuser['username'] ?></a> - <?= $comment['comment'] ?></h6>
                                                                    <p style="margin:0px;" class="text-muted">(<?= show_time($comment['created_at']) ?>)</p>
                                                                </div>
                                                            </div>

                                                        <?php
                                                        }
                                                        ?>






                                                    </div>
                                                    <div class="input-group p-2 border-top">
                                                        <input type="text" class="form-control rounded-0 border-0 comment-input" placeholder="say something.."
                                                            aria-label="Recipient's username" aria-describedby="button-addon2">
                                                        <button class="btn btn-outline-primary rounded-0 border-0 add-comment" data-cs="comment-section<?= $post['id'] ?>" data-post-id="<?= $post['id'] ?>" type="button"
                                                            id="button-addon2">Post</button>
                                                    </div>
                                                </div>



                                            </div>

                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>
                <?php
                    }
                }

                ?>


            </div>
        </div>
    </div>
    <!-- modal for the follower list  -->
    <div class="modal fade" id="follower_list" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Follower</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <?php


                    foreach ($profile['followers'] as $f) {
                        $fuser = getUser($f['follower_id']);
                        $fbtn = false;

                        if (checkFollowed($f['follower_id'])) {
                            $fbtn = '<button style="background-color:red;" class="friends-join-btn unfollowbtn" data-user-id=' . $fuser['id'] . ' >UnFollow <i class="fa-sharp-duotone fa-regular fa-plus"></i></button>';
                        } else if ($user['id'] == $f['follower_id']) {
                            $fbtn = " ";
                        } else {
                            $fbtn = '<button class="friends-join-btn followbtn" data-user-id=' . $fuser['id'] . ' >Follow <i class="fa-sharp-duotone fa-regular fa-plus"></i></button>';
                        }

                    ?>
                        <div class="friends-suggestion-item">
                            <img src="assets/img/profile/<?= $fuser['profile_pic'] ?>" class="friends-suggestion-pic">
                            <div class="friends-suggestion-info">
                                <a class="friends-name" href="?u=<?= $fuser['username'] ?>">
                                    <p class="friends-name"><?= $fuser['first_name'] ?> <?= $fuser['last_name'] ?></p>
                                </a>
                                <p class="friends-username">@<?= $fuser['username'] ?></p>
                            </div>
                            <?= $fbtn ?>

                        </div>



                    <?php
                    }
                    ?>
                </div>

            </div>
        </div>

    </div>
    <!-- modal for the following list  -->
    <div class="modal fade" id="following_list" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Following</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <?php


                    foreach ($profile['following'] as $f) {
                        $fuser = getUser($f['user_id']);
                        $fbtn = false;

                        if (checkFollowed($f['user_id'])) {
                            $fbtn = '<button style="background-color:red;" class="friends-join-btn unfollowbtn" data-user-id=' . $fuser['id'] . ' >UnFollow <i class="fa-sharp-duotone fa-regular fa-plus"></i></button>';
                        } else if ($user['id'] == $f['user_id']) {
                            $fbtn = " ";
                        } else {
                            $fbtn = '<button class="friends-join-btn followbtn" data-user-id=' . $fuser['id'] . ' >Follow <i class="fa-sharp-duotone fa-regular fa-plus"></i></button>';
                        }

                    ?>
                        <div class="friends-suggestion-item">
                            <img src="assets/img/profile/<?= $fuser['profile_pic'] ?>" class="friends-suggestion-pic">
                            <div class="friends-suggestion-info">
                                <a class="friends-name" href="?u=<?= $fuser['username'] ?>">
                                    <p class="friends-name"><?= $fuser['first_name'] ?> <?= $fuser['last_name'] ?></p>
                                </a>
                                <p class="friends-username">@<?= $fuser['username'] ?></p>
                            </div>
                            <?= $fbtn ?>

                        </div>




                    <?php
                    }
                    ?>
                </div>

            </div>
        </div>

    </div>
</div>