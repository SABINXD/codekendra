package com.example.codekendra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.NetworkError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

public class PostActivity extends BaseActivity {
    private static final String TAG = "PostActivity";
    private EditText postCaption;
    private ImageView mediaPreview;
    private ImageView addImage, addVideo;
    private Button btnCancel, btnUpload;
    private byte[] imageBytes = null;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView profilePicImageView;
    private TextView tvCurrentUserUsername;
    private boolean profileLoaded = false; // Prevent multiple loads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setupBottomNav();
        initializeViews();
        setupImagePicker();
        setupClickListeners();
        displayCurrentUserProfile();
    }

    private void initializeViews() {
        postCaption = findViewById(R.id.post_caption);
        mediaPreview = findViewById(R.id.media_preview);
        addImage = findViewById(R.id.add_image);
        addVideo = findViewById(R.id.add_video);
        btnCancel = findViewById(R.id.btn_cancel);
        btnUpload = findViewById(R.id.btn_upload);
        mediaPreview.setVisibility(View.GONE);
        profilePicImageView = findViewById(R.id.profilePic);
        tvCurrentUserUsername = findViewById(R.id.tvCurrentUserUsername);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        handleSelectedImage(selectedImageUri);
                    }
                }
        );
    }

    private void setupClickListeners() {
        addImage.setOnClickListener(v -> {
            requestImagePermission();
            launchImagePicker();
        });

        addVideo.setOnClickListener(v ->
                Log.d(TAG, "Video picker not yet implemented")
        );

        btnCancel.setOnClickListener(v -> {
            clearForm();
        });

        btnUpload.setOnClickListener(v -> uploadPost());
    }

    private void displayCurrentUserProfile() {
        // Prevent multiple loads
        if (profileLoaded) return;

        SessionManager sessionManager = new SessionManager(this);
        String username = sessionManager.getUsername();
        String profilePicFilename = sessionManager.getProfilePic();
        String serverIp = getString(R.string.server_ip);

        Log.d(TAG, "Loading profile - Username: " + username + ", ProfilePic: " + profilePicFilename);

        // Set username immediately
        if (username != null && !username.isEmpty()) {
            tvCurrentUserUsername.setText("Posting as @" + username);
        } else {
            tvCurrentUserUsername.setText("Loading username...");
        }

        // Set default profile pic immediately to prevent flickering
        profilePicImageView.setImageResource(R.drawable.profile_placeholder);

        // Load profile picture with better caching
        if (profilePicFilename != null && !profilePicFilename.isEmpty() &&
                !profilePicFilename.equals("default_profile.jpg") && !profilePicFilename.equals("null")) {

            String profilePicUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePicFilename;
            Log.d(TAG, "Loading profile pic from: " + profilePicUrl);

            Glide.with(this)
                    .load(profilePicUrl)
                    .circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(profilePicImageView);
        }

        profileLoaded = true;
    }

    private void clearForm() {
        postCaption.setText("");
        mediaPreview.setImageDrawable(null);
        mediaPreview.setVisibility(View.GONE);
        imageBytes = null;
        selectedImageUri = null;
    }

    private void uploadPost() {
        String caption = postCaption.getText().toString().trim();
        if (caption.isEmpty()) {
            Toast.makeText(this, "Please enter a caption", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageBytes == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_LONG).show();
            return;
        }

        btnUpload.setEnabled(false);
        btnUpload.setText("Uploading...");

        String uploadUrl = "http://" + getString(R.string.server_ip) + "/codekendra/api/create_post.php";
        Log.d(TAG, "Starting upload to: " + uploadUrl);
        Log.d(TAG, "User ID: " + userId + ", Caption length: " + caption.length());
        Log.d(TAG, "Image size: " + (imageBytes.length / 1024) + " KB");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                uploadUrl,
                response -> {
                    Log.d(TAG, "Upload response: " + response);
                    btnUpload.setEnabled(true);
                    btnUpload.setText("Upload");
                    Toast.makeText(this, "✅ Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    finish();
                },
                error -> {
                    btnUpload.setEnabled(true);
                    btnUpload.setText("Upload");
                    String errorMessage = "Upload failed: ";
                    if (error instanceof TimeoutError) {
                        errorMessage += "Request timed out. Please check your connection and try again.";
                        Log.e(TAG, "Upload timeout error");
                    } else if (error instanceof NetworkError) {
                        errorMessage += "Network error. Please check your internet connection.";
                        Log.e(TAG, "Upload network error");
                    } else if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        errorMessage += "Server error (Code: " + statusCode + ")";
                        Log.e(TAG, "Upload server error: " + statusCode);
                        if (error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error response: " + responseBody);
                        }
                    } else {
                        errorMessage += "Unknown error occurred.";
                        Log.e(TAG, "Upload unknown error: " + error.toString());
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("caption", caption);
                Log.d(TAG, "POST params: user_id=" + userId + ", caption=" + caption);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("post_img", new DataPart("post.jpg", imageBytes, "image/jpeg"));
                Log.d(TAG, "Image data size: " + imageBytes.length + " bytes");
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                1.0f
        ));

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void handleSelectedImage(Uri uri) {
        try {
            Log.d(TAG, "Processing selected image: " + uri.toString());
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            Log.d(TAG, "Original image size: " + options.outWidth + "x" + options.outHeight);

            int sampleSize = 1;
            int maxDim = Math.max(options.outWidth, options.outHeight);
            if (maxDim > 1200) {
                sampleSize = maxDim / 1200;
            }

            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap == null) {
                throw new Exception("Failed to decode image");
            }

            Log.d(TAG, "Processed image size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            imageBytes = baos.toByteArray();

            Log.d(TAG, "Compressed image size: " + (imageBytes.length / 1024) + " KB");

            mediaPreview.setImageBitmap(bitmap);
            mediaPreview.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "❌ Image error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            imageBytes = null;
            mediaPreview.setVisibility(View.GONE);
        }
    }

    private void requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 101 || requestCode == 102)
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else {
            Toast.makeText(this, "❌ Permission denied. Cannot pick images.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only reload if not already loaded
        if (!profileLoaded) {
            displayCurrentUserProfile();
        }
    }
}