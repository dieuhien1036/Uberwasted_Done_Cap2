<?php

	//tạo kết nối
	include 'config.php';
// Create connection
	$connect = new mysqli($servername, $username, $password, $dbname);
 	//truy xuất
 	$query = "SELECT * FROM volunteer Order by volunteer_score DESC"; 
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class Volunteer{
		function Volunteer($id,$firstName, $lastName, $gender ,$birthDate,$email ,$password,  $job , $image , $score ){
			$this->volunteer_id = $id;
			$this->volunteer_firstName= $firstName;
			$this->volunteer_lastName = $lastName;
			$this->volunteer_gender= $gender;
			$this->volunteer_birthDate = $birthDate;
			$this->volunteer_email = $email;
			$this->volunteer_password = $password;
			$this->volunteer_job = $job;
			$this->volunteer_image = $image;
			$this->volunteer_score = $score;
		}
	}

	$arrayVolunteer = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayVolunteer, new Volunteer($row['volunteer_id'],$row['volunteer_firstName'],$row['volunteer_lastName'],$row['volunteer_gender'],$row['volunteer_birthDate'],$row['volunteer_email'],$row['volunteer_password'],$row['volunteer_job'],$row['volunteer_image'],$row['volunteer_score']));
 	}
	//convert sang json
	echo json_encode($arrayVolunteer); 
?>