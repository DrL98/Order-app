package com.example.order;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.UserFunctions;

public class ViewOrderFeedback extends Activity {
   // Progress Dialog
   private ProgressDialog pDialog;
    private ListView listview;
   // Creating JSON Parser object
   JSONParser jParser = new JSONParser();

   ArrayList<HashMap<String, String>> productsList;

   // url to get all products list
   private static String url_all_feedback = "http://10.0.2.2/order/get_all_feedback.php";

    // JSON Node names
   private static final String TAG_SUCCESS = "success";
   private static final String TAG_FEEDBACK= "feedback";
   //{"feedback":[{"order_id":"7","product":"Cronut","comment":"About to ship item","status":"2","notice":"0"}],"success":1}
   private static final String TAG_NAME = "name";
   private static final String TAG_OID = "orderNo";
   private static final String TAG_COMMENT = "comment";
   private static final String TAG_STATUS = "status";
   private static final String TAG_PRICE = "orderTotal";
   private static final String TAG_QTY = "quantity";
   //text views
    TextView tvoid;
    TextView tvpname;
    TextView tvpcost;
    TextView tvquantity;
    TextView tvostatus;

   //global variables
    private String user_id;
    private String order_id;

    private String fstatus;
    private String comment;
    private int success;

   // products JSONArray
   JSONArray orders = null;

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.view_feedback);
       Intent i = getIntent();
       //get extras
       order_id = i.getStringExtra(TAG_OID);
       String  order_name = i.getStringExtra(TAG_NAME);
       String  order_qty = i.getStringExtra(TAG_QTY);
       String  order_total = i.getStringExtra(TAG_PRICE);

       //processing text views
       tvpname = (TextView) findViewById(R.id.tvProduct);
       tvquantity = (TextView) findViewById(R.id.lblqty);
       tvpcost= (TextView) findViewById(R.id.lbltotalcost);


       //set new text for views
       tvpname.setText(order_name+"(#KM"+order_id+")");
       tvquantity.setText("Order Qty: "+order_qty);
       tvpcost.setText("Order Total: K"+order_total);
       // Hashmap for ListView
       productsList = new ArrayList<HashMap<String, String>>();
       // Loading products in Background Thread
       new LoadAllFeedback().execute();

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
   class LoadAllFeedback extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(ViewOrderFeedback.this);
           pDialog.setMessage("Checking Order. Please wait...");
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
          //add the uid to the request
           params.add(new BasicNameValuePair("oid",order_id));


           // getting JSON string from URL
           JSONObject json = jParser.makeHttpRequest(url_all_feedback, "POST", params);

           // Check your log cat for JSON reponse
          Log.d("All Products: ", json.toString());

           try {
               // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

               if (success == 1) {
                   // products found
                   // Getting Array of Products
                   orders = json.getJSONArray(TAG_FEEDBACK);

                   // looping through All Products
                   for (int i = 0; i < orders.length(); i++) {
                       JSONObject c = orders.getJSONObject(i);

                       // Storing each json item in variable

                       fstatus = c.getString(TAG_STATUS);
                       comment= c.getString(TAG_COMMENT);


                      /*
                       // creating new HashMap       HashMap<String, String> map = new HashMap<String, String>();

                       // adding each child node to HashMap key => value

                       map.put(TAG_NAME, name);
                       map.put(TAG_NOTICE, readNotice);
                       map.put(TAG_STATUS,fstatus);
                       map.put(TAG_COMMENT, comment);
                       map.put(TAG_OID,oid);
                       // adding HashList to ArrayList
                       productsList.add(map);
                        */

                   }
               } else {
                   //updates yet

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
                    * Updating parsed JSON data into given layout
                    * */
           //change status text

                   if (success == 1) {
                       //if updates
                       tvostatus = (TextView) findViewById(R.id.lblstatus);
                       tvostatus.setText("" + fstatus);
                   }
                   else
                   {
                       //if no updates


                   }


               }
           });

       }

   }

    //long press order item menu
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
