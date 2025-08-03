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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.NetworkError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends BaseActivity {
    private static final String TAG = "PostActivityEnhanced";

    // UI Components
    private EditText etContent, etCode, etTag;
    private Spinner spinnerLanguage;
    private Button btnAddTag, btnPost;
    private ImageButton btnImage;
    private RecyclerView rvTags;
    private ImageView mediaPreview;
    private TextView tvCodePreview;

    // Data
    private byte[] imageBytes = null;
    private Uri selectedImageUri = null;
    private List<String> tags = new ArrayList<>();
    private TagAdapter tagAdapter;
    private String serverIp;
    private SessionManager sessionManager;

    // Image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        initializeViews();
        setupImagePicker();
        setupClickListeners();
        setupSpinner();
        setupTagsRecyclerView();
    }

    private void initializeViews() {
        etContent = findViewById(R.id.et_content);
        etCode = findViewById(R.id.et_code);
        etTag = findViewById(R.id.et_tag);
        spinnerLanguage = findViewById(R.id.spinner_language);
        btnAddTag = findViewById(R.id.btn_add_tag);
        btnPost = findViewById(R.id.btn_post);
        btnImage = findViewById(R.id.btn_image);
        mediaPreview = findViewById(R.id.media_preview);
        rvTags = findViewById(R.id.rv_tags);
        tvCodePreview = findViewById(R.id.tv_code_preview);

        mediaPreview.setVisibility(View.GONE);
    }

    private void setupSpinner() {
        String[] languages = {
                "Select Language", "JavaScript", "Python", "Java", "C++", "C#",
                "HTML", "CSS", "PHP", "SQL", "Kotlin", "Swift", "Go", "Rust", "TypeScript"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
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
            tags.remove(position);
            tagAdapter.notifyItemRemoved(position);
            updateTagsVisibility();
        });

        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTags.setAdapter(tagAdapter);
        updateTagsVisibility();
    }

    private void updateTagsVisibility() {
        rvTags.setVisibility(tags.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupClickListeners() {
        btnImage.setOnClickListener(v -> {
            requestImagePermission();
            launchImagePicker();
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

        btnPost.setOnClickListener(v -> uploadPost());

        // Add text watcher for code preview
        etCode.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCodePreview();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
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

    private void uploadPost() {
        String content = etContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter content", Toast.LENGTH_SHORT).show();
            return;
        }

        // At least one of image or code is required
        String codeContent = etCode.getText().toString().trim();
        if (imageBytes == null && codeContent.isEmpty()) {
            Toast.makeText(this, "Please add an image or code", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_LONG).show();
            return;
        }

        btnPost.setEnabled(false);
        btnPost.setText("Uploading...");

        String uploadUrl = "http://" + serverIp + "/codekendra/api/create_post.php";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                uploadUrl,
                response -> {
                    Log.d(TAG, "✅ Upload successful: " + response);
                    btnPost.setEnabled(true);
                    btnPost.setText("✈️ Post");
                    Toast.makeText(this, "✅ Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    finish();
                },
                error -> {
                    btnPost.setEnabled(true);
                    btnPost.setText("✈️ Post");
                    String errorMessage = "Upload failed: ";
                    if (error instanceof TimeoutError) {
                        errorMessage += "Request timed out";
                    } else if (error instanceof NetworkError) {
                        errorMessage += "Network error";
                    } else if (error.networkResponse != null) {
                        errorMessage += "Server error (Code: " + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Upload error: " + error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("caption", content);

                if (!codeContent.isEmpty()) {
                    params.put("code_content", codeContent);
                    String selectedLanguage = spinnerLanguage.getSelectedItem().toString();
                    if (!selectedLanguage.equals("Select Language")) {
                        params.put("code_language", selectedLanguage);
                    }
                }

                if (!tags.isEmpty()) {
                    StringBuilder tagsString = new StringBuilder();
                    for (int i = 0; i < tags.size(); i++) {
                        if (i > 0) tagsString.append(",");
                        tagsString.append(tags.get(i));
                    }
                    params.put("tags", tagsString.toString());
                }

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imageBytes != null) {
                    String fileName = "post_" + System.currentTimeMillis() + ".jpg";
                    params.put("post_img", new DataPart(fileName, imageBytes, "image/jpeg"));
                }
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1.0f));
        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private void clearForm() {
        etContent.setText("");
        etCode.setText("");
        etTag.setText("");
        tags.clear();
        tagAdapter.notifyDataSetChanged();
        updateTagsVisibility();
        mediaPreview.setImageDrawable(null);
        mediaPreview.setVisibility(View.GONE);
        tvCodePreview.setVisibility(View.GONE);
        spinnerLanguage.setSelection(0);
        imageBytes = null;
        selectedImageUri = null;
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void handleSelectedImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                throw new Exception("Failed to decode image");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            imageBytes = baos.toByteArray();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 101 || requestCode == 102) && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else {
            Toast.makeText(this, "❌ Permission denied. Cannot pick images.", Toast.LENGTH_SHORT).show();
        }
    }
}