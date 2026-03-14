package com.example.wordscramble;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvScrambledWord, tvHint, tvFeedback, tvScore, tvDifficulty;
    private EditText etGuess;
    private MaterialButton btnCheck, btnHint, btnNext;
    private ImageButton btnBack;

    // Word bank grouped by length
    private final List<String> easyWords = Arrays.asList("book", "cake", "door", "fish", "game", "hand", "king", "lion", "moon", "nest");
    private final List<String> mediumWords = Arrays.asList("basket", "button", "coffee", "dinner", "garden", "hollow", "jacket", "kitten", "letter", "monkey");
    private final List<String> hardWords = Arrays.asList("elephant", "football", "hospital", "internet", "language", "mountain", "painting", "sunshine", "triangle", "universe");

    private List<String> currentWordList;  // selected based on difficulty
    private String currentWord;
    private String scrambledWord;
    private int score = 0;
    private int totalAttempts = 0;
    private int hintIndex = 0;              // how many letters have been revealed
    private int difficultyLength;            // 4, 6, or 8

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        setupListeners();

        // Get difficulty from intent
        difficultyLength = getIntent().getIntExtra("WORD_LENGTH", 4);
        setDifficultyLabel();

        // Load first word
        loadNewWord();
    }

    private void initViews() {
        tvScrambledWord = findViewById(R.id.tv_scrambled_word);
        tvHint = findViewById(R.id.tv_hint);
        tvFeedback = findViewById(R.id.tv_feedback);
        tvScore = findViewById(R.id.tv_score);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        etGuess = findViewById(R.id.et_guess);
        btnCheck = findViewById(R.id.btn_check);
        btnHint = findViewById(R.id.btn_hint);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextHint();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewWord();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // go back to difficulty selection
            }
        });
    }

    private void setDifficultyLabel() {
        String label;
        switch (difficultyLength) {
            case 4:
                label = "Easy";
                currentWordList = new ArrayList<>(easyWords);
                break;
            case 6:
                label = "Medium";
                currentWordList = new ArrayList<>(mediumWords);
                break;
            case 8:
                label = "Hard";
                currentWordList = new ArrayList<>(hardWords);
                break;
            default:
                label = "Custom";
                currentWordList = new ArrayList<>(easyWords);
        }
        tvDifficulty.setText(label);
    }

    /** Loads a random word from the current list, scrambles it, and resets hint state */
    private void loadNewWord() {
        if (currentWordList.isEmpty()) {
            // In case the list is empty (should not happen)
            Toast.makeText(this, "No words available for this difficulty", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = random.nextInt(currentWordList.size());
        currentWord = currentWordList.get(index);
        scrambledWord = scrambleWord(currentWord);
        tvScrambledWord.setText(scrambledWord);
        etGuess.setText("");
        tvFeedback.setText("");
        hintIndex = 0;
        updateHintDisplay();
        updateScoreDisplay();
    }

    /** Shuffles the letters of a word, ensuring it's different from the original */
    private String scrambleWord(String word) {
        char[] letters = word.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            int j = random.nextInt(letters.length);
            char temp = letters[i];
            letters[i] = letters[j];
            letters[j] = temp;
        }
        String scrambled = new String(letters);
        // If the scrambled word accidentally equals the original (very rare), try again
        if (scrambled.equals(word) && word.length() > 1) {
            return scrambleWord(word);
        }
        return scrambled;
    }

    /** Checks the user's guess. If correct, auto‑advances and increments score. */
    private void checkGuess() {
        String guess = etGuess.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(guess)) {
            Toast.makeText(this, "Enter a guess", Toast.LENGTH_SHORT).show();
            return;
        }

        totalAttempts++;
        if (guess.equals(currentWord)) {
            score++;
            tvFeedback.setText("✓ Correct!");
            tvFeedback.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            // Automatically load next word after a short delay (so user sees feedback)
            etGuess.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadNewWord();
                }
            }, 800);
        } else {
            tvFeedback.setText("✗ Incorrect. Try again.");
            tvFeedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        updateScoreDisplay();
    }

    /** Progressive hint: reveals next letter and updates hint TextView */
    private void showNextHint() {
        if (currentWord == null) return;

        if (hintIndex < currentWord.length()) {
            hintIndex++;
            updateHintDisplay();
        } else {
            Toast.makeText(this, "No more hints!", Toast.LENGTH_SHORT).show();
        }
    }

    /** Builds the hint string (e.g., "a _ _ _") based on current hintIndex */
    private void updateHintDisplay() {
        StringBuilder hintBuilder = new StringBuilder("Hint: ");
        for (int i = 0; i < currentWord.length(); i++) {
            if (i < hintIndex) {
                hintBuilder.append(currentWord.charAt(i));
            } else {
                hintBuilder.append('_');
            }
            if (i < currentWord.length() - 1) {
                hintBuilder.append(' ');
            }
        }
        tvHint.setText(hintBuilder.toString());
    }

    private void updateScoreDisplay() {
        tvScore.setText(String.format("Score: %d / %d", score, totalAttempts));
    }

    // ----- Save / Restore state -----
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentWord", currentWord);
        outState.putString("scrambledWord", scrambledWord);
        outState.putInt("score", score);
        outState.putInt("totalAttempts", totalAttempts);
        outState.putInt("hintIndex", hintIndex);
        outState.putInt("difficultyLength", difficultyLength);
        // We don't save the entire word list, but we can save the current word index
        // For simplicity, we only restore basic data; the list is recreated from difficultyLength.
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentWord = savedInstanceState.getString("currentWord");
        scrambledWord = savedInstanceState.getString("scrambledWord");
        score = savedInstanceState.getInt("score");
        totalAttempts = savedInstanceState.getInt("totalAttempts");
        hintIndex = savedInstanceState.getInt("hintIndex");
        difficultyLength = savedInstanceState.getInt("difficultyLength");

        // Recreate word list based on difficulty
        setDifficultyLabel();
        tvScrambledWord.setText(scrambledWord);
        updateHintDisplay();
        updateScoreDisplay();
    }
}