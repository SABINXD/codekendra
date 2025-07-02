// Menu toggle setup

function setupMenuToggle() {

    const navMenu = document.getElementById('nav-menu');

    const moreMenu = document.getElementById('more-menu');

    const mainWrapper = document.querySelector('.main-wrapper');



    if (window.matchMedia("(max-width: 600px)").matches && moreMenu && mainWrapper) {

        moreMenu.addEventListener('click', function (e) {

            navMenu.style.display = "block";

        });



        mainWrapper.addEventListener('click', function (e) {

            navMenu.style.display = "none";

        });

    }

}



// Call the function initially

setupMenuToggle();



// Recheck when the window is resized

window.addEventListener('resize', setupMenuToggle);



// Appel when the user is blocked

document.addEventListener('DOMContentLoaded', function () {

    const logoutButton = document.getElementById('logoutButton');

    const appealButton = document.getElementById('appealButton');



    logoutButton.addEventListener('click', function () {

        window.location.href = '';  // Specify URL for logout

    });



    appealButton.addEventListener('click', function () {

        const email = "support@CodeKendra.com";

        const subject = "Appeal for Ban";

        const body = "Dear Admin,\n\nI would like to appeal my ban. Please review my case.\n\nThank you.";

        window.location.href = `mailto:${email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;

    });

});



// JS to verify user

const codeInput = document.querySelector('#codenumber');

if (codeInput) {

    codeInput.addEventListener('input', () => {

        // Remove any non-numeric characters

        codeInput.value = codeInput.value.replace(/\D/g, '');



        // Limit input to 6 digits

        if (codeInput.value.length > 6) {

            codeInput.value = codeInput.value.slice(0, 6);

        }

    });

}

//for follw user

$(".followbtn").click(function () {



    var user_id_v = $(this).data('userId');

    var button = this;

    $(button).attr('disabled', true);





    $.ajax({

        url: 'assets/php/ajax.php?follow',

        method: 'post',

        dataType: 'json',

        data: { user_id: user_id_v },



        success: function (response) {

            if (response.status) {

                $(button).data('user-id', 0);

                $(button).html('<i class="fa-solid fa-circle-check"></i> Followed');



            } else {

                $(button).attr('disabled', false);

                alert('something went wrong');

            }

        }

    });

});



//for unfollow user

$(".unfollowbtn").click(function () {



    var user_id_v = $(this).data('userId');

    var button = this;

    $(button).attr('disabled', true);





    $.ajax({

        url: 'assets/php/ajax.php?unfollow',

        method: 'post',

        dataType: 'json',

        data: { user_id: user_id_v },



        success: function (response) {

            if (response.status) {

                $(button).data('user-id', 0);

                $(button).html('<i class="fa-solid fa-circle-check"></i> UnFollowed');


            } else {

                $(button).attr('disabled', false);

                alert('something went wrong');

            }

        }

    });

});





//for like the post

$(".like_btn").click(function () {
    var post_id_v = $(this).data('postId');
    var button = this;
    $(button).attr('disabled', true);

    $.ajax({
        url: 'assets/php/ajax.php?like',
        method: 'post',
        dataType: 'json',
        data: { post_id: post_id_v },
        success: function (response) {
            if (response.status) {
                $(button).attr('disabled', false);
                $(button).hide();
                $(button).siblings('.unlike_btn').css('display', 'block');

                // Update like count using Ajax
                $.ajax({
                    url: 'assets/php/ajax.php?get_like_count', // New endpoint for getting like count
                    method: 'post',
                    dataType: 'json',
                    data: { post_id: post_id_v },
                    success: function (response) {
                        // Update the like count display
                        $(button).parent().find('p').text(response.like_count + ' Likes');
                    }
                });

            } else {
                $(button).attr('disabled', false);
                alert('Something went wrong');
            }
        }
    });
});



// for unlike post
$(".unlike_btn").click(function () {

    var post_id_v = $(this).data('postId');

    var button = this;

    $(button).attr('disabled', true);





    $.ajax({

        url: 'assets/php/ajax.php?unlike',

        method: 'post',

        dataType: 'json',

        data: { post_id: post_id_v },



        success: function (response) {



            if (response.status) {





                $(button).attr('disabled', false);

                $(button).hide();

                $(button).siblings('.like_btn').css('display', 'block');


                $.ajax({
                    url: 'assets/php/ajax.php?get_like_count',
                    method: 'post',
                    dataType: 'json',
                    data: { post_id: post_id_v },
                    success: function (response) {
                        console.log("Like Count Response:", response); // Log the response for debugging
                        $(button).parent().find('p').text(response.like_count + ' Likes');
                    },

                });





            } else {

                $(button).attr('disabled', false);

                alert('something went wrong');

            }

        }

    });

});

// fro commeting in a post
$(".add-comment").click(function () {
    var button = this;

    var comment_v = $(button).siblings('.comment-input').val();
    if (comment_v == '') {
        return 0;
    }
    var post_id_v = $(this).data('postId');
    var cs = $(this).data('cs');

    $(button).attr('disabled', true);
    $(button).siblings('.comment-input').attr('disabled', true);
    $.ajax({
        url: 'assets/php/ajax.php?addcomment',
        method: 'post',
        dataType: 'json',
        data: { post_id: post_id_v, comment: comment_v },
        success: function (response) {

            if (response.status) {
                $(button).attr('disabled', false);
                $(button).siblings('.comment-input').attr('disabled', false);
                $(button).siblings('.comment-input').val('');
                $("#" + cs).append(response.comment);
                $('.nce').hide();

                $.ajax({
                    url: 'assets/php/ajax.php?get_comment_count',
                    method: 'POST',
                    dataType: 'json',
                    data: { post_id: post_id_v },
                    success: function (response) {
                        // Update the comment count display
                        $(button).parent().siblings('.post-react-comment').find('.post-opinion p').text(response.comment_count + ' Comments');
                    }
                });





            } else {
                $(button).attr('disabled', false);
                $(button).siblings('.comment-input').attr('disabled', false);

                alert('Something went wrong');
            }
        }
    });
});
//for the time ago
jQuery(document).ready(function () {
    jQuery("time.timeago").timeago();
});

// unblocking user
$(".unblockbtn").click(function () {
    var user_id_v = $(this).data('userId');
    var button = this;
    $(button).attr('disabled', true);
    console.log('clicked');
    $.ajax({
        url: 'assets/php/ajax.php?unblock',
        method: 'post',
        dataType: 'json',
        data: { user_id: user_id_v },
        success: function (response) {
            console.log(response);
            if (response.status) {
                location.reload();
            } else {
                $(button).attr('disabled', false);

                alert('something is wrong,try again after some time');
            }
        }
    });
});

// function to search user

var sr = false;

$("#search").focus(function () {
    $("#search_result").show();


});



$("#close_search").click(function () {
    $("#search_result").hide();
});

$("#search").keyup(function () {
    var keyword_v = $(this).val();

    $.ajax({
        url: 'assets/php/ajax.php?search',
        method: 'post',
        dataType: 'json',
        data: { keyword: keyword_v },
        success: function (response) {
            console.log(response);
            if (response.status) {
                $("#sra").html(response.users);

            } else {


                $("#sra").html('<p class="text-center text-muted">no user found !</p>');




            }



        }
    });

});
//function for notifctaion read uing jquery
$("#show_not").click(function () {

    $.ajax({
        url: 'assets/php/ajax.php?notread',
        method: 'post',
        dataType: 'json',
        success: function (response) {

            if (response.status) {
                $(".un-count").hide();
            }



        }
    });

});
var chatting_user_id =0;
$(".chatlist_item").click();
function popchat(user_id){
    $("#user_chat").html(`<div class="spinner-border text-primary" role="status">
  <span class="sr-only"></span>
</div>`);
    $("#chatter_username").text('loading....');
    $("#chatter_name").text('');
    $("#chatter_pic").attr('src','assets/img/profile/default_profile.jpg');

    chatting_user_id = user_id;
    $("#sendmsg").attr('data-user-id',user_id);
    
}// function to send messages
$("#sendmsg").click(function () {
    var user_id = chatting_user_id;
    var msg = $("#msginput").val();
    if (!msg) return;

    $("#sendmsg").attr('disabled', true);
    $("#msginput").attr('disabled', true);

    $.ajax({
        url: 'assets/php/ajax.php?sendmessage',
        method: 'post',
        dataType: 'json',
        data: { user_id: user_id, msg: msg }
    })
    .done(function (response) {
        if (response.status) {
            $("#msginput").val('');
        } else {
            alert('Something went wrong while sending the message.');
        }
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        console.error('AJAX Error:', textStatus, errorThrown);
        alert('Network error. Please try again later.');
    })
    .always(function () {
        $("#sendmsg").attr('disabled', false);
        $("#msginput").attr('disabled', false);
    });
});

// function to sync the message using jax
function synmsg() {
    
    $.ajax({
        url: 'assets/php/ajax.php?getmessages',
        method: 'post',
        dataType: 'json',
        data:{chatter_id:chatting_user_id},
        success: function (response) {
          $("#chatlist").html(response.chatlist);
          if(response.newmsgcount==0){
            $("#msgcounter").hide();
          }else{
            $("#msgcounter").show();
            $("#msgcounter").html("<small>"+response.newmsgcount+"</small>");
          }
          if(response.blocked){

$("#msgsender").hide();
$("#blerror").show();
          }else{
            $("#msgsender").show();
$("#blerror").hide();
          }
            if (chatting_user_id !== 0) {
                $("#user_chat").html(response.chat.msgs);
                $("#chatter_username").text(response.chat.userdata.username);
                $("#chatter_name").text(response.chat.userdata.first_name+' '+response.chat.userdata.last_name);
                $("#chatter_pic").attr('src','assets/img/profile/'+ response.chat.userdata.profile_pic);
    
            }
         

        },
        error: function (xhr, status, error) {
            console.error("Error:", error, "Status:", status, "Response:", xhr.responseText);
        }

    });
}
synmsg();
setInterval(() => {
    synmsg();
}, 1000);



