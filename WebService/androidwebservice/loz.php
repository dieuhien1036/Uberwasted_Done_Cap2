<?php

$servername = "localhost";
//Define your database username here.
$username = "root";
//Define your database password here.
$password = "";
//Define your database name here.
$dbname = "uberwasted";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

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

?>