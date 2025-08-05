package com.example.codekendra;

import java.util.List;

public class Post {
    private int id;
    private int userId;
    private String userName;
    private String username;
    private String postDescription;
    private String postImage;
    private String profilePic;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private String createdAt;

    // Code-related fields
    private String codeContent;
    private String codeLanguage;
    private List<String> tags;
    private String sourceTable;

    // Constructors
    public Post() {}

    public Post(int id, int userId, String userName, String postDescription) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.postDescription = postDescription;
    }

    // Helper methods
    public boolean hasCode() {
        return codeContent != null && !codeContent.trim().isEmpty();
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public boolean isCodePost() {
        return "code_post".equals(sourceTable);
    }

    public boolean isSimplePost() {
        return "posts".equals(sourceTable);
    }

    public boolean isLiked() {
        return likedByCurrentUser;
    }

    public void setLiked(boolean liked) {
        this.likedByCurrentUser = liked;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", hasCode=" + hasCode() +
                ", hasTags=" + hasTags() +
                ", sourceTable='" + sourceTable + '\'' +
                '}';
    }
}