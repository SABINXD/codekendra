package com.example.codekendra;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SearchView;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search); 

        SearchView searchView = findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.GRAY);
        searchEditText.setTextColor(Color.BLACK);
    }
}
