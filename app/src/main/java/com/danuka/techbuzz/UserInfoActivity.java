package com.danuka.techbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    Button signOutBtn, editBtn;
    TextView textUsername, textEmail;
    View editPopup, signoutPopup;

    String currentUsername; // track the currently logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        signOutBtn = findViewById(R.id.signoutBtn);
        editBtn = findViewById(R.id.editBtn);
        editPopup = findViewById(R.id.editPopup);
        signoutPopup = findViewById(R.id.signoutPopup);

        fetchAndDisplayUserInfo();

        signOutBtn.setOnClickListener(view -> signoutPopup.setVisibility(View.VISIBLE));

        editBtn.setOnClickListener(view -> {
            editPopup.setVisibility(View.VISIBLE);

            // Hide warning when popup opens
            TextView usernameWarning = editPopup.findViewById(R.id.usernameWarning);
            usernameWarning.setVisibility(View.GONE);

            // Clear inputs when popup opens (optional)
            EditText editUser = editPopup.findViewById(R.id.editUsername);
            EditText editEmail = editPopup.findViewById(R.id.editEmail);
            editUser.setText(textUsername.getText().toString());
            editEmail.setText(textEmail.getText().toString());
        });

        Button cancelBtn = editPopup.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {

            TextView usernameWarning = editPopup.findViewById(R.id.usernameWarning);
            usernameWarning.setVisibility(View.GONE);

            // Hide popup
            editPopup.setVisibility(View.GONE);
        });

        Button signOutCancel = signoutPopup.findViewById(R.id.cancelBtn);
        signOutCancel.setOnClickListener(v -> signoutPopup.setVisibility(View.GONE));

        Button signOutOkay = signoutPopup.findViewById(R.id.okayBtn);

        signOutOkay.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("TechBuzzPrefs", MODE_PRIVATE);
            prefs.edit().remove("loggedInUsername").apply();
            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(intent);
            signoutPopup.setVisibility(View.GONE);
        });

        // Handle edit + save
        Button okayBtn = editPopup.findViewById(R.id.okayBtn);

        // warning visibility on typing
        TextView usernameWarning = editPopup.findViewById(R.id.usernameWarning);
        EditText editUser = editPopup.findViewById(R.id.editUsername);
        editUser.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no action
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentUsername)) {
                    usernameWarning.setVisibility(View.VISIBLE);
                } else {
                    usernameWarning.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // no action
            }
        });

        okayBtn.setOnClickListener(v -> {
            EditText editUsername = editPopup.findViewById(R.id.editUsername);
            EditText editEmail = editPopup.findViewById(R.id.editEmail);

            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(UserInfoActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            usersRef.child(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentPassword = snapshot.child("password").getValue(String.class); // preserve password

                        Map<String, Object> updatedUser = new HashMap<>();
                        updatedUser.put("username", newUsername);
                        updatedUser.put("email", newEmail);
                        updatedUser.put("password", currentPassword); // keep original password

                        // If username changed, remove old node
                        if (!newUsername.equals(currentUsername)) {
                            usersRef.child(currentUsername).removeValue();
                        }

                        usersRef.child(newUsername).setValue(updatedUser).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update SharedPreferences
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserInfoActivity.this);
                                prefs.edit().putString("username", newUsername).apply();

                                textUsername.setText(newUsername);
                                textEmail.setText(newEmail);
                                currentUsername = newUsername;

                                Snackbar.make(findViewById(android.R.id.content), "User updated successfully", Snackbar.LENGTH_LONG).show();
                                editPopup.setVisibility(View.GONE);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Failed to update", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    private void fetchAndDisplayUserInfo() {
        SharedPreferences prefs = getSharedPreferences("TechBuzzPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);

        if (username == null) {
            Snackbar.make(findViewById(android.R.id.content), "No user logged in", Snackbar.LENGTH_LONG).show();
            return;
        }

        currentUsername = username;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String fetchedUsername = snapshot.child("username").getValue(String.class);

                    textUsername.setText(fetchedUsername);
                    textEmail.setText(email);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "User data not found", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Snackbar.make(findViewById(android.R.id.content), "Failed to load user info", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
