<?php if (isset($_SESSION['Auth'])) { ?>
  <!-- Add Post Type Selection Modal -->
  <div class="modal fade" id="codeOptionModal" tabindex="-1" aria-labelledby="codeOptionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content rounded-2xl shadow-xl border border-gray-200 overflow-hidden">
        <!-- Modal Header -->
        <div class="modal-header bg-gradient-to-r from-blue-500 to-purple-600 px-6 py-4">
          <h5 class="modal-title text-xl font-bold text-white" id="codeOptionModalLabel">
            Select Post Type
          </h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <!-- Modal Body -->
        <div class="modal-body px-6 py-8 text-center">
          <p class="text-gray-600 mb-6">Do you want to create a post with code or without code?</p>
          <div class="flex justify-center gap-6">
            <button data-bs-toggle="modal" data-bs-target="#addpostcode" class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white font-medium py-3 px-6 rounded-xl shadow-lg transform transition-all duration-300 hover:scale-105 flex items-center" onclick="chooseOption('with')">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M12.316 3.051a1 1 0 01.633 1.265l-4 12a1 1 0 11-1.898-.632l4-12a1 1 0 011.265-.633zM5.707 6.293a1 1 0 010 1.414L3.414 10l2.293 2.293a1 1 0 11-1.414 1.414l-3-3a1 1 0 010-1.414l3-3a1 1 0 011.414 0zm8.586 0a1 1 0 011.414 0l3 3a1 1 0 010 1.414l-3 3a1 1 0 11-1.414-1.414L16.586 10l-2.293-2.293a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
              With Code
            </button>
            <button data-bs-toggle="modal" data-bs-target="#addpostnocode" class="bg-gradient-to-r from-gray-600 to-gray-700 hover:from-gray-700 hover:to-gray-800 text-white font-medium py-3 px-6 rounded-xl shadow-lg transform transition-all duration-300 hover:scale-105 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M4 5a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V7a2 2 0 00-2-2h-1.586a1 1 0 01-.707-.293l-1.121-1.121A2 2 0 0011.172 3H8.828a2 2 0 00-1.414.586L6.293 4.707A1 1 0 015.586 5H4zm6 9a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd" />
              </svg>
              Without Code
            </button>
          </div>
        </div>
        <!-- Modal Footer -->
        <div class="modal-footer bg-gray-50 px-6 py-3">
          <button type="button" class="text-sm text-gray-500 hover:text-red-600 transition-colors" data-bs-dismiss="modal">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Add Post Without Code Modal -->
  <div class="modal fade" id="addpostnocode" tabindex="-1" aria-labelledby="addPostLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content rounded-2xl shadow-xl overflow-hidden">
        <div class="modal-header bg-gradient-to-r from-gray-600 to-gray-700 px-6 py-4">
          <h5 class="modal-title font-bold text-white" id="addPostLabel">Add New Post</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body px-6 py-6 bg-white">
          <!-- Image Preview -->
          <img src="" id="post_img_nocode" style="display: none;" class="w-full h-64 object-cover rounded-xl border-2 border-gray-200 mb-6 shadow-md">
          <!-- Form -->
          <form method="post" action="assets/php/actions.php?addnocodepost" enctype="multipart/form-data" class="space-y-6">
            <!-- File Input -->
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700">Upload Image</label>
              <div class="flex items-center justify-center w-full">
                <label for="select_post_img_nocode" class="flex flex-col items-center justify-center w-full h-32 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 hover:bg-gray-100 transition-colors">
                  <div class="flex flex-col items-center justify-center pt-5 pb-6">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 mb-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <p class="text-sm text-gray-500"><span class="font-semibold">Click to upload</span> or drag and drop</p>
                    <p class="text-xs text-gray-500">PNG, JPG, GIF up to 2MB</p>
                  </div>
                  <input name="post_img" type="file" id="select_post_img_nocode" class="hidden" />
                </label>
              </div>
            </div>
            <!-- Caption -->
            <div>
              <label for="post_text" class="block text-sm font-medium text-gray-700 mb-2">Say Something</label>
              <textarea name="post_text" id="post_text" rows="3"
                class="w-full rounded-xl border border-gray-300 shadow-sm focus:border-blue-500 focus:ring focus:ring-blue-200 resize-none text-sm p-4 transition-all"
                placeholder="What's on your mind?"></textarea>
            </div>
            <!-- Submit Button -->
            <div class="flex justify-end">
              <button type="submit"
                class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-600 to-gray-700 text-white text-sm font-medium rounded-xl shadow-md hover:from-gray-700 hover:to-gray-800 focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-all transform hover:scale-105">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
                </svg>
                Post
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <!-- Add Post With Code Modal -->
  <div class="modal fade" id="addpostcode" tabindex="-1" aria-labelledby="addPostLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content rounded-2xl shadow-xl overflow-hidden">
        <!-- Modal Header -->
        <div class="modal-header bg-gradient-to-r from-blue-500 to-purple-600 px-6 py-4">
          <h5 class="modal-title font-bold text-white" id="addPostLabel">Add New Post With Code</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <!-- Modal Body -->
        <div class="modal-body px-6 py-6 bg-white">
          <img src="" id="post_img_code" style="display: none;" class="w-full h-64 object-cover rounded-xl border-2 border-gray-200 mb-6 shadow-md">
          <form method="post" action="assets/php/actions.php?addcodepost" enctype="multipart/form-data" class="space-y-6">
            <!-- Upload Image -->
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700">Upload Image</label>
              <div class="flex items-center justify-center w-full">
                <label for="select_post_img_code" class="flex flex-col items-center justify-center w-full h-32 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 hover:bg-gray-100 transition-colors">
                  <div class="flex flex-col items-center justify-center pt-5 pb-6">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 mb-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <p class="text-sm text-gray-500"><span class="font-semibold">Click to upload</span> or drag and drop</p>
                    <p class="text-xs text-gray-500">PNG, JPG, GIF up to 2MB</p>
                  </div>
                  <input name="post_img" type="file" id="select_post_img_code" class="hidden" />
                </label>
              </div>
            </div>
            <!-- Post Text -->
            <div>
              <label for="post_text" class="block text-sm font-medium text-gray-700 mb-2">Say Something</label>
              <textarea name="post_text" id="post_text" rows="3"
                class="w-full rounded-xl border border-gray-300 shadow-sm focus:border-blue-500 focus:ring focus:ring-blue-200 resize-none text-sm p-4 transition-all"
                placeholder="What's on your mind?"></textarea>
            </div>
            <!-- Code Language -->
            <div>
              <label for="language" class="block mb-2 text-sm font-medium text-gray-700">Code Language</label>
              <select name="code_language" id="language"
                class="w-full border border-gray-300 rounded-xl p-3 text-sm focus:ring-blue-500 focus:border-blue-500 transition-all">
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
              <label for="code_input" class="block mb-2 text-sm font-medium text-gray-700">Your Code</label>
              <textarea id="code_input" name="code_content" rows="8"
                class="w-full rounded-xl border border-gray-300 p-4 text-sm font-mono resize-none focus:border-blue-500 focus:ring focus:ring-blue-200 transition-all"
                placeholder="Write your code here..."></textarea>
              <input type="hidden" id="code_text">
              <div class="mt-3 rounded-lg bg-gray-900 text-gray-100 p-4 overflow-auto text-sm ring-1 ring-gray-700">
                <code id="code_display" class="language-javascript line-numbers"></code>
              </div>
            </div>
            <!-- Tags -->
            <div>
              <label for="tag_input" class="block text-sm font-medium text-gray-700 mb-2">Tags</label>
              <div id="tag-container"
                class="flex flex-wrap gap-2 p-3 border border-gray-300 rounded-xl min-h-[52px] focus-within:border-blue-500 focus-within:ring focus-within:ring-blue-200 transition-all">
                <input type="text" name="tags" id="tag-input"
                  class="flex-grow border-none focus:ring-0 text-sm outline-none min-w-[100px]"
                  placeholder="Type and press Enter" />
              </div>
              <input type="hidden" name="tags" id="post_tags">
            </div>
            <!-- Submit -->
            <div class="flex justify-end">
              <button type="submit" onclick="syncCodeInput()"
                class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white text-sm font-medium rounded-xl shadow-md hover:from-blue-600 hover:to-purple-700 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all transform hover:scale-105">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
                </svg>
                Post
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <!-- Notifications Sidebar -->
  <div class="offcanvas offcanvas-start" tabindex="-1" id="notification_sidebar" aria-labelledby="notificationSidebarLabel">
    <div class="offcanvas-header bg-gradient-to-r from-blue-500 to-purple-600 text-white">
      <h5 class="offcanvas-title font-bold" id="notificationSidebarLabel">Notifications</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body bg-gray-50 p-0">
      <div class="p-4 border-b border-gray-200 bg-white">
        <h6 class="font-semibold text-gray-700">Recent Notifications</h6>
      </div>
      <div class="divide-y divide-gray-200">
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
          <div class="p-4 hover:bg-gray-100 transition-colors cursor-pointer">
            <div class="flex items-start">
              <img src="assets/img/profile/<?= $fuser['profile_pic'] ?>" alt="" class="w-12 h-12 rounded-full border-2 border-white shadow-sm">
              <div class="ml-3 flex-1">
                <div class="flex items-center justify-between">
                  <a href='?u=<?= $fuser['username'] ?>' class="text-decoration-none text-dark">
                    <h6 class="font-semibold text-gray-900"><?= $fuser['first_name'] ?> <?= $fuser['last_name'] ?></h6>
                  </a>
                  <span class="text-xs text-gray-500"><?= show_time($time) ?></span>
                </div>
                <p class="text-sm text-gray-600 mt-1">@<?= $fuser['username'] ?> <?= $not['message'] ?></p>
              </div>
              <div class="ml-2 flex items-center">
                <?php
                if ($not['read_status'] == 0) {
                ?>
                  <div class="w-3 h-3 bg-blue-500 rounded-full"></div>
                <?php
                } else if ($not['read_status'] == 2) {
                ?>
                  <span class="badge bg-danger">Post Deleted</span>
                <?php
                }
                ?>
              </div>
            </div>
          </div>
        <?php
        }
        ?>
      </div>
    </div>
  </div>

  <!-- Messages Sidebar -->
  <div class="offcanvas offcanvas-start" tabindex="-1" id="messages_sidebar" aria-labelledby="messagesSidebarLabel">
    <div class="offcanvas-header bg-gradient-to-r from-blue-500 to-purple-600 text-white">
      <h5 class="offcanvas-title font-bold" id="messagesSidebarLabel">Messages</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body bg-gray-50 p-0" id="chatlist">
      <!-- Chat list will be populated here -->
    </div>
  </div>

  <!-- Chat Box Modal -->
  <div class="modal fade" id="chatbox" tabindex="-1" aria-labelledby="chatboxLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content rounded-2xl shadow-xl overflow-hidden">
        <div class="modal-header bg-gradient-to-r from-blue-500 to-purple-600 text-white p-4">
          <a href="" id="cplink" class="text-decoration-none text-white flex items-center">
            <img src="assets/img/profile/default_profile.jpg" id="chatter_pic" class="w-10 h-10 rounded-full border-2 border-white mr-3">
            <div>
              <h5 class="modal-title font-bold" id="chatboxLabel">
                <span id="chatter_name"></span> 
                <span class="text-sm font-normal opacity-75">(@<span id="chatter_username">loading..</span>)</span>
              </h5>
            </div>
          </a>
          <button type="button" class="btn-close btn-close-white ms-auto" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body p-4 bg-gray-50" id="user_chat">
          <!-- Chat messages will be loaded here -->
        </div>
        <div class="p-3 bg-danger text-white text-center" id="blerror" style="display:none">
          <i class="bi bi-x-octagon-fill me-2"></i> You are not allowed to send messages to this user anymore
        </div>
        <div class="modal-footer bg-white p-3">
          <div class="input-group">
            <input type="text" class="form-control rounded-pill border-0 bg-gray-100 focus:bg-white focus:shadow-sm" id="msginput" placeholder="Type your message..." aria-label="Message">
            <button class="btn btn-primary rounded-pill ms-2" id="sendmsg" data-user-id="0" type="button">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
<?php } ?>

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
      if (typeof Prism !== 'undefined') {
        Prism.highlightElement(codeDisplay);
      }
    }
    
    if (codeInput && codeDisplay && langSelect) {
      codeInput.addEventListener("input", updateHighlight);
      langSelect.addEventListener("change", updateHighlight);
      // Initial highlight
      updateHighlight();
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
        tagEl.className = "tag-item bg-blue-100 text-blue-800 text-xs font-medium px-3 py-1 rounded-full flex items-center";
        tagEl.innerHTML = `${tag}<button type="button" class="ml-2 text-blue-500 hover:text-blue-800" onclick="removeTag(${index})">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>`;
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
    
    // Post image preview 
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
    window.removeTag = removeTag;
  });
</script>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
  integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
  crossorigin="anonymous"></script>
<script src="./assets/js/jquery.js"></script>
<script src="./assets/js/timeago_jquery.js"></script>

<!-- Prism.js for code highlighting -->
<link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet" />
<link href="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/prism-core.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/line-numbers/prism-line-numbers.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/prismjs@1.29.0/plugins/autoloader/prism-autoloader.min.js"></script>
<script>
  Prism.plugins.autoloader.languages_path = 'https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/';
</script>

<script src="./assets/js/index.js?v=<?= time() ?>"></script>
</body>
</html>