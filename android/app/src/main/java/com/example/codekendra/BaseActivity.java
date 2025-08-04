package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Base Activity class that provides common functionality for all activities
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected SessionManager sessionManager;
    protected String serverIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize common components
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);
    }

    /**
     * Setup toolbar with back navigation
     */
    protected void setupToolbar(Toolbar toolbar, String title, boolean showBackButton) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
                getSupportActionBar().setDisplayShowHomeEnabled(showBackButton);
            }
        }
    }

    /**
     * Setup toolbar with default settings
     */
    protected void setupToolbar(Toolbar toolbar, String title) {
        setupToolbar(toolbar, title, true);
    }

    /**
     * Handle toolbar back button clicks
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Redirect to login activity and clear task stack
     */
    protected void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Check if user session is valid, redirect to login if not
     */
    protected boolean checkSession() {
        if (!sessionManager.isSessionValid()) {
            redirectToLogin();
            return false;
        }
        return true;
    }

    /**
     * Get the server base URL
     */
    protected String getServerUrl() {
        return "http://" + serverIp + "/codekendra/api/";
    }

    /**
     * Get image base URL
     */
    protected String getImageUrl(String imagePath, String folder) {
        if (imagePath == null || imagePath.isEmpty() || "null".equals(imagePath)) {
            return null;
        }
        return "http://" + serverIp + "/codekendra/web/assets/img/" + folder + "/" + imagePath;
    }

    /**
     * Get profile image URL
     */
    protected String getProfileImageUrl(String profilePic) {
        return getImageUrl(profilePic, "profile");
    }

    /**
     * Get post image URL
     */
    protected String getPostImageUrl(String postImage) {
        return getImageUrl(postImage, "posts");
    }
}