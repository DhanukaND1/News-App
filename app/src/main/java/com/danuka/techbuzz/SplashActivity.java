package com.danuka.techbuzz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set custom colored title text
        TextView titleText = findViewById(R.id.splashTitle);
        SpannableString styledTitle = new SpannableString("TechBuzz");

        // Set "Tech" to blue
        styledTitle.setSpan(
                new ForegroundColorSpan(Color.parseColor("#1A237E")),
                0, 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set "Buzz" to orange
        styledTitle.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FF6F00")),
                4, 8,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setText(styledTitle);

        // Redirect to MainActivity after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish splash so it's removed from back stack
        }, SPLASH_DISPLAY_LENGTH);
    }
}
