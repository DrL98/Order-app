package com.example.order;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import library.UserFunctions;

public class MainActivity extends ActionBarActivity {

    Button btnViewProducts;
    Button btnViewOrders;
    Button btnViewFeedback;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
 
        // Buttons
        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnViewOrders = (Button) findViewById(R.id.btnViewOrders);


        // view products click event
        btnViewProducts.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                startActivity(i);
 
            }
        });
 
        // view products click event
       btnViewOrders.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), ViewOrders.class);
                startActivity(i);
 
            }
        });
    }

    //menu implementation

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            //use switch statement to handle various menu actions
            //switch to settings page
            case R.id.action_settings:
                //access settings

                return true;
            case R.id.action_orders:
                //access your orders

                return true;
            case R.id.action_logout: //access logout function
                //clear current session then
                UserFunctions.logoutUser(this);
                //redirect to the login page after successful logout
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //end menu implement


}