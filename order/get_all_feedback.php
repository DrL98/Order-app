<?php
 
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
 
if (isset($_POST['oid']) && $_POST['oid'] != '') {
    // get tag
    $tag = $_POST['oid'];
 
// get all products from products table
$result = mysql_query("SELECT 	
o.order_id,
p.name,
o.customer_id,
of.o_comment,
of.o_status,
of.notice_status
FROM orders o, order_feedback  of,products p WHERE of.order_id=o.order_id AND o.pid = p.pid AND o.order_id=".$tag."") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["feedback"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
		$product["comment"] = $row["o_comment"];
		$order_status = $row["o_status"];
		//condition the response type
		if($order_status == 1)
		{
	        $product["status"] = "Order Processing";
		}
        else if($order_status == 2) 
		{
			$product["status"] = "Order Confirmed";
		}
		else
		{
			$product["status"] = "Unknown status";
		}
		
        // push single product into final response array
        array_push($response["feedback"], $product);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No Feedback";
 
    // echo no users JSON
    echo json_encode($response);
}
}
else {
        echo "Invalid Request";
    }
?>