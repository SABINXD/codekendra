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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private EditText postCaption;
    private ImageView mediaPreview;
    private Button uploadBtn, cancelBtn;
    private ImageButton addImageBtn;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private byte[] imageBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postCaption = findViewById(R.id.post_caption);
        mediaPreview = new ImageView(this); // Fallback in case layout doesn't have preview
        try {
            mediaPreview = findViewById(R.id.media_preview); 
        } catch (Exception e) {
            Log.e("CreatePost", "mediaPreview not found in layout");
        }

        mediaPreview = findViewById(R.id.media_preview);
        uploadBtn = findViewById(R.id.btn_upload);
        cancelBtn = findViewById(R.id.btn_cancel);
        addImageBtn = findViewById(R.id.add_image);

        addImageBtn.setOnClickListener(v -> {
            requestImagePermission();
            pickImage();
        });

        cancelBtn.setOnClickListener(v -> finish());

        uploadBtn.setOnClickListener(v -> {
            String caption = postCaption.getText().toString().trim();

            if (caption.isEmpty() || imageBytes == null) {
                Toast.makeText(this, "Please enter caption and select image", Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager sessionManager = new SessionManager(CreatePostActivity.this);
            int userId = sessionManager.getUserId();

            Log.d("CreatePost", "userId to post: " + userId);

            if (userId == -1) {
                Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Uploading post...", Toast.LENGTH_SHORT).show();

            String uploadUrl = "http://" + getString(R.string.server_ip) + "/codekendra/api/create_post.php";

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                    Request.Method.POST,
                    uploadUrl,
                    response -> {
                        Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("UploadError", "Error: " + error.toString());
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("caption", caption);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("post_img", new DataPart("post.jpg", imageBytes, "image/jpeg"));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(multipartRequest);
        });
    }

    private void requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();

                int sampleSize = 1;
                int maxDim = Math.max(options.outWidth, options.outHeight);
                if (maxDim > 1200) sampleSize = maxDim / 1200;

                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;

                inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();

                if (bitmap == null) throw new Exception("Bitmap decode failed");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                imageBytes = baos.toByteArray();

                mediaPreview.setImageBitmap(bitmap);
                mediaPreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                mediaPreview.setVisibility(View.GONE);
                imageBytes = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 101 || requestCode == 102) && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied. Cannot pick images.", Toast.LENGTH_SHORT).show();
        }
    }
}
