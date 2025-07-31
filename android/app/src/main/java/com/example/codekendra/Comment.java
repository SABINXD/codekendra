package com.example.codekendra;

public class Comment {
    private int id;
    private int userId;
    private String userName;
    private String commentText;
    private String createdAt;
    private String profilePic;

    public Comment() {}

    public Comment(int id, int userId, String userName, String commentText, String createdAt, String profilePic) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.profilePic = profilePic;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
}