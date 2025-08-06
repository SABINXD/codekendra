<div class="min-h-screen flex items-center justify-center bg-gray-100 px-4">
  <div class="flex flex-col md:flex-row w-full max-w-5xl bg-white rounded-2xl shadow-lg overflow-hidden">

    <!-- Left Panel -->
    <div class="md:w-1/2 bg-gradient-to-tr from-orange-500 to-pink-500 text-white p-10 flex flex-col justify-center">
      <h2 class="text-4xl font-bold mb-4">CodeKendra</h2>
      <p class="text-lg">Connect with friends and the world around you on CodeKendra.</p>
    </div>

    <!-- Right Panel -->
    <div class="md:w-1/2 p-10">
      <h2 class="text-2xl font-bold text-gray-800 mb-2">Hi There</h2>
      <p class="text-gray-600 mb-6">Stay Closer, Share Better!</p>

      <form method="post" action="./assets/php/actions.php?signup" class="space-y-4">

        <!-- First & Last Name -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <input type="text" name="first_name" value="<?= showFormData('first_name') ?>" placeholder="First name"
              class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
            <?= showError('first_name') ?>
          </div>
          <div>
            <input type="text" name="last_name" value="<?= showFormData('last_name') ?>" placeholder="Last name"
              class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
            <?= showError('last_name') ?>
          </div>
        </div>

        <!-- Gender -->
        <div class="flex space-x-4 mt-2 text-sm text-gray-700">
          <label class="flex items-center gap-2">
            <input type="radio" name="gender" value="1" class="text-orange-500" <?= isset($_SESSION['formdata']) ? '' : 'checked' ?> <?= showFormData('gender') == 1 ? 'checked' : '' ?>>
            <span>Male</span>
          </label>
          <label class="flex items-center gap-2">
            <input type="radio" name="gender" value="2" class="text-orange-500" <?= showFormData('gender') == 2 ? 'checked' : '' ?>>
            <span>Female</span>
          </label>
          <label class="flex items-center gap-2">
            <input type="radio" name="gender" value="3" class="text-orange-500" <?= showFormData('gender') == 3 ? 'checked' : '' ?>>
            <span>Others</span>
          </label>
        </div>

        <!-- Email -->
        <div>
          <input type="email" name="email" value="<?= showFormData('email') ?>" placeholder="Email address"
            class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
          <?= showError('email') ?>
        </div>

        <!-- Username -->
        <div>
          <input type="text" name="username" value="<?= showFormData('username') ?>" placeholder="Username"
            class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
          <?= showError('username') ?>
        </div>

        <!-- Password -->
        <div>
          <input type="password" name="password" id="change_pass" value="<?= showFormData('password') ?>" placeholder="Password"
            class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
          <?= showError('password') ?>
        </div>

        <!-- Submit Button -->
        <button type="submit"
          class="w-full bg-orange-500 hover:bg-orange-600 text-white py-3 rounded-md font-medium transition duration-200">
          Sign up
        </button>

        <!-- Login Redirect -->
        <p class="text-center text-sm text-gray-600 mt-4">
          Already have an account?
          <a href="?login" class="text-blue-600 hover:underline">Login</a>
        </p>
      </form>
    </div>
  </div>
</div>
