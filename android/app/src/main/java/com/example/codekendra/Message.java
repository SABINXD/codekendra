package com.example.codekendra;

public class Message {
    private int id;
    private int from_user_id;
    private int to_user_id;
    private String message;
    private int read_status;
    private String created_at;

    // Default constructor
    public Message() {}

    // Constructor for new messages
    public Message(int from_user_id, int to_user_id, String message) {
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.message = message;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFrom_user_id() { return from_user_id; }
    public void setFrom_user_id(int from_user_id) { this.from_user_id = from_user_id; }

    public int getTo_user_id() { return to_user_id; }
    public void setTo_user_id(int to_user_id) { this.to_user_id = to_user_id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRead_status() { return read_status; }
    public void setRead_status(int read_status) { this.read_status = read_status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}