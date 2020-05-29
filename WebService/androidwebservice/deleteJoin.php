<?php
	
	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");


	$join_id = $_POST['join_id'];

	$query = "DELETE FROM join_clean WHERE join_id = '$join_id'";

	if(mysqli_query($connect,$query)){
		echo "success";	
	}else{
		echo "fail";
	}
?>