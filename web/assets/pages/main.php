<?php
global $user;
global $posts;
global $follow_sugesstions;

// Get user statistics
$userPosts = getPostById($user['id']);
$postCount = count($userPosts);
$userFollowers = getFollowersCount($user['id']);
$followerCount = count($userFollowers);
$userFollowing = getFollowingCount($user['id']);
$followingCount = count($userFollowing);

// Function to fetch tech news ONLY from Wired RSS feed
function getTechNewsFromRSS($cacheTime = 3600) {
    $cacheFile = 'cache/tech_news_rss.json';
    
    // Check if cache exists and is still valid
    if (file_exists($cacheFile) && (time() - filemtime($cacheFile)) < $cacheTime) {
        $data = file_get_contents($cacheFile);
        $cachedNews = json_decode($data, true);
        if (!empty($cachedNews)) {
            return $cachedNews;
        }
    }
    
    // ONLY use Wired RSS feed
    $feedUrl = 'https://www.wired.com/feed/rss';
    $news = [];
    
    try {
        // Use cURL with better error handling for localhost
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $feedUrl);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_TIMEOUT, 15);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
        curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');
        
        $feedContent = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);
        
        if ($httpCode !== 200 || empty($feedContent)) {
            error_log("Wired RSS feed failed: HTTP $httpCode, Error: $error");
            return null;
        }
        
        $xml = @simplexml_load_string($feedContent);
        if ($xml === false) {
            error_log("XML parsing failed for Wired RSS feed");
            return null;
        }
        
        // Parse Wired RSS feed (standard RSS format)
        if (isset($xml->channel->item)) {
            foreach ($xml->channel->item as $item) {
                $news[] = [
                    'id' => md5($item->link),
                    'title' => (string)$item->title,
                    'source' => 'Wired',
                    'published_at' => strtotime((string)$item->pubDate),
                    'url' => (string)$item->link
                ];
            }
        }
        
        // Sort by published_at (newest first)
        usort($news, function($a, $b) {
            return $b['published_at'] - $a['published_at'];
        });
        
        // Limit to 10 items
        $news = array_slice($news, 0, 10);
        
        // Save to cache
        if (!empty($news)) {
            if (!file_exists('cache')) {
                mkdir('cache', 0777, true);
            }
            file_put_contents($cacheFile, json_encode($news));
        }
        
        return $news;
        
    } catch (Exception $e) {
        error_log("Exception fetching Wired RSS feed: " . $e->getMessage());
        return null;
    }
}

// Get tech news from Wired RSS feed
$tech_news = getTechNewsFromRSS();

// If no news from RSS, show empty state
if (empty($tech_news)) {
    $tech_news = [];
}
?>
<div class="max-w-7xl mx-auto px-4 py-6 pt-24">
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - User Profile & Suggestions -->
        <div class="lg:col-span-1">
            <div class="sticky top-24 space-y-6">
                <!-- User Profile Card -->
                <div class="bg-white rounded-2xl shadow-xl overflow-hidden user-profile-card transform transition-all duration-300 hover:shadow-2xl">
                    <div class="bg-gradient-to-r from-blue-500 to-purple-600 h-20"></div>
                    <div class="px-6 pb-6 -mt-10">
                        <div class="flex flex-col items-center">
                            <img src="./assets/img/profile/<?= htmlspecialchars($user['profile_pic']) ?>"
                                alt="Profile picture"
                                class="w-20 h-20 rounded-full border-4 border-white shadow-lg mb-4 profile-pic ring-2 ring-purple-500 ring-opacity-50">
                            <a href="?u=<?= htmlspecialchars($user['username']) ?>" class="text-center hover:opacity-80 transition-opacity">
                                <h2 class="text-xl font-bold text-gray-900 mb-1">
                                    <?= htmlspecialchars($user['first_name'] . ' ' . $user['last_name']) ?>
                                </h2>
                                <p class="text-sm text-gray-600 font-medium">@<?= htmlspecialchars($user['username']) ?></p>
                            </a>
                            <div class="flex space-x-4 mt-3">
                                <div class="text-center">
                                    <p class="font-bold text-gray-900"><?= $postCount ?></p>
                                    <p class="text-xs text-gray-600">Posts</p>
                                </div>
                                <div class="text-center">
                                    <p class="font-bold text-gray-900"><?= $followerCount ?></p>
                                    <p class="text-xs text-gray-600">Followers</p>
                                </div>
                                <div class="text-center">
                                    <p class="font-bold text-gray-900"><?= $followingCount ?></p>
                                    <p class="text-xs text-gray-600">Following</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Friend Suggestions -->
                <div class="bg-white rounded-2xl shadow-xl p-6 suggestions-card transform transition-all duration-300 hover:shadow-2xl">
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
                            <div class="flex items-center space-x-3 rounded-xl hover:bg-gray-50 transition-colors suggestion-item p-2">
                                <img src="assets/img/profile/<?= htmlspecialchars($suser['profile_pic']) ?>"
                                    class="w-12 h-12 rounded-full object-cover ring-2 ring-purple-500 ring-opacity-30">
                                <div class="flex-1 min-w-0">
                                    <a href="?u=<?= htmlspecialchars($suser['username']) ?>" class="block text-sm hover:opacity-80">
                                        <p class="font-semibold text-gray-900 truncate">
                                            <?= htmlspecialchars($suser['first_name'] . ' ' . $suser['last_name']) ?>
                                        </p>
                                        <p class="text-sm text-gray-600 truncate">@<?= htmlspecialchars($suser['username']) ?></p>
                                    </a>
                                </div>
                                <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 transform hover:scale-105 followbtn"
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
        <div class="lg:col-span-2">
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
                    <article class="bg-white rounded-2xl shadow-xl overflow-hidden post-card transform transition-all duration-300 hover:shadow-2xl" data-post-id="<?= (int)$post['id'] ?>">
                        <!-- Post Header -->
                        <div class="flex items-center justify-between p-6 pb-4">
                            <a href="?u=<?= htmlspecialchars($post['username']) ?>" class="flex items-center space-x-3 hover:opacity-80 transition-opacity">
                                <img src="assets/img/profile/<?= htmlspecialchars($post['profile_pic']) ?>"
                                    alt="Profile"
                                    class="w-12 h-12 rounded-full object-cover ring-2 ring-purple-500 ring-opacity-30">
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
                                <div class="relative">
                                    <pre class="font-mono text-sm whitespace-pre-wrap max-h-40 overflow-hidden p-3 bg-gray-900 text-white rounded-md cursor-pointer code-preview"
                                        data-code-content="<?= htmlspecialchars($post['code_content']) ?>"
                                        data-code-language="<?= strtolower(htmlspecialchars($post['code_language'])) ?>"
                                        data-code-tags="<?= htmlspecialchars($post['tags']) ?>"
                                        data-post-id="<?= (int)$post['id'] ?>">
                                        <code class="language-<?= strtolower(htmlspecialchars($post['code_language'])) ?>"><?= htmlspecialchars(substr($post['code_content'], 0, 300)) ?><?php if (strlen($post['code_content']) > 300) echo " ..."; ?> </code>
                                    </pre>
                                    <button class="copy-code-btn absolute top-2 right-2 bg-gray-700 text-white px-2 py-1 rounded-md text-xs hover:bg-gray-600 transition-colors z-10">Copy</button>
                                    <?php if (strlen($post['code_content']) > 300): ?>
                                        <button class="show-more-code-btn absolute bottom-2 right-2 bg-purple-600 text-white px-3 py-1 rounded-md text-xs hover:bg-purple-700 transition-colors z-10"
                                            data-post-id="<?= (int)$post['id'] ?>">
                                            Show More
                                        </button>
                                    <?php endif; ?>
                                </div>
                                <p class="mt-2 text-xs text-gray-600 font-semibold">
                                    Language: <span class="text-purple-600"><?= htmlspecialchars($post['code_language']) ?></span> |
                                    Tags: <span class="text-purple-600"><?= htmlspecialchars($post['tags']) ?></span>
                                </p>
                            </div>
                        <?php endif; ?>
                        <div class="flex flex-col md:flex-row items-center justify-between px-6 py-4 border-t border-gray-100">
                            <div class="flex items-center space-x-8 mb-4 md:mb-0">
                                <!-- Like Section -->
                                <div class="flex items-center space-x-2 like-container">
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
                                    <p class="text-sm font-medium text-gray-700 hover:text-red-500 cursor-pointer transition likes-count-<?= (int)$post['id'] ?>"
                                        onclick="openLikesModal('<?= (int)$post['id'] ?>')">
                                        <?= count($likes) ?> Likes
                                    </p>
                                </div>
                                <!-- Comment Section -->
                                <div class="flex items-center space-x-2 cursor-pointer hover:text-blue-500 transition-colors"
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
                                    class="w-8 h-8 rounded-full object-cover ring-2 ring-purple-500 ring-opacity-30">
                                <div class="flex-1 flex items-center space-x-2">
                                    <input type="text"
                                        class="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-600 comment-input"
                                        placeholder="Write a comment...">
                                    <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 transform hover:scale-105 add-comment"
                                        data-cs="comment-section<?= (int)$post['id'] ?>"
                                        data-post-id="<?= (int)$post['id'] ?>">
                                        Post
                                    </button>
                                </div>
                            </div>
                        </div>
                    </article>
                    <!-- Post Modal -->
                    <div id="post-modal-<?= (int)$post['id'] ?>" class="modal-overlay fixed inset-0 bg-transparent z-50 hidden flex items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-6xl w-full max-h-[90vh] flex flex-col md:flex-row overflow-hidden shadow-2xl">
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
                                        <img src="assets/img/profile/<?= htmlspecialchars($post['profile_pic']) ?>" class="w-10 h-10 rounded-full object-cover ring-2 ring-purple-500 ring-opacity-30" />
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
                                        <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 transform hover:scale-105 add-comment" data-cs="comment-section<?= (int)$post['id'] ?>" data-post-id="<?= (int)$post['id'] ?>">
                                            Post
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Code Modal -->
                    <div id="code-modal-<?= (int)$post['id'] ?>" class="modal-overlay fixed inset-0 bg-transparent z-50 hidden flex items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] flex flex-col overflow-hidden shadow-2xl">
                            <!-- Header -->
                            <div class="flex items-center justify-between p-4 border-b border-gray-100 bg-gray-900 text-white">
                                <div class="flex items-center space-x-3">
                                    <h5 class="text-lg font-bold">Code</h5>
                                    <span class="text-sm bg-purple-600 px-2 py-1 rounded"><?= htmlspecialchars($post['code_language']) ?></span>
                                    <span class="text-sm bg-gray-700 px-2 py-1 rounded"><?= htmlspecialchars($post['tags']) ?></span>
                                </div>
                                <button onclick="closeCodeModal('<?= (int)$post['id'] ?>')" class="p-2 hover:bg-gray-800 rounded-full">
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
                                        class="lucide lucide-x text-white">
                                        <path d="M18 6 6 18" />
                                        <path d="m6 6 12 12" />
                                    </svg>
                                </button>
                            </div>
                            <!-- Code Content -->
                            <div class="flex-1 overflow-auto bg-gray-900 p-4">
                                <pre class="rounded-xl bg-gray-900 text-white font-mono p-4 w-full overflow-auto relative"
                                    data-code-content="<?= htmlspecialchars($post['code_content']) ?>">
                                    <code class="language-<?= strtolower(htmlspecialchars($post['code_language'])) ?>"><?= htmlspecialchars($post['code_content']) ?></code>
                                    <button class="copy-code-btn absolute top-2 right-2 bg-gray-700 text-white px-2 py-1 rounded-md text-xs hover:bg-gray-600 transition-colors">Copy</button>
                                </pre>
                            </div>
                        </div>
                    </div>
                    <!-- Likes Modal -->
                    <div id="likes-modal-<?= (int)$post['id'] ?>" class="modal-overlay fixed inset-0 bg-transparent z-50 hidden flex items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-md w-full max-h-96 flex flex-col overflow-hidden shadow-2xl">
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
                                                <button class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 transform hover:scale-105 unfollowbtn"
                                                    data-user-id="<?= (int)$fuser['id'] ?>">
                                                    Unfollow
                                                </button>
                                            <?php else: ?>
                                                <button class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 transform hover:scale-105 followbtn"
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
        
        <!-- Right Sidebar - Tech News -->
        <div class="lg:col-span-1">
            <div class="sticky top-24 space-y-6">
                <!-- Tech News Section -->
                <div class="bg-white rounded-2xl shadow-xl p-6 tech-news-card transform transition-all duration-300 hover:shadow-2xl">
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
                            class="lucide lucide-newspaper mr-2 text-blue-600">
                            <path d="M4 2v20h16V2z"/>
                            <path d="M8 6h8"/>
                            <path d="M8 10h8"/>
                            <path d="M8 14h5"/>
                        </svg>
                        Wired News
                    </h3>
                    <div class="space-y-4">
                        <?php if (!empty($tech_news)): ?>
                            <?php foreach ($tech_news as $news): ?>
                                <div class="border-b border-gray-100 pb-3 last:border-0 hover:bg-gray-50 p-2 rounded-lg transition-colors cursor-pointer news-item"
                                     data-news-id="<?= (int)$news['id'] ?>"
                                     data-news-url="<?= htmlspecialchars($news['url']) ?>"
                                     data-news-title="<?= htmlspecialchars($news['title']) ?>">
                                    <h4 class="font-semibold text-gray-900 text-sm mb-1"><?= htmlspecialchars($news['title']) ?></h4>
                                    <p class="text-xs text-gray-600"><?= htmlspecialchars($news['source']) ?> • <?= show_time($news['published_at']) ?></p>
                                </div>
                            <?php endforeach; ?>
                        <?php else: ?>
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
                                    class="lucide lucide-newspaper text-gray-300 mx-auto mb-3">
                                    <path d="M4 2v20h16V2z"/>
                                    <path d="M8 6h8"/>
                                    <path d="M8 10h8"/>
                                    <path d="M8 14h5"/>
                                </svg>
                                <p class="text-gray-600 font-medium">Unable to load Wired news</p>
                                <p class="text-xs text-gray-500 mt-1">Please check your connection</p>
                            </div>
                        <?php endif; ?>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Tech News Modal -->
<div id="news-modal" class="modal-overlay fixed inset-0 bg-black bg-opacity-50 z-50 hidden flex items-center justify-center p-4">
    <div class="modal-content bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] flex flex-col overflow-hidden shadow-2xl">
        <!-- Header -->
        <div class="flex items-center justify-between p-4 border-b border-gray-100 bg-blue-600 text-white">
            <h3 id="news-modal-title" class="text-lg font-bold">Wired News</h3>
            <button onclick="closeNewsModal()" class="p-2 hover:bg-blue-700 rounded-full">
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
                    class="lucide lucide-x text-white">
                    <path d="M18 6 6 18" />
                    <path d="m6 6 12 12" />
                </svg>
            </button>
        </div>
        <!-- News Content -->
        <div class="flex-1 overflow-auto bg-white">
            <iframe id="news-iframe" 
                src="" 
                class="w-full h-full min-h-[70vh]" 
                frameborder="0"
                allowfullscreen>
            </iframe>
        </div>
        <!-- Footer -->
        <div class="p-4 border-t border-gray-100 bg-gray-50 flex justify-between items-center">
            <p class="text-sm text-gray-600">Viewing article from Wired</p>
            <button onclick="closeNewsModal()" class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-full text-sm font-medium transition-colors">
                Close
            </button>
        </div>
    </div>
</div>

<!-- Include Prism.js for code highlighting -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-javascript.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-python.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-php.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-css.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-cpp.min.js"></script>
<script>
    // Register GSAP ScrollTrigger plugin
    gsap.registerPlugin(ScrollTrigger);
    // GSAP Animations with ScrollTrigger
    document.addEventListener('DOMContentLoaded', () => {
        // User profile card animation
        gsap.fromTo('.user-profile-card', 
            { opacity: 0, y: 40 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.8,
                scrollTrigger: {
                    trigger: '.user-profile-card',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // Suggestions card animation
        gsap.fromTo('.suggestions-card', 
            { opacity: 0, y: 40 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.8,
                delay: 0.2,
                scrollTrigger: {
                    trigger: '.suggestions-card',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // Tech news card animation
        gsap.fromTo('.tech-news-card', 
            { opacity: 0, y: 40 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.8,
                delay: 0.4,
                scrollTrigger: {
                    trigger: '.tech-news-card',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // Suggestion items animation
        gsap.fromTo('.suggestion-item', 
            { opacity: 0, y: 20 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.5,
                stagger: 0.1,
                scrollTrigger: {
                    trigger: '.suggestions-card',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // News items animation
        gsap.fromTo('.news-item', 
            { opacity: 0, y: 20 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.5,
                stagger: 0.1,
                scrollTrigger: {
                    trigger: '.tech-news-card',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // Post cards animation
        gsap.fromTo('.post-card', 
            { opacity: 0, y: 50 }, 
            { 
                opacity: 1, 
                y: 0, 
                duration: 0.8,
                stagger: 0.2,
                scrollTrigger: {
                    trigger: '.posts-container',
                    start: 'top 80%',
                    toggleActions: 'play none none none'
                }
            }
        );
        // Hover animations for interactive elements
        const hoverElements = document.querySelectorAll('.followbtn, .unfollowbtn, .add-comment, .like_btn, .unlike_btn, .news-item');
        hoverElements.forEach(element => {
            element.addEventListener('mouseenter', () => {
                gsap.to(element, { scale: 1.05, duration: 0.2 });
            });
            element.addEventListener('mouseleave', () => {
                gsap.to(element, { scale: 1, duration: 0.2 });
            });
        });
        // Initialize PrismJS highlighting
        if (typeof Prism !== 'undefined') {
            Prism.highlightAll();
        } else {
            console.warn('PrismJS not found. Code highlighting will not work.');
        }
        // Copy code button functionality
        document.querySelectorAll('.copy-code-btn').forEach(button => {
            button.addEventListener('click', async (event) => {
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
                        console.error('Failed to copy text:', err);
                        alert('Failed to copy code.');
                    }
                }
            });
        });
        // Show more code button functionality
        document.querySelectorAll('.show-more-code-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                event.stopPropagation();
                const postId = button.dataset.postId;
                openCodeModal(postId);
            });
        });
        
        // Tech news click functionality
        document.querySelectorAll('.news-item').forEach(item => {
            item.addEventListener('click', function() {
                const newsUrl = this.dataset.newsUrl;
                const newsTitle = this.dataset.newsTitle;
                openNewsModal(newsUrl, newsTitle);
            });
        });
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
        const modal = document.getElementById(`post-modal-${id}`);
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        
        // Re-highlight code when modal opens
        if (typeof Prism !== 'undefined') {
            Prism.highlightAllUnder(modal);
        }
        
        // Add entrance animation
        gsap.fromTo(modal.querySelector('.modal-content'), 
            { scale: 0.9, opacity: 0 }, 
            { scale: 1, opacity: 1, duration: 0.3 }
        );
    }
    
    function closePostModal(id) {
        const modal = document.getElementById(`post-modal-${id}`);
        const modalContent = modal.querySelector('.modal-content');
        
        // Add exit animation
        gsap.to(modalContent, {
            scale: 0.9,
            opacity: 0,
            duration: 0.2,
            onComplete: () => {
                modal.classList.add('hidden');
                document.body.style.overflow = 'auto';
            }
        });
    }
    
    function openCodeModal(id) {
        const modal = document.getElementById(`code-modal-${id}`);
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        
        // Re-highlight code when modal opens
        if (typeof Prism !== 'undefined') {
            Prism.highlightAllUnder(modal);
        }
        
        // Add entrance animation
        gsap.fromTo(modal.querySelector('.modal-content'), 
            { scale: 0.9, opacity: 0 }, 
            { scale: 1, opacity: 1, duration: 0.3 }
        );
    }
    
    function closeCodeModal(id) {
        const modal = document.getElementById(`code-modal-${id}`);
        const modalContent = modal.querySelector('.modal-content');
        
        // Add exit animation
        gsap.to(modalContent, {
            scale: 0.9,
            opacity: 0,
            duration: 0.2,
            onComplete: () => {
                modal.classList.add('hidden');
                document.body.style.overflow = 'auto';
            }
        });
    }
    
    function openLikesModal(id) {
        const modal = document.getElementById(`likes-modal-${id}`);
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        
        // Add entrance animation
        gsap.fromTo(modal.querySelector('.modal-content'), 
            { scale: 0.9, opacity: 0 }, 
            { scale: 1, opacity: 1, duration: 0.3 }
        );
    }
    
    function closeLikesModal(id) {
        const modal = document.getElementById(`likes-modal-${id}`);
        const modalContent = modal.querySelector('.modal-content');
        
        // Add exit animation
        gsap.to(modalContent, {
            scale: 0.9,
            opacity: 0,
            duration: 0.2,
            onComplete: () => {
                modal.classList.add('hidden');
                document.body.style.overflow = 'auto';
            }
        });
    }
    
    // Tech News Modal Functions
    function openNewsModal(url, title) {
        const modal = document.getElementById('news-modal');
        const iframe = document.getElementById('news-iframe');
        const titleElement = document.getElementById('news-modal-title');
        
        // Set the iframe source and title
        iframe.src = url;
        titleElement.textContent = title;
        
        // Show the modal
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        
        // Add entrance animation
        gsap.fromTo(modal.querySelector('.modal-content'), 
            { scale: 0.9, opacity: 0 }, 
            { scale: 1, opacity: 1, duration: 0.3 }
        );
    }
    
    function closeNewsModal() {
        const modal = document.getElementById('news-modal');
        const modalContent = modal.querySelector('.modal-content');
        const iframe = document.getElementById('news-iframe');
        
        // Add exit animation
        gsap.to(modalContent, {
            scale: 0.9,
            opacity: 0,
            duration: 0.2,
            onComplete: () => {
                modal.classList.add('hidden');
                document.body.style.overflow = 'auto';
                // Clear the iframe to stop loading
                iframe.src = '';
            }
        });
    }
    
    // Like button animation and logic
    document.querySelectorAll('.like-toggle-btn').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const iconSvg = this;
            const likesCountSpan = document.querySelector(`.likes-count-${postId}`);
            const likeContainer = this.closest('.like-container');
            let action;
            
            // Determine action based on current icon class
            if (iconSvg.classList.contains('lucide-heart')) { // Currently unliked
                action = 'like';
            } else { // Currently liked
                action = 'unlike';
            }
            
            // Heart animation
            gsap.fromTo(iconSvg, 
                { scale: 1 }, 
                { 
                    scale: 1.4, 
                    duration: 0.15, 
                    yoyo: true, 
                    repeat: 1, 
                    ease: "power2.out" 
                }
            );
            
            // Heart burst effect (only for liking)
            if (action === 'like') {
                // Create heart particles
                for (let i = 0; i < 12; i++) {
                    const particle = document.createElement('div');
                    particle.innerHTML = '❤️';
                    particle.style.position = 'absolute';
                    particle.style.pointerEvents = 'none';
                    particle.style.fontSize = Math.random() * 10 + 10 + 'px';
                    particle.style.zIndex = '1000';
                    particle.style.opacity = Math.random() * 0.5 + 0.5;
                    
                    const rect = likeContainer.getBoundingClientRect();
                    const centerX = rect.left + rect.width / 2;
                    const centerY = rect.top + rect.height / 2;
                    
                    particle.style.left = centerX + 'px';
                    particle.style.top = centerY + 'px';
                    
                    document.body.appendChild(particle);
                    
                    // Animate the particle
                    const angle = Math.random() * Math.PI * 2;
                    const distance = Math.random() * 100 + 50;
                    const duration = Math.random() * 0.5 + 0.5;
                    
                    gsap.to(particle, {
                        x: Math.cos(angle) * distance,
                        y: Math.sin(angle) * distance - 50,
                        opacity: 0,
                        scale: 0,
                        duration: duration,
                        ease: "power2.out",
                        onComplete: () => particle.remove()
                    });
                    
                    // Add some rotation
                    gsap.to(particle, {
                        rotation: Math.random() * 360,
                        duration: duration,
                        ease: "power2.out"
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
                            iconSvg.setAttribute('fill', 'currentColor');
                        } else {
                            iconSvg.classList.remove('lucide-heart-fill', 'text-red-500');
                            iconSvg.classList.add('lucide-heart', 'text-gray-500', 'hover:text-red-500');
                            iconSvg.setAttribute('fill', 'none');
                        }
                        
                        // Update like count
                        if (likesCountSpan) {
                            likesCountSpan.textContent = `${data.likes_count} Likes`;
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
    
    // Comment button logic
    document.querySelectorAll('.add-comment').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const commentSectionId = this.dataset.cs;
            const commentInput = this.parentElement.querySelector('.comment-input');
            const commentText = commentInput.value.trim();
            
            if (commentText === '') {
                // Show a subtle error message instead of alert
                gsap.to(commentInput, {
                    x: 10,
                    duration: 0.1,
                    repeat: 5,
                    yoyo: true,
                    ease: "power2.inOut",
                    onComplete: () => {
                        commentInput.focus();
                    }
                });
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
                        commentInput.value = '';
                        
                        // Dynamically add comment to the comment section
                        const commentSection = document.getElementById(commentSectionId);
                        if (commentSection) {
                            // If "No comments yet" message exists, remove it
                            const noCommentsMsg = commentSection.querySelector('p.text-center');
                            if (noCommentsMsg) {
                                noCommentsMsg.remove();
                            }
                            
                            const newCommentHtml = `
                                <div class="flex items-start space-x-4 mb-4 new-comment">
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
                            
                            // Animate the new comment
                            const newComment = commentSection.querySelector('.new-comment');
                            gsap.fromTo(newComment, 
                                { opacity: 0, y: 20 }, 
                                { opacity: 1, y: 0, duration: 0.4 }
                            );
                            
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
    
    // Follow/Unfollow button logic
    document.querySelectorAll('.followbtn, .unfollowbtn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.dataset.userId;
            const isFollowBtn = this.classList.contains('followbtn');
            const action = isFollowBtn ? 'follow' : 'unfollow';
            
            // Send AJAX request
            fetch(`assets/php/actions.php?toggle_follow=1&user_id=${userId}&action=${action}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'success') {
                        // Toggle button classes and text
                        if (isFollowBtn) {
                            this.classList.remove('bg-purple-600', 'hover:bg-purple-700', 'followbtn');
                            this.classList.add('bg-red-500', 'hover:bg-red-600', 'unfollowbtn');
                            this.textContent = 'Unfollow';
                        } else {
                            this.classList.remove('bg-red-500', 'hover:bg-red-600', 'unfollowbtn');
                            this.classList.add('bg-purple-600', 'hover:bg-purple-700', 'followbtn');
                            this.textContent = 'Follow';
                        }
                        
                        // Add a subtle animation
                        gsap.fromTo(this, 
                            { scale: 1 }, 
                            { scale: 1.1, duration: 0.2, yoyo: true, repeat: 1 }
                        );
                    } else {
                        console.error('Error toggling follow:', data.message);
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