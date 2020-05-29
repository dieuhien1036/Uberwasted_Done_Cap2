<?php  

	class volunteers{
		function volunteers($checkin_id, $join_id, $image){
			$this->checkin_id = $checkin_id;
			$this->join_id = $join_id;
			$this->image = $image;	
		}
	}

	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");
	$query = "SELECT * FROM check_in";
	$data = mysqli_query($connect,$query);

	$array_volunteers = array();
	
	while($row = mysqli_fetch_assoc($data)){
		array_push($array_volunteers,new volunteers($row['checkin_id'],$row['join_id'],$row['image']));
	}

   	echo json_encode($array_volunteers);


?>
