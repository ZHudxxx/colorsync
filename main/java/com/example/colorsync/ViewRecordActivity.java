package com.example.colorsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewRecordActivity extends AppCompatActivity {

    private EditText etSearchNickname;
    private ArrayAdapter<String> recordsAdapter;
    private List<String> recordsList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        etSearchNickname = findViewById(R.id.etSearchNickname);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnBack = findViewById(R.id.btnBack);
        ListView lvRecords = findViewById(R.id.lvRecords);

        databaseHelper = new DatabaseHelper(this);

        recordsList = new ArrayList<>();

        recordsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordsList);
        lvRecords.setAdapter(recordsAdapter);

        btnSearch.setOnClickListener(v -> {
            String nickname = etSearchNickname.getText().toString().trim();
            if (!nickname.isEmpty()) {
                fetchRecords(nickname);
            } else {
                Toast.makeText(ViewRecordActivity.this, "Please enter a nickname to search", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ViewRecordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchRecords(String nickname) {
        recordsList.clear();

        Cursor cursor = databaseHelper.getHighScore(nickname);

        if (cursor != null && cursor.moveToFirst()) {
            int highScoreIndex = cursor.getColumnIndex("high_score");
            if (highScoreIndex != -1) {
                int highScore = cursor.getInt(highScoreIndex);
                recordsList.add("Record for " + nickname + ": High Score " + highScore);
            } else {
                recordsList.add("No high score found for " + nickname);
            }
        } else {
            recordsList.add("No record found for " + nickname);
        }

        if (cursor != null) cursor.close();
        recordsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
