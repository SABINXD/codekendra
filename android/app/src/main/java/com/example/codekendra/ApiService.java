package com.example.codekendra;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("get_feed.php")
    Call<List<Post>> getAllPosts();
}
