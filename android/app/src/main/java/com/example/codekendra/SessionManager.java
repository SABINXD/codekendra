package com.example.codekendra;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "user_session";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createSession(int userId, String email, String username, String profilePicUrl) {
        Log.d(TAG, "Creating session for user ID: " + userId);
        String profilePicFilename = extractFilenameFromUrl(profilePicUrl);

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PROFILE_PIC, profilePicFilename);
        editor.apply();

        Log.d(TAG, "Session created - Profile pic filename: " + profilePicFilename);
    }

    public void createSession(int userId, String email, String username, String profilePicUrl,
                              String firstName, String lastName) {
        Log.d(TAG, "Creating full session for user ID: " + userId);
        String profilePicFilename = extractFilenameFromUrl(profilePicUrl);

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PROFILE_PIC, profilePicFilename);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.apply();

        Log.d(TAG, "Full session created - Profile pic filename: " + profilePicFilename);
    }

    private String extractFilenameFromUrl(String profilePicUrl) {
        if (profilePicUrl == null || profilePicUrl.trim().isEmpty()) {
            return "default_profile.jpg";
        }

        if (!profilePicUrl.startsWith("http")) {
            return profilePicUrl;
        }

        try {
            String[] parts = profilePicUrl.split("/");
            String filename = parts[parts.length - 1];
            Log.d(TAG, "Extracted filename '" + filename + "' from URL: " + profilePicUrl);
            return filename;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting filename from URL: " + profilePicUrl, e);
            return "default_profile.jpg";
        }
    }

    public boolean isLoggedIn() {
        boolean loggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);
        int userId = getUserId();
        Log.d(TAG, "Login check - LoggedIn: " + loggedIn + ", UserID: " + userId);
        return loggedIn && userId != -1;
    }

    public int getUserId() {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Retrieved user ID: " + userId);
        return userId;
    }

    public String getUserIdAsString() {
        int userId = getUserId();
        if (userId == -1) {
            Log.e(TAG, "Invalid user ID: " + userId);
            return null;
        }
        return String.valueOf(userId);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getProfilePic() {
        String profilePic = sharedPreferences.getString(KEY_PROFILE_PIC, "default_profile.jpg");
        Log.d(TAG, "Retrieved profile pic filename: " + profilePic);
        return profilePic;
    }

    public String getFirstName() {
        return sharedPreferences.getString(KEY_FIRST_NAME, null);
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_NAME, null);
    }

    public void logout() {
        Log.d(TAG, "Logging out user");
        editor.clear();
        editor.apply();
    }

    public void updateProfilePic(String profilePicUrl) {
        String filename = extractFilenameFromUrl(profilePicUrl);
        Log.d(TAG, "Updating profile pic filename: " + filename);
        editor.putString(KEY_PROFILE_PIC, filename);
        editor.apply();
    }

    public void updateUserInfo(String firstName, String lastName, String email) {
        Log.d(TAG, "Updating user info");
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void debugSession() {
        Log.d(TAG, "=== SESSION DEBUG ===");
        Log.d(TAG, "Logged in: " + isLoggedIn());
        Log.d(TAG, "User ID: " + getUserId());
        Log.d(TAG, "Email: " + getEmail());
        Log.d(TAG, "Username: " + getUsername());
        Log.d(TAG, "Profile pic filename: " + getProfilePic());
        Log.d(TAG, "First name: " + getFirstName());
        Log.d(TAG, "Last name: " + getLastName());
        Log.d(TAG, "==================");
    }

    public boolean isSessionValid() {
        boolean hasUserId = getUserId() != -1;
        boolean hasUsername = getUsername() != null && !getUsername().trim().isEmpty();
        boolean isLoggedIn = isLoggedIn();
        boolean valid = isLoggedIn && hasUserId && hasUsername;

        Log.d(TAG, "Session validation - LoggedIn: " + isLoggedIn +
                ", HasUserID: " + hasUserId +
                ", HasUsername: " + hasUsername +
                ", Valid: " + valid);

        if (!valid) {
            debugSession();
        }
        return valid;
    }
}
