package com.example.codekendra;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("user_name")
    public String userName;

    @SerializedName("post_text")
    public String postDescription;

    @SerializedName("post_img")
    public String postImage;

    public int likeCount;
    public int commentCount;
}
