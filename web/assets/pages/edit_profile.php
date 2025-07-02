<div>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
    <style>
        <?php

        global $user;
        ?>.container {
            max-width: 100%;
            margin: auto;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 0px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-top: 90px
        }

        .header {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
            color: #333;
        }

        .profile-section {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-bottom: 20px;
        }

        .profile-section .image-preview {
            width: 150px;
            height: 150px;
            border: 2px dashed #ddd;
            border-radius: 50%;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 10px;
            background-color: #f9f9f9;
        }

        .profile-section .image-preview img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .profile-section button {
            margin-top: 10px;
            padding: 10px 20px;
            background: #007bff;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .profile-section button:hover {
            background: #0056b3;
        }

        .form-section {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            padding: 20px;
        }

        .form-section .input-group {
            flex: 1 1 calc(50% - 20px);
            display: flex;
            flex-direction: column;
        }

        .form-section .input-group label {
            font-size: 14px;
            color: #333;
            margin-bottom: 5px;
        }

        .form-section .input-group input {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }

        .form-section .input-group input:focus {
            border-color: #007bff;
            outline: none;
        }

        .gender {
            display: flex;
            flex-direction: column;
            width: 100%;
            margin-top: 10px;
        }

        .gender label {
            font-size: 14px;
            color: #333;
            margin-bottom: 5px;
        }

        .gender-options {
            display: flex;
            gap: 10px;
        }

        .form-actions {
            margin-top: 20px;
            text-align: center;
        }

        .form-actions button {
            padding: 10px 20px;
            background: #28a745;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        label {
            font-size: 20px;
        }

        .form-actions button:hover {
            background: #218838;
        }
    </style>


    <div class="container">

        <div style="background-color: #007bff; color: #fff; padding: 20px; ; " class="header"> Edit Profile CodeKendra</div>
        <?php
        if (isset($_GET['success'])) {
        ?>
            <p style="color:green;text-align:center;">Profile Updated Sucessfully !</p>
        <?php
        }


        ?>
        <form method="post" action="assets/php/actions.php?updateprofile" enctype="multipart/form-data">
            <div class="profile-section">
                <div class="image-preview">
                    <img src="assets/img/profile/<?= $user['profile_pic'] ?>" name="profile_pic" alt="Profile Preview" id="profilePreview">

                </div>

                <!-- Add an external button to trigger file input -->
                <button type="button" onclick="triggerFileInput()">Choose File</button>
                <input type="file" id="profileInput" name="profile_pic" accept="image/*" style="display: none;">
                <?= showError('profile_pic') ?>
            </div>


            <div class="form-section">
                <div class="input-group">
                    <label for="firstName">First Name</label>
                    <input name="first_name" value="<?= $user['first_name']; ?>" type="text" id="firstName" placeholder="Enter your first name">
                    <?= showError('first_name') ?>
                </div>
                <div class="input-group">
                    <label for="lastName">Last Name</label>
                    <input name="last_name" value="<?= $user['last_name']; ?>" type="text" id="lastName" placeholder="Enter your last name">
                    <?= showError('last_name') ?>
                </div>
                <div class="gender">
                    <label for="gender">Choose Your Gender:</label>
                    <div class="gender-options">
                        <label><input type="radio" <?= $user['gender'] == 1 ? 'checked' : '' ?> disabled name="gender" id="male"> Male</label>
                        <label><input type="radio" <?= $user['gender'] == 2 ? 'checked' : '' ?> disabled name="gender" id="female"> Female</label>
                        <label><input type="radio" <?= $user['gender'] == 0 ? 'checked' : '' ?> disabled name="gender" id="other"> Other</label>
                    </div>
                </div>
                <div class="input-group">
                    <label for="email">Email</label>
                    <input name="email" value="<?= $user['email']; ?>" type="email" id="email" placeholder="Enter your email" disabled>
                </div>
                <div class="input-group">
                    <label for="username">Username</label>
                    <input value="<?= $user['username']; ?>" name="username" type="text" id="username" placeholder="Enter your username">
                    <?= showError('username') ?>
                </div>
                <div class="input-group">
                    <label for="password">Password</label>
                    <input name="password" type="password" id="password" placeholder="Enter your password">
                </div>
            </div>
            <div class="form-actions">
                <button type="submit">Update Profile</button>
            </div>
        </form>
    </div>

    <script>
        const profileInput = document.getElementById('profileInput');
        const profilePreview = document.getElementById('profilePreview');

        function triggerFileInput() {
            profileInput.click();
        }

        profileInput.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function() {
                    profilePreview.src = reader.result;
                };
                reader.readAsDataURL(file);
            }
        });
    </script>
</div>