<?php if(isset($_SESSION['Auth'])){ ?>
<!-- modal for add post  -->
<div class="modal fade" id="addpost" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add New Post</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <img src="" style="display: none;" id="post_img" class="w-100 rounded border">
                <form method="post" action="assets/php/actions.php?addpost" enctype="multipart/form-data">
                    <div class="my-3">

                        <input class="form-control" name="post_img" type="file" id="select_post_img">
                    </div>
                    <div class="mb-3">
                        <label for="exampleFormControlTextarea1" class="form-label">Say Something</label>
                        <textarea name="post_text" class="form-control" id="exampleFormControlTextarea1"
                            rows="1"></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Post</button>
                </form>
            </div>

        </div>
    </div>
</div>

<!-- modal for notification bar  -->
<div class="offcanvas offcanvas-start" tabindex="-1" id="notification_sidebar" aria-labelledby="offcanvasExampleLabel">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="offcanvasExampleLabel">Notifications</h5>
        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body">
        <?php
        $notifications = getNotifications();
        foreach ($notifications as $not) {
            $time = $not['created_at'];
            $fuser = getUser($not['from_user_id']);
            $post = '';
            if ($not['post_id']) {
                $post = 'data-bs-toggle="modal" data-bs-target="#postview' . $not['post_id'] . '"';
            }
            $fbtn = '';
        ?>
            <div class="d-flex justify-content-between border-bottom">
                <div class="d-flex align-items-center p-2">
                    <div><img src="assets/img/profile/<?= $fuser['profile_pic'] ?>" alt="" height="40" width="40" class="rounded-circle border">
                    </div>
                    <div>&nbsp;&nbsp;</div>
                    <div class="d-flex flex-column justify-content-center" <?= $post ?>>
                        <a href='?u=<?= $fuser['username'] ?>' class="text-decoration-none text-dark">
                            <h6 style="margin: 0px;font-size: small;"><?= $fuser['first_name'] ?> <?= $fuser['last_name'] ?></h6>
                        </a>
                        <p style="margin:0px;font-size:small" class="<?= $not['read_status'] ? 'text-muted' : '' ?>">@<?= $fuser['username'] ?> <?= $not['message'] ?></p>
                        <time style="font-size:small" class="timeago <?= $not['read_status'] ? 'text-muted' : '' ?> text-small" datetime="<?= $time ?>"></time>
                    </div>
                </div>
                <div class="d-flex align-items-center">
                    <?php
                    if ($not['read_status'] == 0) {
                    ?>
                        <div class="p-1 bg-primary rounded-circle"></div>

                    <?php

                    } else if ($not['read_status'] == 2) {
                    ?>
                        <span class="badge bg-danger">Post Deleted</span>
                    <?php
                    }
                    ?>

                </div>
            </div>
        <?php
        }
        ?>

    </div>
</div>

<!-- modal for messageBar bar  -->
<div class="offcanvas offcanvas-start" tabindex="-1" id="messages_sidebar" aria-labelledby="offcanvasExampleLabel">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="offcanvasExampleLabel">Messages</h5>
        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body" id="chatlist">
      

    </div>
</div>
<!-- modal for chat box -->
<div class="modal fade" id="chatbox" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <a href="" id="cplink" class="text-decoration-none text-dark"><h5 class="modal-title" id="exampleModalLabel"><img src="assets/img/profile/default_profile.jpg" id="chatter_pic" height="40" width="40"
         class="m-1 rounded-circle border"><span id="chatter_name"></span> (@<span class="text-small"  id="chatter_username">loading..</span>)</h5></a>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body d-flex flex-column-reverse gap-2" id="user_chat">
     loading.....
      </div>
      <div class="modal-footer">
         
          <p class="p-2 text-danger mx-auto" id="blerror" style="display:none"> 
          <i class="bi bi-x-octagon-fill"></i> you are not allowed to send msg to this user anymore

</div>
      <div class="input-group p-2 " id="msgsender">
                            <input type="text" class="form-control rounded-0 border-0" id="msginput" placeholder="Enter You Message"
                                aria-label="Recipient's username" aria-describedby="button-addon2">
                            <button class="btn btn-outline-primary rounded-0 border-0" id="sendmsg" data-user-id="0" type="button"
                                ><i style="font-size: 22px;" class="fa-solid fa-paper-plane"></i></button>
                        </div>
      </div>
    </div>
  </div>
</div>

<?php

    }
?>

<script>
    //show post image that we upload

    var input = document.querySelector("#select_post_img");

    input.addEventListener("change", preview);

    function preview() {
        var fileobject = this.files[0];
        var filereader = new FileReader();

        filereader.readAsDataURL(fileobject);

        filereader.onload = function() {
            var image_src = filereader.result;
            var image = document.querySelector("#post_img");
            image.setAttribute('src', image_src);
            image.setAttribute('style', 'display:');
        }
    }
</script>


<!-- linking js  -->


<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
    crossorigin="anonymous"></script>
<script src="./assets/js/jquery.js"></script>
<script src="./assets/js/timeago_jquery.js"></script>



<script src="./assets/js/index.js?v=<?= time() ?>"></script>

<!-- linking js finshed  -->

</body>


</html>