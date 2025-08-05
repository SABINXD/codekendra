<?php
global $user;
global $posts;
global $follow_sugesstions;


?>
<div class="max-w-7xl mx-auto px-4 py-6 pt-24">
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - User Profile & Suggestions -->
        <div class="lg:col-span-1">
            <div class="sticky top-24 space-y-6">
                <!-- User Profile Card -->
                <div class="bg-white rounded-2xl shadow-lg overflow-hidden user-profile-card">
                    <div class="bg-gradient-to-r from-blue-500 to-purple-600 h-20"></div>
                    <div class="px-6 pb-6 -mt-10">
                        <div class="flex flex-col items-center">
                            <img src="./assets/img/profile/<?= htmlspecialchars($user['profile_pic']) ?>"
                                alt="Profile picture"
                                class="w-20 h-20 rounded-full border-4 border-white shadow-lg mb-4 profile-pic">
                            <a href="?u=<?= htmlspecialchars($user['username']) ?>" class="text-center hover:opacity-80 transition-opacity">
                                <h2 class="text-xl font-bold text-gray-900 mb-1">
                                    <?= htmlspecialchars($user['first_name'] . ' ' . $user['last_name']) ?>
                                </h2>
                                <p class="text-sm text-gray-600 font-medium">@<?= htmlspecialchars($user['username']) ?></p>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Friend Suggestions -->
                <div class="bg-white rounded-2xl shadow-lg p-6 suggestions-card">
                    <h3 class="text-lg font-bold text-gray-900 mb-4 flex items-center">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="2"
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            class="lucide lucide-users-round mr-2 text-purple-600">
                            <path d="M18 21a8 8 0 0 0-16 0" />
                            <circle cx="10" cy="8" r="5" />
                            <path d="M22 21a8 8 0 0 0-16 0" />
                            <polyline points="16 3 21 8 22 7" />
                        </svg>
                        You may Know
                    </h3>
                    <div class="space-y-4">
                        <?php foreach ($follow_sugesstions as $suser): ?>
                            <div class="flex items-center space-x-3 rounded-xl hover:bg-gray-50 transition-colors suggestion-item">
                                <img src="assets/img/profile/<?= htmlspecialchars($suser['profile_pic']) ?>"
                                    class="w-12 h-12 rounded-full object-cover">
                                <div class="flex-1 min-w-0">
                                    <a href="?u=<?= htmlspecialchars($suser['username']) ?>" class="block text-sm hover:opacity-80">
                                        <p class="font-semibold text-gray-900 truncate">
                                            <?= htmlspecialchars($suser['first_name'] . ' ' . $suser['last_name']) ?>
                                        </p>
                                        <p class="text-sm text-gray-600 truncate">@<?= htmlspecialchars($suser['username']) ?></p>
                                    </a>
                                </div>
                                <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors followbtn"
                                    data-user-id='<?= (int)$suser['id'] ?>'>
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="16"
                                        height="16"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        class="lucide lucide-plus mr-1">
                                        <path d="M12 5v14" />
                                        <path d="M5 12h14" />
                                    </svg>
                                    Follow
                                </button>
                            </div>
                        <?php endforeach; ?>
                        <?php if (count($follow_sugesstions) < 1): ?>
                            <div class="text-center py-8">
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="48"
                                    height="48"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    stroke-width="2"
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    class="lucide lucide-users-round text-gray-300 mx-auto mb-3">
                                    <path d="M18 21a8 8 0 0 0-16 0" />
                                    <circle cx="10" cy="8" r="5" />
                                    <path d="M22 21a8 8 0 0 0-16 0" />
                                    <polyline points="16 3 21 8 22 7" />
                                </svg>
                                <p class="text-gray-600 font-medium">No user suggestions available</p>
                            </div>
                        <?php endif; ?>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Content - Posts Feed -->
        <div class="lg:col-span-3">
            <div class="space-y-6 posts-container">
                <?php
                showError('post_img');

                foreach ($posts as $post):

                    $likes = getLikesCount($post['id']);
                    $comments = getComments($post['id']);
                    // Prepare image source
                    $image = $post['post_img'];
                    $img_src = '';
                    if (!empty($image)) {
                        if (strpos($image, 'http') === 0) {
                            $img_src = $image;
                        } else if (strpos($image, 'web/assets/img/posts/') === 0) {
                            $img_src = substr($image, strpos($image, 'assets/img/posts/'));
                        } else {
                            $img_src = 'assets/img/posts/' . $image;
                        }
                    }
                ?>
                    <article class="bg-white rounded-2xl shadow-lg overflow-hidden post-card" data-post-id="<?= (int)$post['id'] ?>">
                        <!-- Post Header -->
                        <div class="flex items-center justify-between p-6 pb-4">
                            <a href="?u=<?= htmlspecialchars($post['username']) ?>" class="flex items-center space-x-3 hover:opacity-80 transition-opacity">
                                <img src="assets/img/profile/<?= htmlspecialchars($post['profile_pic']) ?>"
                                    alt="Profile"
                                    class="w-12 h-12 rounded-full object-cover">
                                <div>
                                    <h3 class="font-bold text-gray-900">
                                        <?= htmlspecialchars($post['first_name'] . ' ' . $post['last_name']) ?>
                                    </h3>
                                    <p class="text-sm text-gray-600">@<?= htmlspecialchars($post['username']) ?></p>
                                </div>
                            </a>
                            <?php if ($post['user_id'] == $user['id']): ?>
                                <div class="relative">
                                    <button class="p-2 hover:bg-gray-100 rounded-full transition-colors"
                                        onclick="toggleDropdown('dropdown-<?= (int)$post['id'] ?>')">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            width="20"
                                            height="20"
                                            viewBox="0 0 24 24"
                                            fill="none"
                                            stroke="currentColor"
                                            stroke-width="2"
                                            stroke-linecap="round"
                                            stroke-linejoin="round"
                                            class="lucide lucide-ellipsis-vertical text-gray-500">
                                            <circle cx="12" cy="12" r="1" />
                                            <circle cx="12" cy="5" r="1" />
                                            <circle cx="12" cy="19" r="1" />
                                        </svg>
                                    </button>
                                    <div id="dropdown-<?= (int)$post['id'] ?>" class="hidden absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 z-10">
                                        <a href="assets/php/actions.php?deletepost=<?= (int)$post['id'] ?>"
                                            class="flex items-center px-4 py-3 text-red-600 hover:bg-red-50 transition-colors">
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                width="18"
                                                height="18"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                stroke-width="2"
                                                stroke-linecap="round"
                                                stroke-linejoin="round"
                                                class="lucide lucide-trash-2 mr-3">
                                                <path d="M3 6h18" />
                                                <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
                                                <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
                                                <line x1="10" x2="10" y1="11" y2="17" />
                                                <line x1="14" x2="14" y1="11" y2="17" />
                                            </svg>
                                            Delete Post
                                        </a>
                                    </div>
                                </div>
                            <?php endif; ?>
                        </div>

                        <!-- Post Content -->
                        <?php if (!empty($post['post_text'])): ?>
                            <div class="px-6 pb-4">
                                <p class="text-gray-800 leading-relaxed"><?= htmlspecialchars($post['post_text']) ?></p>
                            </div>
                        <?php endif; ?>

                        <!-- Post Image -->
                        <?php if (!empty($img_src)): ?>
                            <div class="px-6 pb-4">
                                <img src="<?= htmlspecialchars($img_src) ?>"
                                    alt="Post image"
                                    class="w-full rounded-xl object-cover cursor-pointer hover:opacity-95 transition-opacity post-image"
                                    style="max-height: 500px;"
                                    onclick="openPostModal('<?= (int)$post['id'] ?>')">
                            </div>
                        <?php endif; ?>

                        <!-- If code post, show code preview, language and tags summary -->
                        <?php if ($post['code_status'] == 1): ?>
                            <div class="px-6 pb-4 bg-gray-50 rounded-lg border border-gray-200 relative mx-6 mb-4">
                                <pre class="font-mono text-sm whitespace-pre-wrap max-h-40 overflow-auto p-3 bg-gray-900 text-white rounded-md cursor-pointer"
                                    onclick="openPostModal('<?= (int)$post['id'] ?>')"
                                    data-code-content="<?= htmlspecialchars($post['code_content']) ?>">
                                    <code class="language-<?= strtolower(htmlspecialchars($post['code_language'])) ?>"><?= htmlspecialchars(substr($post['code_content'], 0, 300)) ?><?php if (strlen($post['code_content']) > 300) echo " ..."; ?></code>
                                </pre>
                                <button class="copy-code-btn absolute top-2 right-2 bg-gray-700 text-white px-2 py-1 rounded-md text-xs hover:bg-gray-600 transition-colors">Copy</button>
                                <p class="mt-2 text-xs text-gray-600 font-semibold">
                                    Language: <span class="text-purple-600"><?= htmlspecialchars($post['code_language']) ?></span> |
                                    Tags: <span class="text-purple-600"><?= htmlspecialchars($post['tags']) ?></span>
                                </p>
                            </div>
                        <?php endif; ?>

                        <div class="flex flex-col md:flex-row items-center justify-between px-6 py-4 border-t border-gray-100">
                            <div class="flex items-center space-x-8 mb-4 md:mb-0">
                                <!-- Like Section -->
                                <div class="flex items-center space-x-2">
                                    <?php
                                    if (checkLiked($post['id'])) {
                                        $like_btn_display = 'none';
                                        $unlike_btn_display = 'inline-block';
                                    } else {
                                        $like_btn_display = 'inline-block';
                                        $unlike_btn_display = 'none';
                                    }
                                    ?>
                                    <!-- Heart Icons -->
                                    <svg
                                        data-post-id="<?= (int)$post['id'] ?>"
                                        style="display:<?= $unlike_btn_display ?>;"
                                        class="unlike_btn text-red-500 text-xl cursor-pointer hover:scale-110 transition-transform duration-150 lucide lucide-heart-fill"
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="24"
                                        height="24"
                                        viewBox="0 0 24 24"
                                        fill="currentColor"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        stroke-linecap="round"
                                        stroke-linejoin="round">
                                        <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
                                    </svg>
                                    <svg
                                        data-post-id="<?= (int)$post['id'] ?>"
                                        style="display:<?= $like_btn_display ?>;"
                                        class="like-toggle-btn like_btn text-gray-500 text-xl cursor-pointer hover:text-red-500 transition-colors hover:scale-110 duration-150 lucide lucide-heart"
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="24"
                                        height="24"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        stroke-linecap="round"
                                        stroke-linejoin="round">
                                        <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
                                    </svg>
                                    <!-- Like Count -->
                                    <p class="text-sm font-medium text-gray-700 hover:text-red-500 cursor-pointer transition"
                                        onclick="openLikesModal('<?= (int)$post['id'] ?>')">
                                        <?= count($likes) ?> Likes
                                    </p>
                                </div>
                                <!-- Comment Section -->
                                <div class="flex items-center space-x-2 cursor-pointer"
                                    onclick="openPostModal('<?= (int)$post['id'] ?>')">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="24"
                                        height="24"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        class="lucide lucide-message-circle text-gray-500 hover:text-blue-500 text-xl transition-colors">
                                        <path d="M7.9 20A9 9 0 1 0 4 16.1L2 22Z" />
                                    </svg>
                                    <p class="text-sm font-medium text-gray-700 hover:text-blue-500 transition">
                                        <?= count($comments) ?> Comments
                                    </p>
                                </div>
                                <span class="text-xs text-gray-500">
                                    Posted <?= show_time($post['created_at']) ?>
                                </span>
                            </div>
                            <!-- Add Comment -->
                            <div class="flex items-center space-x-3 w-full md:w-auto">
                                <img src="./assets/img/profile/<?= htmlspecialchars($user['profile_pic']) ?>"
                                    class="w-8 h-8 rounded-full object-cover">
                                <div class="flex-1 flex items-center space-x-2">
                                    <input type="text"
                                        class="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-600 comment-input"
                                        placeholder="Write a comment...">
                                    <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors add-comment"
                                        data-cs="comment-section<?= (int)$post['id'] ?>"
                                        data-post-id="<?= (int)$post['id'] ?>">
                                        Post
                                    </button>
                                </div>
                            </div>
                        </div>
                    </article>
                    <!-- Post Modal -->
                    <div id="post-modal-<?= (int)$post['id'] ?>" class="modal-overlay fixed inset-0 bg-black bg-opacity-70 z-50 hidden items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-6xl w-full max-h-[90vh] flex flex-col md:flex-row overflow-hidden">
                            <!-- Left Section: Code and/or Image -->
                            <div class="flex-1 bg-gray-900 flex flex-col items-center justify-center space-y-4 p-4 overflow-auto">
                                <?php if ($post['code_status'] == 1): ?>
                                    <!-- Code block preview first -->
                                    <pre class="rounded-xl bg-gray-900 text-white font-mono p-4 max-h-[50vh] w-full overflow-auto relative"
                                        data-code-content="<?= htmlspecialchars($post['code_content']) ?>">
                                        <code class="language-<?= strtolower(htmlspecialchars($post['code_language'])) ?>"><?= htmlspecialchars($post['code_content']) ?></code>
                                        <button class="copy-code-btn absolute top-2 right-2 bg-gray-700 text-white px-2 py-1 rounded-md text-xs hover:bg-gray-600 transition-colors">Copy</button>
                                    </pre>
                                <?php endif; ?>
                                <?php if (!empty($img_src)): ?>
                                    <img src="<?= htmlspecialchars($img_src) ?>" alt="Post image" class="max-w-full max-h-[40vh] object-contain rounded-xl" />
                                <?php endif; ?>
                            </div>

                            <!-- Right Section: Comments -->
                            <div class="w-full md:w-96 flex flex-col border-l border-gray-200 bg-white">
                                <!-- Header -->
                                <div class="flex items-center justify-between p-4 border-b border-gray-100">
                                    <div class="flex items-center space-x-3">
                                        <img src="assets/img/profile/<?= htmlspecialchars($post['profile_pic']) ?>" class="w-10 h-10 rounded-full object-cover" />
                                        <div>
                                            <h6 class="font-semibold text-gray-900"><?= htmlspecialchars($post['first_name'] . ' ' . $post['last_name']) ?></h6>
                                            <p class="text-sm text-gray-600">@<?= htmlspecialchars($post['username']) ?></p>
                                        </div>
                                    </div>
                                    <button onclick="closePostModal('<?= (int)$post['id'] ?>')" class="p-2 hover:bg-gray-100 rounded-full">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            width="20"
                                            height="20"
                                            viewBox="0 0 24 24"
                                            fill="none"
                                            stroke="currentColor"
                                            stroke-width="2"
                                            stroke-linecap="round"
                                            stroke-linejoin="round"
                                            class="lucide lucide-x text-gray-500">
                                            <path d="M18 6 6 18" />
                                            <path d="m6 6 12 12" />
                                        </svg>
                                    </button>
                                </div>

                                <!-- Comments List -->
                                <div class="flex-1 overflow-y-auto p-4" id="comment-section<?= (int)$post['id'] ?>">
                                    <?php if (count($comments) < 1): ?>
                                        <p class="text-center text-gray-600 py-8">No comments yet</p>
                                    <?php endif; ?>
                                    <?php foreach ($comments as $comment):
                                        $cuser = getUser($comment['user_id']);
                                    ?>
                                        <div class="flex items-start space-x-4 mb-4">
                                            <img src="assets/img/profile/<?= htmlspecialchars($cuser['profile_pic']) ?>" class="w-16 h-16 rounded-full object-cover border-2 border-gray-200 shadow-sm" />
                                            <div class="flex-1 flex flex-col">
                                                <div class="bg-gray-100 rounded-2xl px-4 py-2">
                                                    <div class="flex items-baseline space-x-1">
                                                        <a href="?u=<?= htmlspecialchars($cuser['username']) ?>" class="font-bold text-gray-900 text-lg hover:opacity-80">
                                                            @<?= htmlspecialchars($cuser['username']) ?>
                                                        </a>
                                                        <span class="text-sm text-gray-600 ml-2">(<?= show_time($comment['created_at']) ?>)</span>
                                                    </div>
                                                    <p class="text-gray-800 text-base mt-1"><?= htmlspecialchars($comment['comment']) ?></p>
                                                </div>
                                            </div>
                                        </div>
                                    <?php endforeach; ?>
                                </div>

                                <!-- Add Comment -->
                                <div class="p-4 border-t border-gray-100">
                                    <div class="flex items-center space-x-2">
                                        <input type="text" class="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-600 comment-input" placeholder="Add a comment..." />
                                        <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors add-comment" data-cs="comment-section<?= (int)$post['id'] ?>" data-post-id="<?= (int)$post['id'] ?>">
                                            Post
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Likes Modal -->
                    <div id="likes-modal-<?= (int)$post['id'] ?>" class="modal-overlay fixed inset-0 bg-black bg-opacity-70 z-50 hidden items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-md w-full max-h-96 flex flex-col overflow-hidden">
                            <div class="flex items-center justify-between p-4 border-b border-gray-100">
                                <h5 class="text-lg font-bold text-gray-900">Likes</h5>
                                <button onclick="closeLikesModal('<?= (int)$post['id'] ?>')" class="p-2 hover:bg-gray-100 rounded-full">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="20"
                                        height="20"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        class="lucide lucide-x text-gray-500">
                                        <path d="M18 6 6 18" />
                                        <path d="m6 6 12 12" />
                                    </svg>
                                </button>
                            </div>
                            <div class="overflow-y-auto p-4">
                                <?php if (count($likes) < 1): ?>
                                    <p class="text-center text-gray-600 py-8">No likes yet</p>
                                <?php endif; ?>
                                <?php foreach ($likes as $like):
                                    $fuser = getUser($like['user_id']);
                                    $isFollowed = checkFollowed($like['user_id']);
                                ?>
                                    <div class="flex items-center justify-between py-3">
                                        <div class="flex items-center space-x-3">
                                            <img src="assets/img/profile/<?= htmlspecialchars($fuser['profile_pic']) ?>" class="w-10 h-10 rounded-full object-cover" />
                                            <div>
                                                <a href="?u=<?= htmlspecialchars($fuser['username']) ?>" class="block hover:opacity-80">
                                                    <p class="font-semibold text-gray-900"><?= htmlspecialchars($fuser['first_name'] . ' ' . $fuser['last_name']) ?></p>
                                                    <p class="text-sm text-gray-600">@<?= htmlspecialchars($fuser['username']) ?></p>
                                                </a>
                                            </div>
                                        </div>
                                        <?php if ($user['id'] != $like['user_id']): ?>
                                            <?php if ($isFollowed): ?>
                                                <button class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors unfollowbtn"
                                                    data-user-id="<?= (int)$fuser['id'] ?>">
                                                    Unfollow
                                                </button>
                                            <?php else: ?>
                                                <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors followbtn"
                                                    data-user-id="<?= (int)$fuser['id'] ?>">
                                                    Follow
                                                </button>
                                            <?php endif; ?>
                                        <?php endif; ?>
                                    </div>
                                <?php endforeach; ?>
                            </div>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </div>
</div>
<script>
    // GSAP Animations and modal scripts (your existing JS from earlier)
    gsap.from('.user-profile-card', {
        opacity: 0,
        y: 40,
        delay: 0.5
    });
    gsap.from('.suggestions-card', {
        opacity: 0,
        y: 40,
        delay: 0.7
    });
    gsap.from('.suggestion-item', {
        opacity: 0,
        y: 40,
        stagger: 0.15,
        delay: 1
    });
    gsap.from('.post-card', {
        opacity: 0,
        y: 40,
        stagger: 0.3,
        delay: 1.2
    });

    function toggleDropdown(id) {
        const dropdown = document.getElementById(id);
        if (dropdown.style.display === 'block') {
            dropdown.style.display = 'none';
        } else {
            dropdown.style.display = 'block';
        }
    }

    function openPostModal(id) {
        document.getElementById(`post-modal-${id}`).classList.remove('hidden');
        document.body.style.overflow = 'hidden'; // Disable page scroll when modal is open
        // Re-highlight code when modal opens, in case it wasn't visible before
        if (typeof Prism !== 'undefined') {
            Prism.highlightAll();
        }
    }

    function closePostModal(id) {
        document.getElementById(`post-modal-${id}`).classList.add('hidden');
        document.body.style.overflow = 'auto'; // Enable page scroll back
    }

    function openLikesModal(id) {
        document.getElementById(`likes-modal-${id}`).classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeLikesModal(id) {
        document.getElementById(`likes-modal-${id}`).classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    // Initialize PrismJS highlighting on page load
    document.addEventListener('DOMContentLoaded', () => {
        // Ensure PrismJS is loaded and available
        if (typeof Prism !== 'undefined') {
            Prism.highlightAll();
        } else {
            console.warn('PrismJS not found. Code highlighting will not work.');
        }
        // Copy code button functionality
        document.querySelectorAll('.copy-code-btn').forEach(button => {
            button.addEventListener('click', async (event) => {
                // Prevent the modal from opening when clicking the copy button
                event.stopPropagation();
                const preElement = event.target.closest('pre');
                if (preElement) {
                    const codeContent = preElement.dataset.codeContent;
                    try {
                        await navigator.clipboard.writeText(codeContent);
                        const originalText = button.textContent;
                        button.textContent = 'Copied!';
                        setTimeout(() => {
                            button.textContent = originalText;
                        }, 2000);
                    } catch (err) {
                        console.error('Failed to copy text: ', err);
                        alert('Failed to copy code.');
                    }
                }
            });
        });
    });
    // You can add your add-comment, like/unlike, follow/unfollow button handlers here
    // MODIFIED: Like button animation and logic to send AJAX request
    document.querySelectorAll('.like-toggle-btn').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const iconSvg = this.querySelector('svg'); // Select the SVG element
            const likesCountSpan = document.querySelector(`.likes-count-${postId}`);
            let action;

            // Determine action based on current icon class
            if (iconSvg.classList.contains('lucide-heart')) { // Currently unliked (outline heart)
                action = 'like';
            } else { // Currently liked (filled heart)
                action = 'unlike';
            }

            // GSAP heart animation (before fetch, for immediate visual feedback)
            gsap.fromTo(iconSvg, {
                scale: 1
            }, {
                scale: 1.4,
                duration: 0.15,
                yoyo: true,
                repeat: 1,
                ease: "power2.out"
            });

            // Heart burst effect (only for liking)
            if (action === 'like') {
                for (let i = 0; i < 6; i++) {
                    const particle = document.createElement('div');
                    particle.innerHTML = '❤️';
                    particle.style.position = 'absolute';
                    particle.style.pointerEvents = 'none';
                    particle.style.fontSize = '12px';
                    particle.style.zIndex = '1000';
                    const rect = iconSvg.getBoundingClientRect();
                    particle.style.left = rect.left + rect.width / 2 + 'px';
                    particle.style.top = rect.top + rect.height / 2 + 'px';
                    document.body.appendChild(particle);
                    gsap.to(particle, {
                        duration: 1,
                        x: (Math.random() - 0.5) * 100,
                        y: -50 - Math.random() * 50,
                        opacity: 0,
                        scale: 0,
                        ease: "power2.out",
                        onComplete: () => particle.remove()
                    });
                }
            }
            // Send AJAX request
            fetch(`assets/php/actions.php?toggle_like=1&post_id=${postId}&action=${action}`)
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'success') {
                        // Update icon classes based on the action performed
                        if (action === 'like') {
                            iconSvg.classList.remove('lucide-heart', 'text-gray-500', 'hover:text-red-500');
                            iconSvg.classList.add('lucide-heart-fill', 'text-red-500');
                            iconSvg.setAttribute('fill', 'currentColor'); // Ensure fill is set for filled heart
                        } else {
                            iconSvg.classList.remove('lucide-heart-fill', 'text-red-500');
                            iconSvg.classList.add('lucide-heart', 'text-gray-500', 'hover:text-red-500');
                            iconSvg.setAttribute('fill', 'none'); // Ensure fill is none for outline heart
                        }
                        // Update like count immediately
                        if (likesCountSpan) {
                            likesCountSpan.textContent = data.likes_count;
                        }
                    } else {
                        console.error('Error toggling like:', data.message);
                        // Optionally revert animation or show error to user if the server call failed
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    // Handle network errors
                });
        });
    });
    // NEW: Comment button logic
    document.querySelectorAll('.add-comment').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const commentSectionId = this.dataset.cs;
            const commentInput = this.parentElement.querySelector('.comment-input');
            const commentText = commentInput.value.trim();
            if (commentText === '') {
                alert('Comment cannot be empty.');
                return;
            }
            // Send AJAX request to add comment
            fetch('assets/php/actions.php?add_comment=1', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `post_id=${postId}&comment_text=${encodeURIComponent(commentText)}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'success') {
                        commentInput.value = ''; // Clear input field
                        // Dynamically add comment to the comment section
                        const commentSection = document.getElementById(commentSectionId);
                        if (commentSection) {
                            const newCommentHtml = `
                            <div class="flex items-start space-x-4 mb-4">
                                <img src="assets/img/profile/${data.user_profile_pic}"
                                    class="w-16 h-16 rounded-full object-cover border-2 border-gray-200 shadow-sm">
                                <div class="flex-1 flex flex-col">
                                    <div class="bg-gray-100 rounded-2xl px-4 py-2">
                                        <div class="flex items-baseline space-x-1">
                                            <a href="?u=${data.username}" class="font-bold text-gray-900 text-lg hover:opacity-80">
                                                @${data.username}
                                            </a>
                                            <span class="text-sm text-gray-600 ml-2">(${data.time_ago})</span>
                                        </div>
                                        <p class="text-gray-800 text-base mt-1">${data.comment_text}</p>
                                    </div>
                                </div>
                            </div>
                        `;
                            commentSection.insertAdjacentHTML('beforeend', newCommentHtml);
                            // Scroll to the bottom of the comment section
                            commentSection.scrollTop = commentSection.scrollHeight;
                        }
                    } else {
                        console.error('Error adding comment:', data.message);
                       
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                   
                });
        });
    });
    // NEW: Like/Unlike button logic
    document.querySelectorAll('.like_btn').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const likeButton = this;
            const unlikeButton = this.parentElement.querySelector('.unlike_btn');
            // Send AJAX request to toggle like
            fetch(`assets/php/actions.php?toggle_like=1&post_id=${postId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'success') {
                        // Toggle button visibility
                        if (likeButton.style.display === 'none') {
                            likeButton.style.display = 'inline-block';
                            unlikeButton.style.display = 'none';
                        } else {
                            likeButton.style.display = 'none';
                            unlikeButton.style.display = 'inline-block';
                        }
                    } else {
                        console.error('Error toggling like:', data.message);
                      
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                   
                });
        });
    });
    // Smooth scroll for better UX
    document.documentElement.style.scrollBehavior = 'smooth';
</script>