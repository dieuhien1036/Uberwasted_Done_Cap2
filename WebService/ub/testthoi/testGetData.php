<?php

	//tạo kết nối
 	$connect = mysqli_connect("localhost","root","","uberwasted");
 	mysqli_query($connect,"SET NAMES 'utf8'");
 	//truy xuất
 	$query = "SELECT * FROM user";
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class User{
		function User($id,$firstName, $lastName, $gender ,$birthDate,$email ,$password,  $job , $image , $score , $user_id){
			$this->Id = $id;
			$this->FirstName= $firstName;
			$this->LastName = $lastName;
			$this->Gender= $gender;
			$this->Email = $email;
			$this->Password = $password;
			$this->birthDate = $birthDate;
			$this->Job = $job;
			$this->Image = $image;
			$this->Score = $score;
			$this->User_id = $user_id;
		}
	}

	$arrayUser = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayUser, new User($row['user_id']),$row['user_firstName'],$row['user_lastName'],$row['user_gender'],$row['user_birthDate'],$row['user_email'],$row['user_password'],$row['user_job'],$row['user_image'],$row['user_score9']);
 	}
	//convert sang json
	echo json_encode($arrayUser); 
?>