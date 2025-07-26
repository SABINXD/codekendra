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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

public class PostActivity extends AppCompatActivity {

    private EditText postCaption;
    private ImageView mediaPreview;
    private ImageButton addImage, addVideo;
    private Button btnCancel, btnUpload;

    private byte[] imageBytes = null;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postCaption   = findViewById(R.id.post_caption);
        mediaPreview = findViewById(R.id.media_preview);
        addImage      = findViewById(R.id.add_image);
        addVideo      = findViewById(R.id.add_video);
        btnCancel     = findViewById(R.id.btn_cancel);
        btnUpload     = findViewById(R.id.btn_upload);

        mediaPreview.setVisibility(View.GONE); // default hidden

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        handleSelectedImage(selectedImageUri);
                    }
                }
        );

        addImage.setOnClickListener(v -> {
            requestImagePermission();
            launchImagePicker();
        });

        addVideo.setOnClickListener(v -> {
            Toast.makeText(this, "Video picker not yet implemented", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            postCaption.setText("");
            mediaPreview.setImageDrawable(null);
            mediaPreview.setVisibility(View.GONE);
            imageBytes = null;
            selectedImageUri = null;
            Toast.makeText(this, "Post cancelled", Toast.LENGTH_SHORT).show();
        });

        btnUpload.setOnClickListener(v -> {
            String caption = postCaption.getText().toString().trim();

            if (caption.isEmpty() || imageBytes == null) {
                Toast.makeText(this, "Please enter caption and select image", Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager sessionManager = new SessionManager(this);
            int userId = sessionManager.getUserId();

            if (userId == -1) {
                Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_LONG).show();
                return;
            }

            String uploadUrl = "http://" + getString(R.string.server_ip) + "/codekendra/api/create_post.php";

            Toast.makeText(this, "Uploading post...", Toast.LENGTH_SHORT).show();

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

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void handleSelectedImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            int sampleSize = 1;
            int maxDim = Math.max(options.outWidth, options.outHeight);
            if (maxDim > 1200) sampleSize = maxDim / 1200;

            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap == null) throw new Exception("Bitmap decode failed");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            imageBytes = baos.toByteArray();

            mediaPreview.setImageBitmap(bitmap);
            mediaPreview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Permission denied. Cannot pick images.", Toast.LENGTH_SHORT).show();
        }
    }
}
