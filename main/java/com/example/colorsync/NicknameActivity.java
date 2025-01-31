package com.example.colorsync;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class NicknameActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    EditText nicknameInput;
    Button addButton;
    ListView nicknameList;
    ArrayAdapter<String> adapter;
    ArrayList<String> nicknames;
    ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        dbHelper = new DatabaseHelper(this);
        nicknameInput = findViewById(R.id.nicknameInput);
        addButton = findViewById(R.id.addButton);
        nicknameList = findViewById(R.id.nicknameList);
        nicknames = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nicknames);
        nicknameList.setAdapter(adapter);

        loadNicknames();

        addButton.setOnClickListener(view -> {
            String nickname = nicknameInput.getText().toString().trim();
            if (!nickname.isEmpty()) {
                if (dbHelper.addNickname(nickname)) {
                    Toast.makeText(this, "Nickname added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NicknameActivity.this, GameActivity.class);
                    intent.putExtra("nickname", nickname); // Pass the nickname to the game
                    startActivity(intent);
                    finish(); // Close NicknameActivity
                } else {
                    Toast.makeText(this, "Failed to add!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        nicknameList.setOnItemClickListener((parent, view, position, id) -> showEditDialog(ids.get(position), nicknames.get(position)));
    }

    private void loadNicknames() {
        nicknames.clear();
        ids.clear();
        Cursor cursor = dbHelper.getNicknames();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nickname = cursor.getString(1);
                ids.add(id);
                nicknames.add(nickname);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(int id, String currentNickname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Nickname");

        final EditText input = new EditText(this);
        input.setText(currentNickname);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newNickname = input.getText().toString().trim();
            if (!newNickname.isEmpty()) {
                if (dbHelper.updateNickname(id, newNickname)) {
                    Toast.makeText(this, "Nickname updated!", Toast.LENGTH_SHORT).show();
                    loadNicknames();
                } else {
                    Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setNeutralButton("Delete", (dialog, which) -> deleteNickname(id));

        // Show the dialog and change button colors
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set button colors to blue
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_dark));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue_dark));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.blue_dark));
    }

    private void deleteNickname(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Nickname");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (dbHelper.deleteNickname(id)) {
                Toast.makeText(this, "Nickname deleted!", Toast.LENGTH_SHORT).show();
                loadNicknames();
            } else {
                Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
