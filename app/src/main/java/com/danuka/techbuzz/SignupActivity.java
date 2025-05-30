package com.danuka.techbuzz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText password = findViewById(R.id.password_input);
        EditText confirmPassword = findViewById(R.id.confirm_password_input);

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

        // 2. Make "Login" blue and clickable
        TextView loginPrompt = findViewById(R.id.login_prompt);
        String fullLogin = "Already have an account? Login";
        SpannableString styledLogin = new SpannableString(fullLogin);
        ClickableSpan loginClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#01579B"));
                ds.setUnderlineText(false);
            }
        };
        styledLogin.setSpan(
                loginClick,
                fullLogin.indexOf("Login"),
                fullLogin.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginPrompt.setText(styledLogin);
        loginPrompt.setMovementMethod(LinkMovementMethod.getInstance());

        // 3. Style Terms & Privacy text
        TextView termsText = findViewById(R.id.terms_and_policy);
        String fullTerms = "By clicking continue, you agree to our Terms of Service and Privacy Policy";
        SpannableString styledTerms = new SpannableString(fullTerms);

        int termsStart = fullTerms.indexOf("Terms of Service");
        int termsEnd = termsStart + "Terms of Service".length();
        styledTerms.setSpan(new ForegroundColorSpan(Color.BLACK), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int privacyStart = fullTerms.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();
        styledTerms.setSpan(new ForegroundColorSpan(Color.BLACK), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsText.setText(styledTerms);

        // Reusable toggle listener for password fields
        View.OnTouchListener togglePasswordListener = new View.OnTouchListener() {

            boolean isPasswordVisible = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText editText = (EditText) v;
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isPasswordVisible) {
                            // Hide password
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.visibility_icon, 0);
                        } else {
                            // Show password
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.visibility_off_icon, 0);
                        }
                        isPasswordVisible = !isPasswordVisible;
                        editText.setSelection(editText.getText().length());
                        editText.performClick();
                        return true;
                    }
                }
                return false;
            }
        };

        password.setOnTouchListener(togglePasswordListener);
        confirmPassword.setOnTouchListener(togglePasswordListener);
    }
}
