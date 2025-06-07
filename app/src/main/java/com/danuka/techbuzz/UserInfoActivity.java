package com.danuka.techbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class UserInfoActivity extends AppCompatActivity {

    Button signOutBtn, editBtn;
    TextView textUsername, textEmail;
    View editPopup, signoutPopup;

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

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoutPopup.setVisibility(View.VISIBLE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPopup.setVisibility(View.VISIBLE); // show popup
            }
        });

        // You can find buttons inside editPopup and set listeners, e.g.
        Button cancelBtn = editPopup.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> editPopup.setVisibility(View.GONE)); // hide popup

        Button signOutCancel = signoutPopup.findViewById(R.id.cancelBtn);
        signOutCancel.setOnClickListener(v -> signoutPopup.setVisibility(View.GONE));

        Button okayBtn = editPopup.findViewById(R.id.okayBtn);
        okayBtn.setOnClickListener(v -> {
            // Handle saving here
            EditText editUsername = editPopup.findViewById(R.id.editUsername);
            EditText editEmail = editPopup.findViewById(R.id.editEmail);
            String username = editUsername.getText().toString();
            String email = editEmail.getText().toString();
            // TODO: Save or update

            editPopup.setVisibility(View.GONE); // hide popup after saving
        });

        Button signOutOkay = signoutPopup.findViewById(R.id.okayBtn);
        signOutOkay.setOnClickListener(v -> {

            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
            startActivity(intent);
            signoutPopup.setVisibility(View.GONE); // hide popup after saving
        });


    }


}
