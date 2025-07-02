<?php global $user; ?>
<div class=" main-container">
    <div class=" nav-bar">
        <div class="nav-logo">
            <a href="?"><img src="assets/img/circle-icon.png" alt="CodeKendra"></a>
        </div>

        <div class="search-box">
            <form class="d-flex search-bar" id="searchform">
                <i class="fa-solid fa-bars"></i>
                <input class="form-control me-2" type="search" id="search" placeholder="looking for someone.."
                    aria-label="Search" autocomplete="off">
                <i class="fa-solid fa-magnifying-glass"></i>


            </form>

        </div>

        <div id="more-menu" class="more-hamburger">
            <i class="fa-solid fa-bars"></i>
        </div>
        <div id="nav-menu" class="nav-menu">
            <li><a class="nav-active" href="?"><i class="fa-solid fa-house"></i></a></li>
            <li data-bs-toggle="modal" data-bs-target="#addpost"><a href="#"><i class="fa-solid fa-square-plus"></i></a>
            </li>
            <li class="nav-item">


                <?php
                if (getUnreadNotificationsCount() > 0) {
                ?>
                    <a class="nav-link text-dark position-relative" id="show_not" data-bs-toggle="offcanvas" href="#notification_sidebar" role="button" aria-controls="offcanvasExample">
                        <i style="color:#95979a;" class="fa-solid fa-bell"></i>
                        <span class="un-count position-absolute start-10 translate-middle badge p-1 rounded-pill bg-danger">
                            <small><?= getUnreadNotificationsCount() ?></small>
                        </span>
                    </a>

                <?php
                } else {
                ?>
                    <a class="nav-link text-dark" data-bs-toggle="offcanvas" href="#notification_sidebar" role="button" aria-controls="offcanvasExample"><i style="color:#95979a;" class="fa-solid fa-bell"></i></a>
                <?php
                }
                ?>


            </li>
            <li><a data-bs-toggle="offcanvas" href="#messages_sidebar"><i class="fa-solid fa-message"></i></a> <span id="msgcounter" class="un-count position-absolute start-10 translate-middle badge p-1 rounded-pill bg-danger">

                </span></li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown"
                    aria-expanded="false">
                    <img src="assets/img/profile/<?= $user['profile_pic'] ?>" alt="" height="30"
                        class="rounded-circle border">
                </a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li><a class="dropdown-item" href="?u=<?= $user['username'] ?>"><i class="bi bi-person"></i> My Profile</a></li>
                    <li><a class="dropdown-item" href="?editprofile">Edit Profile</a></li>
                    <li><a class="dropdown-item" href="#">Account Settings</a></li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>
                    <li><a class="dropdown-item" href="./assets/php/actions.php?logout">Logout</a></li>
                </ul>
            </li>
        </div>
    </div>


    <!-- Navbar code finshed  -->


    <div id="search_result" class="bg-white text-end rounded border shadow py-3 px-4 mt-5"
        style="display: none; margin: 10vh auto; padding-top: 200px; width: 300px; position: absolute; z-index: 99; top:5%; left:35%;"
        data-bs-auto-close="true">
        <button type="button" class="btn-close" aria-label="Close" id="close_search"></button>
        <div id="sra" class="text-start">
            <p class="text-center text-muted">Enter name or username</p>
        </div>
    </div>


    <script>
        // JavaScript to handle showing and hiding search results
        document.addEventListener('DOMContentLoaded', () => {
            const searchInput = document.getElementById('search');
            const searchResult = document.getElementById('search_result');
            const closeSearch = document.getElementById('close_search');

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
        });
    </script>