package com.example.codekendra;

public class ChatUser {
    private int userId;
    private String userName;
    private String profilePic;
    private String lastMessage;
    private String timestamp;
    private int unreadCount;

    // Default constructor
    public ChatUser() {}

    // Constructor
    public ChatUser(int userId, String userName, String profilePic, String lastMessage, String timestamp, int unreadCount) {
        this.userId = userId;
        this.userName = userName;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}