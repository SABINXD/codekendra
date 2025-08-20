<?php
global $profile;
global $profile_post;
global $user;
?>
<div class="min-h-screen bg-gray-50 pt-16">
    <!-- Cover Photo Section -->
    <div class="relative">
        <div class="h-48 md:h-64 bg-gradient-to-r from-purple-500 to-indigo-600 relative overflow-hidden">
            <div class="absolute inset-0 bg-black opacity-20"></div>
            <div class="absolute inset-0 flex items-center justify-center">
                <div class="text-white text-center">
                    <h1 class="text-3xl md:text-4xl font-bold"><?= htmlspecialchars($profile['first_name']) ?> <?= htmlspecialchars($profile['last_name']) ?></h1>
                    <p class="text-lg opacity-90">@<?= htmlspecialchars($profile['username']) ?></p>
                </div>
            </div>
        </div>
        <!-- Profile Picture -->
        <div class="absolute -bottom-16 left-6 md:left-12">
            <div class="relative">
                <img src="assets/img/profile/<?= htmlspecialchars($profile['profile_pic']) ?>" alt="Profile Picture"
                    class="w-32 h-32 rounded-full object-cover border-4 border-white shadow-lg"
                    onerror="this.onerror=null; this.src='assets/img/profile/default-avatar.png'">
                <div class="absolute bottom-0 right-0 bg-green-500 w-8 h-8 rounded-full border-2 border-white flex items-center justify-center">
                    <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                </div>
            </div>
        </div>
    </div>
    <!-- Profile Info Section -->
    <div class="max-w-6xl mx-auto mt-20 px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-6 border-b border-gray-200">
            <div class="mt-2">
                <h2 class="text-2xl font-bold text-gray-900"><?= htmlspecialchars($profile['first_name']) ?> <?= htmlspecialchars($profile['last_name']) ?></h2>
                <div class="flex items-center gap-2 mt-1">
                    <span class="text-gray-600">@<?= htmlspecialchars($profile['username']) ?></span>
                    <?php if (!empty($profile['is_verified']) && $profile['is_verified']): ?>
                        <svg class="w-5 h-5 text-blue-500" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                            <path fill-rule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                        </svg>
                    <?php endif; ?>
                </div>
                <?php if (!empty($profile['bio'])): ?>
                    <p class="mt-3 text-gray-700"><?= htmlspecialchars($profile['bio']) ?></p>
                <?php endif; ?>
                <!-- Stats -->
                <div class="flex flex-wrap gap-4 mt-4">
                    <div class="flex items-center gap-1">
                        <span class="font-semibold text-gray-900"><?= count($profile_post) ?></span>
                        <span class="text-gray-600">posts</span>
                    </div>
                    <div class="flex items-center gap-1 cursor-pointer" data-bs-toggle="modal" data-bs-target="#follower_list">
                        <span class="font-semibold text-gray-900"><?= count($profile['followers']) ?></span>
                        <span class="text-gray-600">followers</span>
                    </div>
                    <div class="flex items-center gap-1 cursor-pointer" data-bs-toggle="modal" data-bs-target="#following_list">
                        <span class="font-semibold text-gray-900"><?= count($profile['following']) ?></span>
                        <span class="text-gray-600">following</span>
                    </div>
                </div>
            </div>
            <!-- Action Buttons -->
            <div class="flex flex-wrap gap-3 w-full md:w-auto">
                <?php if ($user['id'] == $profile['id']): ?>
                    <a href="?editprofile" class="flex-1 md:flex-none bg-white border border-gray-300 text-gray-700 font-medium py-2 px-4 rounded-md hover:bg-gray-50 transition-colors duration-200 flex items-center justify-center gap-2">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                        </svg>
                        Edit Profile
                    </a>
                <?php else: ?>
                    <?php if (checkBlockStatus($user['id'], $profile['id'])): ?>
                        <button class="flex-1 md:flex-none bg-red-600 text-white font-medium py-2 px-4 rounded-md hover:bg-red-700 transition-colors duration-200 unblockbtn" data-user-id="<?= htmlspecialchars($profile['id']) ?>">
                            Unblock
                        </button>
                    <?php elseif (checkBS($profile['id'])): ?>
                        <div class="flex-1 md:flex-none bg-gray-100 text-gray-500 font-medium py-2 px-4 rounded-md cursor-not-allowed">
                            Blocked
                        </div>
                    <?php else: ?>
                        <?php if (checkFollowed($profile['id'])): ?>
                            <button class="flex-1 md:flex-none bg-white border border-gray-300 text-gray-700 font-medium py-2 px-4 rounded-md hover:bg-gray-50 transition-colors duration-200 unfollowbtn" data-user-id="<?= htmlspecialchars($profile['id']) ?>">
                                Following
                            </button>
                        <?php else: ?>
                            <button class="flex-1 md:flex-none bg-black text-white font-medium py-2 px-4 rounded-md hover:bg-gray-800 transition-colors duration-200 followbtn" data-user-id="<?= htmlspecialchars($profile['id']) ?>">
                                Follow
                            </button>
                        <?php endif; ?>
                        <div class="relative inline-block text-left">
                            <button type="button" class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full transition-colors duration-200" id="menu-button" aria-expanded="true" aria-haspopup="true">
                                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"></path>
                                </svg>
                            </button>
                            <!-- Dropdown menu -->
                            <div class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50 hidden" role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabindex="-1" id="dropdown-menu">
                                <a href="#"
                                    data-bs-toggle="modal" data-bs-target="#chatbox"
                                    onclick="popchat(<?= htmlspecialchars($profile['id']) ?>)"
                                    class="block px-4 py-3 text-gray-700 hover:bg-gray-100 flex items-center gap-2" role="menuitem" tabindex="-1">
                                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                                    </svg>
                                    Message
                                </a>
                                <a href="assets/php/actions.php?block=<?= htmlspecialchars($profile['id']) ?>&username=<?= htmlspecialchars($profile['username']) ?>"
                                    class="block px-4 py-3 text-gray-700 hover:bg-gray-100 flex items-center gap-2" role="menuitem" tabindex="-1">
                                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path>
                                    </svg>
                                    Block
                                </a>
                            </div>
                        </div>
                    <?php endif; ?>
                <?php endif; ?>
            </div>
        </div>
        <!-- Navigation Tabs -->
        <div class="border-b border-gray-200">
            <nav class="-mb-px flex space-x-8" aria-label="Tabs">
                <a href="#" class="border-purple-500 text-purple-600 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
                    <svg class="w-5 h-5 inline-block mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
                    </svg>
                    POSTS
                </a>
                <a href="#" class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
                    <svg class="w-5 h-5 inline-block mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
                    </svg>
                    SAVED
                </a>
                <a href="#" class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
                    <svg class="w-5 h-5 inline-block mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
                    TAGGED
                </a>
            </nav>
        </div>

        <?php
        // Check if account is private and user is not following
        if (checkBS($profile['id'])) {
            $profile_post = [];
        ?>
            <div class="bg-white rounded-lg shadow-sm p-8 text-center">
                <div class="flex justify-center mb-4">
                    <svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                    </svg>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-1">This account is private</h3>
                <p class="text-gray-500">Follow this account to see their photos and videos.</p>
            </div>
        <?php
        } else if (count($profile_post) < 1 && $user['id'] == $profile['id']) {
        ?>
            <div class="bg-white rounded-lg shadow-sm p-8 text-center">
                <div class="flex justify-center mb-4">
                    <svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                    </svg>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-1">Share Photos</h3>
                <p class="text-gray-500 mb-4">When you share photos, they will appear on your profile.</p>
                <label for="addpost" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 cursor-pointer">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                    </svg>
                    Share your first photo
                </label>
            </div>
        <?php
        } else if (count($profile_post) < 1) {
        ?>
            <div class="bg-white rounded-lg shadow-sm p-8 text-center">
                <div class="flex justify-center mb-4">
                    <svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-1">No Posts Yet</h3>
                <p class="text-gray-500">When they post, you'll see their photos and videos here.</p>
            </div>
        <?php
        } else {
        ?>
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
        <?php
        }
        ?>
    </div>
</div>
<div class="py-6">
    <!-- Back to top button -->
    <button id="backToTop" class="fixed bottom-8 right-8 bg-purple-600 text-white p-3 rounded-full shadow-lg hover:bg-purple-700 transition-all duration-300 opacity-0 invisible">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
        </svg>
    </button>
    <!-- Follower List Modal -->
    <div class="modal fade" id="follower_list" tabindex="-1" aria-labelledby="followerListLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content rounded-lg overflow-hidden">
                <div class="modal-header flex justify-between items-center p-4 border-b border-gray-200">
                    <h5 class="text-xl font-bold text-gray-900">Followers</h5>
                    <button type="button" class="text-gray-400 hover:text-gray-500 focus:outline-none" data-bs-dismiss="modal" aria-label="Close">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                </div>
                <div class="modal-body max-h-96 overflow-y-auto p-0">
                    <?php if (count($profile['followers']) < 1): ?>
                        <div class="p-8 text-center">
                            <svg class="w-16 h-16 mx-auto text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                            </svg>
                            <h3 class="mt-4 text-lg font-medium text-gray-900">No followers yet</h3>
                            <p class="mt-1 text-gray-500">When someone follows this account, they'll appear here.</p>
                        </div>
                    <?php else: ?>
                        <ul class="divide-y divide-gray-200">
                            <?php foreach ($profile['followers'] as $f):
                                $fuser = getUser($f['follower_id']);
                                $fbtn = "";
                                if (checkFollowed($f['follower_id'])) {
                                    $fbtn = '<button class="bg-white border border-gray-300 text-gray-700 text-sm font-medium py-1 px-3 rounded-md hover:bg-gray-50 transition-colors duration-200 unfollowbtn" data-user-id="' . htmlspecialchars($fuser['id']) . '">Following</button>';
                                } else if ($user['id'] == $f['follower_id']) {
                                    $fbtn = "";
                                } else {
                                    $fbtn = '<button class="bg-black text-white text-sm font-medium py-1 px-3 rounded-md hover:bg-gray-800 transition-colors duration-200 followbtn" data-user-id="' . htmlspecialchars($fuser['id']) . '">Follow</button>';
                                }
                            ?>
                                <li class="p-4 hover:bg-gray-50">
                                    <div class="flex items-center justify-between">
                                        <div class="flex items-center gap-3">
                                            <img src="assets/img/profile/<?= htmlspecialchars($fuser['profile_pic']) ?>" alt="Follower Pic" class="w-12 h-12 rounded-full object-cover"
                                                onerror="this.onerror=null; this.src='assets/img/profile/default-avatar.png'">
                                            <div>
                                                <a href="?u=<?= htmlspecialchars($fuser['username']) ?>" class="font-semibold text-gray-900 hover:underline"><?= htmlspecialchars($fuser['first_name'] . ' ' . $fuser['last_name']) ?></a>
                                                <p class="text-sm text-gray-500">@<?= htmlspecialchars($fuser['username']) ?></p>
                                            </div>
                                        </div>
                                        <div><?= $fbtn ?></div>
                                    </div>
                                </li>
                            <?php endforeach; ?>
                        </ul>
                    <?php endif; ?>
                </div>
            </div>
        </div>
    </div>
    <!-- Following List Modal -->
    <div class="modal fade" id="following_list" tabindex="-1" aria-labelledby="followingListLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content rounded-lg overflow-hidden">
                <div class="modal-header flex justify-between items-center p-4 border-b border-gray-200">
                    <h5 class="text-xl font-bold text-gray-900">Following</h5>
                    <button type="button" class="text-gray-400 hover:text-gray-500 focus:outline-none" data-bs-dismiss="modal" aria-label="Close">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                </div>
                <div class="modal-body max-h-96 overflow-y-auto p-0">
                    <?php if (count($profile['following']) < 1): ?>
                        <div class="p-8 text-center">
                            <svg class="w-16 h-16 mx-auto text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283-.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                            </svg>
                            <h3 class="mt-4 text-lg font-medium text-gray-900">Not following anyone</h3>
                            <p class="mt-1 text-gray-500">When they follow someone, they'll appear here.</p>
                        </div>
                    <?php else: ?>
                        <ul class="divide-y divide-gray-200">
                            <?php foreach ($profile['following'] as $f):
                                $fuser = getUser($f['user_id']);
                                $fbtn = "";
                                if (checkFollowed($f['user_id'])) {
                                    $fbtn = '<button class="bg-white border border-gray-300 text-gray-700 text-sm font-medium py-1 px-3 rounded-md hover:bg-gray-50 transition-colors duration-200 unfollowbtn" data-user-id="' . htmlspecialchars($fuser['id']) . '">Following</button>';
                                } else if ($user['id'] == $f['user_id']) {
                                    $fbtn = "";
                                } else {
                                    $fbtn = '<button class="bg-black text-white text-sm font-medium py-1 px-3 rounded-md hover:bg-gray-800 transition-colors duration-200 followbtn" data-user-id="' . htmlspecialchars($fuser['id']) . '">Follow</button>';
                                }
                            ?>
                                <li class="p-4 hover:bg-gray-50">
                                    <div class="flex items-center justify-between">
                                        <div class="flex items-center gap-3">
                                            <img src="assets/img/profile/<?= htmlspecialchars($fuser['profile_pic']) ?>" alt="Following Pic" class="w-12 h-12 rounded-full object-cover"
                                                onerror="this.onerror=null; this.src='assets/img/profile/default-avatar.png'">
                                            <div>
                                                <a href="?u=<?= htmlspecialchars($fuser['username']) ?>" class="font-semibold text-gray-900 hover:underline"><?= htmlspecialchars($fuser['first_name'] . ' ' . $fuser['last_name']) ?></a>
                                                <p class="text-sm text-gray-500">@<?= htmlspecialchars($fuser['username']) ?></p>
                                            </div>
                                        </div>
                                        <div><?= $fbtn ?></div>
                                    </div>
                                </li>
                            <?php endforeach; ?>
                        </ul>
                    <?php endif; ?>
                </div>
            </div>
        </div>
    </div>
    <!-- Add your chatbox modal & other modals here if needed -->
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            // Dropdown menu toggle logic for the ellipsis menu
            document.querySelectorAll('[id="menu-button"]').forEach(button => {
                button.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
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
            // Back to top button
            const backToTopButton = document.getElementById('backToTop');
            window.addEventListener('scroll', () => {
                if (window.scrollY > 300) {
                    backToTopButton.classList.remove('opacity-0', 'invisible');
                    backToTopButton.classList.add('opacity-100', 'visible');
                } else {
                    backToTopButton.classList.add('opacity-0', 'invisible');
                    backToTopButton.classList.remove('opacity-100', 'visible');
                }
            });
            backToTopButton.addEventListener('click', () => {
                window.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });
            });
            // Like functionality
            document.querySelectorAll('[class^="like-btn-"]').forEach(button => {
                button.addEventListener('click', function() {
                    const postId = this.getAttribute('data-post-id');
                    const userLiked = this.getAttribute('data-user-liked') === 'true';
                    // Send AJAX request
                    fetch('assets/php/actions.php', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: `like_post=${postId}&user_liked=${userLiked ? '1' : '0'}`
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                // Update like count
                                const likeCountElements = document.querySelectorAll(`.like-count-${postId}, .modal-like-count-${postId}`);
                                likeCountElements.forEach(element => {
                                    element.textContent = data.like_count;
                                });
                                // Update button state
                                if (userLiked) {
                                    // Unlike
                                    this.setAttribute('data-user-liked', 'false');
                                    this.classList.remove('text-red-500');
                                    this.classList.add('text-gray-500');
                                    this.querySelector('svg').setAttribute('fill', 'none');
                                } else {
                                    // Like
                                    this.setAttribute('data-user-liked', 'true');
                                    this.classList.remove('text-gray-500');
                                    this.classList.add('text-red-500');
                                    this.querySelector('svg').setAttribute('fill', 'currentColor');
                                }
                            } else {
                                // Show error message
                                alert('Error: ' + data.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred while processing your request.');
                        });
                });
            });
            // Comment functionality
            document.querySelectorAll('.add-comment').forEach(button => {
                button.addEventListener('click', function() {
                    const postId = this.getAttribute('data-post-id');
                    const commentSection = document.getElementById(this.getAttribute('data-cs'));
                    const input = commentSection.parentElement.querySelector('.comment-input');
                    const commentText = input.value.trim();
                    if (commentText === '') {
                        return;
                    }
                    // Send AJAX request
                    fetch('assets/php/actions.php', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: `add_comment=${postId}&comment=${encodeURIComponent(commentText)}`
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                // Add new comment to the section
                                const newComment = document.createElement('div');
                                newComment.className = 'flex items-start gap-3 mb-4';
                                newComment.innerHTML = `
                                <img src="assets/img/profile/<?= htmlspecialchars($user['profile_pic']) ?>" alt="Your Pic" class="w-8 h-8 rounded-full object-cover"
                                     onerror="this.onerror=null; this.src='assets/img/profile/default-avatar.png'">
                                <div class="flex-1">
                                    <div class="bg-gray-100 rounded-lg p-3">
                                        <a href="?u=<?= htmlspecialchars($user['username']) ?>" class="font-semibold text-sm text-gray-900 hover:underline">
                                            <?= htmlspecialchars($user['first_name']) ?> <?= htmlspecialchars($user['last_name']) ?>
                                        </a>
                                        <p class="text-sm text-gray-700 mt-1">${commentText.replace(/</g, '&lt;').replace(/>/g, '&gt;')}</p>
                                    </div>
                                    <p class="text-xs text-gray-500 mt-1">Just now</p>
                                </div>
                            `;
                                // If there's a "No comments" message, remove it
                                const noCommentsMsg = commentSection.querySelector('.text-center.text-gray-400');
                                if (noCommentsMsg) {
                                    noCommentsMsg.remove();
                                }
                                // Add the new comment
                                commentSection.appendChild(newComment);
                                // Clear input
                                input.value = '';
                                // Update comment count in the grid
                                const commentCounts = document.querySelectorAll(`[data-bs-target="#postview${postId}"] .flex.items-center.text-white.font-medium:last-child span`);
                                commentCounts.forEach(count => {
                                    const currentCount = parseInt(count.textContent);
                                    count.textContent = currentCount + 1;
                                });
                            } else {
                                // Show error message
                                alert('Error: ' + data.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred while posting your comment.');
                        });
                });
            });
        });
    </script>
</div>