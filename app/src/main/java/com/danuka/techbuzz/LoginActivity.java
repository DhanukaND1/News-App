package com.danuka.techbuzz;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Color "Tech" and "Buzz" in different colors
        TextView appName = findViewById(R.id.login_appname);
        String techBuzz = "TechBuzz";
        SpannableString styledTitle = new SpannableString(techBuzz);
        styledTitle.setSpan(
                new ForegroundColorSpan(Color.parseColor("#1A237E")),
                0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "Tech" = blue
        styledTitle.setSpan(
                new ForegroundColorSpan(Color.parseColor("#D84315")),
                4, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "Buzz" = orange
        appName.setText(styledTitle);

        // 2. Make "Sign up" blue and clickable
        TextView signUpPrompt = findViewById(R.id.signup_prompt);
        String fullSignUp = "Don’t have an account? Sign up";
        SpannableString styledSignup = new SpannableString(fullSignUp);
        ClickableSpan signUpClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#01579B"));
                ds.setUnderlineText(false);
            }
        };
        styledSignup.setSpan(
                signUpClick,
                fullSignUp.indexOf("Sign up"),
                fullSignUp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpPrompt.setText(styledSignup);
        signUpPrompt.setMovementMethod(LinkMovementMethod.getInstance());

        // 3. Style Terms & Privacy text
        TextView termsText = findViewById(R.id.terms_and_policy);
        String fullTerms = "By clicking continue, you agree to our Terms of Service and Privacy Policy";
        SpannableString styledTerms = new SpannableString(fullTerms);

        // Make "Terms of Service" black
        int termsStart = fullTerms.indexOf("Terms of Service");
        int termsEnd = termsStart + "Terms of Service".length();
        styledTerms.setSpan(new ForegroundColorSpan(Color.BLACK), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make "Privacy Policy" black
        int privacyStart = fullTerms.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();
        styledTerms.setSpan(new ForegroundColorSpan(Color.BLACK), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set text (rest will use the default gray set in XML)
        termsText.setText(styledTerms);
    }
}
