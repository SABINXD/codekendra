package com.example.codekendra;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private static final String TAG = "PostAdapter";
    private Context context;
    private List<Post> posts;
    private String serverIp;
    private int currentUserId;
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeUpdater;

    // VS Code Dark Theme Colors
    private static final int COLOR_KEYWORD = Color.parseColor("#C586C0");
    private static final int COLOR_STRING = Color.parseColor("#CE9178");
    private static final int COLOR_COMMENT = Color.parseColor("#6A9955");
    private static final int COLOR_METHOD = Color.parseColor("#DCDCAA");
    private static final int COLOR_TYPE = Color.parseColor("#4EC9B0");
    private static final int COLOR_NUMBER = Color.parseColor("#B5CEA8");
    private static final int COLOR_VARIABLE = Color.parseColor("#9CDCFE");
    private static final int COLOR_DEFAULT = Color.parseColor("#D4D4D4");
    private static final int COLOR_PROPERTY = Color.parseColor("#9CDCFE");
    private static final int COLOR_BRACKET = Color.parseColor("#FFD700");

    public PostAdapter(Context context, List<Post> posts, String serverIp, int currentUserId) {
        this.context = context;
        this.posts = posts;
        this.serverIp = serverIp;
        this.currentUserId = currentUserId;

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                timeHandler.postDelayed(this, 60000);
            }
        };
        timeHandler.postDelayed(timeUpdater, 60000);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        Log.d(TAG, "Binding post: " + post.getId() + " by " + post.getUserName());

        // Set user info
        holder.userName.setText(post.getUserName());
        holder.postDescription.setText(post.getPostDescription());
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()));
        holder.postTime.setText(getTimeAgo(post.getCreatedAt()));

        // Load profile picture - FIXED: Handle both CircleImageView and regular ImageView
        if (holder.profilePic != null) {
            if (post.getProfilePic() != null && !post.getProfilePic().isEmpty() && !post.getProfilePic().equals("null")) {
                Picasso.get()
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.profilePic);
            } else {
                holder.profilePic.setImageResource(R.drawable.profile_placeholder);
            }
        } else if (holder.profilePicRegular != null) {
            if (post.getProfilePic() != null && !post.getProfilePic().isEmpty() && !post.getProfilePic().equals("null")) {
                Picasso.get()
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.profilePicRegular);
            } else {
                holder.profilePicRegular.setImageResource(R.drawable.profile_placeholder);
            }
        }

        // Load post image
        if (post.getPostImage() != null && !post.getPostImage().isEmpty() && !post.getPostImage().equals("null")) {
            holder.imageCard.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(post.getPostImage())
                    .placeholder(R.drawable.ic_post)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.postImage);
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }

        // Display tags
        setupTags(holder, post.getTags());

        // Handle code content
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            setupEnhancedCodeView(holder, post);
        } else {
            holder.codeCard.setVisibility(View.GONE);
        }

        // Set like button state
        updateLikeButton(holder, post.isLikedByCurrentUser());

        // Setup click listeners
        setupClickListeners(holder, post, position);
    }

    private void setupTags(PostViewHolder holder, List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            holder.rvTags.setVisibility(View.VISIBLE);
            TagAdapter tagAdapter = new TagAdapter(context, tags, false);
            holder.rvTags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvTags.setAdapter(tagAdapter);
        } else {
            holder.rvTags.setVisibility(View.GONE);
        }
    }

    private void setupEnhancedCodeView(PostViewHolder holder, Post post) {
        try {
            holder.codeCard.setVisibility(View.VISIBLE);

            // Set language info
            String language = post.getCodeLanguage();
            if (language != null && !language.isEmpty()) {
                holder.codeLanguage.setText(getFileName(language));
                holder.codeLanguageIcon.setText(getLanguageIcon(language));
            } else {
                holder.codeLanguage.setText("code.txt");
                holder.codeLanguageIcon.setText("📄");
            }

            // Clean and format code content
            String codeContent = post.getCodeContent();
            if (codeContent != null) {
                codeContent = cleanAndFormatCode(codeContent);
                // Apply syntax highlighting
                SpannableString highlightedCode = applyEnhancedHighlighting(codeContent, language);
                holder.codeContent.setText(highlightedCode);

                // Force line break handling
                holder.codeContent.setSingleLine(false);
                holder.codeContent.setMaxLines(Integer.MAX_VALUE);
                holder.codeContent.setHorizontallyScrolling(true);
            } else {
                holder.codeContent.setText("// No code content");
                codeContent = "// No code content";
            }

            // Apply VS Code Dark theme
            holder.codeCard.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            holder.codeContent.setTextColor(COLOR_DEFAULT);
            holder.codeContent.setBackgroundColor(Color.parseColor("#1E1E1E"));

            // Set proper text size and font
            holder.codeContent.setTextSize(12f);
            holder.codeContent.setTypeface(android.graphics.Typeface.MONOSPACE);

            // Generate line numbers
            generateEnhancedLineNumbers(holder, codeContent);

            // Set up copy button
            final String finalCodeContent = codeContent;
            holder.btnCopyCode.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Code", finalCodeContent);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "✅ Code copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            // Set up expand button
            holder.btnExpandCode.setOnClickListener(v -> {
                Intent intent = new Intent(context, CodeViewActivity.class);
                intent.putExtra("code_content", finalCodeContent);
                intent.putExtra("code_language", language);
                intent.putExtra("file_name", getFileName(language));
                context.startActivity(intent);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up enhanced code view: " + e.getMessage());
            holder.codeCard.setVisibility(View.GONE);
        }
    }

    private String cleanAndFormatCode(String codeContent) {
        if (codeContent == null) return "";

        // Remove prefixes
        codeContent = codeContent.replaceAll("^Check out this [^:]+:\\s*\\n\\n", "");

        // Normalize line endings
        codeContent = codeContent.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");

        // Remove excessive blank lines
        codeContent = codeContent.replaceAll("\\n{3,}", "\n\n");

        // Trim
        codeContent = codeContent.trim();

        // Add line breaks for long single lines
        if (!codeContent.contains("\n") && codeContent.length() > 50) {
            codeContent = codeContent.replaceAll(";", ";\n")
                    .replaceAll("\\{", "{\n")
                    .replaceAll("\\}", "\n}")
                    .replaceAll("\\n\\s*\\n", "\n");
        }

        return codeContent;
    }

    private SpannableString applyEnhancedHighlighting(String code, String language) {
        SpannableString spannableString = new SpannableString(code);

        try {
            if (language == null) return spannableString;

            // Apply language-specific highlighting
            switch (language.toLowerCase()) {
                case "javascript":
                case "js":
                    highlightJavaScript(spannableString);
                    break;
                case "java":
                    highlightJava(spannableString);
                    break;
                case "php":
                    highlightPhp(spannableString);
                    break;
                case "python":
                    highlightPython(spannableString);
                    break;
                case "sql":
                    highlightSql(spannableString);
                    break;
                default:
                    highlightGeneric(spannableString);
                    break;
            }

            // Apply common highlighting
            highlightStrings(spannableString);
            highlightNumbers(spannableString);
            highlightBrackets(spannableString);
            highlightComments(spannableString, language);

        } catch (Exception e) {
            Log.e(TAG, "Error in syntax highlighting: " + e.getMessage());
        }

        return spannableString;
    }

    private void highlightJavaScript(SpannableString spannable) {
        String[] keywords = {"const", "let", "var", "function", "class", "if", "else", "for", "while",
                "do", "switch", "case", "default", "break", "continue", "return", "try",
                "catch", "finally", "throw", "new", "this", "super", "extends", "import",
                "export", "from", "as", "async", "await", "yield", "typeof", "instanceof"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        String[] builtins = {"console", "log", "Array", "Object", "String", "Number", "Boolean",
                "Date", "Math", "JSON", "Promise", "setTimeout", "setInterval"};
        for (String builtin : builtins) {
            highlightKeyword(spannable, builtin, COLOR_TYPE);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
        highlightPattern(spannable, "(?<=\\.)\\w+", COLOR_PROPERTY);
    }

    private void highlightJava(SpannableString spannable) {
        String[] keywords = {"public", "private", "protected", "static", "final", "abstract", "class",
                "interface", "extends", "implements", "package", "import", "void", "int",
                "String", "boolean", "double", "float", "long", "char", "byte", "short",
                "if", "else", "for", "while", "do", "switch", "case", "default", "break",
                "continue", "return", "try", "catch", "finally", "throw", "throws", "new",
                "this", "super", "null", "true", "false"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
        highlightPattern(spannable, "@\\w+", COLOR_TYPE);
    }

    private void highlightPhp(SpannableString spannable) {
        String[] keywords = {"<?php", "?>", "function", "class", "public", "private", "protected",
                "static", "final", "abstract", "interface", "extends", "implements",
                "namespace", "use", "if", "else", "elseif", "endif", "for", "foreach",
                "endfor", "while", "endwhile", "do", "switch", "case", "default", "break",
                "continue", "return", "try", "catch", "finally", "throw", "new", "clone",
                "echo", "print", "var", "array", "true", "false", "null"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        highlightPattern(spannable, "\\$\\w+", COLOR_VARIABLE);
        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
    }

    private void highlightPython(SpannableString spannable) {
        String[] keywords = {"def", "class", "if", "elif", "else", "for", "while", "try", "except",
                "finally", "with", "as", "import", "from", "return", "yield", "break",
                "continue", "pass", "lambda", "and", "or", "not", "in", "is", "True",
                "False", "None", "self", "super", "global", "nonlocal"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        String[] builtins = {"print", "len", "range", "str", "int", "float", "list", "dict", "set",
                "tuple", "type", "isinstance", "hasattr", "getattr", "setattr"};
        for (String builtin : builtins) {
            highlightKeyword(spannable, builtin, COLOR_TYPE);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
    }

    private void highlightSql(SpannableString spannable) {
        String[] keywords = {"SELECT", "FROM", "WHERE", "JOIN", "INNER", "LEFT", "RIGHT", "OUTER",
                "ON", "GROUP", "BY", "ORDER", "HAVING", "INSERT", "INTO", "VALUES",
                "UPDATE", "SET", "DELETE", "CREATE", "TABLE", "ALTER", "DROP", "INDEX",
                "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "NOT", "NULL", "UNIQUE",
                "DEFAULT", "AUTO_INCREMENT", "LIMIT", "OFFSET", "UNION", "DISTINCT",
                "COUNT", "SUM", "AVG", "MIN", "MAX", "COALESCE"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
    }

    private void highlightGeneric(SpannableString spannable) {
        String[] keywords = {"function", "class", "if", "else", "for", "while", "return", "var",
                "let", "const", "true", "false", "null", "undefined"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
    }

    // FIXED: Safe pattern highlighting with proper error handling
    private void highlightPattern(SpannableString spannable, String pattern, int color) {
        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(spannable.toString());

            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(color),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting pattern: " + pattern, e);
        }
    }

    private void highlightNumbers(SpannableString spannable) {
        highlightPattern(spannable, "\\b\\d+(\\.\\d+)?\\b", COLOR_NUMBER);
    }

    // FIXED: Proper bracket highlighting with escaped characters
    private void highlightBrackets(SpannableString spannable) {
        try {
            // Highlight each bracket type separately to avoid regex issues
            String[] brackets = {"\\[", "\\]", "\\{", "\\}", "\\", "\\"};

            for (String bracket : brackets) {
                Pattern pattern = Pattern.compile(bracket);
                Matcher matcher = pattern.matcher(spannable.toString());

                while (matcher.find()) {
                    spannable.setSpan(new ForegroundColorSpan(COLOR_BRACKET),
                            matcher.start(), matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting brackets", e);
        }
    }

    private void highlightKeyword(SpannableString spannable, String keyword, int color) {
        try {
            String text = spannable.toString();
            int index = 0;

            while ((index = text.indexOf(keyword, index)) != -1) {
                boolean isWholeWord = true;

                // Check character before
                if (index > 0) {
                    char before = text.charAt(index - 1);
                    if (Character.isLetterOrDigit(before) || before == '_') {
                        isWholeWord = false;
                    }
                }

                // Check character after
                if (index + keyword.length() < text.length()) {
                    char after = text.charAt(index + keyword.length());
                    if (Character.isLetterOrDigit(after) || after == '_') {
                        isWholeWord = false;
                    }
                }

                if (isWholeWord) {
                    spannable.setSpan(new ForegroundColorSpan(color),
                            index, index + keyword.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                index += keyword.length();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting keyword: " + keyword, e);
        }
    }

    private void highlightStrings(SpannableString spannable) {
        try {
            String text = spannable.toString();

            // Double-quoted strings
            Pattern doubleQuotePattern = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");
            Matcher matcher = doubleQuotePattern.matcher(text);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(COLOR_STRING),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Single-quoted strings
            Pattern singleQuotePattern = Pattern.compile("'(?:[^'\\\\]|\\\\.)*'");
            matcher = singleQuotePattern.matcher(text);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(COLOR_STRING),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Template literals
            Pattern templatePattern = Pattern.compile("`(?:[^`\\\\]|\\\\.)*`");
            matcher = templatePattern.matcher(text);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(COLOR_STRING),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting strings", e);
        }
    }

    private void highlightComments(SpannableString spannable, String language) {
        try {
            if (language != null) {
                switch (language.toLowerCase()) {
                    case "java":
                    case "javascript":
                    case "js":
                        highlightPattern(spannable, "//.*$", COLOR_COMMENT);
                        highlightPattern(spannable, "/\\*[\\s\\S]*?\\*/", COLOR_COMMENT);
                        break;
                    case "php":
                        highlightPattern(spannable, "(//|#).*$", COLOR_COMMENT);
                        highlightPattern(spannable, "/\\*[\\s\\S]*?\\*/", COLOR_COMMENT);
                        break;
                    case "python":
                        highlightPattern(spannable, "#.*$", COLOR_COMMENT);
                        highlightPattern(spannable, "\"\"\"[\\s\\S]*?\"\"\"", COLOR_COMMENT);
                        highlightPattern(spannable, "'''[\\s\\S]*?'''", COLOR_COMMENT);
                        break;
                    case "sql":
                        highlightPattern(spannable, "--.*$", COLOR_COMMENT);
                        highlightPattern(spannable, "/\\*[\\s\\S]*?\\*/", COLOR_COMMENT);
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting comments", e);
        }
    }

    private String getFileName(String language) {
        switch (language.toLowerCase()) {
            case "java": return "MainActivity.java";
            case "javascript": case "js": return "script.js";
            case "python": return "main.py";
            case "html": return "index.html";
            case "css": return "styles.css";
            case "php": return "index.php";
            case "sql": return "query.sql";
            case "typescript": case "ts": return "app.ts";
            case "jsx": return "component.jsx";
            case "tsx": return "component.tsx";
            default: return "code.txt";
        }
    }

    private String getLanguageIcon(String language) {
        switch (language.toLowerCase()) {
            case "java": return "☕";
            case "php": return "🐘";
            case "javascript": case "js": return "🟨";
            case "python": return "🐍";
            case "html": return "🌐";
            case "css": return "🎨";
            case "sql": return "🗃️";
            case "typescript": case "ts": return "🔷";
            case "jsx": case "tsx": return "⚛️";
            default: return "📄";
        }
    }

    private void generateEnhancedLineNumbers(PostViewHolder holder, String codeContent) {
        String[] lines = codeContent.split("\n");
        StringBuilder lineNumbers = new StringBuilder();
        int totalLines = lines.length;
        int padding = String.valueOf(totalLines).length();

        for (int i = 1; i <= totalLines; i++) {
            lineNumbers.append(String.format("%" + padding + "d", i));
            if (i < totalLines) {
                lineNumbers.append("\n");
            }
        }

        holder.lineNumbers.setText(lineNumbers.toString());
        holder.lineNumbers.setTextSize(11f);
        holder.lineNumbers.setTypeface(android.graphics.Typeface.MONOSPACE);
        holder.lineNumbers.setTextColor(Color.parseColor("#858585"));

        // Update stats
        holder.codeLineCount.setText("• " + totalLines + " lines");
        int charCount = codeContent.length();
        holder.codeStats.setText("UTF-8 • LF • " + charCount + " chars");
    }

    private void updateLikeButton(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
            holder.likeIcon.setColorFilter(Color.parseColor("#FF6B6B"));
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_icon);
            holder.likeIcon.setColorFilter(Color.parseColor("#666666"));
        }
    }

    private void setupClickListeners(PostViewHolder holder, Post post, int position) {
        // Like button click
        holder.likeLayout.setOnClickListener(v -> toggleLike(post, holder, position));

        // Comment button click
        holder.commentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            intent.putExtra("created_at", post.getCreatedAt());
            intent.putExtra("code_content", post.getCodeContent());
            intent.putExtra("code_language", post.getCodeLanguage());
            context.startActivity(intent);
        });

        // Post options (3-dot menu)
        holder.postOptions.setOnClickListener(v -> showPostOptions(post, position, v));

        // Post image click
        if (holder.postImage != null) {
            holder.postImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post_id", post.getId());
                intent.putExtra("post_img", post.getPostImage());
                intent.putExtra("post_text", post.getPostDescription());
                intent.putExtra("user_name", post.getUserName());
                intent.putExtra("created_at", post.getCreatedAt());
                intent.putExtra("code_content", post.getCodeContent());
                intent.putExtra("code_language", post.getCodeLanguage());
                context.startActivity(intent);
            });
        }
    }

    private void showPostOptions(Post post, int position, View anchorView) {
        PopupMenu popup = new PopupMenu(context, anchorView);

        if (post.getUserId() == currentUserId) {
            popup.getMenu().add("Delete Post");
        }

        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            popup.getMenu().add("Copy Code");
            popup.getMenu().add("Share Code");
        }

        popup.getMenu().add("Share Post");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Delete Post":
                    showDeleteConfirmation(post.getId(), position);
                    break;
                case "Copy Code":
                    copyCodeToClipboard(post.getCodeContent());
                    break;
                case "Share Code":
                    shareCode(post.getCodeContent(), post.getCodeLanguage());
                    break;
                case "Share Post":
                    sharePost(post);
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showDeleteConfirmation(int postId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(postId, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(int postId, int position) {
        String url = "http://" + serverIp + "/codekendra/api/delete_post.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            posts.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, posts.size());
                            Toast.makeText(context, "✅ Post deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Failed to delete: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "⚠️ Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Delete error: " + error.toString());
                    Toast.makeText(context, "❌ Delete failed: network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(currentUserId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void toggleLike(Post post, PostViewHolder holder, int position) {
        String url = "http://" + serverIp + "/codekendra/api/toggle_like.php";
        Log.d(TAG, "Toggling like for post " + post.getId() + " by user " + currentUserId);

        boolean currentLikeState = post.isLikedByCurrentUser();
        boolean newLikeState = !currentLikeState;
        int currentLikeCount = post.getLikeCount();
        int newLikeCount = currentLikeCount + (newLikeState ? 1 : -1);

        post.setLikedByCurrentUser(newLikeState);
        post.setLikeCount(newLikeCount);
        updateLikeButton(holder, newLikeState);
        holder.likeCount.setText(String.valueOf(newLikeCount));

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Like response: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            int serverLikeCount = json.getInt("like_count");
                            boolean serverIsLiked = json.getBoolean("is_liked");

                            post.setLikeCount(serverLikeCount);
                            post.setLikedByCurrentUser(serverIsLiked);
                            updateLikeButton(holder, serverIsLiked);
                            holder.likeCount.setText(String.valueOf(serverLikeCount));

                            String action = json.optString("action", "");
                            Toast.makeText(context, "✅ Post " + action, Toast.LENGTH_SHORT).show();
                        } else {
                            post.setLikedByCurrentUser(currentLikeState);
                            post.setLikeCount(currentLikeCount);
                            updateLikeButton(holder, currentLikeState);
                            holder.likeCount.setText(String.valueOf(currentLikeCount));

                            String error = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing like response", e);
                        post.setLikedByCurrentUser(currentLikeState);
                        post.setLikeCount(currentLikeCount);
                        updateLikeButton(holder, currentLikeState);
                        holder.likeCount.setText(String.valueOf(currentLikeCount));
                        Toast.makeText(context, "❌ Failed to update like", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Like toggle error: " + error.toString());
                    post.setLikedByCurrentUser(currentLikeState);
                    post.setLikeCount(currentLikeCount);
                    updateLikeButton(holder, currentLikeState);
                    holder.likeCount.setText(String.valueOf(currentLikeCount));
                    Toast.makeText(context, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(post.getId()));
                params.put("user_id", String.valueOf(currentUserId));
                Log.d(TAG, "Like params: " + params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void copyCodeToClipboard(String code) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "✅ Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareCode(String code, String language) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this " + (language != null ? language : "code") + ":\n\n" + code);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Code from CodeKendra");
        context.startActivity(Intent.createChooser(shareIntent, "Share Code"));
    }

    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = post.getPostDescription();
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            shareText += "\n\nCode:\n" + post.getCodeContent();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Post from CodeKendra");
        context.startActivity(Intent.createChooser(shareIntent, "Share Post"));
    }

    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = format.parse(rawTimestamp);
            if (postDate == null) return "just now";

            long diff = System.currentTimeMillis() - postDate.getTime();
            if (diff < 0) return "just now";

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (seconds < 60) return seconds + "s ago";
            else if (minutes < 60) return minutes + "m ago";
            else if (hours < 24) return hours + "h ago";
            else if (days < 7) return days + "d ago";
            else if (days < 30) return (days / 7) + "w ago";
            else if (days < 365) return (days / 30) + "mo ago";
            else return (days / 365) + "y ago";
        } catch (Exception e) {
            return "just now";
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        timeHandler.removeCallbacks(timeUpdater);
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts.clear();
        this.posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    public void addPost(Post post) {
        this.posts.add(0, post);
        notifyItemInserted(0);
    }

    // FIXED: PostViewHolder with support for both CircleImageView and regular ImageView
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        ImageView profilePicRegular; // Fallback for regular ImageView
        TextView userName, postTime, postDescription, likeCount, commentCount;
        TextView codeLanguage, codeLanguageIcon, codeLineCount, codeContent, lineNumbers, codeStats;
        ImageView postImage, likeIcon, commentIcon, postOptions;
        LinearLayout likeLayout, commentLayout;
        CardView imageCard, codeCard;
        RecyclerView rvTags;
        ImageButton btnCopyCode, btnExpandCode;
        ScrollView codeVerticalScroll;
        HorizontalScrollView codeHorizontalScroll;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Try to find CircleImageView first, then fallback to regular ImageView
            try {
                profilePic = itemView.findViewById(R.id.profilePic);
            } catch (ClassCastException e) {
                Log.w("PostAdapter", "ProfilePic is not CircleImageView, using regular ImageView");
                profilePic = null;
                profilePicRegular = itemView.findViewById(R.id.profilePic);
            }

            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            postDescription = itemView.findViewById(R.id.postDescription);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postImage = itemView.findViewById(R.id.postImage);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            postOptions = itemView.findViewById(R.id.postOptions);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            imageCard = itemView.findViewById(R.id.imageCard);
            codeCard = itemView.findViewById(R.id.code_card);
            rvTags = itemView.findViewById(R.id.rv_tags);

            // Code view elements
            codeLanguage = itemView.findViewById(R.id.code_language);
            codeLanguageIcon = itemView.findViewById(R.id.code_language_icon);
            codeLineCount = itemView.findViewById(R.id.code_line_count);
            codeContent = itemView.findViewById(R.id.code_content);
            lineNumbers = itemView.findViewById(R.id.line_numbers);
            btnCopyCode = itemView.findViewById(R.id.btn_copy_code);
            btnExpandCode = itemView.findViewById(R.id.btn_expand_code);
            codeVerticalScroll = itemView.findViewById(R.id.code_vertical_scroll);
            codeHorizontalScroll = itemView.findViewById(R.id.code_horizontal_scroll);
            codeStats = itemView.findViewById(R.id.code_stats);
        }
    }
}
