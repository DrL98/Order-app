package com.example.order;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import library.DatabaseHandler;
import library.UserFunctions;

public class PlaceOrder extends Activity {

   // Progress Dialog
   private ProgressDialog pDialog;

   JSONParser jsonParser = new JSONParser();
   TextView tvpid;
   TextView tvpname;
   TextView tvpprice;
   EditText edinputqty;
    UserFunctions userFunctions;

   // url to create new product
   private static String url_create_product = "http://10.0.2.2/b-tap/order/create_order.php";

   // JSON Node names
   private static final String TAG_PID = "pid";
   private static final String TAG_NAME = "name";
   private static final String TAG_PRICE = "price";
   private static String KEY_SUCCESS = "success";
   private static String KEY_ERROR = "error";
   private static String KEY_ERROR_MSG = "error_msg";
   private static String KEY_UID = "uid";
   private static String KEY_NAME = "name";
   private static String KEY_EMAIL = "email";
   private static String KEY_CREATED_AT = "created_at";
    private String res;
    private String pid;
    private String product_name;
    private String product_price;
    private String user_id;
    private String totalPrice;
    private int qty;
    @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.order_product);
       //get requested item details
       Intent i = getIntent();
       // getting product id (pid) from intent
       pid = i.getStringExtra(TAG_PID);
       product_name = i.getStringExtra(TAG_NAME);
       product_price = i.getStringExtra(TAG_PRICE);
       user_id = UserFunctions.getUID(PlaceOrder.this);









       // Edit Text
       tvpid = (TextView) findViewById(R.id.pid);
       tvpname = (TextView) findViewById(R.id.tvProductName);
       tvpprice = (TextView) findViewById(R.id.tvPrice);


       //set the text in the appropriate text views

       tvpid.setText(pid);
       tvpname.setText(product_name);
       tvpprice.setText(product_price);


        edinputqty = (EditText) findViewById(R.id.inputQty);
        edinputqty.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {

                //on change recalculate the total

                String wordquantity = edinputqty.getText().toString();
                if(wordquantity.length() > 0)
                {
                    int itemQty = Integer.valueOf(wordquantity);

                    if (itemQty > 0 && itemQty < 101) {
                        float newPrice = Float.valueOf(product_price) * Float.valueOf(itemQty);
                        DecimalFormat form = new DecimalFormat ("0.00");
                        String Formattedprice = form.format(newPrice);
                        tvpprice.setText(Formattedprice);

                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });













        // Create button
       Button btnOrderProduct = (Button) findViewById(R.id.btnOrderProduct);

       // button click event
       btnOrderProduct.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
               //don quantity calculations




               String edqty = edinputqty.getText().toString();

               if(edqty.length() > 0)
               {

                   qty = Integer.valueOf(edqty);

                   if (qty > 0 && qty < 101) {
                       //convert the order quantity and calculate total price for product

                       float calculateTotal = Float.valueOf(product_price) * Float.valueOf(qty);
                       DecimalFormat form = new DecimalFormat ("0.00");
                       totalPrice = form.format(calculateTotal);

                       //correct
                       new makeOrder().execute();

                   } else {
                       //output error and prevent calculation
                       displayAlert("Quantity has to be 1-100", "Invalid Quantity");
                   }
               }


           }
       });
   }

    //
    private class makeOrder extends AsyncTask<Void, Void, Void> {
        //private Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PlaceOrder.this);
            pDialog.setMessage("Placing order...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //calculate the new total cost before posting to the database

            userFunctions = new UserFunctions();
            JSONObject json = userFunctions.makeOrder(user_id, pid,String.valueOf(qty),totalPrice);

            if (json != null)
            {
                //log the login response
                //Log.d("call_response: ", json.toString());
                try {

                    //handle server response validation and local db storage
                    try {
                        if (json.getString(KEY_SUCCESS) != null) {

                            res = json.getString(KEY_SUCCESS);
                            //grab details of user if login passed
                            if(res.matches("1")){

                                //create new object to capture user details


                            }
                        }
                    } catch (JSONException e) {

                        res = "0";
                        e.printStackTrace();
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                //Log any errors that occur.
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            //}
            return null;
        }
        //

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            //clean up and handle action after AsyncTask is done
            //if(Integer.parseInt(res) == 1){
            if(res.matches("1")){//if success
                // if order successful
                finishAlert("Your order was successfully placed", "Order Complete");
                //finish();
            }else{
                //show alert of incorrect details
                displayAlert("An error occurred", "Problem making order");

            }
        }
    }
    //
    public void displayAlert(String message, String title)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setTitle(title)
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                //Do nothing

                            }
                        })
                .show();
    }
   //


    public void finishAlert(String message, String title)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setTitle(title)
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();

                            }
                        })
                .show();
    }






    ///

}