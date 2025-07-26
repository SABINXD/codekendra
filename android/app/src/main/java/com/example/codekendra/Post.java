package com.example.codekendra;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("id")
    private int id;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("post_text")
    private String postDescription;

    @SerializedName("post_img")
    private String postImage;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("comment_count")
    private int commentCount;

    @SerializedName("created_at") 
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getPostDescription() { return postDescription; }
    public String getPostImage() { return postImage; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPostDescription(String postDescription) { this.postDescription = postDescription; }
    public void setPostImage(String postImage) { this.postImage = postImage; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
