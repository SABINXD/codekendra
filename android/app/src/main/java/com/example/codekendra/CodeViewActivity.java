package com.example.codekendra;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeViewActivity extends AppCompatActivity {
    private static final String TAG = "CodeViewActivity";

    // VS Code Dark Theme Colors
    private static final int COLOR_KEYWORD = Color.parseColor("#C586C0");
    private static final int COLOR_STRING = Color.parseColor("#CE9178");
    private static final int COLOR_COMMENT = Color.parseColor("#6A9955");
    private static final int COLOR_METHOD = Color.parseColor("#DCDCAA");
    private static final int COLOR_TYPE = Color.parseColor("#4EC9B0");
    private static final int COLOR_NUMBER = Color.parseColor("#B5CEA8");
    private static final int COLOR_VARIABLE = Color.parseColor("#9CDCFE");
    private static final int COLOR_DEFAULT = Color.parseColor("#D4D4D4");
    private static final int COLOR_BRACKET = Color.parseColor("#FFD700");

    private TextView codeContent;
    private TextView lineNumbers;
    private TextView fileName;
    private ScrollView lineNumbersScroll;
    private ScrollView codeVerticalScroll;
    private HorizontalScrollView codeHorizontalScroll;
    private ImageButton btnCopy;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);

        initializeViews();
        setupCodeView();
        setupClickListeners();
    }

    private void initializeViews() {
        codeContent = findViewById(R.id.code_content_full);
        lineNumbers = findViewById(R.id.line_numbers_full);
        fileName = findViewById(R.id.file_name);
        lineNumbersScroll = findViewById(R.id.line_numbers_scroll_full);
        codeVerticalScroll = findViewById(R.id.code_vertical_scroll_full);
        codeHorizontalScroll = findViewById(R.id.code_horizontal_scroll_full);
        btnCopy = findViewById(R.id.btn_copy_full);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupCodeView() {
        String code = getIntent().getStringExtra("code_content");
        String language = getIntent().getStringExtra("code_language");
        String fileNameStr = getIntent().getStringExtra("file_name");

        if (code == null) code = "// No code content";
        if (fileNameStr == null) fileNameStr = "code.txt";

        fileName.setText(fileNameStr);

        // Apply syntax highlighting
        SpannableString highlightedCode = applyFullHighlighting(code, language);
        codeContent.setText(highlightedCode);

        // FORCE LINE BREAK HANDLING
        codeContent.setSingleLine(false);
        codeContent.setMaxLines(Integer.MAX_VALUE);
        codeContent.setHorizontallyScrolling(true);

        // Generate line numbers
        generateLineNumbers(code);

        // Sync scrolling
        syncScrollViews();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCopy.setOnClickListener(v -> {
            String code = getIntent().getStringExtra("code_content");
            if (code != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "✅ Code copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SpannableString applyFullHighlighting(String code, String language) {
        SpannableString spannableString = new SpannableString(code);

        try {
            if (language != null) {
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
            }

            // Apply common highlighting
            highlightStrings(spannableString);
            highlightNumbers(spannableString);
            highlightBrackets(spannableString);
            highlightComments(spannableString, language);

        } catch (Exception e) {
            Log.e(TAG, "Error in full syntax highlighting: " + e.getMessage());
        }

        return spannableString;
    }

    // Copy highlighting methods from PostAdapter
    private void highlightJavaScript(SpannableString spannable) {
        String[] keywords = {"const", "let", "var", "function", "class", "if", "else", "for", "while",
                "do", "switch", "case", "default", "break", "continue", "return", "try",
                "catch", "finally", "throw", "new", "this", "super", "extends", "import",
                "export", "from", "as", "async", "await", "yield", "typeof", "instanceof"};

        for (String keyword : keywords) {
            highlightKeyword(spannable, keyword, COLOR_KEYWORD);
        }

        highlightPattern(spannable, "\\b\\w+(?=\\()", COLOR_METHOD);
        highlightPattern(spannable, "(?<=\\.)\\w+", COLOR_VARIABLE);
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

    private void highlightPattern(SpannableString spannable, String pattern, int color) {
        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(spannable.toString());
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting pattern: " + pattern, e);
        }
    }

    private void highlightKeyword(SpannableString spannable, String keyword, int color) {
        try {
            String text = spannable.toString();
            int index = 0;

            while ((index = text.indexOf(keyword, index)) != -1) {
                boolean isWholeWord = true;

                if (index > 0) {
                    char before = text.charAt(index - 1);
                    if (Character.isLetterOrDigit(before) || before == '_') {
                        isWholeWord = false;
                    }
                }

                if (index + keyword.length() < text.length()) {
                    char after = text.charAt(index + keyword.length());
                    if (Character.isLetterOrDigit(after) || after == '_') {
                        isWholeWord = false;
                    }
                }

                if (isWholeWord) {
                    spannable.setSpan(new ForegroundColorSpan(color), index, index + keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                index += keyword.length();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting keyword: " + keyword, e);
        }
    }

    private void highlightStrings(SpannableString spannable) {
        try {
            highlightPattern(spannable, "\"(?:[^\"\\\\]|\\\\.)*\"", COLOR_STRING);
            highlightPattern(spannable, "'(?:[^'\\\\]|\\\\.)*'", COLOR_STRING);
            highlightPattern(spannable, "`(?:[^`\\\\]|\\\\.)*`", COLOR_STRING);
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting strings", e);
        }
    }

    private void highlightNumbers(SpannableString spannable) {
        highlightPattern(spannable, "\\b\\d+(\\.\\d+)?\\b", COLOR_NUMBER);
    }

    private void highlightBrackets(SpannableString spannable) {
        highlightPattern(spannable, "[\\[\\]\\{\\}\\]", COLOR_BRACKET);
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

    private void generateLineNumbers(String code) {
        String[] lines = code.split("\n");
        StringBuilder lineNumbers = new StringBuilder();

        int totalLines = lines.length;
        int padding = String.valueOf(totalLines).length();

        for (int i = 1; i <= totalLines; i++) {
            lineNumbers.append(String.format("%" + padding + "d", i));
            if (i < totalLines) {
                lineNumbers.append("\n");
            }
        }

        this.lineNumbers.setText(lineNumbers.toString());
        this.lineNumbers.setTextSize(14f);
        this.lineNumbers.setTypeface(android.graphics.Typeface.MONOSPACE);
        this.lineNumbers.setTextColor(Color.parseColor("#858585"));
    }

    private void syncScrollViews() {
        codeVerticalScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            lineNumbersScroll.scrollTo(0, scrollY);
        });
    }
}
