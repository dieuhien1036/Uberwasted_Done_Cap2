<?php  

	class volunteers{
		function volunteers($join_id, $waste_id, $volunteer_id, $date){
			$this->join_id = $join_id;
			$this->waste_id = $waste_id;
			$this->volunteer_id = $volunteer_id;
			$this->date = $date;	
		}
	}

	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");
	$query = "SELECT * FROM join_clean";
	$data = mysqli_query($connect,$query);

	$array_volunteers = array();
	
	while($row = mysqli_fetch_assoc($data)){
		array_push($array_volunteers,new volunteers($row['join_id'],$row['waste_id'],$row['volunteer_id'],$row['date']));
	}

   	echo json_encode($array_volunteers);


?>
