package com.example.codekendra;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id") // Add user_id of the post creator
    private int userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("profile_pic") // Add profile_pic of the post creator
    private String profilePic;
    @SerializedName("post_text")
    private String postDescription;
    @SerializedName("post_img")
    private String postImage;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("is_liked_by_current_user") // Add this field
    private boolean isLikedByCurrentUser;
    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; } // Getter for userId
    public String getUserName() { return userName; }
    public String getProfilePic() { return profilePic; } // Getter for profilePic
    public String getPostDescription() { return postDescription; }
    public String getPostImage() { return postImage; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; } // Getter for isLikedByCurrentUser
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; } // Setter for userId
    public void setUserName(String userName) { this.userName = userName; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; } // Setter for profilePic
    public void setPostDescription(String postDescription) { this.postDescription = postDescription; }
    public void setPostImage(String postImage) { this.postImage = postImage; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.isLikedByCurrentUser = likedByCurrentUser; } // Setter for isLikedByCurrentUser
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}