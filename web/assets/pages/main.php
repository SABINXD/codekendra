<!-- home body conent  -->
<?php
global $user;
global $posts;
global $follow_sugesstions;

?>
<div class="main-wrapper">
    <div class="details-user user-list">
        <div class="friends-header">
            <img src="./assets/img/profile/<?= $user['profile_pic'] ?>" alt="Profile picture"
                class="friends-profile-pic">
            <a style="text-decoration: none; color:#273257;" href="?u=<?= $user['username'] ?>">
                <div class="friends-profile-info">
                    <h2><?= $user['first_name'] ?> <?= $user['last_name'] ?></h2>
                    <p style="font-size:12px; font-weight: 500; ">@<?= $user['username'] ?></p>
                </div>
            </a>
        </div>
        <div class="friends-suggestions">
            <h3>You may Know</h3>
            <?php
            foreach ($follow_sugesstions as $suser) {
            ?>
                <div class="friends-suggestion-item">
                    <img src="assets/img/profile/<?= $suser['profile_pic'] ?>" class="friends-suggestion-pic">
                    <div class="friends-suggestion-info">
                        <a class="friends-name" href="?u=<?= $suser['username'] ?>">
                            <p class="friends-name"><?= $suser['first_name'] ?> <?= $suser['last_name'] ?></p>
                        </a>
                        <p class="friends-username">@<?= $suser['username'] ?></p>
                    </div>
                    <button class="friends-join-btn followbtn" data-user-id='<?= $suser['id'] ?>'>Follow <i class="fa-sharp-duotone fa-regular fa-plus"></i></button>
                </div>

            <?php
            }
            if (count($follow_sugesstions) < 1) {
                echo "<h5 style='font-weight:500; font-size:16px;
                padding:10px 3px; text-align:center; border:2px solid #bababa; background-color:#fff;border-radius:20px; '>Currenlty! No user Sugesstion</h5>";
            }

            ?>


        </div>



    </div>
    <div class="main-content-post post-scroll">
        <?php
        showError('post_img');
        foreach ($posts as $post) {
            $likes = getLikesCount($post['id']);
            $comments = getComments($post['id']);
        ?>
            <div class="main-post-box">

                <a style="text-decoration: none; color:#273257;" href="?u=<?= $post['username'] ?>">
                    <div class="post-user-deatils">
                        <div class="post-user-name-deatils">
                            <img src="assets/img/profile/<?= $post['profile_pic'] ?>" alt="Profile user">
                            <h3><?= $post['first_name'] ?> <?= $post['last_name'] ?></h3>
                        </div>
                </a>
                <?php
                if ($post['user_id'] == $user['id']) {
                ?>
                    <div class="post-user-required post-options-require dropdown">
                        <i class="fa-solid fa-ellipsis-vertical" id="option<?= $post['id'] ?>" data-bs-toggle="dropdown" aria-expanded="false"></i>
                        <ul class="dropdown-menu" aria-labelledby="option<?= $post['id'] ?>">
                            <li><a class="dropdown-item" href="assets/php/actions.php?deletepost=<?= $post['id'] ?>"><i class="bi bi-trash-fill"></i> Delete Post</a></li>
                        </ul>
                    </div>
                <?php
                }
                ?>
            </div>
            <div class="post-image post-thumbnail-img">
                <img src="assets/img/posts/<?= $post['post_img'] ?>" alt="post-thubnail ">
            </div>

            <div class="post-react-comment post-user-intreact">
                <div class="post-like post-react  ">
                    <?php
                    if (checkLiked($post['id'])) {
                        $like_btn_display = 'none';
                        $unlike_btn_display = ' ';
                    } else {
                        $like_btn_display = ' ';
                        $unlike_btn_display = 'none';
                    }
                    ?>
                    <span>
                        <i data-post-id="<?= $post['id'] ?>" style="display:<?= $unlike_btn_display ?>; color:red;" class="fa-solid fa-heart unlike_btn "></i>
                        <i data-post-id="<?= $post['id'] ?>" style="display:<?= $like_btn_display ?>" class="fa-regular fa-heart like_btn "></i>
                        <p style="font-weight: 500; margin-left:30px; cursor:pointer;" data-bs-toggle="modal" data-bs-target="#likes<?= $post['id'] ?>"><?= count($likes) ?> Likes </p>
                    </span>


                </div>
                <div data-bs-toggle="modal" data-bs-target="#postview<?= $post['id'] ?>" class="post-comment post-opinion">
                    <i class="fa-solid fa-comment"></i>
                    <p style="font-weight: 500; margin-left:30px; cursor:pointer;"><?= count($comments) ?> Comments </p>

                </div>


            </div>
            <span style="font-size:small; margin-bottom:15px; text-align:right;" class=" text-muted">Posted <?= show_time($post['created_at']) ?></span>

            <div class="post-desc user-post-deatils">
                <p><?= $post['post_text'] ?></p>
            </div>

            <div class="input-group p-2 border-top">
                <input type="text" class="form-control rounded-0 border-0 comment-input" placeholder="Say something..."
                    aria-label="Add a comment" aria-describedby="button-addon2">
                <button style="background-color: #e65b0b; border-radius:30px; color:white;" class="btn btn-outline-primary rounded-0 border-0 add-comment" data-cs="comment-section<?= $post['id'] ?>" data-post-id="<?= $post['id'] ?>" type="button" id="button-addon2">Post</button>
            </div>

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
                                    <div><img src="assets/img/profile/<?= $post['profile_pic'] ?>" alt="" height="50" width="50" class="rounded-circle border">
                                    </div>
                                    <div>&nbsp;&nbsp;&nbsp;</div>
                                    <div class="d-flex flex-column justify-content-start">
                                        <h6 style="margin: 0px;"><?= $post['first_name'] ?> <?= $post['last_name'] ?></h6>
                                        <p style="margin:0px;" class="text-muted">@<?= $post['username'] ?></p>
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
                                        <div style="font-size:small" class="text-muted">Posted <?= show_time($post['created_at']) ?> </div>

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

            <!-- modal for likes count  -->
            <div class="modal fade" id="likes<?= $post['id'] ?>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Likes</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <?php
                            if (count($likes) < 1) {
                            ?>
                                <p>Currently No Likes</p>

                            <?php
                            }

                            foreach ($likes as $f) {
                                $fuser = getUser($f['user_id']);
                                $fbtn = '';

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

<?php

        }
?>









</div>

</div>