package com.example.gameapp.giannis;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gameapp.MainActivity;
import com.example.gameapp.MainActivity;
import com.example.gameapp.R;
import com.example.gameapp.alex.ApplicationApproval;
import com.example.gameapp.db.SQLiteConnection;
import com.example.gameapp.db.ShownResultsContract;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAdmin extends AppCompatActivity {

    private PieChart pieChart;
    private LinearLayout progressBarContainer;
    private SQLiteConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_admin);

        // Initialize PieChart
        pieChart = findViewById(R.id.pie_chart);

        // Initialize database connection
        dbConnection = new SQLiteConnection(this);

        // Set up bottom navigation view and its item selection listener
        BottomNavigationView nav = findViewById(R.id.adminBottomNavigationView);
        nav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent = null;

                // Handle navigation item clicks
                if (itemId == R.id.ic_admin_logout) {
                    intent = new Intent(StatisticsAdmin.this, MainActivity.class);
                } else if (itemId == R.id.ic_admin_stats) {
                    // Stay on the current activity
                } else if (itemId == R.id.ic_admin_approve) {
                    intent = new Intent(StatisticsAdmin.this, ApplicationApproval.class);
                }

                // If an intent was set, start the corresponding activity
                if (intent != null) {
                    finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Initialize arrays to hold user data
        ArrayList<Integer> firstUserItemsAndPoints = new ArrayList<>();
        ArrayList<Integer> secondUserItemsAndPoints = new ArrayList<>();
        ArrayList<Integer> thirdUserItemsAndPoints = new ArrayList<>();
        String firstUsername, secondUsername, thirdUsername;

        // Get references to TextViews for the top three users
        TextView firstUserTextView = (TextView) findViewById(R.id.firstUser);
        TextView secondUserTextView = (TextView) findViewById(R.id.secondUser);
        TextView thirdUserTextView = (TextView) findViewById(R.id.thirdUser);

        // Get the top three users from the database
        List<ContentValues> topThree = dbConnection.getTopThreeUsers();

        // If there are users, display their data
        if (topThree.size() > 0) {
            firstUserTextView.setVisibility(View.VISIBLE);
            firstUserItemsAndPoints = populateArrayListOfTopUserItems(topThree.get(0));
            firstUsername = "@" + topThree.get(0).getAsString(ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME);
            firstUserTextView.setText(firstUsername);
            setupPieChart(firstUserItemsAndPoints);  // Display data for the first user in the PieChart
        } else {
            firstUserTextView.setVisibility(View.GONE);
        }

        if (topThree.size() > 1) {
            secondUserTextView.setVisibility(View.VISIBLE);
            secondUserItemsAndPoints = populateArrayListOfTopUserItems(topThree.get(1));
            secondUsername = "@" +  topThree.get(1).getAsString(ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME);
            secondUserTextView.setText(secondUsername);
        } else {
            secondUserTextView.setVisibility(View.GONE);
        }

        if (topThree.size() > 2) {
            thirdUserTextView.setVisibility(View.VISIBLE);
            thirdUserItemsAndPoints = populateArrayListOfTopUserItems(topThree.get(2));
            thirdUsername =  "@" + topThree.get(2).getAsString(ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME);
            thirdUserTextView.setText(thirdUsername);
        } else {
            thirdUserTextView.setVisibility(View.GONE);
        }

        // Set up layouts for the top three users
        LinearLayout l1 = (LinearLayout) findViewById(R.id.firstUserLayout);
        LinearLayout l2 = (LinearLayout) findViewById(R.id.secondUserLayout);
        LinearLayout l3 = (LinearLayout) findViewById(R.id.thirdUserLayout);

        // Set initial background color for the first user's layout
        l1.setBackgroundColor(Color.WHITE);

        // Set up click listeners for each user layout to update the PieChart and background colors
        ArrayList<Integer> finalFirstUserItemsAndPoints = firstUserItemsAndPoints;
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPieChart(finalFirstUserItemsAndPoints);
                l1.setBackgroundColor(Color.WHITE);
                l2.setBackgroundColor(Color.parseColor("#98FB98"));
                l3.setBackgroundColor(Color.parseColor("#98FB98"));
            }
        });

        ArrayList<Integer> finalSecondUserItemsAndPoints = secondUserItemsAndPoints;
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPieChart(finalSecondUserItemsAndPoints);
                l1.setBackgroundColor(Color.parseColor("#98FB98"));
                l2.setBackgroundColor(Color.WHITE);
                l3.setBackgroundColor(Color.parseColor("#98FB98"));
            }
        });

        ArrayList<Integer> finalThirdUserItemsAndPoints = thirdUserItemsAndPoints;
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPieChart(finalThirdUserItemsAndPoints);
                l1.setBackgroundColor(Color.parseColor("#98FB98"));
                l2.setBackgroundColor(Color.parseColor("#98FB98"));
                l3.setBackgroundColor(Color.WHITE);
            }
        });
    }

    // Method to set up the PieChart with given values
    private void setupPieChart(ArrayList<Integer> values) {
        List<PieEntry> entries = new ArrayList<>();

        // Entries for each type of recycled material
        entries.add(new PieEntry(values.get(0), "Glass"));
        entries.add(new PieEntry(values.get(1), "Paper"));
        entries.add(new PieEntry(values.get(2), "Aluminium"));
        entries.add(new PieEntry(values.get(3), "Electric Devices"));
        entries.add(new PieEntry(values.get(4), "Batteries"));
        entries.add(new PieEntry(values.get(5), "Clothes"));
        entries.add(new PieEntry(values.get(6), "Used Oil"));
        entries.add(new PieEntry(values.get(7), "Plastic"));

        PieDataSet dataSet = new PieDataSet(entries, "Recycled Materials");
        dataSet.setColors(new int[]{Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.DKGRAY});
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(12f);

        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    // Method to populate a list with quantities of various recycled materials for a given user
    private ArrayList<Integer> populateArrayListOfTopUserItems(ContentValues topUser) {
        ArrayList<Integer> items = new ArrayList<>();
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_GlASS_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_PAPER_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_ALUMINIUM_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_ELECTRIC_DEVICES_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_BATTERIES_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_CLOTHING_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_OIL_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_PLASTIC_QUANTITY));
        items.add(topUser.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_POINTS));

        return items;
    }

    // Close the database connection when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbConnection.close();
    }
}
