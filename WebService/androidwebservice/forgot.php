<?php
$connect = mysqli_connect("localhost","root","","uberwasted");
 mysqli_query($connect,"SET NAMES 'utf8'");

	$emailName = $_POST['emailName'];
	$newPassword = $_POST['newPassword'];

$query = "UPDATE volunteer SET volunteer_password = '$newPassword' where volunteer_email = '$emailName' ";

if(mysqli_query($connect,$query))
	{
		echo "SUCCESS";
	} 
else
	{
		echo "ERROR";
	}
?>