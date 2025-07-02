<?php
global $user;

?>
<div class="verify-container">

  <div class="header-bar">
    <p>CodeKendra</p>
  </div>

  <div class="verify-panel">
    <h1>Verify Yourself </h1>
    <div class="separating-line-verify"></div>
    <p style="text-align: center;">Enter 6 Digit code that you got have on email:
      <br>
      (<?= $user['email'] ?>)
    </p>
    <form method="post" action="./assets/php/actions.php?verify_email">
      <input type="text" name="code" id="codenumber" placeholder="Enter 6 digit code">

      <?= showError('email_verify') ?>
      <?php
      if (isset($_GET['codesent'])) {
      ?>
        <p style="color: green; font-size: 16px; ">Verification code Sent Succesfully !</p>
      <?php
      }

      ?>



      <div class="btn-need">
        <button type="submit" class="code-verify">Verify</button>
        <a href="./assets/php/actions.php?resend_code" type="submit" class="simple_txt">Resend Code</a>
      </div>
    </form>
    <a style="background-color: red;" href="./assets/php/actions.php?logout" class="code-verify">Logout <i style="margin: 2px;"
        class="fa-solid fa-right-from-bracket"></i></a>


  </div>
</div>