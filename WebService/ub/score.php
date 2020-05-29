<?php 
	//tạo kết nối
	include 'config.php';
// Create connection
	$connect = new mysqli($servername, $username, $password, $dbname);

 	$volunteer_id = $_POST['volunteer_id'];
 	$score = $_POST['score'];


 	$query = "Insert INTO ranking(volunteer_id,score) VALUES ('$volunteer_id','$score')";

 	if(mysqli_query($connect, $query)){
 		echo "Success";
 	}else{
 		echo "Fail";
 	}
 ?>