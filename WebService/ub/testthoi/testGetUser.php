<?php
 	$connect = mysqli_connect("localhost","root","","uberwasted");
 	mysqli_query($connect,"SET NAMES 'utf8'");
 	//truy xuất
 	$query = "SELECT * FROM user";
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class User{
		function User($id,$firstName, $lastName, $age){
			$this->Id = $id;
			$this->FirstName= $firstName;
			$this->LastName = $lastName;
			$this->Gender= $age;
		}
	}

	$arrayUser = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayUser, new User($row['id'],$row['last_name'],$row['first_name'],$row['age']));
 	}
	//convert sang json
	echo json_encode($arrayUser); 
?>