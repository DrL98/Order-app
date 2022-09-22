package library;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
 
import android.content.Context;
import android.util.Log;

public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://10.0.2.2/order/";
    private static String registerURL = "http://10.0.2.2/order/";
    private static String orderURL = "http://10.0.2.2/order/create_order.php";
    private static String feedbackURL = "http://10.0.2.2/order/create_feedback.php";

    private static String login_tag = "login";
    private static String register_tag = "register";
     
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        // return json
        //Log.e("JSON", json.toString());
        return json;
    }
     
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // return json
        return json;
    }


    /**
     *Function to mak an order
     *
     */
 //user_id, pid,product_price,qty,totalPrice
    public JSONObject makeOrder(String customer, String pid, String qty,String totalprice){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cid", customer));
        params.add(new BasicNameValuePair("orderTotal", totalprice));
        params.add(new BasicNameValuePair("pid", pid));
        params.add(new BasicNameValuePair("qty", qty));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(orderURL, params);
        // return json
        return json;
    }
         //send feedback

  //
    public JSONObject makeFeedback(String customer, String pid, String rating,String comment){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cid", customer));
        params.add(new BasicNameValuePair("comment", comment));
        params.add(new BasicNameValuePair("pid", pid));
        params.add(new BasicNameValuePair("rating", rating));
        Log.e("JSON", ""+customer + "-"+ pid + "- " + rating +"-" + comment);
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(feedbackURL, params);
        // return json
        return json;
    }











    /**
     * Function get Login status
     * */
    public static boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public static boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

    /*get user's unique identifier
    *
    * */
    public static String getUID(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        HashMap<String, String> uid = db.getUserDetails();
        String identifier = uid.get("uid");
        return identifier;
    }



}