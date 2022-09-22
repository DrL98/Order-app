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
 
 
if (isset($_POST['uid']) && $_POST['uid'] != '') {
    // get tag
    $tag = $_POST['uid'];
 
// get all products from products table
$result = mysql_query("SELECT 	
o.order_id,
o.pid,
o.customer_id,
o.quantity,
o.orderTotal,
p.name,
p.description,
p.price,
of.o_comment,
of.o_status,
of.notice_status
FROM orders o, products p,order_feedback of WHERE p.pid=o.pid AND customer_id=".$tag."") or die(mysql_error());
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["orders"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
		$product["pid"] = $row["pid"];
		$product["name"] = $row["name"];
		$product["orderNo"] = $row["order_id"];
		$product["description"] = $row["description"];
		$product["quantity"] = $row["quantity"];
		$product["orderTotal"] = $row["orderTotal"];
        // push single product into final response array
        array_push($response["orders"], $product);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No orders found";
 
    // echo no users JSON
    echo json_encode($response);
}
}
else {
        echo "Invalid Request";
    }
?>