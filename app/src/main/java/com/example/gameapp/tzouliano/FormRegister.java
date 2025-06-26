package com.example.gameapp.tzouliano;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.MainActivity;
import com.example.gameapp.arxontia.userProfile;
import com.example.gameapp.db.SQLiteConnection;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gameapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FormRegister extends AppCompatActivity {
    //Quantities of each item the user has submitted
    private int glass;
    private int paper;
    private int aluminium;
    private int electricDevices;
    private int batteries;
    private int clothes;
    private int oil;
    private int plastic;
    private Button submitBtn;
    private String username;

    SQLiteConnection lite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_register);

        //Get the username from the Login or Signup
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //bottom navigation view listener to switch activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent = null;

                //
                if (itemId == R.id.ic_points) {
                    View v = findViewById(android.R.id.content).getRootView();
                    sendMessage(v);
                } else if (itemId == R.id.ic_recycle) {}
                  else if (itemId == R.id.ic_logout) {
                    intent = new Intent(FormRegister.this, MainActivity.class);
                }

                if (intent != null) {
                    finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        }); bottomNavigationView.setSelectedItemId(R.id.ic_recycle);



        submitBtn = (Button) findViewById(R.id.rejectBtn);
        submitBtn.setOnClickListener(this::onClick);

    };


    public void onClick(View v) {
        // Gets the activity's view and inserts the application information in the application table
        // of the SQLite database
        //
        // Parameters:
        //    v (View): The FormRegister view
        // Returns:

        //Connecting to database
        lite = new SQLiteConnection(this);
        //Getting the assets folder in order to get the points.xml file
        //which contains the points per material
        AssetManager assets = getAssets();

        EditText glassQuantity;
        EditText paperQuantity;
        EditText aluminiumQuantity;
        EditText electricDevicesQuantity;
        EditText batteriesQuantity;
        EditText clothesQuantity;
        EditText oilQuantity;
        EditText plasticQuantity;
        //Total points that are added from the items in the form
        int totalPoints = 0;
        //The quantities of each item of the form stored in an ArrayList.
        // E.g. if the form contains 2 plastic items, 10 glass items etc then tempQuantities = {2,10,etc..)
        ArrayList<Integer> tempQuantities = new ArrayList<>();

        glassQuantity = (EditText) findViewById(R.id.glassQuantity);
        paperQuantity = (EditText) findViewById(R.id.paperQuantity);
        aluminiumQuantity = (EditText) findViewById(R.id.aluminiumQuantity);
        electricDevicesQuantity = (EditText) findViewById(R.id.electricDevicesQuantity);
        batteriesQuantity = (EditText) findViewById(R.id.batteriesQuantity);
        clothesQuantity = (EditText) findViewById(R.id.clothesQuantity);
        oilQuantity = (EditText) findViewById(R.id.oilQuantity);
        plasticQuantity = (EditText) findViewById(R.id.plasticQuantity);

        //Parsing the quantities from the TextViews
        glass = Integer.parseInt(glassQuantity.getText().toString());
        paper = Integer.parseInt(paperQuantity.getText().toString());
        aluminium = Integer.parseInt(aluminiumQuantity.getText().toString());
        electricDevices = Integer.parseInt(electricDevicesQuantity.getText().toString());
        batteries = Integer.parseInt(batteriesQuantity.getText().toString());
        clothes = Integer.parseInt(clothesQuantity.getText().toString());
        oil = Integer.parseInt(oilQuantity.getText().toString());
        plastic = Integer.parseInt(plasticQuantity.getText().toString());



        //Adding the quantities of the items in the ArrayList
        tempQuantities.add(plastic);
        tempQuantities.add(glass);
        tempQuantities.add(paper);
        tempQuantities.add(aluminium);
        tempQuantities.add(oil);
        tempQuantities.add(electricDevices);
        tempQuantities.add(batteries);
        tempQuantities.add(clothes);


        //Parsing the points per material from the points.xml file and calculating the total points of the form
        //The order that the items are added in the ArrayList is the same as the order int points.xml file. E.g. ArrayList={Plastic, glass, etc}
        //points.xml = <points name="plasticPoints">30</points>
        //              <points name="glassPoints">50</points>
        //              etc.
        //Any changes in the order of the one must also be applied to the other one
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
                int pointsValue = Integer.parseInt(pointsElement.getTextContent().trim());
                totalPoints = totalPoints + tempQuantities.get(i) * pointsValue;
            }

        } catch (Exception e) {e.printStackTrace();}

        lite.insert_application_form(username,glass,paper,aluminium,electricDevices,batteries,clothes,oil,plastic,totalPoints);
        //Finnish the current activity and start the userProfile activity
        finish();
        sendMessage(v);

        Toast.makeText(getApplicationContext(), "Submitted form.", Toast.LENGTH_SHORT).show();
    }

    public void sendMessage(View view){
        // Starts the userProfile activity
        //
        // Parameters:
        //    view (View): The FormRegister view
        // Returns:
        Intent intent = new Intent(this, userProfile.class);
        intent.putExtra("username", username);
        startActivity(intent);

    }


}