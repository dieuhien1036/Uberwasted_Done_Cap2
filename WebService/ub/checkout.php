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
 if (isset ($_POST ['waste_id'])) {$Waste_id = $_POST ['waste_id']; }

 $ImagePath = "imageCheckout/$ImageName.jpg";
 
 $ServerURL = "192.168.43.112/$ImagePath";
 
  $InsertSQL = "INSERT INTO check_out (checkin_id,image,process,evl_size,evl_material, evl_people) values('$Checkin_id','$ImageName.jpg','$Process','$Size','$People','$Material')";
/*
$x = 2;

$sql1 = mysqli_query($conn,'Select count(join_clean.volunteer_id) as ppck
from check_out join check_in on check_out.checkin_id = check_in.checkin_id join join_clean on join_clean.join_id = check_in.join_id 
where check_out.process >= 20 and join_clean.waste_id= '.$x.'');

    $y = mysqli_fetch_assoc($sql1);

	$sql2 = mysqli_query($conn,'select waste_people as numpp
	from waste_location
	where waste_id = '.$x.'');

    $z = mysqli_fetch_assoc($sql2);

if($z['numpp'] == $y['ppck'])
{
	$sql3 = 'Update waste_location set waste_code = 1 where waste_id ='.$x.'';
	mysqli_query($conn, $sql3);
} 
*/
if(mysqli_query($conn, $InsertSQL)){

 file_put_contents($ImagePath,base64_decode($ImageDat));
 $x = $Waste_id;

$sql1 = mysqli_query($conn,'Select count(join_clean.volunteer_id) as ppck
from check_out join check_in on check_out.checkin_id = check_in.checkin_id join join_clean on join_clean.join_id = check_in.join_id 
where check_out.process >= 20 and join_clean.waste_id= '.$x.'');

    $y = mysqli_fetch_assoc($sql1);

	$sql2 = mysqli_query($conn,'select waste_people as numpp
	from waste_location
	where waste_id = '.$x.'');

    $z = mysqli_fetch_assoc($sql2);

	if($z['numpp'] == $y['ppck'])
	{
		$sql3 = 'Update waste_location set waste_code = 1 where waste_id ='.$x.'';
		mysqli_query($conn, $sql3);
	} 
 echo "Your Image Has Been Uploaded.";
 }
	 
 mysqli_close($conn);
 }else{
 echo "Please Try Again";
 }
 
?>