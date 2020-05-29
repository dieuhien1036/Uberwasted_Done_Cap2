<?php
	
	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");


	$waste_id = $_POST['waste_id'];
	$volunteer_id = $_POST['volunteer_id'];
	$date = $_POST['date'];


	$query = "INSERT INTO join_clean VALUES(null,'$waste_id', 
					'$volunteer_id', '$date')";

	if(mysqli_query($connect,$query)){
		echo "success";	
	}else{
		echo "fail";
	}
?>