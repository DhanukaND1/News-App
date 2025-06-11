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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupUsername,signupEmail,signupPassword,confirmPassword;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

         signupUsername = findViewById(R.id.username_input);
         signupEmail = findViewById(R.id.email_input);
         signupPassword = findViewById(R.id.password_input);
         confirmPassword = findViewById(R.id.confirm_password_input);
         signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String username = signupUsername.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirmPass = confirmPassword.getText().toString().trim();

                // Basic field validation
                if (username.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference.child(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Toast.makeText(SignupActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            // Continue with other validations
                            if (email.isEmpty()) {
                                Toast.makeText(SignupActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (password.isEmpty()) {
                                Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (password.length() < 6) {
                                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            if (confirmPass.isEmpty()) {
                                Toast.makeText(SignupActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!password.equals(confirmPass)) {
                                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Create user and store in DB
                            HelperClass helperClass = new HelperClass(username, email, password);
                            reference.child(username).setValue(helperClass);

                            Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Error checking username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


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
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
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
                            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.visibility_off_icon, 0);
                        } else {
                            // Show password
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.visibility_icon, 0);
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

        signupPassword.setOnTouchListener(togglePasswordListener);
        confirmPassword.setOnTouchListener(togglePasswordListener);
    }
}
