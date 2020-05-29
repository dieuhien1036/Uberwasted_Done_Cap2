<?php
 
include 'config.php';
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);


 	$Join_id = $_POST['join_id'];
 	$ImageName = $_POST['image_tag'];
 	$ImageDat = $_POST['image_dat'];
 $ImagePath = "imageCheckin/$ImageName.jpg";

 $ServerURL = "192.168.1.6/$ImagePath";
 
 $InsertSQL = "INSERT INTO check_in (join_id,image) values('$Join_id','$ImageName')";
 
if(mysqli_query($conn, $InsertSQL)){

 file_put_contents($ImagePath,base64_decode($ImageDat));

 echo "Your Image Has Been Uploaded.";
 	
	 
 mysqli_close($conn);
 }else{
 echo "Please Try Again";
 }

?>