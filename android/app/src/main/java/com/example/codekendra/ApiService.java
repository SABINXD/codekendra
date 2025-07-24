package com.example.codekendra;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("create_post.php")
    Call<ResponseBody> createPost(
            @Field("username") String username,
            @Field("caption") String caption,
            @Field("image") String imageBase64
    );

  
    @POST("get_feed.php")
    Call<List<Post>> getAllPosts();

}
