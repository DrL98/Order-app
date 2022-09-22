package com.example.order;
import library.DatabaseHandler;
import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 

 
public class Login extends Activity {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
    private ProgressDialog pDialog;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private String res;
    private String user_name = "";
    private String user_email = "";
    private String user_uid = "";
    private String user_created = "";

    UserFunctions userFunctions;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFunctions = new UserFunctions();

        //test login status to determine the landing page
       if(UserFunctions.isUserLoggedIn(this) == false)
       {
           //render the layout of the login activity
           setContentView(R.layout.login);
           // Importing all assets like buttons, text fields
           inputEmail = (EditText) findViewById(R.id.loginEmail);
           inputPassword = (EditText) findViewById(R.id.loginPassword);
           btnLogin = (Button) findViewById(R.id.btnLogin);
           btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

           // Login button Click Event
           btnLogin.setOnClickListener(new View.OnClickListener() {

               public void onClick(View view) {
                   //execute async task to avoid processing on main thread
                   new LoginHandler().execute();
               }
           });


           // Link to Register Screen
           btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

               public void onClick(View view) {
                   Intent i = new Intent(getApplicationContext(),
                           Register.class);
                   startActivity(i);
                   finish();
               }
           });

       }

       else {

           //redirect to the dashboard or main activity
           Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
           dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(dashboard);
           finish();



       }



    }

    //private async task class that handles the login attempt to the provided API or login URL
    private class LoginHandler extends AsyncTask<Void, Void, Void> {
        //private Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //get the values of the username and password before posting them to the userFunctions class instance
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            //Construct array to contain email address and password before making the call to the API
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);

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
                                JSONObject json_user = json.getJSONObject("user");
                                user_name = json_user.getString(KEY_NAME);//name
                                user_email = json_user.getString(KEY_EMAIL);//email address
                                user_uid = json.getString(KEY_UID);//unique identifier
                                user_created =  json_user.getString(KEY_CREATED_AT);//date created
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
                // user successfully logged in
                // Store user details in SQLite Database
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                // Clear all previous data in database
                UserFunctions userFunctions = new UserFunctions();
                userFunctions.logoutUser(getApplicationContext());
                db.addUser(user_name, user_email, user_uid, user_created);
                // Launch Dashboard Screen
                Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
                // Close all views before launching Dashboard
                dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dashboard);
                // Close Login Screen
                finish();
            }else{
                //show alert of incorrect details
                displayAlert("Incorrect username/password", "Password/Username Incorrect");
            }
        }
    }
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


}