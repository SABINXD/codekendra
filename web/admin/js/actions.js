$(".verify_user_btn").click(function () {
  var user_id_v = $(this).data("userId");
  var button = this;
  $(button).attr("disabled", true);

  $.ajax({
    url: "php/admin_ajax.php?verify_user",
    method: "post",
    dataType: "json",
    data: { user_id: user_id_v },
    success: function (response) {
      console.log(response);
      if (response.status) {
        $(button).text("Verified");
      } else {
        $(button).attr("disabled", false);
        alert("something is wrong,try again after some time");
      }
    },
  });
});

$(".block_user_btn").click(function () {
  var user_id_v = $(this).data("userId");
  var button = this;
  $(button).attr("disabled", true);

  $.ajax({
    url: "php/admin_ajax.php?block_user",
    method: "post",
    dataType: "json",
    data: { user_id: user_id_v },
    success: function (response) {
      console.log(response);
      if (response.status) {
        $(button).hide();
        $(button).siblings(".unblock_user_btn").show();
        $(button).siblings(".unblock_user_btn").attr("disabled", false);
      } else {
        $(button).attr("disabled", false);
        alert("something is wrong,try again after some time");
      }
    },
  });
});

$(".unblock_user_btn").click(function () {
  var user_id_v = $(this).data("userId");
  var button = this;
  $(button).attr("disabled", true);

  $.ajax({
    url: "php/admin_ajax.php?unblock_user",
    method: "post",
    dataType: "json",
    data: { user_id: user_id_v },
    success: function (response) {
      console.log(response);
      if (response.status) {
        $(button).hide();
        $(button).siblings(".block_user_btn").show();
        $(button).siblings(".block_user_btn").attr("disabled", false);
      } else {
        $(button).attr("disabled", false);
        alert("something is wrong,try again after some time");
      }
    },
  });
});
$(".delete-post").click(function () {
  var post_id_v = $(this).data("postId");
  var button = this;
  $(button).attr("disabled", true);
  $(button).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Deleting...'); // Show spinner

  $.ajax({
    url: "php/admin_ajax.php?delete_post",
    method: "post",
    dataType: "json",
    data: { post_id: post_id_v },
    success: function (response) {
      console.log(response);
      if (response.status) {
        $(button).closest('tr').fadeOut(500, function() { // Fade out the row
          $(this).remove(); // Remove the row after fade out
        });
        // Optionally update counts on the page if needed
         let totalPosts =  parseInt($('.totalPosts').text()) -1;
        $('.totalPosts').text(totalPosts);


      } else {
        $(button).attr("disabled", false);
        $(button).text("Delete"); // Reset button text
        alert("Something went wrong, try again after some time.");
      }
    },
    error: function(xhr, status, error) {
      console.error("AJAX Error:", status, error);
      $(button).attr("disabled", false);
      $(button).text("Delete"); // Reset button text
      alert("An error occurred during the request.");
    }
  });
});

$(".warn-post").click(function () {
    var post_id_v = $(this).data("postId");
    var button = this;
    $(button).attr("disabled", true);
    $(button).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Warning...');

    $.ajax({
        url: "php/admin_ajax.php?warn_post",
        method: "post",
        dataType: "json",
        data: { post_id: post_id_v },
        success: function (response) {
            console.log("Response:", response); // Check the response in the console
            if (response.status) {
                $(button).text("Warned"); // Update button text on success
                $(button).removeClass("btn-primary").addClass("btn-success"); // Change button color
                $(button).prop("disabled", true); // Disable the button
                // Optionally remove the row if you want:
                // $(button).closest('tr').fadeOut(500, function() {
                //     $(this).remove();
                // });

            } else {
                console.error("Warning failed:", response); // Log error for debugging
                $(button).attr("disabled", false);
                $(button).text("Send Warning");
                alert("Something went wrong, try again later.");
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX Error:", status, error, xhr); // Include xhr in the error log
            $(button).attr("disabled", false);
            $(button).text("Send Warning");
            alert("An error occurred during the request.");
        }
    });
});