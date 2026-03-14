package com.example.wordscramble;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        MaterialButton btnEasy = findViewById(R.id.btn_easy);
        MaterialButton btnMedium = findViewById(R.id.btn_medium);
        MaterialButton btnHard = findViewById(R.id.btn_hard);

        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(4); // 4-letter words
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(6); // 6-letter words
            }
        });

        btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(8); // 8+ letters
            }
        });
    }

    private void startGame(int wordLength) {
        Intent intent = new Intent(DifficultyActivity.this, GameActivity.class);
        intent.putExtra("WORD_LENGTH", wordLength);
        startActivity(intent);
    }
}