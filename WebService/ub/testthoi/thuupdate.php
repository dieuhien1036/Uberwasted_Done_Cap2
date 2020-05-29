<?php 
	$connect = mysqli_connect("localhost","root","","uberwasted");
 	mysqli_query($connect,"SET NAMES 'utf8'");

 	$firstName = "dat";
 	$lastName = "nguye";
 	$gender = "0";
 	$email = "kdat036@gmail.com";
 	$birthDate = "1009-11-11";
 	$job = "dev";

 	$query = "UPDATE user SET user_firstName = '$firstName' , user_lastName = '$lastName', user_gender =b'$gender' , user_birthDate = '$birthDate' , user_job = '$job' where user_email= '$email' ";

 	mysqli_query($connect,$query);
 	if(mysqli_query($connect, $query)){
 		echo "Success";
 	}else{
 		echo "Fail";
 	}
 ?>