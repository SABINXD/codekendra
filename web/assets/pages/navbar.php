<?php global $user; ?>
<div class="fixed top-0 left-0 right-0 z-50 w-full flex items-center justify-between px-4 py-3 border-b border-gray-100 bg-white shadow-md">
    <!-- Nav Logo -->
    <div class="flex-shrink-0">
        <a href="?" class="block">
            <img src="assets/img/logo.png" alt="CodeKendra" class="h-9 w-auto">
        </a>
    </div>

    <!-- Search Box -->
    <div class="relative mx-4 max-w-sm flex-grow">
        <form class="flex items-center w-full border border-gray-300 rounded-full bg-gray-100 px-4 py-2 focus-within:ring-2 focus-within:ring-purple-600">
            <input class="flex-grow bg-transparent border-none focus:outline-none focus:ring-0 px-0 py-0 h-auto text-sm text-gray-800 placeholder-gray-500"
                   type="search" id="search" placeholder="Looking for someone..."
                   aria-label="Search" autocomplete="off">
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
                class="lucide lucide-search text-gray-500 ml-2"
            >
                <circle cx="11" cy="11" r="8" />
                <path d="m21 21-4.3-4.3" />
            </svg>
        </form>
    </div>

    <!-- Nav Menu -->
    <ul id="nav-menu" class="flex items-center space-x-2 md:space-x-4">
        <li>
            <a class="flex items-center justify-center w-10 h-10 rounded-full text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors duration-200" href="?">
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
                    class="lucide lucide-home text-xl"
                >
                    <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                    <polyline points="9 22 9 12 15 12 15 22" />
                </svg>
                <span class="sr-only">Home</span>
            </a>
        </li>
        <li data-bs-toggle="modal" data-bs-target="#codeOptionModal">
            <a href="#" class="flex items-center justify-center w-10 h-10 rounded-full text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors duration-200">
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
                    class="lucide lucide-plus-square text-xl"
                >
                    <rect width="18" height="18" x="3" y="3" rx="2" />
                    <path d="M12 8v8" />
                    <path d="M8 12h8" />
                </svg>
                <span class="sr-only">Add Post</span>
            </a>
        </li>
        <li class="nav-item">
            <?php if (getUnreadNotificationsCount() > 0) { ?>
                <a class="relative flex items-center justify-center w-10 h-10 rounded-full text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors duration-200"
                   id="show_not" data-bs-toggle="offcanvas" href="#notification_sidebar" role="button" aria-controls="offcanvasExample">
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
                        class="lucide lucide-bell text-xl"
                    >
                        <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
                        <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
                    </svg>
                    <span class="absolute top-0 right-0 transform translate-x-1/2 -translate-y-1/2 bg-red-500 text-white text-xs rounded-full px-1.5 py-0.5">
                        <small><?= getUnreadNotificationsCount() ?></small>
                    </span>
                    <span class="sr-only">Notifications</span>
                </a>
            <?php } else { ?>
                <a class="flex items-center justify-center w-10 h-10 rounded-full text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors duration-200"
                   data-bs-toggle="offcanvas" href="#notification_sidebar" role="button" aria-controls="offcanvasExample">
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
                        class="lucide lucide-bell text-xl"
                    >
                        <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
                        <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
                    </svg>
                    <span class="sr-only">Notifications</span>
                </a>
            <?php } ?>
        </li>
        <li>
            <a class="relative flex items-center justify-center w-10 h-10 rounded-full text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors duration-200"
               data-bs-toggle="offcanvas" href="#messages_sidebar">
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
                    class="lucide lucide-message-circle-dots text-xl"
                >
                    <path d="M7.9 20A9 9 0 1 0 4 16.1L2 22Z" />
                    <path d="M8 10h.01" />
                    <path d="M12 10h.01" />
                    <path d="M16 10h.01" />
                </svg>
                <span id="msgcounter" class="absolute top-0 right-0 transform translate-x-1/2 -translate-y-1/2 bg-red-500 text-white text-xs rounded-full px-1.5 py-0.5">
                    <!-- This span will be updated by your JS for message count -->
                </span>
                <span class="sr-only">Messages</span>
            </a>
        </li>
        <li class="relative group"> <!-- Added 'group' class for dropdown hover -->
            <a class="flex items-center justify-center w-10 h-10 rounded-full" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <img src="assets/img/profile/<?= $user['profile_pic'] ?>" alt="Profile" height="30" width="30" class="rounded-full border border-gray-300 object-cover">
            </a>
            <ul class="dropdown-menu absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-md shadow-lg py-1 z-50 hidden group-hover:block"
                aria-labelledby="navbarDropdown">
                <li><a class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" href="?u=<?= $user['username'] ?>">
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
                        class="lucide lucide-user-circle inline-block h-4 w-4 mr-2"
                    >
                        <circle cx="12" cy="12" r="10" />
                        <circle cx="12" cy="10" r="3" />
                        <path d="M7 20.662V19a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v1.662" />
                    </svg>
                    My Profile
                </a></li>
                <li><a class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" href="?editprofile">Edit Profile</a></li>
                <li><a class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" href="#">Account Settings</a></li>
                <li>
                    <hr class="my-1 border-t border-gray-200">
                </li>
                <li><a class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" href="./assets/php/actions.php?logout">Logout</a></li>
            </ul>
        </li>
    </ul>
</div>
<!-- Navbar code finished -->

<!-- Search Result Overlay -->
<div id="search_result" class="absolute top-16 left-1/2 -translate-x-1/2 bg-white text-center rounded-lg border border-gray-200 shadow-lg py-3 px-4 w-80 z-50 hidden"
    data-bs-auto-close="true">
    <button type="button" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700" aria-label="Close" id="close_search">
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
            class="lucide lucide-x text-xl"
        >
            <path d="M18 6 6 18" />
            <path d="m6 6 12 12" />
        </svg>
    </button>
    <div id="sra" class="text-start mt-6">
        <p class="text-center text-gray-600 text-sm">Enter name or username</p>
        <!-- Placeholder for search results -->
    </div>
</div>

<script>
    // JavaScript to handle showing and hiding search results
    document.addEventListener('DOMContentLoaded', () => {
        const searchInput = document.getElementById('search');
        const searchResult = document.getElementById('search_result');
        const closeSearch = document.getElementById('close_search');
        const navbarDropdown = document.getElementById('navbarDropdown');
        const dropdownMenu = navbarDropdown.nextElementSibling; // Assuming dropdown-menu is the next sibling

        // Show search results when the search input is clicked
        searchInput.addEventListener('focus', () => {
            searchResult.style.display = 'block';
        });

        // Close the search result when the close button is clicked
        closeSearch.addEventListener('click', () => {
            searchResult.style.display = 'none';
        });

        // Hide search results if clicking outside the search box or results
        document.addEventListener('click', (event) => {
            if (!searchInput.contains(event.target) && !searchResult.contains(event.target)) {
                searchResult.style.display = 'none';
            }
        });

        // Handle dropdown menu visibility on hover for desktop
        // Note: data-bs-toggle="dropdown" might interfere with pure CSS hover.
        // If using Bootstrap JS for dropdowns, this part might need adjustment or removal.
        navbarDropdown.addEventListener('mouseenter', () => {
            if (window.innerWidth >= 768) { // Apply only on desktop (md breakpoint)
                dropdownMenu.classList.remove('hidden');
            }
        });

        navbarDropdown.parentElement.addEventListener('mouseleave', () => {
            if (window.innerWidth >= 768) {
                dropdownMenu.classList.add('hidden');
            }
        });

        // For mobile/click, Bootstrap's data-bs-toggle="dropdown" should handle it.
        // Ensure Bootstrap JS is loaded if you rely on data-bs-toggle.
    });
</script>
