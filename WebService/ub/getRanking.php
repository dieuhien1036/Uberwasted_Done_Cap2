<?php

	//tạo kết nối
	include 'config.php';
// Create connection
	$connect = new mysqli($servername, $username, $password, $dbname);
 	//truy xuất
 	$query = "SELECT  volunteer.volunteer_lastName,volunteer.volunteer_job, SUM(ranking.score) as score1 from volunteer, ranking where ranking.volunteer_id = volunteer.volunteer_id GROUP BY volunteer.volunteer_id ORDER BY score1 DESC";
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class Ranking{
		function Ranking($lname,$job, $score){
			$this->volunteer_lastName = $lname;
			$this->volunteer_job= $job;
			$this->volunteer_score = $score;
	}
}

	$arrayVolunteer = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayVolunteer, new Ranking($row['volunteer_lastName'],$row['volunteer_job'],$row['score1']));
 	}
	//convert sang json
	echo json_encode($arrayVolunteer); 
?>