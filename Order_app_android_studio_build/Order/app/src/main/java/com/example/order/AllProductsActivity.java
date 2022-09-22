package com.example.order;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import library.UserFunctions;

public class AllProductsActivity extends ListActivity {
   // Progress Dialog
   private ProgressDialog pDialog;

   // Creating JSON Parser object
   JSONParser jParser = new JSONParser();

   ArrayList<HashMap<String, String>> productsList;

   // url to get all products list
   private static String url_all_products = "http://10.0.2.2/order/get_all_products.php";

    // JSON Node names
   private static final String TAG_SUCCESS = "success";
   private static final String TAG_PRODUCTS = "products";
   private static final String TAG_PID = "pid";
   private static final String TAG_NAME = "name";
   private static final String TAG_PRICE = "price";


   // products JSONArray
   JSONArray products = null;

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.all_products);

       // Hashmap for ListView
       productsList = new ArrayList<HashMap<String, String>>();

       // Loading products in Background Thread
       new LoadAllProducts().execute();

       // Get listview
       ListView lv = getListView();

       // on seleting single product
       // launching Edit Product Screen
       lv.setOnItemClickListener(new OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view,
                   int position, long id) {
               // getting values from selected ListItem
               String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                       .toString();//product id
               String pname = ((TextView) view.findViewById(R.id.name)).getText()
                       .toString();//product name
               String pprice = ((TextView) view.findViewById(R.id.item_price)).getText()
                       .toString();//product price

               // Starting new intent
               Intent in = new Intent(getApplicationContext(),PlaceOrder.class);
               // sending pid to next activity
               in.putExtra(TAG_PID, pid);
               in.putExtra(TAG_NAME, pname);
               in.putExtra(TAG_PRICE, pprice);
               // starting new activity and expecting some response back
               startActivity(in);
           }
       });

   }

   // Response from Edit Product Activity
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       // if result code 100
       if (resultCode == 100) {
           // if result code 100 is received
           // means user edited/deleted product
           // reload this screen again
           Intent intent = getIntent();
           finish();
           startActivity(intent);
       }

   }

   /**
    * Background Async Task to Load all product by making HTTP Request
    * */
   class LoadAllProducts extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(AllProductsActivity.this);
           pDialog.setMessage("Loading Orders. Please wait...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(false);
           pDialog.show();
       }

       /**
        * getting All Orders from url
        * */
       protected String doInBackground(String... args) {
           // Building Parameters
           List<NameValuePair> params = new ArrayList<NameValuePair>();
           // getting JSON string from URL
           JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

           // Check your log cat for JSON reponse
           Log.d("All Products: ", json.toString());

           try {
               // Checking for SUCCESS TAG
               int success = json.getInt(TAG_SUCCESS);

               if (success == 1) {
                   // products found
                   // Getting Array of Products
                   products = json.getJSONArray(TAG_PRODUCTS);

                   // looping through All Products
                   for (int i = 0; i < products.length(); i++) {
                       JSONObject c = products.getJSONObject(i);

                       // Storing each json item in variable
                       String id = c.getString(TAG_PID);
                       String name = c.getString(TAG_NAME);
                       String price = c.getString(TAG_PRICE);


                       // creating new HashMap
                       HashMap<String, String> map = new HashMap<String, String>();

                       // adding each child node to HashMap key => value
                       map.put(TAG_PID, id);//product unique identification
                       map.put(TAG_NAME, name);//product name
                       map.put(TAG_PRICE, price);//product price

                       // adding HashList to ArrayList
                       productsList.add(map);
                   }
               } else {
                   // no products found
                   // Launch Add New product Activity
                   Intent i = new Intent(getApplicationContext(),
                           NewProductActivity.class);
                   // Closing all previous activities
                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(i);
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           return null;
       }

       /**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String file_url) {
           // dismiss the dialog after getting all products
           pDialog.dismiss();
           // updating UI from Background Thread
           runOnUiThread(new Runnable() {
               public void run() {
                   /**
                    * Updating parsed JSON data into ListView
                    * */
                   ListAdapter adapter = new SimpleAdapter(
                           AllProductsActivity.this, productsList,
                           R.layout.list_item, new String[] { TAG_PID,
                                   TAG_NAME,TAG_PRICE},
                           new int[] { R.id.pid, R.id.name,R.id.item_price });
                   // updating listview
                   setListAdapter(adapter);
               }
           });

       }

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
