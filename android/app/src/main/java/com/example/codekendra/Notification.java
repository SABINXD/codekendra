package com.example.codekendra;

public class Notification {
    private int id;
    private int fromUserId;
    private String message;
    private String type;
    private int readStatus;
    private String createdAt;
    private String userName;
    private String profilePic;

    // Constructors
    public Notification() {}

    public Notification(int id, int fromUserId, String message, String type, int readStatus,
                        String createdAt, String userName, String profilePic) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.message = message;
        this.type = type;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
        this.userName = userName;
        this.profilePic = profilePic;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFromUserId() { return fromUserId; }
    public void setFromUserId(int fromUserId) { this.fromUserId = fromUserId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getReadStatus() { return readStatus; }
    public void setReadStatus(int readStatus) { this.readStatus = readStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
}