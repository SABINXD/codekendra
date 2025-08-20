<?php global $user; ?>
<nav class="fixed top-0 left-0 right-0 z-50 w-full backdrop-blur-lg bg-white/90 border-b border-gray-100 shadow-sm transition-all duration-300">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
            <!-- Logo with animation -->
            <div class="flex-shrink-0 transform transition-transform duration-500 hover:scale-110">
                <a href="?" class="flex items-center space-x-2">
                    <div class="relative">
                        <img src="assets/img/logo.png" alt="CodeKendra" class="h-9 w-auto">
                        <div class="absolute inset-0 bg-gradient-to-r from-purple-500 to-indigo-600 rounded-full opacity-0 hover:opacity-20 transition-opacity duration-300"></div>
                    </div>
                    <!-- <span class="hidden sm:block text-xl font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">CodeKendra</span> -->
                </a>
            </div>
            
            <!-- Enhanced Search Bar -->
            <div class="flex-1 max-w-lg mx-4">
                <div class="relative">
                    <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
                        </svg>
                    </div>
                    <input id="search" type="search" placeholder="Looking for someone..." 
                           class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-full bg-gray-50 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300">
                    <div class="absolute inset-y-0 right-0 flex items-center pr-3">
                        <button id="voice-search" class="text-gray-400 hover:text-purple-600 focus:outline-none transition-colors duration-200">
                            <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M7 4a3 3 0 016 0v4a3 3 0 11-6 0V4zm4 10.93A7.001 7.001 0 0017 8a1 1 0 10-2 0A5 5 0 015 8a1 1 0 00-2 0 7.001 7.001 0 006 6.93V17H6a1 1 0 100 2h8a1 1 0 100-2h-3v-2.07z" clip-rule="evenodd" />
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
            
            <!-- Navigation Items -->
            <div class="flex items-center space-x-1 md:space-x-2">
                <!-- Home Button -->
                <a href="?" class="p-2 rounded-full text-gray-600 hover:bg-purple-50 hover:text-purple-600 transition-all duration-200 group">
                    <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                    <span class="sr-only">Home</span>
                </a>
                
                <!-- Create Button -->
                <button data-bs-toggle="modal" data-bs-target="#codeOptionModal" class="p-2 rounded-full text-gray-600 hover:bg-purple-50 hover:text-purple-600 transition-all duration-200 group relative">
                    <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    <!-- <span class="sr-only">Create</span> -->
                    <!-- <div class="absolute -top-1 -right-1 w-3 h-3 bg-gradient-to-r from-purple-500 to-indigo-600 rounded-full animate-ping opacity-75 group-hover:opacity-100"></div> -->
                </button>
                
                <!-- Notifications -->
                <div class="relative">
                    <button id="show_not" data-bs-toggle="offcanvas" href="#notification_sidebar" role="button" aria-controls="offcanvasExample" 
                            class="p-2 rounded-full text-gray-600 hover:bg-purple-50 hover:text-purple-600 transition-all duration-200 group">
                        <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                        </svg>
                        <span class="sr-only">Notifications</span>
                        <?php if (getUnreadNotificationsCount() > 0) { ?>
                            <span class="absolute top-0 right-0 flex items-center justify-center w-5 h-5 text-xs font-bold text-white bg-gradient-to-r from-purple-500 to-indigo-600 rounded-full animate-pulse">
                                <?= getUnreadNotificationsCount() ?>
                            </span>
                        <?php } ?>
                    </button>
                </div>
                
                <!-- Messages -->
                <div class="relative">
                    <button data-bs-toggle="offcanvas" href="#messages_sidebar" 
                            class="p-2 rounded-full text-gray-600 hover:bg-purple-50 hover:text-purple-600 transition-all duration-200 group">
                        <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                        </svg>
                        <span class="sr-only">Messages</span>
                        <span id="msgcounter" class="absolute top-0 right-0 flex items-center justify-center w-5 h-5 text-xs font-bold text-white bg-gradient-to-r from-purple-500 to-indigo-600 rounded-full animate-pulse hidden">
                            <!-- This will be updated by JS -->
                        </span>
                    </button>
                </div>
                
                <!-- User Profile Dropdown -->
                <div class="relative ml-2">
                    <button id="user-menu-button" type="button" class="flex items-center justify-center w-10 h-10 rounded-full focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-200" 
                            aria-expanded="false" aria-haspopup="true">
                        <span class="sr-only">Open user menu</span>
                        <img class="h-9 w-9 rounded-full object-cover border-2 border-white shadow-md" 
                             src="assets/img/profile/<?= !empty($user['profile_pic']) ? $user['profile_pic'] : 'default-avatar.png' ?>" 
                             alt="<?= !empty($user['name']) ? $user['name'] : 'User' ?>"
                             onerror="this.src='assets/img/profile/default-avatar.png'">
                    </button>
                    
                    <!-- Dropdown Menu -->
                    <div id="user-menu" class="hidden origin-top-right absolute right-0 mt-2 w-56 rounded-xl shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none transition-all duration-300 transform opacity-0 scale-95" 
                         role="menu" aria-orientation="vertical" aria-labelledby="user-menu-button">
                        <div class="py-1">
                            <div class="px-4 py-3 border-b border-gray-100">
                                <p class="text-sm font-medium text-gray-900"><?= !empty($user['name']) ? $user['name'] : 'User' ?></p>
                                <p class="text-sm text-gray-500"><?= !empty($user['email']) ? $user['email'] : 'user@example.com' ?></p>
                            </div>
                            <a href="?u=<?= !empty($user['username']) ? $user['username'] : 'profile' ?>" class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors duration-200">
                                <svg class="mr-3 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                                Your Profile
                            </a>
                            <a href="?editprofile" class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors duration-200">
                                <svg class="mr-3 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                </svg>
                                Settings
                            </a>
                            <a href="#" class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors duration-200">
                                <svg class="mr-3 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                </svg>
                                Privacy
                            </a>
                            <div class="border-t border-gray-100"></div>
                            <a href="./assets/php/actions.php?logout" class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-purple-50 hover:text-purple-600 transition-colors duration-200">
                                <svg class="mr-3 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                </svg>
                                Sign out
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Search Results Overlay -->
    <div id="search_result" class="absolute top-16 left-1/2 transform -translate-x-1/2 w-full max-w-2xl bg-white rounded-xl shadow-xl border border-gray-200 overflow-hidden z-50 hidden">
        <div class="p-4 border-b border-gray-200 flex justify-between items-center">
            <h3 class="text-lg font-medium text-gray-900">Search Results</h3>
            <button id="close_search" class="text-gray-400 hover:text-gray-500">
                <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
        </div>
        <div id="sra" class="p-4 max-h-96 overflow-y-auto">
            <p class="text-center text-gray-500 py-8">Enter name or username</p>
        </div>
    </div>
</nav>

<script>
document.addEventListener('DOMContentLoaded', () => {
    // Search functionality
    const searchInput = document.getElementById('search');
    const searchResult = document.getElementById('search_result');
    const closeSearch = document.getElementById('close_search');
    const sra = document.getElementById('sra');
    
    // Show search results when the search input is focused
    searchInput.addEventListener('focus', () => {
        searchResult.classList.remove('hidden');
    });
    
    // Update search results when typing
    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        
        if (query.length > 0) {
            // Show search results immediately
            sra.innerHTML = `
                <div class="space-y-4">
                    <div class="flex items-center p-3 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <img src="https://picsum.photos/seed/user1/40/40.jpg" alt="User" class="h-10 w-10 rounded-full mr-3">
                        <div>
                            <p class="font-medium">Sabin Dhungana</p>
                            <p class="text-sm text-gray-500">@alexj • Frontend Developer</p>
                        </div>
                    </div>
                    <div class="flex items-center p-3 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <img src="https://picsum.photos/seed/user2/40/40.jpg" alt="User" class="h-10 w-10 rounded-full mr-3">
                        <div>
                            <p class="font-medium">Sam Wilson</p>
                            <p class="text-sm text-gray-500">@samw • UI/UX Designer</p>
                        </div>
                    </div>
                    <div class="p-3 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <p class="font-medium">Post: "Best practices for responsive design"</p>
                        <p class="text-sm text-gray-500">By Taylor Kim • 2 days ago</p>
                    </div>
                </div>
            `;
        } else {
            // Show initial message when input is empty
            sra.innerHTML = `<p class="text-center text-gray-500 py-8">Enter name or username</p>`;
        }
    });
    
    // Close the search result when the close button is clicked
    closeSearch.addEventListener('click', () => {
        searchResult.classList.add('hidden');
    });
    
    // Hide search results if clicking outside the search box or results
    document.addEventListener('click', (event) => {
        if (!searchInput.contains(event.target) && !searchResult.contains(event.target)) {
            searchResult.classList.add('hidden');
        }
    });
    
    // User dropdown menu
    const userMenuButton = document.getElementById('user-menu-button');
    const userMenu = document.getElementById('user-menu');
    
    userMenuButton.addEventListener('click', () => {
        userMenu.classList.toggle('hidden');
        if (!userMenu.classList.contains('hidden')) {
            // Animate in
            setTimeout(() => {
                userMenu.classList.remove('opacity-0', 'scale-95');
                userMenu.classList.add('opacity-100', 'scale-100');
            }, 10);
        } else {
            // Animate out
            userMenu.classList.remove('opacity-100', 'scale-100');
            userMenu.classList.add('opacity-0', 'scale-95');
        }
    });
    
    // Close dropdown when clicking outside
    document.addEventListener('click', (event) => {
        if (!userMenuButton.contains(event.target) && !userMenu.contains(event.target)) {
            userMenu.classList.add('hidden');
            userMenu.classList.remove('opacity-100', 'scale-100');
            userMenu.classList.add('opacity-0', 'scale-95');
        }
    });
    
    // Voice search functionality
    const voiceSearch = document.getElementById('voice-search');
    voiceSearch.addEventListener('click', () => {
        // Placeholder for voice search implementation
        alert('Voice search would be activated here. This requires Web Speech API integration.');
    });
    
    // Simulate message counter updates
    const msgCounter = document.getElementById('msgcounter');
    // Simulate receiving a new message after 5 seconds
    setTimeout(() => {
        msgCounter.textContent = '3';
        msgCounter.classList.remove('hidden');
    }, 5000);
});
</script>