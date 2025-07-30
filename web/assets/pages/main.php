<?php
global $user;
global $posts;
global $follow_sugesstions;
?>
<!-- Fixed: Added proper padding-top to account for navbar -->
<div class="max-w-7xl mx-auto px-4 py-6 pt-[350px]">
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">

        <!-- Left Sidebar - User Profile & Suggestions -->
        <div class="lg:col-span-1">
            <!-- Fixed: Changed sticky positioning to account for navbar -->
            <div class="sticky top-24 space-y-6">

                <!-- User Profile Card -->
                <div class="bg-white rounded-2xl shadow-lg overflow-hidden user-profile-card">
                    <div class="bg-gradient-to-r from-blue-500 to-purple-600 h-20"></div>
                    <div class="px-6 pb-6 -mt-10">
                        <div class="flex flex-col items-center">
                            <img src="./assets/img/profile/<?= $user['profile_pic'] ?>"
                                alt="Profile picture"
                                class="w-20 h-20 rounded-full border-4 border-white shadow-lg mb-4 profile-pic">
                            <a href="?u=<?= $user['username'] ?>" class="text-center hover:opacity-80 transition-opacity">
                                <h2 class="text-xl font-bold text-primary mb-1">
                                    <?= $user['first_name'] ?> <?= $user['last_name'] ?>
                                </h2>
                                <p class="text-sm text-gray-500 font-medium">@<?= $user['username'] ?></p>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Friend Suggestions -->
                <div class="bg-white rounded-2xl shadow-lg p-6 suggestions-card">
                    <h3 class="text-lg font-bold text-primary mb-4 flex items-center">
                        <i class="fas fa-users mr-2 text-accent"></i>
                        You may Know
                    </h3>

                    <div class="space-y-4">
                        <?php foreach ($follow_sugesstions as $suser): ?>
                            <div class="flex items-center space-x-3  rounded-xl hover:bg-gray-50 transition-colors suggestion-item">
                                <img src="assets/img/profile/<?= $suser['profile_pic'] ?>"
                                    class="w-12 h-12 rounded-full object-cover">
                                <div class="flex-1 min-w-0">
                                    <a href="?u=<?= $suser['username'] ?>" class="block text-sm hover:opacity-80">
                                        <p class="font-semibold text-primary truncate">
                                            <?= $suser['first_name'] ?> <?= $suser['last_name'] ?>
                                        </p>
                                        <p class="text-sm text-gray-500 truncate">@<?= $suser['username'] ?></p>
                                    </a>
                                </div>
                                <button class="bg-accent hover:bg-orange-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors followbtn"
                                    data-user-id='<?= $suser['id'] ?>'>
                                    <i class="fas fa-plus mr-1"></i>Follow
                                </button>
                            </div>
                        <?php endforeach; ?>

                        <?php if (count($follow_sugesstions) < 1): ?>
                            <div class="text-center py-8">
                                <i class="fas fa-user-friends text-4xl text-gray-300 mb-3"></i>
                                <p class="text-gray-500 font-medium">No user suggestions available</p>
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
                ?>
                    <article class="bg-white rounded-2xl shadow-lg overflow-hidden post-card" data-post-id="<?= $post['id'] ?>">

                        <!-- Post Header -->
                        <div class="flex items-center justify-between p-6 pb-4">
                            <a href="?u=<?= $post['username'] ?>" class="flex items-center space-x-3 hover:opacity-80 transition-opacity">
                                <img src="assets/img/profile/<?= $post['profile_pic'] ?>"
                                    alt="Profile"
                                    class="w-12 h-12 rounded-full object-cover">
                                <div>
                                    <h3 class="font-bold text-primary">
                                        <?= $post['first_name'] ?> <?= $post['last_name'] ?>
                                    </h3>
                                    <p class="text-sm text-gray-500">@<?= $post['username'] ?></p>
                                </div>
                            </a>

                            <?php if ($post['user_id'] == $user['id']): ?>
                                <div class="relative">
                                    <button class="p-2 hover:bg-gray-100 rounded-full transition-colors"
                                        onclick="toggleDropdown('dropdown-<?= $post['id'] ?>')">
                                        <i class="fas fa-ellipsis-v text-gray-500"></i>
                                    </button>
                                    <div id="dropdown-<?= $post['id'] ?>" class="hidden absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border z-10">
                                        <a href="assets/php/actions.php?deletepost=<?= $post['id'] ?>"
                                            class="flex items-center px-4 py-3 text-red-600 hover:bg-red-50 transition-colors">
                                            <i class="fas fa-trash mr-3"></i>Delete Post
                                        </a>
                                    </div>
                                </div>
                            <?php endif; ?>
                        </div>

                        <!-- Post Content -->
                        <?php if (!empty($post['post_text'])): ?>
                            <div class="px-6 pb-4">
                                <p class="text-gray-800 leading-relaxed"><?= $post['post_text'] ?></p>
                            </div>
                        <?php endif; ?>

                        <!-- Post Image -->
                        <?php
                        $image = $post['post_img'];
                        if (!empty($image)):
                            $img_src = '';
                            if (strpos($image, 'http') === 0) {
                                $img_src = $image;
                            } else if (strpos($image, 'web/assets/img/posts/') === 0) {
                                $img_src = substr($image, strpos($image, 'assets/img/posts/'));
                            } else {
                                $img_src = 'assets/img/posts/' . $image;
                            }
                        ?>
                            <div class="px-6 pb-4">
                                <img src="<?= htmlspecialchars($img_src) ?>"
                                    alt="Post image"
                                    class="w-full rounded-xl object-cover cursor-pointer hover:opacity-95 transition-opacity post-image"
                                    style="max-height: 500px;"
                                    onclick="openPostModal('<?= $post['id'] ?>')">
                            </div>
                        <?php endif; ?>
                        <div class="flex items-center justify-between px-6 py-4 border-t border-gray-100">
                            <div class="flex items-center space-x-8">

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
                                    <i data-post-id="<?= $post['id'] ?>" style="display:<?= $unlike_btn_display ?>;"
                                        class="fa-solid fa-heart unlike_btn text-red-500 text-xl cursor-pointer hover:scale-110 transition-transform duration-150"></i>
                                        
                                    <i data-post-id="<?= $post['id'] ?>" style="display:<?= $like_btn_display ?>;"
                                        class=" like-toggle-btn fa-regular fa-heart like_btn text-gray-500 text-xl cursor-pointer hover:text-red-500 transition-colors hover:scale-110 duration-150"></i>

                                    <!-- Like Count -->
                                    <p class="text-sm font-medium text-gray-700 hover:text-red-500 cursor-pointer transition"
                                        onclick="openLikesModal('<?= $post['id'] ?>')">
                                        <?= count($likes) ?> Likes
                                    </p>
                                </div>

                                <!-- Comment Section -->
                                <div class="flex items-center space-x-2 cursor-pointer"
                                    onclick="openPostModal('<?= $post['id'] ?>')">
                                    <i class="fa-solid fa-comment text-gray-500 hover:text-blue-500 text-xl transition-colors"></i>
                                    <p class="text-sm font-medium text-gray-700 hover:text-blue-500 transition">
                                        <?= count($comments) ?> Comments
                                    </p>
                                </div>




                                <span class="text-xs text-gray-500">
                                    Posted <?= show_time($post['created_at']) ?>
                                </span>
                            </div>

                            <!-- Add Comment -->
                            <div class="flex items-center space-x-3">
                                <img src="./assets/img/profile/<?= $user['profile_pic'] ?>"
                                    class="w-8 h-8 rounded-full object-cover">
                                <div class="flex-1 flex items-center space-x-2">
                                    <input type="text"
                                        class="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent comment-input"
                                        placeholder="Write a comment...">
                                    <button class="bg-accent hover:bg-orange-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors add-comment"
                                        data-cs="comment-section<?= $post['id'] ?>"
                                        data-post-id="<?= $post['id'] ?>">
                                        Post
                                    </button>
                                </div>
                            </div>
                        </div>
                    </article>

                    <!-- Post Modal -->
                    <div id="post-modal-<?= $post['id'] ?>" class="modal-overlay fixed inset-0 bg-black bg-opacity-50 z-50 hidden items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-6xl w-full max-h-[90vh] overflow-hidden">
                            <div class="flex h-full">
                                <!-- Image Section -->
                                <?php if (!empty($post['post_img'])): ?>
                                    <div class="flex-1 bg-black flex items-center justify-center">
                                        <img src="<?= htmlspecialchars($img_src) ?>"
                                            class="max-h-full max-w-full object-contain">
                                    </div>
                                <?php endif; ?>

                                <!-- Comments Section -->
                                <div class="w-96 flex flex-col">
                                    <!-- Header -->
                                    <div class="flex items-center justify-between p-4 border-b">
                                        <div class="flex items-center space-x-3">
                                            <img src="assets/img/profile/<?= $post['profile_pic'] ?>"
                                                class="w-10 h-10 rounded-full object-cover">
                                            <div>
                                                <h6 class="font-semibold text-primary"><?= $post['first_name'] ?> <?= $post['last_name'] ?></h6>
                                                <p class="text-sm text-gray-500">@<?= $post['username'] ?></p>
                                            </div>
                                        </div>
                                        <button onclick="closePostModal('<?= $post['id'] ?>')"
                                            class="p-2 hover:bg-gray-100 rounded-full">
                                            <i class="fas fa-times text-gray-500"></i>
                                        </button>
                                    </div>

                                    <!-- Comments -->
                                    <div class="flex-1 overflow-y-auto p-4" id="comment-section<?= $post['id'] ?>">
                                        <?php if (count($comments) < 1): ?>
                                            <p class="text-center text-gray-500 py-8">No comments yet</p>
                                        <?php endif; ?>

                                        <?php foreach ($comments as $comment):
                                            $cuser = getUser($comment['user_id']);
                                        ?>
                                            <div class="flex items-start space-x-4 mb-4">
                                                <img src="assets/img/profile/<?= $cuser['profile_pic'] ?>"
                                                    class="w-16 h-16 rounded-full object-cover border-2 border-gray-200 shadow-sm">
                                                <div class="flex-1 flex flex-col">
                                                    <div class="bg-gray-100 rounded-2xl px-4 py-2">
                                                        <div class="flex items-baseline space-x-1">
                                                            <a href="?u=<?= $cuser['username'] ?>" class="font-bold text-primary text-lg hover:opacity-80">
                                                                @<?= $cuser['username'] ?>
                                                            </a>
                                                            <span class="text-sm text-gray-500 ml-2">(<?= show_time($comment['created_at']) ?>)</span>
                                                        </div>
                                                        <p class="text-gray-800 text-base mt-1"><?= $comment['comment'] ?></p>
                                                    </div>
                                                </div>
                                            </div>
                                        <?php endforeach; ?>
                                    </div>

                                    <!-- Add Comment -->
                                    <div class="p-4 border-t">
                                        <div class="flex items-center space-x-2">
                                            <input type="text"
                                                class="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent comment-input"
                                                placeholder="Add a comment...">
                                            <button class="bg-accent hover:bg-orange-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors add-comment"
                                                data-cs="comment-section<?= $post['id'] ?>"
                                                data-post-id="<?= $post['id'] ?>">
                                                Post
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Likes Modal -->
                    <div id="likes-modal-<?= $post['id'] ?>" class="modal-overlay fixed inset-0 bg-black bg-opacity-50 z-50 hidden items-center justify-center p-4">
                        <div class="modal-content bg-white rounded-2xl max-w-md w-full max-h-96 overflow-hidden">
                            <div class="flex items-center justify-between p-4 border-b">
                                <h5 class="text-lg font-bold text-primary">Likes</h5>
                                <button onclick="closeLikesModal('<?= $post['id'] ?>')"
                                    class="p-2 hover:bg-gray-100 rounded-full">
                                    <i class="fas fa-times text-gray-500"></i>
                                </button>
                            </div>
                            <div class="overflow-y-auto p-4">
                                <?php if (count($likes) < 1): ?>
                                    <p class="text-center text-gray-500 py-8">No likes yet</p>
                                <?php endif; ?>

                                <?php foreach ($likes as $like):
                                    $fuser = getUser($like['user_id']);
                                    $isFollowed = checkFollowed($like['user_id']);
                                ?>
                                    <div class="flex items-center justify-between py-3">
                                        <div class="flex items-center space-x-3">
                                            <img src="assets/img/profile/<?= $fuser['profile_pic'] ?>"
                                                class="w-10 h-10 rounded-full object-cover">
                                            <div>
                                                <a href="?u=<?= $fuser['username'] ?>" class="block hover:opacity-80">
                                                    <p class="font-semibold text-primary"><?= $fuser['first_name'] ?> <?= $fuser['last_name'] ?></p>
                                                    <p class="text-sm text-gray-500">@<?= $fuser['username'] ?></p>
                                                </a>
                                            </div>
                                        </div>

                                        <?php if ($user['id'] != $like['user_id']): ?>
                                            <?php if ($isFollowed): ?>
                                                <button class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors unfollowbtn"
                                                    data-user-id="<?= $fuser['id'] ?>">
                                                    Unfollow
                                                </button>
                                            <?php else: ?>
                                                <button class="bg-accent hover:bg-orange-600 text-white px-4 py-2 rounded-full text-sm font-medium transition-colors followbtn"
                                                    data-user-id="<?= $fuser['id'] ?>">
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
    // GSAP Animations
    gsap.registerPlugin(ScrollTrigger);

    // Animate profile card on load
    gsap.from(".user-profile-card", {
        duration: 0.8,
        y: -50,
        opacity: 0,
        ease: "power2.out"
    });

    // Animate suggestions card
    gsap.from(".suggestions-card", {
        duration: 0.8,
        y: 50,
        opacity: 0,
        delay: 0.2,
        ease: "power2.out"
    });

    // Animate suggestion items
    gsap.from(".suggestion-item", {
        duration: 0.6,
        x: -30,
        opacity: 0,
        stagger: 0.1,
        delay: 0.4,
        ease: "power2.out"
    });

    // Animate posts on scroll
    gsap.utils.toArray(".post-card").forEach((post, index) => {
        gsap.from(post, {
            scrollTrigger: {
                trigger: post,
                start: "top 80%",
                end: "bottom 20%",
                toggleActions: "play none none reverse"
            },
            duration: 0.8,
            y: 50,
            opacity: 0,
            delay: index * 0.1,
            ease: "power2.out"
        });
    });

    // Animate post images on hover
    document.querySelectorAll('.post-image').forEach(img => {
        img.addEventListener('mouseenter', () => {
            gsap.to(img, {
                duration: 0.3,
                scale: 1.02,
                ease: "power2.out"
            });
        });

        img.addEventListener('mouseleave', () => {
            gsap.to(img, {
                duration: 0.3,
                scale: 1,
                ease: "power2.out"
            });
        });
    });

    // Fixed Modal functions - Reset GSAP properties before showing
    function openPostModal(postId) {
        const modal = document.getElementById(`post-modal-${postId}`);
        const modalContent = modal.querySelector('.modal-content');

        // Reset any previous GSAP properties
        gsap.set(modalContent, {
            scale: 1,
            opacity: 1
        });

        modal.classList.remove('hidden');
        modal.classList.add('flex');

        // Animate entrance
        gsap.fromTo(modalContent, {
            scale: 0.8,
            opacity: 0
        }, {
            scale: 1,
            opacity: 1,
            duration: 0.4,
            ease: "power2.out"
        });
    }

    function closePostModal(postId) {
        const modal = document.getElementById(`post-modal-${postId}`);
        const modalContent = modal.querySelector('.modal-content');

        gsap.to(modalContent, {
            duration: 0.3,
            scale: 0.8,
            opacity: 0,
            ease: "power2.in",
            onComplete: () => {
                modal.classList.add('hidden');
                modal.classList.remove('flex');
                // Reset properties for next time
                gsap.set(modalContent, {
                    scale: 1,
                    opacity: 1
                });
            }
        });
    }

    function openLikesModal(postId) {
        const modal = document.getElementById(`likes-modal-${postId}`);
        const modalContent = modal.querySelector('.modal-content');

        // Reset any previous GSAP properties
        gsap.set(modalContent, {
            scale: 1,
            opacity: 1
        });

        modal.classList.remove('hidden');
        modal.classList.add('flex');

        // Animate entrance
        gsap.fromTo(modalContent, {
            scale: 0.8,
            opacity: 0
        }, {
            scale: 1,
            opacity: 1,
            duration: 0.4,
            ease: "power2.out"
        });
    }

    function closeLikesModal(postId) {
        const modal = document.getElementById(`likes-modal-${postId}`);
        const modalContent = modal.querySelector('.modal-content');

        gsap.to(modalContent, {
            duration: 0.3,
            scale: 0.8,
            opacity: 0,
            ease: "power2.in",
            onComplete: () => {
                modal.classList.add('hidden');
                modal.classList.remove('flex');
                // Reset properties for next time
                gsap.set(modalContent, {
                    scale: 1,
                    opacity: 1
                });
            }
        });
    }

    function toggleDropdown(dropdownId) {
        const dropdown = document.getElementById(dropdownId);
        dropdown.classList.toggle('hidden');
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.relative')) {
            document.querySelectorAll('[id^="dropdown-"]').forEach(dropdown => {
                dropdown.classList.add('hidden');
            });
        }
    });

    // Fixed: Close modals when clicking outside - using proper class selector
    document.querySelectorAll('.modal-overlay').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                const postId = modal.id.split('-').pop();
                if (modal.id.includes('post-modal')) {
                    closePostModal(postId);
                } else {
                    closeLikesModal(postId);
                }
            }
        });
    });

    // Like button animation and logic
    document.querySelectorAll('.like-toggle-btn').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.dataset.postId;
            const heartIcon = this;


            // GSAP heart animation
            gsap.fromTo(heartIcon, {
                scale: 1
            }, {
                scale: 1.4,
                duration: 0.15,
                yoyo: true,
                repeat: 1,
                ease: "power2.out"
            });

            // Heart burst effect
            for (let i = 0; i < 6; i++) {
                const particle = document.createElement('div');
                particle.innerHTML = '❤️';
                particle.style.position = 'absolute';
                particle.style.pointerEvents = 'none';
                particle.style.fontSize = '12px';
                particle.style.zIndex = '1000';

                const rect = heartIcon.getBoundingClientRect();
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

            // This is where you would typically make an AJAX call to your PHP backend
            // to toggle the like status in the database and update the count.
            // For demonstration, we'll just toggle the class visually.
            heartIcon.classList.toggle('fas');
            heartIcon.classList.toggle('far');
            heartIcon.classList.toggle('text-red-500');
            heartIcon.classList.toggle('text-gray-500');
            heartIcon.classList.toggle('group-hover:text-red-500'); // Ensure hover effect is maintained
        });
    });


    // Smooth scroll for better UX
    document.documentElement.style.scrollBehavior = 'smooth';
</script>