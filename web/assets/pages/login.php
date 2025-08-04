<div class="min-h-screen flex items-center justify-center bg-gray-100 px-4">
  <div class="flex flex-col md:flex-row w-full max-w-4xl bg-white rounded-2xl shadow-lg overflow-hidden">

    <!-- Left Panel -->
    <div class="md:w-1/2 bg-gradient-to-tr from-orange-500 to-pink-500 text-white p-10 flex flex-col justify-center">
      <h2 class="text-4xl font-bold mb-4">CodeKendra</h2>
      <p class="text-lg">Connect with friends and the world around you on CodeKendra.</p>
    </div>

    <!-- Right Panel -->
    <div class="md:w-1/2 p-10">
      <h2 class="text-2xl font-bold text-gray-800 mb-2">Hello Again</h2>
      <p class="text-gray-600 mb-6">Stay Closer, Share Better!</p>

      <form method="post" action="assets/php/actions.php?login" class="space-y-4">
        <div>
          <input
            type="text"
            name="username_email"
            value="<?= showFormData('username_email') ?>"
            placeholder="Email address"
            class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
          >
          <?= showError('username_email') ?>
        </div>

        <div>
          <input
            type="password"
            name="password"
            placeholder="Password"
            class="w-full px-4 py-3 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
          >
          <?= showError('password') ?>
        </div>

        <?= showError('checkuser') ?>

        <div class="flex justify-end text-sm text-blue-600 hover:underline">
          <a href="?forgotpassword&newfp">Forgot Password?</a>
        </div>

        <button
          type="submit"
          class="w-full bg-orange-500 hover:bg-orange-600 text-white py-3 rounded-md font-medium transition duration-200"
        >
          Login
        </button>

        <p class="text-center text-sm text-gray-600 mt-4">
          Don’t have an account?
          <a href="?signup" class="text-blue-600 hover:underline">Sign Up</a>
        </p>
      </form>
    </div>
  </div>
</div>
