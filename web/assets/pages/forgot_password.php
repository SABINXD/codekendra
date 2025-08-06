<?php
global $user;

?>
<div class="verify-container">

    <div class="header-bar">
        <p>CodeKendra</p>
    </div>

    <div class="verify-panel">
        <h1>Reset Password </h1>
        <div class="separating-line-verify"></div>
        <?php
        if (isset($_SESSION['forgot_code']) && !isset($_SESSION['auth_temp'])) {
            $action = 'verifycode';
        } elseif (isset($_SESSION['forgot_code']) && isset($_SESSION['auth_temp'])) {
            $action = 'changepassword';
        } else {
            $action = 'forgotpassword';
        }

        ?>
        <form method="post" action="./assets/php/actions.php?<?= $action ?>">

            <?php
            if ($action == 'forgotpassword') {
            ?>
                <p style="text-align: center;">Enter 6 digit code sended to you :
                    <br>

                </p>
                <input type="text" name="email" id="input-place" placeholder="Enter your email">
                <?= showError('email') ?>

                <button type="submit" class="code-verify">Send Code</button>
            <?php

            }
            ?>
            <?php
            if ($action == 'verifycode') {
            ?>
                <p style="text-align: center;">Enter 6 digit code sended to you <?= $_SESSION['forgot_email'] ?> :
                    <br>

                </p>

                <input type="text" id="codenumber" name="code" id="input-place" placeholder="Enter code">
                <?= showError('email_verify') ?>

                <button class="code-verify">Verify Code</button>

            <?php

            }
            ?>
            <?php
            if ($action == 'changepassword') {
            ?>
                <p style="text-align: center;">Enter new password : <?= $_SESSION['forgot_email'] ?></p>

                <input type="password" name="password" value="<?= showFormData('password') ?>" placeholder="Password"
                    class="signup-input">
                <?= showError('password') ?>
                <button id="submitBtn" type="submit" class="code-verify">Change Password</button>

            <?php

            }
            ?>







        </form>

        <a style="background-color: red; text-decoration:none;" href="./" class="code-verify">go back <i
                style="margin: 2px;" class="fa-solid fa-right-from-bracket"></i></a>


    </div>
</div>