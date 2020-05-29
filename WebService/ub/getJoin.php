<?php

	//tạo kết nối
	include 'config.php';
// Create connection
	$connect = new mysqli($servername, $username, $password, $dbname);
 	//truy xuất
 	$query = "SELECT * FROM join_clean"; 
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class Join{
		function Join($join_id,$waste_id, $volunteer_id){
			$this->join_id = $join_id;
			$this->waste_id= $waste_id;
			$this->volunteer_id = $volunteer_id;
	}
}

	$arrayVolunteer = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayVolunteer, new Join($row['join_id'],$row['waste_id'],$row['volunteer_id']));
 	}
	//convert sang json
	echo json_encode($arrayVolunteer); 
?>