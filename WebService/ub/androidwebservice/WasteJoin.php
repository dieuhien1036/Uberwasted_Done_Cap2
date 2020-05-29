	<?php class volunteers{
		function volunteers($join_id, $volunteer_id, $waste_latitude, $waste_longtitude,$waste_id,$waste_address){
			$this->join_id = $join_id;
			$this->volunteer_id = $volunteer_id;
			$this->waste_latitude = $waste_latitude;
			$this->waste_longtitude = $waste_longtitude;	
			$this->waste_id = $waste_id;	
			$this->waste_address = $waste_address;	
		}
	}

	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");



	$query = "SELECT  join_clean.join_id,join_clean.volunteer_id, waste_location.waste_latitude,waste_location.waste_longtitude,waste_location.waste_id,waste_location.waste_address from join_clean, volunteer,waste_location WHERE join_clean.waste_id = waste_location.waste_id   and waste_location.waste_code=0
		GROUP BY join_clean.join_id
		ORDER BY join_clean.volunteer_id";
	
	$data = mysqli_query($connect,$query);
	$array_volunteers = array();
	
	while($row = mysqli_fetch_assoc($data)){
		array_push($array_volunteers,new volunteers($row['join_id'],$row['volunteer_id'],$row['waste_latitude'],$row['waste_longtitude'],$row['waste_id'],$row['waste_address']));
	}
 	

   	echo json_encode($array_volunteers);
   	?>