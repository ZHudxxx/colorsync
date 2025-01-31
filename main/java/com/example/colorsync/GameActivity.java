package com.example.colorsync;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Collections;

import android.os.Handler;
import android.content.Intent;
import android.os.SystemClock;

public class GameActivity extends AppCompatActivity {

    private TextView nickname, score;
    private long startTime;
    private boolean gameStarted = false;
    private int playerScore = 0;

    // timer
    private TextView timerValue;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;

    ImageView[] cardImages;

    // images array
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 107, 108, 201, 202, 203, 204, 205, 206, 207, 208};

    // actual images
    int image101, image102, image103, image104, image105, image106, image107, image108, image201, image202, image203, image204, image205, image206, image207, image208;


    int firstCard, secondCard;
    int clickedFirst, clickedSecond;
    int cardNumber = 1;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        databaseHelper = new DatabaseHelper(this);

        nickname = findViewById(R.id.nickname);
        score = findViewById(R.id.score);
        timerValue = findViewById(R.id.timerValue);

        cardImages = new ImageView[]{
                findViewById(R.id.iv_11), findViewById(R.id.iv_12), findViewById(R.id.iv_13), findViewById(R.id.iv_14),
                findViewById(R.id.iv_21), findViewById(R.id.iv_22), findViewById(R.id.iv_23), findViewById(R.id.iv_24),
                findViewById(R.id.iv_31), findViewById(R.id.iv_32), findViewById(R.id.iv_33), findViewById(R.id.iv_34),
                findViewById(R.id.iv_41), findViewById(R.id.iv_42), findViewById(R.id.iv_43), findViewById(R.id.iv_44)
        };

        for (int i = 0; i < cardImages.length; i++) {
            cardImages[i].setTag(i);
        }

        frontCardResources();

        Collections.shuffle(Arrays.asList(cardsArray));

        setCardClickListeners();
        setButtonListeners();
    }

    private void setButtonListeners() {
        // Play Button
        findViewById(R.id.btnPlay).setOnClickListener(view -> startGame());

        // Reset Button
        findViewById(R.id.btnReset).setOnClickListener(view -> resetGame());
    }
        private void exitGame() {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    private void startGame() {
        if (!gameStarted) {
            startTime = SystemClock.uptimeMillis();
            gameStarted = true;
            playerScore = 0;
            score.setText("Score: " + playerScore);
            timerValue.setText("00:00:000");
            customHandler.postDelayed(updateTimerThread, 0);
        }
    }
    private void resetGame() {
        // Reset the cards
        for (ImageView cardImage : cardImages) {
            cardImage.setImageResource(R.drawable.card_back);
            cardImage.setVisibility(View.VISIBLE);
            cardImage.setEnabled(true);
        }
        Collections.shuffle(Arrays.asList(cardsArray));

        cardNumber = 1;
        firstCard = secondCard = 0;
        clickedFirst = clickedSecond = -1;
        playerScore = 0;
        score.setText("Score: " + playerScore);
        timeInMilliseconds = 0L;
        timerValue.setText("00:00:000");
        customHandler.removeCallbacks(updateTimerThread);
        nickname.setText("nickname");
        gameStarted = false;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (timeInMilliseconds % 1000);
            timerValue.setText(String.format("%02d:%02d:%03d", mins, secs, milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    private void setCardClickListeners() {
        for (ImageView cardImage : cardImages) {
            cardImage.setOnClickListener(view -> {
                if (!gameStarted) startGame();
                doStuff((ImageView) view, (int) view.getTag());
            });
        }
    }

    private void doStuff(ImageView iv, int cardIndex) {
        int card = cardsArray[cardIndex];
        if (card == 101) {
            iv.setImageResource(image101);
        } else if (card == 102) {
            iv.setImageResource(image102);
        } else if (card == 103) {
            iv.setImageResource(image103);
        } else if (card == 104) {
            iv.setImageResource(image104);
        } else if (card == 105) {
            iv.setImageResource(image105);
        } else if (card == 106) {
            iv.setImageResource(image106);
        } else if (card == 107) {
            iv.setImageResource(image107);
        } else if (card == 108) {
            iv.setImageResource(image108);
        } else if (card == 201) {
            iv.setImageResource(image201);
        } else if (card == 202) {
            iv.setImageResource(image202);
        } else if (card == 203) {
            iv.setImageResource(image203);
        } else if (card == 204) {
            iv.setImageResource(image204);
        } else if (card == 205) {
            iv.setImageResource(image205);
        } else if (card == 206) {
            iv.setImageResource(image206);
        } else if (card == 207) {
            iv.setImageResource(image207);
        } else if (card == 208) {
            iv.setImageResource(image208);
        }

        //check selected images
        if (cardNumber == 1) {
            firstCard = card;
            if (firstCard > 200) {
                firstCard = firstCard - 100;
            }
            cardNumber = 2;
            clickedFirst = cardIndex;

            iv.setEnabled(false);
        } else if (cardNumber == 2) {
            secondCard = card;
            if (secondCard > 200) {
                secondCard = secondCard - 100;
            }
            cardNumber = 1;
            clickedSecond = cardIndex;

            // Disable all cards
            disableAllCards();

            // Check for a match after a delay
            Handler handler = new Handler();
            handler.postDelayed(this::calculate, 1000);
        }
    }


    private void disableAllCards() {
        for (ImageView cardImage : cardImages) {
            cardImage.setEnabled(false);
        }
    }

    private void calculate() {
        if (firstCard == secondCard) {
            cardImages[clickedFirst].setVisibility(View.INVISIBLE);
            cardImages[clickedSecond].setVisibility(View.INVISIBLE);

            // Increment the score for a successful match
            playerScore += 100; // You can adjust the score increment as needed
            score.setText("Score: " + playerScore); // Update the score TextView
        } else {
            cardImages[clickedFirst].setImageResource(R.drawable.card_back);
            cardImages[clickedSecond].setImageResource(R.drawable.card_back);
        }

        for (ImageView cardImage : cardImages) {
            cardImage.setEnabled(true);
        }

        checkEnd();
    }

    private void checkEnd() {
        boolean allMatched = true;
        for (ImageView cardImage : cardImages) {
            if (cardImage.getVisibility() != View.INVISIBLE) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
            long endTime = SystemClock.uptimeMillis();
            long timeTaken = endTime - startTime;

            customHandler.removeCallbacks(updateTimerThread);

            // Store the score in the database
            storeScore(timeTaken, playerScore); // Pass the playerScore to storeScore

            // Show game over dialog
            showGameOverDialog(timeTaken);
        }
    }

    private void showGameOverDialog(long timeTaken) {
        int secs = (int) (timeTaken / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (timeTaken % 1000);

        String formattedTime = String.format("%02d:%02d:%03d", mins, secs, milliseconds);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        String message = String.format("GAME OVER!\n%s : %s\nTime: %s", nickname.getText(), playerScore, formattedTime);

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("NEW", (dialog, i) -> {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("EXIT", (dialog, i) -> finish());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void storeScore(long timeTaken, int scoreValue) {
        String playerNickname = nickname.getText().toString(); // Get the player's nickname

        boolean isInserted = databaseHelper.insertScore(playerNickname, scoreValue);
        if (isInserted) {
            Toast.makeText(this, "Score saved successfully!", Toast.LENGTH_SHORT).show();
            // Check if this is a high score
            checkAndUpdateHighScore(playerNickname, scoreValue); // Ensure scoreValue is int
        } else {
            Toast.makeText(this, "Failed to save score.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndUpdateHighScore(String playerNickname, int scoreValue) {
        // Get the current high score for the player
        Cursor cursor = databaseHelper.getHighScore(playerNickname);
        int currentHighScore = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(databaseHelper.getHighScoreColumn()); // Use the getter method
                if (columnIndex != -1) { // Check if the column index is valid
                    currentHighScore = cursor.getInt(columnIndex);
                } else {
                    // Handle the case where the column index is invalid
                    Toast.makeText(this, "High score column not found.", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close(); // Close the cursor after use
        }

        // If the new score is higher than the current high score, update it
        if (scoreValue > currentHighScore) {
            boolean isUpdated = databaseHelper.updateHighScore(playerNickname, scoreValue);
            if (isUpdated) {
                Toast.makeText(this, "New high score!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void frontCardResources(){
        image101 = R.drawable.card_blue;
        image102 = R.drawable.card_brown;
        image103 = R.drawable.card_green;
        image104 = R.drawable.card_orange;
        image105 = R.drawable.card_pink;
        image106 = R.drawable.card_purple;
        image107 = R.drawable.card_red;
        image108 = R.drawable.card_yellow;

        image201 = R.drawable.match_blue;
        image202 = R.drawable.match_brown;
        image203 = R.drawable.match_green;
        image204 = R.drawable.match_orange;
        image205 = R.drawable.match_pink;
        image206 = R.drawable.match_purple;
        image207 = R.drawable.match_red;
        image208 = R.drawable.match_yellow;
    }
}