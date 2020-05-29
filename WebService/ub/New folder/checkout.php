<?php
 
include 'config.php';
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
 if($_SERVER['REQUEST_METHOD'] == 'POST')
 { 
 $DefaultId = 0;
 if (isset ($_POST ['checkin_id'])) {$Checkin_id = $_POST ['checkin_id']; }
 if (isset ($_POST ['image_tag'])) {$ImageName = $_POST ['image_tag']; }
 if (isset ($_POST ['image_dat'])) {$ImageDat = $_POST ['image_dat']; }
 if (isset ($_POST ['process'])) {$Process = $_POST ['process']; }
 if (isset ($_POST ['size'])) {$Size = $_POST ['size']; }
 if (isset ($_POST ['people'])) {$People = $_POST ['people']; }
 if (isset ($_POST ['material'])) {$Material = $_POST ['material']; }

 $ImagePath = "imageCheckout/$ImageName.jpg";
 
 $ServerURL = "10.141.128.59/$ImagePath";
 
  $InsertSQL = "INSERT INTO check_out (checkin_id,image,process,evl_size,evl_material, evl_people) values('$Checkin_id','$ImageName','$Process','$Size','$People','$Material')";
 
if(mysqli_query($conn, $InsertSQL)){

 file_put_contents($ImagePath,base64_decode($ImageDat));

 echo "Your Image Has Been Uploaded.";
 }
	 
 mysqli_close($conn);
 }else{
 echo "Please Try Again";
 }
 
?>