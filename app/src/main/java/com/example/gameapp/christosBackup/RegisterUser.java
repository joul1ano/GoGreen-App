package com.example.gameapp.christosBackup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.MainActivity;
import com.example.gameapp.db.SQLiteConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gameapp.R;
import com.example.gameapp.tzouliano.FormRegister;

public class RegisterUser extends AppCompatActivity {
    private String username; // Variable to store the username
    private String passwordFirstTry; // Variable to store the first password entry
    private String passwordSecondTry; // Variable to store the second password entry
    private EditText editTextUsername; // EditText for the username
    private EditText editTextPasswordFirstTry; // EditText for the first password entry
    private EditText editTextPasswordSecondTry; // EditText for the second password entry
    private Button signUpBtn; // Button for sign-up
    SQLiteConnection lite; // SQLite connection instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Initializing the sign-up button
        signUpBtn = (Button) findViewById(R.id.loginBtn);

        // Initializing the SQLite connection
        lite = new SQLiteConnection(this);

        // Setting the click listener for the sign-up button
        signUpBtn.setOnClickListener(this::onClick);
    }

    // Method for sign-up button click
    public void onClick(View view) {
        // Get username and passwords from input fields
        editTextUsername = (EditText) findViewById(R.id.newUserUsernameInput);
        editTextPasswordFirstTry = (EditText) findViewById(R.id.newUserPasswordInputFirst);
        editTextPasswordSecondTry = (EditText) findViewById(R.id.newUserPasswordInputFirst2);

        username = editTextUsername.getText().toString();
        passwordFirstTry = editTextPasswordFirstTry.getText().toString();
        passwordSecondTry = editTextPasswordSecondTry.getText().toString();

        // Check for invalid input
        if (usernameContainsSpace(username)) {
            Toast.makeText(getApplicationContext(), "Username must not contain space!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usernameOrPasswordIsEmpty(username, passwordFirstTry)) {
            Toast.makeText(getApplicationContext(), "Username or Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordFirstTry.equals(passwordSecondTry)) {
            Toast.makeText(getApplicationContext(), "Passwords are different!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lite.userUsernameExists(username)) {
            Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the new user into the database
        lite.insert_user(username, passwordFirstTry);

        // Navigate to the next activity
        sendMessage(view);
    }

    // Method to navigate to FormRegister activity
    public void sendMessage(View view) {
        Intent intent = new Intent(this, FormRegister.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    // Method to check if username contains space
    public boolean usernameContainsSpace(String u) {
        return u.contains(" ");
    }

    // Method to check if username or password is empty
    public boolean usernameOrPasswordIsEmpty(String u, String p) {
        return (u.matches("") && p.matches(""));
    }
}
