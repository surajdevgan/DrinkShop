<?php

$phone = $_POST['Phone'];

include("DrinkShopConfig.php");
$result = @mysql_query("SELECT * from DrinkUsers where PHONE='$phone'");

$response = array();

if(@mysql_num_rows($result)>0){

 	$response['students'] = array();


while($row=@mysql_fetch_array($result)){

			array_push($response['students'], $row);

		}



 	$response['message']="Already registered";

 }

  else{

 
 	
 	$response['message']="Login Fail";
 

 


 }

 
 echo json_encode($response);

?>