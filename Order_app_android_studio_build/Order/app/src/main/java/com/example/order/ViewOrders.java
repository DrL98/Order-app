package com.example.order;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.UserFunctions;

public class ViewOrders extends Activity {
   // Progress Dialog
   private ProgressDialog pDialog;
    private ListView listview;
   // Creating JSON Parser object
   JSONParser jParser = new JSONParser();

   ArrayList<HashMap<String, String>> productsList;

   // url to get all products list
   private static String url_all_orders = "http://10.0.2.2/order/get_all_orders.php";

    // JSON Node names
   private static final String TAG_SUCCESS = "success";
   private static final String TAG_ORDERS= "orders";
   private static final String TAG_PID = "pid";

   //"name":"Cronut","description":"Doughnut and Croissant","quantity":"2","orderTotal":"30.00"
   private static final String TAG_NAME = "name";
   private static final String TAG_PRICE = "orderTotal";
   private static final String TAG_DESC = "description";
   private static final String   TAG_QTY = "quantity";
   private static final String   TAG_OID = "orderNo";
   private String user_id;
   private String[] menuOptions = {"View Feedback","Product Details" , "Go Back"};

   // products JSONArray
   JSONArray orders = null;

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.all_orders);

       // Hashmap for ListView
       productsList = new ArrayList<HashMap<String, String>>();

       // Loading products in Background Thread
       new LoadAllOrders().execute();

       // Get listview


       // on seleting single product
       // launching Edit Product Screen

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
   class LoadAllOrders extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(ViewOrders.this);
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
           //get user id
           user_id = UserFunctions.getUID(ViewOrders.this);
          //add the uid to the request
           params.add(new BasicNameValuePair("uid",user_id));


           // getting JSON string from URL
           JSONObject json = jParser.makeHttpRequest(url_all_orders, "POST", params);

           // Check your log cat for JSON reponse
          // Log.d("All Products: ", json.toString());

           try {
               // Checking for SUCCESS TAG
               int success = json.getInt(TAG_SUCCESS);

               if (success == 1) {
                   // products found
                   // Getting Array of Products
                   orders = json.getJSONArray(TAG_ORDERS);

                   // looping through All Products
                   for (int i = 0; i < orders.length(); i++) {
                       JSONObject c = orders.getJSONObject(i);

                       // Storing each json item in variable
                      // String id = c.getString(TAG_PID);
                       String name = c.getString(TAG_NAME);
                       String price = c.getString(TAG_PRICE);
                       String description = c.getString(TAG_DESC);
                       String quantity = c.getString(TAG_QTY);
                       String pid= c.getString(TAG_PID);
                       String orderId= c.getString(TAG_OID);

                       // creating new HashMap
                       HashMap<String, String> map = new HashMap<String, String>();

                       // adding each child node to HashMap key => value

                       map.put(TAG_NAME, name);//product name
                       map.put(TAG_PRICE, price);//product price
                       map.put(TAG_DESC,description);
                       map.put(TAG_QTY, quantity);
                       map.put(TAG_PID, pid);
                       map.put(TAG_OID, orderId);
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

                   listview = (ListView) findViewById(R.id.orderslist);
                   ListAdapter adapter = new SimpleAdapter(
                           ViewOrders.this, productsList,
                           R.layout.order_item, new String[] { TAG_DESC,
                                   TAG_NAME,TAG_PRICE,TAG_QTY},
                           new int[] { R.id.pid, R.id.opname,R.id.opprice ,R.id.opqty});
                   // updating listview
                   listview.setAdapter(adapter);
                   //register menu
                   registerForContextMenu(listview);
               }
           });

       }

   }

    //long press order item menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId()==R.id.orderslist)
        {
            menu.setHeaderTitle("Item Options");
            for (int i = 0; i< menuOptions.length; i++)
            {
                menu.add(Menu.NONE, i, i, menuOptions[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        // Getting the Id
        int menuItemIndex = item.getItemId();
        int listPosition = info.position;


        //cancel reservation
        if(menuItemIndex == 0)

        {
            //get product details before making feedback post
            Intent in = new Intent(getApplicationContext(),ViewOrderFeedback.class);
            // sending pid to next activity
            in.putExtra(TAG_OID, productsList.get(listPosition).get(TAG_OID));
            in.putExtra(TAG_NAME, productsList.get(listPosition).get(TAG_NAME));
            in.putExtra(TAG_PRICE, productsList.get(listPosition).get(TAG_PRICE));
            in.putExtra(TAG_QTY, productsList.get(listPosition).get(TAG_QTY));
            startActivity(in);
            finish();
        }

        else
            //view movie information
            if(menuItemIndex == 1)
            {
                //display a summary
              previewDialog(productsList.get(listPosition).get(TAG_DESC), "Product Description");
            }        else
            //view movie information
            if(menuItemIndex == 2)
            {

                //do nothing

            }
        //Toast.makeText(UserAccount.this, "Clicked Item Position :"+info.position+"\n"+"Seleted Option Id :"+menuItemIndex, Toast.LENGTH_SHORT).show();
        return true;
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

    public void previewDialog(String message, String title)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setTitle(title)
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                            }
                        })
                .show();
    }

}
