package com.example.gameapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.alex.ApplicationApproval;
import com.example.gameapp.arxontia.userProfile;
import com.example.gameapp.christosBackup.RegisterUser;
import com.example.gameapp.db.SQLiteConnection;
import com.example.gameapp.tzouliano.FormRegister;
//ANDROID STUDIO VERSION : Flamingo // 2022.2.1
public class MainActivity extends AppCompatActivity {
    SQLiteConnection lite; // SQLite connection instance
    private Button loginBtn; // Button for login
    private Button gotoRegisterBtn; // Button for going to registration screen
    private String username; // Variable to store the username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initializing buttons
        loginBtn = (Button) findViewById(R.id.loginBtn);
        gotoRegisterBtn = (Button) findViewById(R.id.gotoRegisterBtn);

        // Initializing SQLite connection
        lite = new SQLiteConnection(this);

        /*TO INSERT AN ADMIN, UN-COMMENT THE COMMENT BELOW THIS BLOCK , PRESS "RUN APP", STOP THE EXECUTION
          OF THE APP AND THEN DELETE THE LINE SO THAT YOU DONT INSERT THE SAME ADMIN MORE THAN ONCE. USE THE
          CREDENTIALS GIVEN TO LOG IN AS AN ADMIN
        */
        //lite.insert_admin("admin","admin");

        // Setting click listeners for buttons
        loginBtn.setOnClickListener(this::onClick1);

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to RegisterUser activity
                Intent registerIntent = new Intent(MainActivity.this, RegisterUser.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });
    }

    // Method for login button click
    public void onClick1(View view) {
        // Get username and password from input fields
        EditText loginTextUsername = (EditText) findViewById(R.id.loginUserUsernameInput);
        username = loginTextUsername.getText().toString();
        EditText loginTextPassword = (EditText) findViewById(R.id.loginUserPasswordInput);
        String password = loginTextPassword.getText().toString();

        // Check for invalid input
        if (containsSpace(username)) {
            Toast.makeText(getApplicationContext(), "Username must not contain space!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usernameOrPasswordIsEmpty(username, password)) {
            Toast.makeText(getApplicationContext(), "Username or Password is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check database if the person signing in is a user or an admin
        if (loginUser(username, password)) {
            sendMessage(view);
        } else if (loginAdmin(username, password)) {
            sendMessage2(view);
        }
    }

    // Method to navigate to FormRegister activity for users
    public void sendMessage(View view) {
        Intent formIntent = new Intent(this, FormRegister.class);
        formIntent.putExtra("username", username);
        startActivity(formIntent);
    }

    // Method to navigate to ApplicationApproval activity for admins
    public void sendMessage2(View view) {
        Intent applicationIntent = new Intent(this, ApplicationApproval.class);
        startActivity(applicationIntent);
    }

    // Method to validate user login
    public boolean loginUser(String username, String password) {
        if (!lite.userUsernameExists(username)) {
            return false;
        }
        if (!lite.isUserPasswordCorrect(username, password)) {
            return false;
        }
        return true;
    }

    // Method to validate admin login
    public boolean loginAdmin(String username, String password) {
        if (!lite.adminUsernameExists(username)) {
            Toast.makeText(getApplicationContext(), "Wrong password or username.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!lite.isAdminPasswordCorrect(username, password)) {
            Toast.makeText(getApplicationContext(), "Wrong password or username.", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(getApplicationContext(), "Admin correct", Toast.LENGTH_SHORT).show();
        return true;
    }

    // Method to check if a string contains space
    public boolean containsSpace(String u) {
        return u.contains(" ");
    }

    // Method to check if username or password is empty
    public boolean usernameOrPasswordIsEmpty(String u, String p) {
        return (u.matches("") && p.matches(""));
    }
}
