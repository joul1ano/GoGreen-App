package com.example.gameapp.alex;

import android.content.ContentValues;

import com.example.gameapp.MainActivity;
import com.example.gameapp.arxontia.userProfile;
import com.example.gameapp.db.SQLiteConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.db.SQLiteConnection;
import com.example.gameapp.db.ShownResultsContract;
import com.example.gameapp.giannis.StatisticsAdmin;
import com.example.gameapp.tzouliano.FormRegister;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class ApplicationApproval extends AppCompatActivity {
    // ContentValues userValues: the database values for the current user (username, glassQuantity, etc)
    ContentValues userValues;
    // The quantities for each item the user has recycled
    private int glass;
    private int paper;
    private int aluminium;
    private int electricDevices;
    private int batteries;
    private int clothes;
    private int oil;
    private int plastic;
    private int points;
    private String username;
    private Button approveBtn;
    private Button rejectBtn;
    SQLiteConnection lite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_approval);

        // Navigation bar
        BottomNavigationView nav = findViewById(R.id.adminBottomNavigationView);
        // Set up navigation bar functionality
        // The navigation bar can get you to the adminStatistics screen or log you out
        nav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent = null;

                if (itemId == R.id.ic_admin_logout) {
                    intent = new Intent(ApplicationApproval.this, MainActivity.class);
                } else if (itemId == R.id.ic_admin_stats) {
                    intent = new Intent(ApplicationApproval.this, StatisticsAdmin.class);
                }



                if (intent != null) {
                    finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }

        });

        // Make conection to the database
        lite = new SQLiteConnection(this);

        // The admin approves serially the applications that the users make until no more applications are left to approve
        // This is why he gets the first application in the applications table of the database
        // and then after the admin approves or rejects the application, it deletes it from the table
        // so that it then can get the next one

        //  This is the first user if there is one
        userValues = lite.getFirstApplicationInstance();

        rejectBtn = (Button) findViewById(R.id.rejectBtn);
        approveBtn = (Button) findViewById(R.id.approveBtn);
        // If there is a user application show the buttons and and the application values in the screen
        // else show the application values as blank
        if (userValues != null) {
            approveBtn.setVisibility(View.VISIBLE);
            rejectBtn.setVisibility(View.VISIBLE);

            populateApplicationForm();
        } else {
            populateApplicationForm();
        }

        // give the approve and reject buttons their functionality
        rejectBtn.setOnClickListener(this::rejectApplication);
        approveBtn.setOnClickListener(this::approveApplication);
    }

    private void approveApplication(View v) {
        // When approving an application you delete the first column of the database Applications TABLE
        // and add the value quantities of the application items to the user item quantities. Doing
        // that means we have verified that the user actually recycled that amount of items
        //
        // Parameters:
        //    v (View): The current ApplicationApproval View

        // get the values of the first application in the Applications database TABLE
        ContentValues appInstances = lite.getFirstApplicationInstance();

        // get username (the user that made the application)
        String username = appInstances.getAsString(ShownResultsContract.ShownResultEntry.COLUMN_APPLICANT_USERNAME);
        // get glass items
        int glass = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_GLASS_ITEMS);
        // get paper items
        int paper = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_PAPER_ITEMS);
        // get aluminium items
        int aluminium = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_ALUMINIUM_ITEMS);
        // get electric device items
        int electricDevices = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_ELECTRIC_DEVICE_ITEMS);
        // get batteries
        int batteries = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_BATTERIES);
        // get clothes
        int clothes = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_CLOTHES);
        // get used oil kg
        int oil = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USED_OIL_KG);
        // get plastic items
        int plastic = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_PLASTIC_ITEMS);
        // get application points
        int applicationPoints = appInstances.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_APPLICATION_POINTS);

        // Add the specified amount of items in the user's column in the USERS database TABLE
        // (String username: This is the user that made the application)
        // add glass quantities
        lite.updateGlassItems(username, glass);
        // add paper quantities
        lite.updatePaperItems(username, paper);
        // add aluminium quantities
        lite.updateAluminiumItems(username, aluminium);
        // add electric devices quantities
        lite.updateElectricDeviceItems(username, electricDevices);
        // add batteries quantities
        lite.updateBatteries(username, batteries);
        // add clothes quantities
        lite.updateClothes(username, clothes);
        // add oil quantities
        lite.updateUsedOil(username, oil);
        // add plastic quantities
        lite.updatePlasticItems(username, plastic);
        // add applicationPoints
        lite.updateUserPoints(username, applicationPoints);

        // Delete the user application that we just accessed so that we can then get next application if there is one
        lite.deleteFirstApplicationInstance();

        // Get the next user application and if there is one then populate the screen with its values
        // so that the admin can see them and approve/reject
        userValues = lite.getFirstApplicationInstance();
        populateApplicationForm();

        // Inform admin that the button worked
        Toast.makeText(getApplicationContext(), "Application approved.", Toast.LENGTH_SHORT).show();
    }

    private void rejectApplication(View v) {
        // When rejecting an application you delete the first column of the database Applications TABLE
        // and then just display the contents of the next user application, if there is one.
        //
        // Parameters:
        //    v (View): The current ApplicationApproval View

        // delete the current user application that is show in the screen
        lite.deleteFirstApplicationInstance();

        // Get the next user application and if there is one then populate the screen with its values
        // so that the admin can see them and approve/reject
        userValues = lite.getFirstApplicationInstance();
        populateApplicationForm();

        // Inform admin that the button has worked
        Toast.makeText(getApplicationContext(), "Application rejected.", Toast.LENGTH_SHORT).show();
    }

    private void populateApplicationForm() {

        // if there is a user application then get the item values and display them in the correct TextView
        // else make the TextViews blank and disable the reject and approve buttons (because the admin can do nothing with them, if this is the case)
        //
        // Parameters: None

        // if there is a user application in the Applications TABLE
        if (userValues != null) {
            // Set username
            String username = userValues.getAsString(ShownResultsContract.ShownResultEntry.COLUMN_APPLICANT_USERNAME);
            TextView usernameView = (TextView) findViewById(R.id.usernameTextView);
            String usernameText = "@" + username;
            usernameView.setText(usernameText);

            // Set glass items
            glass = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_GLASS_ITEMS);
            TextView glassView = (TextView) findViewById(R.id.glassQuantity);
            glassView.setText(String.valueOf(glass));

            // Set paper items
            paper = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_PAPER_ITEMS);
            TextView paperView = (TextView) findViewById(R.id.paperQuantity);
            paperView.setText(String.valueOf(paper));

            // Set aluminium items
            aluminium = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_ALUMINIUM_ITEMS);
            TextView aluminiumView = (TextView) findViewById(R.id.aluminiumQuantity);
            aluminiumView.setText(String.valueOf(aluminium));

            // Set electric device items
            electricDevices = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_ELECTRIC_DEVICE_ITEMS);
            TextView electricDevicesView = (TextView) findViewById(R.id.electricDevicesQuantity);
            electricDevicesView.setText(String.valueOf(electricDevices));

            // Set batteries
            batteries = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_BATTERIES);
            TextView batteriesView = (TextView) findViewById(R.id.batteriesQuantity);
            batteriesView.setText(String.valueOf(batteries));

            // Set clothes
            clothes = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_CLOTHES);
            TextView clothesView = (TextView) findViewById(R.id.clothesQuantity);
            clothesView.setText(String.valueOf(clothes));

            // Set used oil kg
            oil = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USED_OIL_KG);
            TextView oilView = (TextView) findViewById(R.id.oilQuantity);
            oilView.setText(String.valueOf(oil));

            // Set plastic items
            plastic = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_PLASTIC_ITEMS);
            TextView plasticView = (TextView) findViewById(R.id.plasticQuantity);
            plasticView.setText(String.valueOf(plastic));

        } else {
            // In this case we have approved/rejected all the pending user applications
            // and there is nothing left to display in the screen

            // Set username;
            TextView usernameView = (TextView) findViewById(R.id.usernameTextView);
            String usernameText = "No Applications";
            usernameView.setText(usernameText);

            // Set glass items
            TextView glassView = (TextView) findViewById(R.id.glassQuantity);
            glassView.setText("-");

            // Set paper items
            TextView paperView = (TextView) findViewById(R.id.paperQuantity);
            paperView.setText("-");

            // Set aluminium items
            TextView aluminiumView = (TextView) findViewById(R.id.aluminiumQuantity);
            aluminiumView.setText("-");

            // Set electric device items
            TextView electricDevicesView = (TextView) findViewById(R.id.electricDevicesQuantity);
            electricDevicesView.setText("-");

            // Set batteries
            TextView batteriesView = (TextView) findViewById(R.id.batteriesQuantity);
            batteriesView.setText("-");

            // Set clothes
            TextView clothesView = (TextView) findViewById(R.id.clothesQuantity);
            clothesView.setText("-");

            // Set used oil kg
            TextView oilView = (TextView) findViewById(R.id.oilQuantity);
            oilView.setText("-");

            // Set plastic items
            TextView plasticView = (TextView) findViewById(R.id.plasticQuantity);
            plasticView.setText("-");

            // change buttons visibility (There are no more application to approve/reject)
            approveBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
        }
    }
}
