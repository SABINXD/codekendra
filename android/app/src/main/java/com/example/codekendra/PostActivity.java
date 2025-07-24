package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);  // Make sure your layout name is correct

  

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String serverIp = getString(R.string.server_ip);
        String baseUrl = "http://" + serverIp + "/codekendra/api/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        fetchPosts();
    }

    private void fetchPosts() {
        progressBar.setVisibility(View.VISIBLE);

        Call<List<Post>> call = apiService.getAllPosts();  // You must define this in ApiService
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();
                    postAdapter = new PostAdapter(PostActivity.this, postList);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Toast.makeText(PostActivity.this, "No posts found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                Log.e("PostFetchError", t.getMessage(), t);
            }
        });
    }
}
