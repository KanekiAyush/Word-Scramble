package com.example.wordscrambel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvScrambledWord, tvFeedback, tvScore;
    private EditText etGuess;
    private MaterialButton btnCheck, btnHint, btnNext;

    // Word list for the game
    private String[] words = {"android", "programming", "java", "kotlin", "studio",
            "scramble", "game", "developer", "application", "mobile"};
    private String currentWord;      // original word
    private String scrambledWord;    // shuffled version
    private int score = 0;            // correct guesses
    private int totalAttempts = 0;    // total checks performed

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        tvScrambledWord = findViewById(R.id.tv_scrambled_word);
        tvFeedback = findViewById(R.id.tv_feedback);
        tvScore = findViewById(R.id.tv_score);
        etGuess = findViewById(R.id.et_guess);
        btnCheck = findViewById(R.id.btn_check);
        btnHint = findViewById(R.id.btn_hint);
        btnNext = findViewById(R.id.btn_next);

        // Set button listeners
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewWord();
            }
        });

        // Load the first word
        loadNewWord();
    }

    /** Picks a random word, scrambles it, and updates the UI */
    private void loadNewWord() {
        int index = random.nextInt(words.length);
        currentWord = words[index];
        scrambledWord = scrambleWord(currentWord);
        tvScrambledWord.setText(scrambledWord);
        etGuess.setText("");
        tvFeedback.setText("");
        updateScoreDisplay();
    }

    /** Shuffles the letters of a given word (ensures result is different from original) */
    private String scrambleWord(String word) {
        char[] letters = word.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            int j = random.nextInt(letters.length);
            char temp = letters[i];
            letters[i] = letters[j];
            letters[j] = temp;
        }
        String scrambled = new String(letters);
        // If the scrambled word accidentally matches the original (and length > 1), try again
        if (scrambled.equals(word) && word.length() > 1) {
            return scrambleWord(word);  // recursive call (low probability of infinite loop)
        }
        return scrambled;
    }

    /** Compares user's guess with the original word and updates score/feedback */
    private void checkGuess() {
        String guess = etGuess.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(guess)) {
            Toast.makeText(this, "Please enter a guess", Toast.LENGTH_SHORT).show();
            return;
        }

        totalAttempts++;
        if (guess.equals(currentWord)) {
            score++;
            tvFeedback.setText("Correct! Well done.");
            tvFeedback.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvFeedback.setText("Incorrect. Try again.");
            tvFeedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        updateScoreDisplay();
    }

    /** Shows a hint (first letter) in a Toast */
    private void showHint() {
        if (currentWord != null && !currentWord.isEmpty()) {
            String hint = "First letter: " + currentWord.charAt(0);
            Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
        }
    }

    /** Updates the score TextView */
    private void updateScoreDisplay() {
        tvScore.setText(String.format("Score: %d / %d", score, totalAttempts));
    }

    // ---------- Save / Restore state on rotation ----------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentWord", currentWord);
        outState.putString("scrambledWord", scrambledWord);
        outState.putInt("score", score);
        outState.putInt("totalAttempts", totalAttempts);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentWord = savedInstanceState.getString("currentWord");
        scrambledWord = savedInstanceState.getString("scrambledWord");
        score = savedInstanceState.getInt("score");
        totalAttempts = savedInstanceState.getInt("totalAttempts");
        tvScrambledWord.setText(scrambledWord);
        updateScoreDisplay();
    }
}