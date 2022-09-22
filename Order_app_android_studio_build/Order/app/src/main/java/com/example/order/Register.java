package com.example.order;
 
import library.DatabaseHandler;
import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.order.R;
 

 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class Register extends Activity {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
    private ProgressDialog pDialog;
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    UserFunctions userFunctions;
    private String res;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {        
            public void onClick(View view) {

                //test for missing information
                if( inputFullName.length() > 0 && inputEmail.length() > 0 && inputPassword.length() > 0)
                {
                    //run async task to register new account
                    new  RegisterHandler().execute();
                }
                else
                {
                    displayAlert("Please fill in all the fields tapping register", "Missing Field(s)");

                }

            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }

    //private async task class that handles the login attempt to the provided API or login URL
    private class RegisterHandler extends AsyncTask<Void, Void, Void> {
        //private Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Registering account...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //get the values of the username and password before posting them to the userFunctions class instance
            String name = inputFullName.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.registerUser(name, email, password);

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

                                // user successfully registered
                                // Store user details in SQLite Database
                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                JSONObject json_user = json.getJSONObject("user");
                                // Clear all previous data in database
                                userFunction.logoutUser(getApplicationContext());
                                //add newly created account to database
                                db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));

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
            if(res.matches("1")){//if success
                // user successfully created
                Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
                // Close all views before launching Dashboard
                dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dashboard);
                // Close Registration Screen
                finish();
            }else{

                //show alert of failed registration
                displayAlert("Failed to register new user", "Registration Failed");
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


}