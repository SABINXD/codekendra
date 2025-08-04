<?php if (isset($_SESSION['Auth'])) { ?>
  <!-- add post modal question  -->
  <div class="modal fade" id="codeOptionModal" tabindex="-1" aria-labelledby="codeOptionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content rounded-2xl shadow-lg border border-gray-200">

        <!-- Modal Header -->
        <div class="modal-header bg-gray-100 px-5 py-3 rounded-t-2xl">
          <h5 class="modal-title text-xl font-semibold text-gray-800" id="codeOptionModalLabel">
            Select Post Type
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <!-- Modal Body -->
        <div class="modal-body px-5 py-4 space-y-4 text-center">
          <p class="text-gray-600">Do you want to create a post with code or without code?</p>

          <div class="flex justify-center gap-4">
            <button data-bs-toggle="modal" data-bs-target="#addpostcode" class="bg-[#e65b0b] hover:bg-green-700 text-white font-medium py-2 px-4 rounded-xl shadow" onclick="chooseOption('with')">
              With Code
            </button>
            <button data-bs-toggle="modal" data-bs-target="#addpostnocode" class="bg-gray-600 hover:bg-gray-700 text-white font-medium py-2 px-4 rounded-xl shadow">

              Without Code
            </button>
          </div>
        </div>

        <!-- Modal Footer (Optional) -->
        <div class=" modal-footer border-t px-4 py-3">
          <button type="button" class="text-sm text-gray-500 hover:text-red-600" data-bs-dismiss="modal">
            Cancel
          </button>
        </div>

      </div>
    </div>
  </div>
    <!-- Add NocodePost Modal -->
  <div class="modal fade" id="addpostnocode" tabindex="-1" aria-labelledby="addPostLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content rounded-xl shadow-lg overflow-hidden">
        <div class="modal-header bg-gray-100 px-4 py-3">
          <h5 class="modal-title font-semibold text-gray-800" id="addPostLabel">Add New Post</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <div class="modal-body px-5 py-4 bg-white">
          <!-- Image Preview -->
          <img src="" id="post_img_nocode" style="display: none;" class="w-full h-auto rounded-lg border mb-4 object-cover">

          <!-- Form -->
          <form method="post" action="assets/php/actions.php?addnocodepost" enctype="multipart/form-data" class="space-y-4">
            <!-- File Input -->
            <label class="block">
              <span class="text-gray-700 text-sm font-medium">Upload Image</span>
              <input name="post_img" type="file" id="select_post_img_nocode"
                class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4
              file:rounded-full file:border-0
              file:text-sm file:font-semibold
              file:bg-blue-50 file:text-blue-700
              hover:file:bg-blue-100 mt-2" />
            </label>

            <!-- Caption -->
            <div>
              <label for="post_text" class="text-sm font-medium text-gray-700 block mb-1">Say Something</label>
              <textarea name="post_text" id="post_text" rows="2"
                class="w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-400 focus:ring focus:ring-blue-100 resize-none text-sm p-2"
                placeholder="What's on your mind?"></textarea>
            </div>

            <!-- Submit Button -->
            <div class="text-end">
              <button type="submit"
                class="inline-flex items-center px-5 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-offset-1 focus:ring-blue-500 transition">
                <i class="fa-solid fa-paper-plane mr-2"></i> Post
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
    <!-- add codepostmodal  -->


  <!-- Modal -->
  <div class="modal fade" id="addpostcode" tabindex="-1" aria-labelledby="addPostLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered max-w-2xl">
      <div class="modal-content rounded-xl shadow-lg overflow-hidden">

        <!-- Modal Header -->
        <div class="modal-header bg-gray-100 px-4 py-3">
          <h5 class="modal-title font-semibold text-gray-800" id="addPostLabel">Add New Post</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <!-- Modal Body -->
        <div class="modal-body px-5 py-4 bg-white">
            <img src="" id="post_img_code" style="display: none;" class="w-full h-auto rounded-lg border mb-4 object-cover">
          <form method="post" action="assets/php/actions.php?addcodepost" enctype="multipart/form-data" class="space-y-5">

            <!-- Upload Image -->
              <label class="block">
              <span class="text-gray-700 text-sm font-medium">Upload Image</span>
              <input name="post_img" type="file" id="select_post_img_code"
                class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4
              file:rounded-full file:border-0
              file:text-sm file:font-semibold
              file:bg-blue-50 file:text-blue-700
              hover:file:bg-blue-100 mt-2" />
            </label>

            <!-- Post Text -->
            <div>
              <label for="post_text" class="text-sm font-medium text-gray-700 block mb-1">Say Something</label>
              <textarea name="post_text" id="post_text" rows="2"
                class="w-full rounded-lg border border-gray-300 shadow-sm focus:border-blue-400 focus:ring focus:ring-blue-100 resize-none text-sm p-2"
                placeholder="What's on your mind?"></textarea>
            </div>

            <!-- Code Language -->
            <div>
              <label for="language" class="block mb-1 text-sm font-medium text-gray-700">Code Language</label>
              <select name="code_language" id="language"
                class="w-full border border-gray-300 rounded-lg p-2 text-sm focus:ring-blue-500 focus:border-blue-500">
                <option value="">-- Select --</option>
                <option value="html">HTML</option>
                <option value="css">CSS</option>
                <option value="javascript">JavaScript</option>
                <option value="php">PHP</option>
                <option value="python">Python</option>
                <option value="java">Java</option>
                <option value="c">C</option>
                <option value="cpp">C++</option>
                <option value="sql">SQL</option>
              </select>
            </div>

            <!-- Code Input -->
            <div>
              <label for="code_input" class="block mb-1 text-sm font-medium text-gray-700">Your Code</label>
              <textarea id="code_input" name="code_content" rows="6"
                class="w-full rounded-lg border border-gray-300 p-3 text-sm font-mono resize-none"
                placeholder="Write your code here..."></textarea>
              <input type="hidden" id="code_text">

              <pre class="rounded-lg bg-gray-900 text-white mt-3 p-4 overflow-auto text-sm ring-1 ring-gray-300">
               <code id="code_display" class="language-javascript line-numbers"></code>
               </pre>
            </div>

            <!-- Tags -->
            <div>
              <label for="tag_input" class="text-sm font-medium text-gray-700 block mb-1">Tags</label>
              <div id="tag-container"
                class="flex flex-wrap gap-2 p-2 border border-gray-300 rounded-lg min-h-[44px]">
                <input type="text" name="tags" id="tag-input"
                  class="flex-grow border-none focus:ring-0 text-sm outline-none min-w-[100px]"
                  placeholder="Type and press Enter" />
              </div>
              <input type="hidden" name="tags" id="post_tags">
            </div>

            <!-- Submit -->
            <div class="text-end">
              <button type="submit" onclick="syncCodeInput()"
                class="inline-flex items-center px-5 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-offset-1 focus:ring-blue-500 transition">
                <i class="fa-solid fa-paper-plane mr-2"></i> Post
              </button>
            </div>
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
          <a href="" id="cplink" class="text-decoration-none text-dark">
            <h5 class="modal-title" id="exampleModalLabel"><img src="assets/img/profile/default_profile.jpg" id="chatter_pic" height="40" width="40"
                class="m-1 rounded-circle border"><span id="chatter_name"></span> (@<span class="text-small" id="chatter_username">loading..</span>)</h5>
          </a>
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
          <button class="btn btn-outline-primary rounded-0 border-0" id="sendmsg" data-user-id="0" type="button"><i style="font-size: 22px;" class="fa-solid fa-paper-plane"></i></button>
        </div>
      </div>
    </div>
  </div>
  </div>

<?php

}
?>


<script>
  document.addEventListener("DOMContentLoaded", function () {
    const codeInput = document.getElementById("code_input");
    const codeDisplay = document.getElementById("code_display");
    const langSelect = document.getElementById("language");
    const hiddenCode = document.getElementById("code_text");

    function syncCodeInput() {
      hiddenCode.value = codeInput.value;
    }

    function updateHighlight() {
      const selectedLang = langSelect.value || "javascript";
      codeDisplay.className = `language-${selectedLang} line-numbers`;
      codeDisplay.textContent = codeInput.value;
      Prism.highlightElement(codeDisplay);
    }

    if (codeInput && codeDisplay && langSelect) {
      codeInput.addEventListener("input", updateHighlight);
      langSelect.addEventListener("change", updateHighlight);
    }

    // Tag functionality
    const tagInput = document.getElementById("tag-input");
    const tagContainer = document.getElementById("tag-container");
    const hiddenTags = document.getElementById("post_tags");
    let tags = [];

    function renderTags() {
      tagContainer.querySelectorAll(".tag-item").forEach(el => el.remove());
      tags.forEach((tag, index) => {
        const tagEl = document.createElement("span");
        tagEl.className = "tag-item bg-blue-100 text-blue-800 text-xs font-medium px-2 py-1 rounded-full flex items-center";
        tagEl.innerHTML = `${tag}<button type="button" class="ml-1 text-blue-500 hover:text-blue-800 text-sm" onclick="removeTag(${index})">&times;</button>`;
        tagContainer.insertBefore(tagEl, tagInput);
      });
      hiddenTags.value = tags.join(",");
    }

    function removeTag(index) {
      tags.splice(index, 1);
      renderTags();
    }

    if (tagInput) {
      tagInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter" && this.value.trim() !== "") {
          e.preventDefault();
          const value = this.value.trim();
          if (!tags.includes(value)) {
            tags.push(value);
            renderTags();
          }
          this.value = "";
        }
      });
    }
// post img preivew 
    ["code", "nocode"].forEach(type => {
  const input = document.getElementById(`select_post_img_${type}`);
  const preview = document.getElementById(`post_img_${type}`);

  if (input && preview) {
    input.addEventListener("change", function () {
      const file = this.files[0];
      if (file) {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function () {
          preview.src = reader.result;
          preview.style.display = "block";
        };
      }
    });
  }
});

    // Expose to global scope if needed
    window.syncCodeInput = syncCodeInput;
  });
</script>


<!-- linking js  -->


<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
  integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
  crossorigin="anonymous"></script>
<script src="./assets/js/jquery.js"></script>
<script src="./assets/js/timeago_jquery.js"></script>



  <link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet" />
  <link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.css" rel="stylesheet" />
  <script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/prism-core.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/autoloader/prism-autoloader.min.js"></script>
  <script>
    Prism.plugins.autoloader.languages_path = 'https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/';
  </script>
<script src="./assets/js/index.js?v=<?= time() ?>"></script>

<!-- linking js finshed  -->

</body>


</html>