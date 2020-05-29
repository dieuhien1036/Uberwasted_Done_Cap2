<?php
 
include 'config.php';
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
 if($_SERVER['REQUEST_METHOD'] == 'POST')
 { 
 $DefaultId = 0;
 if (isset ($_POST ['join_id'])) {$Join_id = $_POST ['join_id']; }
 if (isset ($_POST ['image_tag'])) {$ImageName = $_POST ['image_tag']; }
 if (isset ($_POST ['image_dat'])) {$ImageDat = $_POST ['image_dat']; }
 $ImagePath = "imageCheckin/$ImageName.jpg";

 $ServerURL = "192.168.43.112/$ImagePath";
 
 $InsertSQL = "INSERT INTO check_in (join_id,image) values('$Join_id','$ImageName.jpg')";
 
if(mysqli_query($conn, $InsertSQL)){

 file_put_contents($ImagePath,base64_decode($ImageDat));

 echo "Your Image Has Been Uploaded.";
 }
	 
 mysqli_close($conn);
 }else{
 echo "Please Try Again";
 }

?>