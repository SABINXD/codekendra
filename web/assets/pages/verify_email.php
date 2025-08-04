<?php global $user; ?>
<div class="min-h-screen bg-gray-100 flex items-center justify-center px-4">
  <div class="w-full max-w-md bg-white rounded-xl shadow-lg overflow-hidden">

    <!-- Header Bar -->
    <div class="bg-orange-500 py-4 text-center text-white text-2xl font-bold">
      CodeKendra
    </div>

    <!-- Verification Panel -->
    <div class="px-6 py-8">
      <h2 class="text-2xl font-semibold text-center text-gray-800 mb-4">Verify Yourself</h2>
      <div class="w-20 mx-auto h-1 bg-orange-500 rounded-full mb-6"></div>

      <p class="text-center text-gray-600 mb-6">
        Enter the 6-digit code sent to your email:
        <br>
        <span class="font-medium text-gray-800">(<?= htmlspecialchars($user['email']) ?>)</span>
      </p>

      <form method="post" action="./assets/php/actions.php?verify_email" class="space-y-4">
        <input
          type="text"
          name="code"
          id="codenumber"
          placeholder="Enter 6 digit code"
          class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-orange-500 text-sm"
        >

        <?= showError('email_verify') ?>

        <?php if (isset($_GET['codesent'])): ?>
          <p class="text-green-600 text-sm font-medium">Verification code sent successfully!</p>
        <?php endif; ?>

        <div class="flex items-center justify-between">
          <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-md text-sm font-semibold transition duration-150">
            Verify
          </button>
          <a href="./assets/php/actions.php?resend_code" class="text-sm text-blue-600 hover:underline">Resend Code</a>
        </div>
      </form>

      <hr class="my-6 border-gray-200">

      <a href="./assets/php/actions.php?logout"
        class="w-full block text-center bg-red-500 hover:bg-red-600 text-white py-2 rounded-md text-sm font-semibold transition duration-150">
        Logout <i class="fas fa-right-from-bracket ml-2"></i>
      </a>
    </div>
  </div>
</div>
