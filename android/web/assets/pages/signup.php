<div class="signup-container">
    <div class="signup-left-panel">
        <div class="signup-content">
            <h2 class="signup-title">CodeKendra</h2>
            <p class="signup-description">Connect with friends and the world around you on CodeKendra.</p>
        </div>
    </div>
    <div class="signup-right-panel">
        <h2 class="signup-greeting">Hi There</h2>
        <p class="signup-subtext">Stay Closer, Share Better!</p>
        <form method="post" action="./assets/php/actions.php?signup" class="signup-form">
            <input type="text" value="<?= showFormData('first_name') ?>" name="first_name" placeholder="First name"
                class="signup-input">
            <?= showError('first_name') ?>
            <input type="text" value="<?= showFormData('last_name') ?>" name="last_name" placeholder="Last name"
                class="signup-input">
            <?= showError('last_name') ?>

            <div class="signup-options">
                <label class="signup-gender-label"><input value="1" <?= isset($_SESSION['formdata']) ? '' : 'checked' ?>
                        type="radio" <?= showFormData('gender') == 1 ? 'checked' : '' ?> name="gender"
                        class="signup-gender-input"> <span class="signup-gender-text">Male</span></label>
                <label class="signup-gender-label"><input value="2" type="radio"
                        <?= showFormData('gender') == 2 ? 'checked' : '' ?> name="gender" class="signup-gender-input"> <span
                        class="signup-gender-text">Female</span></label>
                <label class="signup-gender-label"><input value="3" type="radio"
                        <?= showFormData('gender') == 3 ? 'checked' : '' ?> name="gender" class="signup-gender-input"> <span
                        class="signup-gender-text">Others</span></label>
            </div>
            <input type="email" value="<?= showFormData('email') ?>" placeholder="Email address" name="email"
                class="signup-input">
            <?= showError('email') ?>

            <input type="text" value="<?= showFormData('username') ?>" name="username" placeholder="Username"
                class="signup-input">
            <?= showError('username') ?>
            <input type="password" name="password" id="change_pass" value="<?= showFormData('password') ?>" placeholder="Password"
                class="signup-input">
            <?= showError('password') ?>
            <button type="submit" class="signup-btn">Sign up</button>

            <p class="signup-login-text">Already have an account? <a href="?login" class="signup-login-link">Login</a>
            </p>
        </form>
    </div>
</div>