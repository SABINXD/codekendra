package com.example.codekendra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etContent, etCode, etTag;
    private Spinner spinnerLanguage;
    private Button btnAddTag, btnPost;
    private ImageButton btnImage;
    private RecyclerView rvTags;
    private ImageView mediaPreview;
    private TextView tvCodePreview;
    private Uri selectedImageUri = null;
    private byte[] imageBytes = null;
    private List<String> tags = new ArrayList<>();
    private TagAdapter tagAdapter;
    private String[] programmingLanguages = {
            "Select Language", "JavaScript", "Python", "Java", "C++", "C#", "PHP",
            "Ruby", "Go", "Rust", "Swift", "Kotlin", "TypeScript", "HTML", "CSS",
            "SQL", "Shell", "PowerShell", "Dart", "Scala", "R", "MATLAB", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initializeViews();
        setupLanguageSpinner();
        setupTagsRecyclerView();
        setupClickListeners();
        setupTextWatchers();
    }

    private void initializeViews() {
        etContent = findViewById(R.id.et_content);
        etCode = findViewById(R.id.et_code);
        etTag = findViewById(R.id.et_tag);
        spinnerLanguage = findViewById(R.id.spinner_language);
        btnAddTag = findViewById(R.id.btn_add_tag);
        btnPost = findViewById(R.id.btn_post);
        btnImage = findViewById(R.id.btn_image);
        rvTags = findViewById(R.id.rv_tags);
        mediaPreview = findViewById(R.id.media_preview);
        tvCodePreview = findViewById(R.id.tv_code_preview);
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, programmingLanguages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCodePreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTagsRecyclerView() {
        tagAdapter = new TagAdapter(this, tags, true);
        tagAdapter.setOnTagRemovedListener(position -> {
            if (position >= 0 && position < tags.size()) {
                tags.remove(position);
                tagAdapter.notifyItemRemoved(position);
                tagAdapter.notifyItemRangeChanged(position, tags.size());
                updateTagsVisibility();
                Toast.makeText(this, "Tag removed", Toast.LENGTH_SHORT).show();
            }
        });
        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTags.setAdapter(tagAdapter);
        updateTagsVisibility();
    }

    private void setupClickListeners() {
        btnImage.setOnClickListener(v -> {
            requestImagePermission();
            pickImage();
        });
        btnAddTag.setOnClickListener(v -> {
            String tag = etTag.getText().toString().trim();
            if (!tag.isEmpty() && !tags.contains(tag)) {
                tags.add(tag);
                tagAdapter.notifyItemInserted(tags.size() - 1);
                etTag.setText("");
                updateTagsVisibility();
            } else if (tags.contains(tag)) {
                Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
            }
        });
        btnPost.setOnClickListener(v -> createPost());
    }

    private void setupTextWatchers() {
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCodePreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCodePreview() {
        String code = etCode.getText().toString().trim();
        String language = spinnerLanguage.getSelectedItem().toString();
        if (!code.isEmpty() && !language.equals("Select Language")) {
            tvCodePreview.setVisibility(View.VISIBLE);
            tvCodePreview.setText("Preview: " + language + " code (" + code.length() + " chars)");
        } else {
            tvCodePreview.setVisibility(View.GONE);
        }
    }

    private void updateTagsVisibility() {
        rvTags.setVisibility(tags.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void createPost() {
        String content = etContent.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String selectedLanguage = spinnerLanguage.getSelectedItem().toString();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter some content", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_LONG).show();
            return;
        }

        String uploadUrl = "http://" + getString(R.string.server_ip) + "/codekendra/api/create_post.php";
        Toast.makeText(this, "Creating post...", Toast.LENGTH_SHORT).show();
        btnPost.setEnabled(false);
        btnPost.setText("Posting...");

        // Prepare parameters
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("caption", content);

        // Only include code-related parameters if there's actual code content
        if (!code.isEmpty() && !selectedLanguage.equals("Select Language")) {
            params.put("code_content", code);
            params.put("code_language", selectedLanguage);
        }

        // Include tags if they exist
        if (!tags.isEmpty()) {
            params.put("tags", String.join(",", tags));
        }

        // Prepare file data
        Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        if (imageBytes != null) {
            byteData.put("post_img", new VolleyMultipartRequest.DataPart("post.jpg", imageBytes, "image/jpeg"));
        }

        // Create the request with the correct constructor
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                uploadUrl,
                response -> {
                    btnPost.setEnabled(true);
                    btnPost.setText("✈️ Post");
                    Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject eventData = new JSONObject();
                        eventData.put("type", "new");
                        eventData.put("caption", content);
                        eventData.put("user_id", userId);
                        RealTimeManager.getInstance().sendEvent("new-post", eventData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                },
                error -> {
                    btnPost.setEnabled(true);
                    btnPost.setText("✈️ Post");
                    Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Create post error: " + error.toString());
                    if (error.networkResponse != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Error response: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error response", e);
                        }
                    }
                },
                new HashMap<>(), // headers
                params,         // parameters
                byteData        // byte data
        );

        Volley.newRequestQueue(this).add(multipartRequest);
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