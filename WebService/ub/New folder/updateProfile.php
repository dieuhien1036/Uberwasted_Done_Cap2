<?php 
	//tạo kết nối
	include 'config.php';
// Create connection
	$connect = new mysqli($servername, $username, $password, $dbname);

 	$firstName = $_POST['firstName'];
 	$lastName = $_POST['lastName'];
 	$gender = $_POST['gender'];
 	$id = $_POST['id'];
 	$birthDate = $_POST['birthDate'];
 	$job = $_POST['job'];
 	// $firstName = 'hien';
 	// $lastName = 'dieu';
 	// $gender = 'nu';
 	// $id = '3';
 	// $birthDate = '2005/12/12';
 	// $job = 'Student';

 	$query = "UPDATE volunteer SET volunteer_firstName = '$firstName' , volunteer_lastName = '$lastName', volunteer_gender ='$gender' ,volunteer_birthDate = '$birthDate' , volunteer_job = '$job' where volunteer_id= '$id' ";

 	mysqli_query($connect,$query);
 	if(mysqli_query($connect, $query)){
 		echo "Success";
 	}else{
 		echo "Fail";
 	}
 ?>