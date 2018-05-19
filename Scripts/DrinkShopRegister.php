<?php
$name = $_POST['Name'];
$phone = $_POST['Phone'];
$bday =$_POST['bday'];
$address =$_POST['address'];
date_default_timezone_set('Asia/Kolkata');
$time = date('d-m-Y H:i');

include("DrinkShopConfig.php");


 	$log = @mysql_query("INSERT INTO DrinkUsers VALUES(null,'$name','$phone','$bday','$address','$time')");	

 	$response = array();

 	if($log){
 	
 	$response['success']=1;
 	$response['message']="Record Inserted Sucessfully";
 }

 else{
 	
 	 

 	$response['success']=0;
 	$response['message']="Failure";


 }

 
 echo json_encode($response);

?>