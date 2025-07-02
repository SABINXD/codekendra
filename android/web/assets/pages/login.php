<div class="login-container">
  <div class="login-left-panel">
    <div class="login-content">
      <h2>CodeKendra</h2>
      <p>Connect with friends and the world around you on CodeKendra.</p>
    </div>
  </div>
  <div class="login-right-panel">
    <h2>Hello Again</h2>
    <p>Stay Closer, Share Better!</p>
    <form method="post" action="assets/php/actions.php?login" class="login-form">
      <input type="text" value="<?= showFormData('username_email') ?>" name="username_email" placeholder="Email address">
      <?= showError('username_email') ?>
      <input type="password" name="password" placeholder="Password">
      <?= showError('password') ?>
      <?= showError('checkuser') ?>
      <div class="login-options">
        <a href="?forgotpassword&newfp">Forgot Password?</a>
      </div>
      <button type="submit" class="login-btn">Login</button>
      <p>Don’t have an account? <a href="?signup">Sign Up</a></p>
    </form>
  </div>
</div>