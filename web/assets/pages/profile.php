<?php
global $profile;
global $profile_post;
global $user;
?>
<div>
 

<div class="pt-16 max-w-6xl mx-auto mt-10">
        <div class="flex flex-col md:flex-row items-center md:items-start gap-6 border-b border-gray-300 pb-6">
            <!-- Profile Picture -->
            <div class="flex-shrink-0">
                <img src="assets/img/profile/<?= htmlspecialchars($profile['profile_pic']) ?>" alt="Profile Picture"
                    class="w-32 h-32 rounded-full object-cover border-2 border-gray-300">
            </div>

            <!-- Profile Info -->
            <div class="flex-1 w-full">
                <div class="flex items-center justify-between">
                    <h2 class="text-3xl font-semibold capitalize"><?= htmlspecialchars($profile['first_name']) ?> <?= htmlspecialchars($profile['last_name']) ?></h2>

                    <div class="flex items-center gap-3">

                        <?php if ($user['id'] != $profile['id'] && !checkBS($profile['id'])): ?>
                            <?php if (checkFollowed($profile['id'])): ?>
                                <button class="border-2 border-black text-black font-semibold px-4 py-1 rounded-md unfollowbtn"
                                    data-user-id="<?= htmlspecialchars($profile['id']) ?>">Unfollow</button>
                            <?php else: ?>
                                <button class="bg-black text-white font-semibold px-4 py-1 rounded-md followbtn"
                                    data-user-id="<?= htmlspecialchars($profile['id']) ?>">Follow</button>
                            <?php endif; ?>
                        <?php endif; ?>

                        <?php if ($user['id'] != $profile['id'] && !checkBS($profile['id'])): ?>
                            <div class="relative inline-block text-left">
                                <button type="button" class="text-3xl p-1 hover:text-gray-700 focus:outline-none" id="menu-button" aria-expanded="true" aria-haspopup="true">
                                    <i class="fa-solid fa-ellipsis"></i>
                                </button>
                                <!-- Dropdown menu -->
                                <div class="origin-top-right absolute right-0 mt-2 w-40 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50 hidden" role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabindex="-1" id="dropdown-menu">
                                    <a href="#" 
                                       data-bs-toggle="modal" data-bs-target="#chatbox" 
                                       onclick="popchat(<?= htmlspecialchars($profile['id']) ?>)" 
                                       class="block px-4 py-2 text-gray-700 hover:bg-gray-100" role="menuitem" tabindex="-1">
                                       <i class="fa-solid fa-comment mr-2"></i> Message
                                    </a>
                                    <a href="assets/php/actions.php?block=<?= htmlspecialchars($profile['id']) ?>&username=<?= htmlspecialchars($profile['username']) ?>"
                                       class="block px-4 py-2 text-gray-700 hover:bg-gray-100" role="menuitem" tabindex="-1">
                                       <i class="fa-solid fa-user-ninja mr-2"></i> Block
                                    </a>
                                </div>
                            </div>
                        <?php endif; ?>
                    </div>
                </div>

                <!-- Stats -->
                <div class="flex space-x-6 mt-4 text-gray-700">
                    <span><span class="font-semibold"><?= count($profile_post) ?></span> posts</span>
                    <span class="cursor-pointer" data-bs-toggle="modal" data-bs-target="#follower_list">
                        <span class="font-semibold"><?= count($profile['followers']) ?></span> followers
                    </span>
                    <span class="cursor-pointer" data-bs-toggle="modal" data-bs-target="#following_list">
                        <span class="font-semibold"><?= count($profile['following']) ?></span> following
                    </span>
                </div>

                <!-- Bio -->
                <div class="mt-3 text-lg font-mono text-gray-600">@<?= htmlspecialchars($profile['username']) ?></div>

                <!-- Unblock button if blocked -->
                <?php if (checkBlockStatus($user['id'], $profile['id'])): ?>
                    <button class="mt-4 bg-red-600 text-white px-3 py-1 rounded-md unblockbtn" data-user-id="<?= htmlspecialchars($profile['id']) ?>">Unblock</button>
                <?php endif; ?>
            </div>
        </div>

        <!-- Navigation -->
        <nav class="border-b border-gray-300 mt-6">
            <h2 class="text-xl font-semibold py-3">POSTS</h2>
        </nav>

        <!-- Posts Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
            <?php
            if (checkBS($profile['id'])) {
                $profile_post = [];
            ?>
                <div class="col-span-full bg-gray-100 p-4 rounded-md text-center text-gray-600">
                    <i class="bi bi-x-octagon-fill text-3xl block mb-2"></i>
                    You are not allowed to see posts!
                </div>
            <?php
            } else if (count($profile_post) < 1 && $user['id'] == $profile['id']) {
                ?>
                <div class="col-span-full flex flex-col items-center justify-center py-20 border border-dashed border-gray-400 rounded-md text-gray-500">
                    <h2 class="text-3xl font-semibold mb-3">Share Photos</h2>
                    <p class="mb-4 text-center max-w-xs">When you share photos, they will appear on your profile.</p>
                    <label for="addpost" class="cursor-pointer bg-black text-white px-6 py-2 rounded-md hover:bg-gray-800 transition">Share your first photo</label>
                </div>
                <?php
            } else if (count($profile_post) < 1) {
                ?>
                <div class="col-span-full bg-gray-100 p-4 rounded-md text-center text-gray-600">
                    <i class="bi bi-x-octagon-fill text-3xl block mb-2"></i>
                    The user has no post!
                </div>
                <?php
            } else {
                foreach ($profile_post as $post) {
                    $likes = getLikesCount($post['id']);
                    $comments = getComments($post['id']);
                ?>
                    <div class="overflow-hidden rounded-md aspect-[16/9] cursor-pointer group relative" data-bs-toggle="modal" data-bs-target="#postview<?= htmlspecialchars($post['id']) ?>">
                        <img src="assets/img/posts/<?= htmlspecialchars($post['post_img']) ?>" alt="Post Image"
                            class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105">
                    </div>

                   <!-- Modal -->
<div class="modal fade" id="postview<?= htmlspecialchars($post['id']) ?>" tabindex="-1" aria-labelledby="postviewLabel<?= htmlspecialchars($post['id']) ?>" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered max-w-7xl">
        <div class="modal-content rounded-md overflow-hidden shadow-lg">
            <div class="modal-body flex flex-col md:flex-row p-0 h-[90vh]"> <!-- full height modal -->

                <!-- Left: Post image -->
                <div class="md:w-2/3 w-full bg-black flex items-center justify-center">
                    <img src="assets/img/posts/<?= htmlspecialchars($post['post_img']) ?>" alt="Post Image" class="max-h-[90vh] object-contain w-full">
                </div>

                <!-- Right: Comment and info panel -->
                <div class="md:w-1/3 w-full flex flex-col bg-white border-l border-gray-200">

                    <!-- Header -->
                    <div class="flex items-center p-4 border-b border-gray-300 flex-shrink-0">
                        <img src="assets/img/profile/<?= htmlspecialchars($profile['profile_pic']) ?>" alt="Profile Pic" class="w-12 h-12 rounded-full border border-gray-300 object-cover">
                        <div class="ml-4 flex-1">
                            <h6 class="font-semibold text-lg mb-0"><?= htmlspecialchars($profile['first_name']) ?> <?= htmlspecialchars($profile['last_name']) ?></h6>
                            <p class="text-gray-500 text-sm">@<?= htmlspecialchars($profile['username']) ?></p>
                        </div>
                        <div class="relative text-right">
                            <button class="font-medium text-gray-700 <?= count($likes) < 1 ? 'opacity-50 cursor-not-allowed' : '' ?>" id="likesDropdownBtn<?= htmlspecialchars($post['id']) ?>" data-bs-toggle="dropdown" aria-expanded="false">
                                <?= count($likes) ?> likes
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end max-h-48 overflow-auto" aria-labelledby="likesDropdownBtn<?= htmlspecialchars($post['id']) ?>">
                                <?php
                                foreach ($likes as $like) {
                                    $lu = getUser($like['user_id']);
                                ?>
                                    <li>
                                        <a href="?u=<?= htmlspecialchars($lu['username']) ?>" class="block px-4 py-2 hover:bg-gray-100">
                                            <?= htmlspecialchars($lu['first_name'] . ' ' . $lu['last_name']) ?> (@<?= htmlspecialchars($lu['username']) ?>)
                                        </a>
                                    </li>
                                <?php } ?>
                            </ul>
                            <div class="text-xs text-gray-400 mt-1">Posted <?= show_time($profile['created_at']) ?></div>
                        </div>
                    </div>

                    <!-- Comments scrollable area -->
                    <div id="comment-section<?= htmlspecialchars($post['id']) ?>" class="flex-1 overflow-y-auto px-4 py-2">
                        <?php if (count($comments) < 1): ?>
                            <p class="text-center text-gray-400 py-4">No comments</p>
                        <?php endif; ?>
                        <?php foreach ($comments as $comment):
                            $cuser = getUser($comment['user_id']);
                        ?>
                            <div class="flex items-center gap-3 mb-3">
                                <img src="assets/img/profile/<?= htmlspecialchars($cuser['profile_pic']) ?>" alt="Commenter Pic" class="w-10 h-10 rounded-full border border-gray-300 object-cover">
                                <div>
                                    <p class="text-sm font-semibold mb-0">
                                        <a href="?u=<?= htmlspecialchars($cuser['username']) ?>" class="text-gray-700 hover:underline">
                                            @<?= htmlspecialchars($cuser['username']) ?>
                                        </a> - <?= htmlspecialchars($comment['comment']) ?>
                                    </p>
                                    <p class="text-xs text-gray-400 mb-0">(<?= show_time($comment['created_at']) ?>)</p>
                                </div>
                            </div>
                        <?php endforeach; ?>
                    </div>

                    <!-- Comment input fixed at bottom -->
                    <div class="border-t border-gray-300 p-3 flex gap-2 flex-shrink-0">
                        <input type="text" class="flex-1 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 comment-input" placeholder="Say something.."
                            aria-label="Add comment" />
                        <button class="bg-blue-600 text-white px-4 py-2 rounded-md add-comment" data-cs="comment-section<?= htmlspecialchars($post['id']) ?>" data-post-id="<?= htmlspecialchars($post['id']) ?>">Post</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

                <?php
                }
            }
            ?>
        </div>
    </div>

    <!-- Follower List Modal -->
    <div class="modal fade" id="follower_list" tabindex="-1" aria-labelledby="followerListLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered max-w-lg">
            <div class="modal-content rounded-md shadow-lg">
                <div class="modal-header flex justify-between items-center p-4 border-b border-gray-200">
                    <h5 class="text-xl font-semibold">Followers</h5>
                    <button type="button" class="text-gray-600 hover:text-gray-900 focus:outline-none" data-bs-dismiss="modal" aria-label="Close">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                    </button>
                </div>
                <div class="modal-body max-h-96 overflow-y-auto p-4 space-y-4">
                    <?php foreach ($profile['followers'] as $f):
                        $fuser = getUser($f['follower_id']);
                        $fbtn = "";
                        if (checkFollowed($f['follower_id'])) {
                            $fbtn = '<button class="bg-red-600 text-white px-3 py-1 rounded-md unfollowbtn" data-user-id="'.htmlspecialchars($fuser['id']).'">Unfollow</button>';
                        } else if ($user['id'] == $f['follower_id']) {
                            $fbtn = "";
                        } else {
                            $fbtn = '<button class="bg-blue-600 text-white px-3 py-1 rounded-md followbtn" data-user-id="'.htmlspecialchars($fuser['id']).'">Follow</button>';
                        }
                    ?>
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-4">
                                <img src="assets/img/profile/<?= htmlspecialchars($fuser['profile_pic']) ?>" alt="Follower Pic" class="w-12 h-12 rounded-full object-cover border border-gray-300">
                                <div>
                                    <a href="?u=<?= htmlspecialchars($fuser['username']) ?>" class="font-semibold text-gray-800 hover:underline"><?= htmlspecialchars($fuser['first_name'] . ' ' . $fuser['last_name']) ?></a>
                                    <p class="text-gray-500">@<?= htmlspecialchars($fuser['username']) ?></p>
                                </div>
                            </div>
                            <div><?= $fbtn ?></div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </div>
        </div>
    </div>

    <!-- Following List Modal -->
    <div class="modal fade" id="following_list" tabindex="-1" aria-labelledby="followingListLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered max-w-lg">
            <div class="modal-content rounded-md shadow-lg">
                <div class="modal-header flex justify-between items-center p-4 border-b border-gray-200">
                    <h5 class="text-xl font-semibold">Following</h5>
                    <button type="button" class="text-gray-600 hover:text-gray-900 focus:outline-none" data-bs-dismiss="modal" aria-label="Close">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                    </button>
                </div>
                <div class="modal-body max-h-96 overflow-y-auto p-4 space-y-4">
                    <?php foreach ($profile['following'] as $f):
                        $fuser = getUser($f['user_id']);
                        $fbtn = "";
                        if (checkFollowed($f['user_id'])) {
                            $fbtn = '<button class="bg-red-600 text-white px-3 py-1 rounded-md unfollowbtn" data-user-id="'.htmlspecialchars($fuser['id']).'">Unfollow</button>';
                        } else if ($user['id'] == $f['user_id']) {
                            $fbtn = "";
                        } else {
                            $fbtn = '<button class="bg-blue-600 text-white px-3 py-1 rounded-md followbtn" data-user-id="'.htmlspecialchars($fuser['id']).'">Follow</button>';
                        }
                    ?>
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-4">
                                <img src="assets/img/profile/<?= htmlspecialchars($fuser['profile_pic']) ?>" alt="Following Pic" class="w-12 h-12 rounded-full object-cover border border-gray-300">
                                <div>
                                    <a href="?u=<?= htmlspecialchars($fuser['username']) ?>" class="font-semibold text-gray-800 hover:underline"><?= htmlspecialchars($fuser['first_name'] . ' ' . $fuser['last_name']) ?></a>
                                    <p class="text-gray-500">@<?= htmlspecialchars($fuser['username']) ?></p>
                                </div>
                            </div>
                            <div><?= $fbtn ?></div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </div>
        </div>
    </div>

    <!-- Add your chatbox modal & other modals here if needed -->

    <script>
        // Dropdown menu toggle logic for the ellipsis menu
        document.querySelectorAll('[id="menu-button"]').forEach(button => {
            button.addEventListener('click', function (e) {
                e.preventDefault();
                const dropdown = this.nextElementSibling;
                dropdown.classList.toggle('hidden');
            });
        });

        // Close dropdown if clicked outside
        window.addEventListener('click', function(e) {
            document.querySelectorAll('#dropdown-menu').forEach(menu => {
                if (!menu.classList.contains('hidden')) {
                    if (!menu.previousElementSibling.contains(e.target)) {
                        menu.classList.add('hidden');
                    }
                }
            });
        });
    </script>

</div>
