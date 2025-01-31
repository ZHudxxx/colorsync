package com.example.colorsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ColorSync.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_USERS = "user";
    private static final String COL_USER_ID = "id"; // Primary Key
    private static final String COL_USER_NICKNAME = "nickname";

    private static final String TABLE_SCORES = "score";
    private static final String COL_SCORE_ID = "ID"; // Primary Key
    private static final String COL_SCORE_NICKNAME = "nickname";
    private static final String COL_SCORE_VALUE = "score";

    private static final String TABLE_HIGH_SCORES = "high_score";
    private static final String COL_HIGH_SCORE_NICKNAME = "nickname";
    static final String COL_HIGH_SCORE_VALUE = "high_score";
    private static final String HIGH_SCORE_COLUMN = "high_score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NICKNAME + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                COL_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SCORE_NICKNAME + " TEXT, " +
                COL_SCORE_VALUE + " INTEGER, " +
                "FOREIGN KEY(" + COL_SCORE_NICKNAME + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_NICKNAME + "))");

        db.execSQL("CREATE TABLE " + TABLE_HIGH_SCORES + " (" +
                COL_HIGH_SCORE_NICKNAME + " TEXT, " +
                COL_HIGH_SCORE_VALUE + " INTEGER, " +
                "FOREIGN KEY(" + COL_HIGH_SCORE_NICKNAME + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_NICKNAME + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGH_SCORES);
        onCreate(db);
    }

    // Insert score
    public boolean insertScore(String nickname, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SCORE_NICKNAME, nickname);
        contentValues.put(COL_SCORE_VALUE, score);
        long result = db.insert(TABLE_SCORES, null, contentValues);
        return result != -1;
    }

    // Update high score for a user
    public boolean updateHighScore(String nickname, int newHighScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_HIGH_SCORE_VALUE, newHighScore);

        // Check if the user already has a high score
        int rowsAffected = db.update(TABLE_HIGH_SCORES, contentValues, COL_HIGH_SCORE_NICKNAME + " = ?", new String[]{nickname});

        // If no high score is found, insert a new one
        if (rowsAffected == 0) {
            contentValues.put(COL_HIGH_SCORE_NICKNAME, nickname);
            db.insert(TABLE_HIGH_SCORES, null, contentValues);
            return true;
        }

        return rowsAffected > 0;
    }

    // Get high score for a specific nickname
    public Cursor getHighScore(String nickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_HIGH_SCORE_VALUE + " FROM " + TABLE_HIGH_SCORES + " WHERE " + COL_HIGH_SCORE_NICKNAME + " = ?";
        Log.d("DatabaseHelper", "Query executed: " + query + " with nickname: " + nickname);

        return db.rawQuery(query, new String[]{nickname});
    }

    // Add nickname to the users table
    public boolean addNickname(String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NICKNAME, nickname);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Get all nicknames
    public Cursor getNicknames() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    // Update a user's nickname
    public boolean updateNickname(int id, String newNickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NICKNAME, newNickname);
        int result = db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Delete a user's nickname
    public boolean deleteNickname(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
    
        public String getHighScoreColumn() {
            return HIGH_SCORE_COLUMN;
        }
}
