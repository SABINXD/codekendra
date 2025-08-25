package com.example.codekendra;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("profile_pic")
    private String profilePic;
    @SerializedName("post_text")
    private String postDescription;
    @SerializedName("post_img")
    private String postImage;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("is_liked_by_current_user")
    private boolean isLikedByCurrentUser;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("code_content")
    private String codeContent;
    @SerializedName("code_language")
    private String codeLanguage;
    @SerializedName("tags")
    private List<String> tags;

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getProfilePic() { return profilePic; }
    public String getPostDescription() { return postDescription; }
    public String getPostImage() { return postImage; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public String getCreatedAt() { return createdAt; }
    public String getCodeContent() { return codeContent; }
    public String getCodeLanguage() { return codeLanguage; }
    public List<String> getTags() { return tags; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setPostDescription(String postDescription) { this.postDescription = postDescription; }
    public void setPostImage(String postImage) { this.postImage = postImage; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.isLikedByCurrentUser = likedByCurrentUser; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setCodeContent(String codeContent) { this.codeContent = codeContent; }
    public void setCodeLanguage(String codeLanguage) { this.codeLanguage = codeLanguage; }
    public void setTags(List<String> tags) { this.tags = tags; }

    // Helper methods
    public boolean hasCode() {
        return codeContent != null && !codeContent.isEmpty() && !codeContent.equals("null");
    }

    public boolean hasImage() {
        return postImage != null && !postImage.isEmpty() && !postImage.equals("null");
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}