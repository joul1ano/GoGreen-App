package com.example.gameapp.arxontia;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.MainActivity;
import com.example.gameapp.R;
import com.example.gameapp.db.SQLiteConnection;
import com.example.gameapp.db.ShownResultsContract;
import com.example.gameapp.tzouliano.FormRegister;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yangp.ypwaveview.YPWaveView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class userProfile extends AppCompatActivity {

    ContentValues userValues;
    SQLiteConnection lite;

    String username;
    int size = 8; //8 materials
    private int points=0;
    private int numberOfRecycledItems;
    private int litersBeenRecycled;
    private int level=0;

    int points_per_material[] = {0, 0, 0, 0, 0, 0, 0, 0}; //Array with number of points user can get, by recycling one item per material
    int materials_been_recycled[] = {0, 0, 0, 0, 0, 0, 0, 0}; //Array that has how many times every material has been recycled

    ArrayList<Integer> pointsList = new ArrayList<>(size); //ArrayList with points per material
    ArrayList<ProgressBar> progressBars = new ArrayList<>(size); //ArrayList for every progressbar to add progress
    ArrayList<TextView> progressBars_Texts = new ArrayList<>(size); //ArrayList for the texts below every progressBar
    ArrayList<Integer> colorsPieChart = new ArrayList<>(); //ArrayList for the colors in PieChart
    ArrayList<Integer> colorsLegend= new ArrayList<>(); //ArrayList for the colors in legend
    String[] materials = {"plastic", "glass", "paper", "aluminum", "oil", "electric device", "battery", "clothing"}; //String array with materials

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        AssetManager assets = getAssets();

        // get Intent from MainActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        try {
            InputStream is = assets.open("points.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("points");

            for (int i = 0; i < nList.getLength(); i++) {
                Node pointsElement = (Node) nList.item(i);
                points_per_material[i] = Integer.parseInt(pointsElement.getTextContent().trim());

            }

        } catch (Exception e) {e.printStackTrace();}


        // get userValues from the database
        lite = new SQLiteConnection(this);
        userValues = lite.getUserInstances(username);
        populateUserMaterials();



        //bottom navigation view listener to switch activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent = null;

                if (itemId == R.id.ic_points) {}
                else if (itemId == R.id.ic_recycle) {
                    View v = findViewById(android.R.id.content).getRootView();
                    sendMessage(v);
                } else if (itemId == R.id.ic_logout) {
                    intent = new Intent(userProfile.this, MainActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });


        //Calculate points for every material
        calculatePointsPerMaterial();

        //Calculate total points
        calculateTotalPoints();

        //Calculate number of products that have been recycled
        calculateRecycledItems();

        //TextView Setup
        TextviewSetup();

        //Setup ProgressBar
        setupLiquidProgressBar();

        //Setup PieChart
        setupPieChart();

        //Setup ProgressBars
        setupProgressBars();

        //setup user's Reward
        setupReward();

        //SetupLevel user
        setupLevels();

    }

    /*This void is used to go to next level(s), if user has reached his goal*/
    private void setupLevels() {

        YPWaveView pb= findViewById(R.id.liquidProgressBar);

        while (pb.getProgress()>=pb.getMax()) {
            //Reset level progress
            RedeemPoints();
            setNewGoal();
            TextviewSetup();
            setupLiquidProgressBar();
            setupPieChart();
            setupProgressBars();
            setupReward();
        }

    }

    /*This void sets textview for the current points
      and current items been recycled*/
    private void TextviewSetup(){

        TextView currentPoints = (TextView) findViewById(R.id.current_points);
        int points_text = points;
        currentPoints.setText(String.valueOf(points_text)+"  Points");

        calculateRecycledItems();

        TextView items= findViewById(R.id.number_items_recycled);
        items.setText(String.valueOf(numberOfRecycledItems));

        TextView liters=findViewById(R.id.liters_been_recycled);
        liters.setText(String.valueOf(litersBeenRecycled));
    }


    /*This void is used to setup the progressBar.
      It calculates and sets the percent of the progress
      and sets the speed of the wave.*/
    private void setupLiquidProgressBar() {

        YPWaveView LiquidProgressBar = findViewById(R.id.liquidProgressBar);
        LiquidProgressBar.setHideText(true);
        LiquidProgressBar.setAnimationSpeed(90);

        //Progress setup
        if(points>LiquidProgressBar.getMax())
            LiquidProgressBar.setProgress(LiquidProgressBar.getMax()); //to stay in the limits
        else
            LiquidProgressBar.setProgress(points);

        //Textview setup
        TextView pointsTextProgressBar = findViewById(R.id.wave_progress_bar_points_text);
        int max_points = LiquidProgressBar.getMax();
        int current_points = points;

        String points_and_goal=current_points+"/"+max_points+"\n";

        if(max_points<current_points){
            pointsTextProgressBar.setText(points_and_goal+"You exceeded your goal by "+(current_points-max_points)+" points!");
            points=current_points-max_points;

        }
        else if(max_points==current_points){
            pointsTextProgressBar.setText(points_and_goal+"Congrats! you reached your goal");
            points=0;}
        else
            pointsTextProgressBar.setText(points_and_goal+"You need "+(max_points-current_points)+" points to reach your goal");


        //calculate progress and update percent textview
        int progress = (int) ((float) current_points / max_points * 100);
        TextView TextPercent = findViewById(R.id.progress_percent);

        if(progress>=100) //stay in the limits
            TextPercent.setText("100%");
        else
            TextPercent.setText(progress + "%");
    }

    /*
     * This void is used to setup the entries and colors for the pieChart.
     * If everything is zero it adds a "No data" entry with a gray color
     * to avoid matching the background color.
     * Also sets up a center text inside of the pieChart */
    private void setupPieChart() {
        PieChart pieChart =findViewById(R.id.chart);

        colorsPieChart.clear();

        ArrayList<PieEntry> entries = new ArrayList<>();

        int times=0;
        for(Integer PointsList: pointsList){
            if(PointsList>0){
                entries.add(new PieEntry(PointsList));
                addColorsPieChart(times);
            }times++;
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");

        boolean allZero = true;
        for (int value : materials_been_recycled) {
            if (value != 0) {  //if everything is zero it adds the "no data" entry
                allZero = false;
                break;
            }
        }

        pieDataSet.setDrawValues(true);
        if (allZero) { //if all materials are zero add a "no data" entry to not match the background
            entries.add(new PieEntry(1, "No Data"));
            pieDataSet.setDrawValues(false); //to hide "no data" entry in PieChart
            colorsPieChart.add(Color.LTGRAY);
        }

        pieDataSet.setColors(colorsPieChart);
        pieDataSet.setValueTextSize(11);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(70f);
        pieChart.setCenterText("Points per material");
        pieChart.setCenterTextSize(17);
        pieChart.setDrawRoundedSlices(true);
        pieChart.setEntryLabelTextSize(10);

        //Setup Legend
        setupPieChartLegend(pieChart);

        pieChart.invalidate();
    }

    /*This void is used to setup the PieChart Legend.
      It setups its position, text, text size and colors*/
    private void setupPieChartLegend(PieChart pieChart) {

        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setTextColor(Color.BLACK);
        pieChart.getLegend().setXEntrySpace(5f);
        pieChart.getLegend().setYOffset(-50f);
        pieChart.getLegend().setTextSize(13f);

        addColors(colorsLegend);

        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        for (int j = 0; j < materials.length; j++) {
            LegendEntry entry = new LegendEntry();
            entry.label = materials[j] + " " + pointsList.get(j);
            entry.formColor = colorsLegend.get(j);
            legendEntries.add(entry);
        }
        pieChart.getLegend().setCustom(legendEntries);
    }

    /*This void is used to setup progress and textview
      for every progressBar(material) in progressBar layout*/
    private void setupProgressBars() {
        YPWaveView pb=findViewById(R.id.liquidProgressBar);
        int goal = pb.getMax();

        progressBars.clear();

        progressBars.add(findViewById(R.id.progressBar_plastic));
        progressBars.add(findViewById(R.id.progressBar_glass));
        progressBars.add(findViewById(R.id.progressBar_paper));
        progressBars.add(findViewById(R.id.progressBar_aluminium));
        progressBars.add(findViewById(R.id.progressBar_oils));
        progressBars.add(findViewById(R.id.progressBar_electric_devices));
        progressBars.add(findViewById(R.id.progressBar_batteries));
        progressBars.add(findViewById(R.id.progressBar_clothes));


        int i = 0;
        for (ProgressBar progressBar : progressBars) {
            progressBar.setMax(100);
            //calculate progress percentage relative to the goal for every material
            progressBar.setProgress((int)(((double) pointsList.get(i++) / goal) * 100));
        }

        progressBars_Texts.clear();

        progressBars_Texts.add(findViewById(R.id.PB_plastic_text));
        progressBars_Texts.add(findViewById(R.id.PB_glass_text));
        progressBars_Texts.add(findViewById(R.id.PB_paper_text));
        progressBars_Texts.add(findViewById(R.id.PB_aluminium_text));
        progressBars_Texts.add(findViewById(R.id.PB_oils_text));
        progressBars_Texts.add(findViewById(R.id.PB_electric_device_text));
        progressBars_Texts.add(findViewById(R.id.PB_batteries_text));
        progressBars_Texts.add(findViewById(R.id.PB_clothes_text));


        int j = 0;
        for (TextView progressBar_texts : progressBars_Texts) {
            progressBar_texts.setText(materials[j] +"\n"+pointsList.get(j));
            j++;
        }
    }


    /*This void is called everytime user reaches his goal,
    resets the progress by redeeming points.
    Updates the points ArrayList and materials Array*/
    private void RedeemPoints() {
        YPWaveView pb=findViewById(R.id.liquidProgressBar);
        int goal = pb.getMax();

        int sum = 0;
        for (int i = 0; i < pointsList.size(); i++) {
            if ((sum += pointsList.get(i)) <= goal) {
                pointsList.set(i, 0);
                materials_been_recycled[i] = 0;
            } else {
                pointsList.set(i, sum - goal);
                materials_been_recycled[i] = pointsList.get(i) / points_per_material[i];
                if (pointsList.get(i) - (points_per_material[i] * materials_been_recycled[i]) > 0)
                    materials_been_recycled[i]++;
                break;
            }
        }
    }

    /*Calculation of points per material*/
    private void calculatePointsPerMaterial() {
        for (int i = 0; i < size; i++)
            pointsList.add(points_per_material[i] * materials_been_recycled[i]);
    }

    /*Sum of total points*/
    private void calculateTotalPoints() {
        for (int pointPerMaterial : pointsList) {
            points += pointPerMaterial;
        }
    }

    /*sum of materials been recycled*/
    private void calculateRecycledItems() {
        numberOfRecycledItems=0;
        litersBeenRecycled =0;
        int times=0;

        for(Integer materialsRecycled: materials_been_recycled){
            if(times!=4)
            {numberOfRecycledItems+=materialsRecycled;}
            else
            {
                litersBeenRecycled =materialsRecycled;}
            times++;}
    }

    /*The level increases everytime user reaches the goal.
    The textview is updated and a new goal is set*/
    private void setNewGoal() {
        level++;
        TextView levelText= findViewById(R.id.level_text);
        levelText.setText("Level: "+String.valueOf(level));

        YPWaveView pb= findViewById(R.id.liquidProgressBar);
        pb.setMax(pb.getMax()+2500);
    }

    /*Adding colors for the legend in PieChart*/
    private void addColors(ArrayList<Integer> colorsLegend) {

        //turquoise-green shades
        colorsLegend.add(Color.rgb(178, 219, 214));
        colorsLegend.add(Color.rgb(136, 184, 179));
        colorsLegend.add(Color.rgb(102, 161, 157));
        colorsLegend.add(Color.rgb(85, 183, 171));
        colorsLegend.add(Color.rgb(69, 161, 150));
        colorsLegend.add(Color.rgb(23, 130, 121));
        colorsLegend.add(Color.rgb(17, 97, 90));
        colorsLegend.add(Color.rgb(0, 85, 78));
    }


    //this void sets up a color for the pieChart every time it is called
    private void addColorsPieChart(int caseNumber){
        switch (caseNumber) {
            case 0:
                colorsPieChart.add(Color.rgb(178, 219, 214));
                break;
            case 1:
                colorsPieChart.add(Color.rgb(136, 184, 179));
                break;
            case 2:
                colorsPieChart.add(Color.rgb(102, 161, 157));
                break;
            case 3:
                colorsPieChart.add(Color.rgb(85, 183, 171));
                break;
            case 4:
                colorsPieChart.add(Color.rgb(69, 161, 150));
                break;
            case 5:
                colorsPieChart.add(Color.rgb(23, 130, 121));
                break;
            case 6:
                colorsPieChart.add(Color.rgb(17, 97, 90));
                break;
            case 7:
                colorsPieChart.add(Color.rgb(0, 85, 78));
                break;
        }

    }

    //This void gets from database the items per material user has recycled and stores them into an array
    private void populateUserMaterials() {
        if (userValues != null) {
            // Get username
            String username = userValues.getAsString(ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME);
            // Get user item quantities
            int glass = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_GlASS_QUANTITY);
            int paper = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_PAPER_QUANTITY);
            int aluminium = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_ALUMINIUM_QUANTITY);
            int electricDevices = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_ELECTRIC_DEVICES_QUANTITY);
            int batteries = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_BATTERIES_QUANTITY);
            int clothes = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_CLOTHING_QUANTITY);
            int oil = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_OIL_QUANTITY);
            int plastic = userValues.getAsInteger(ShownResultsContract.ShownResultEntry.COLUMN_USER_PLASTIC_QUANTITY);

            // "plastic", "glass", "paper", "aluminum", "oil", "electric device", "battery", "clothing"
            materials_been_recycled[0] = plastic;
            materials_been_recycled[1] = glass;
            materials_been_recycled[2] = paper;
            materials_been_recycled[3] = aluminium;
            materials_been_recycled[4] = oil;
            materials_been_recycled[5] = electricDevices;
            materials_been_recycled[6] = batteries;
            materials_been_recycled[7] = clothes;

        } else {
            // something went wrong, and we should never really get in this block of code
            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    //this void is used to change activity and to get also username
    public void sendMessage(View view){
        Intent intent = new Intent(this, FormRegister.class);
        intent.putExtra("username", username);
        startActivity(intent);

    }

    //this void is setting users reward by adding a 5$ for every level he has reach
    //With the set on click listener a toast informs user about the reward
    private void setupReward(){

        ImageButton imageButton =findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(userProfile.this, "You earn 5$ for every level you reach",
                        Toast.LENGTH_LONG).show();
            }
        });

        TextView moneyEarned=findViewById(R.id.reward_txt);
        int amountOfMoney=level*5;
        moneyEarned.setText(amountOfMoney+"$");

    }


}