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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.username_input);
        loginPassword = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = loginUsername.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkUser(username, password);
            }
        });

        // ===== 1. Style App Name: "TechBuzz" =====
        TextView appName = findViewById(R.id.login_appname);
        String techBuzz = "TechBuzz";
        SpannableString styledTitle = new SpannableString(techBuzz);
        styledTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#1A237E")), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "Tech"
        styledTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#D84315")), 4, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "Buzz"
        appName.setText(styledTitle);

        // ===== 2. Style "Sign up" Link =====
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
                ds.setColor(Color.parseColor("#01579B")); // Blue
                ds.setUnderlineText(false);
            }
        };
        styledSignup.setSpan(signUpClick, fullSignUp.indexOf("Sign up"), fullSignUp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpPrompt.setText(styledSignup);
        signUpPrompt.setMovementMethod(LinkMovementMethod.getInstance());

        // ===== 3. Style Terms and Privacy =====
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
    }

    public void checkUser(String username, String password) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDb = snapshot.child("password").getValue(String.class);

                    if (passwordFromDb != null && passwordFromDb.equals(password)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        loginPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
