package com.example.colorsync;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnMainLogin, btnNickname, btnViewRecords;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMainLogin = findViewById(R.id.btnMainLogin);
        btnNickname = findViewById(R.id.btnNickname);
        btnViewRecords = findViewById(R.id.btnViewRecords);

        btnMainLogin.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, MainPage.class);
                startActivity(intent);

        });

        btnNickname.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NicknameActivity.class);
                startActivity(i);
            }
        });

        btnViewRecords.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewRecordActivity.class);
                startActivity(i);
            }
        });
    }
}