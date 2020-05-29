<?php

	//tạo kết nối
 	$connect = mysqli_connect("localhost","root","","uberwasted");
 	mysqli_query($connect,"SET NAMES 'utf8'");
 	//truy xuất
 	$query = "SELECT * FROM waste_location";
 	$data= mysqli_query($connect,$query);
 	//tạo class
 	class Waste_Location{
		function Waste_Location($waste_id,$waste_latitute, $waste_longtitude, $waste_adress ,$waste_image ,$waste_size,  $waste_material , $waste_people , $waste_code,$waste_date,$waste_detail){
			$this->waste_id = $waste_id;
			$this->waste_latitute= $waste_latitute;
			$this->waste_longtitude = $waste_longtitude;
			$this->waste_adress= $waste_adress;
			$this->waste_image = $waste_image;
			$this->waste_size = $waste_size;
			$this->waste_material = $waste_material;
			$this->waste_people = $waste_people;
			$this->waste_code = $waste_code;
			$this->waste_date = $waste_date;
			$this->waste_detail = $waste_detail;

		}
	}
	$arrayWaste = array();

 	while($row = mysqli_fetch_assoc($data)){
 		array_push($arrayWaste, new Waste_Location($row['waste_id'],$row['waste_latitute'],$row['waste_longtitude'],$row['waste_adress'],$row['waste_image'],$row['waste_size'],$row['waste_material'],$row['waste_people'],$row['waste_code'],$row['waste_date'],$row['waste_detail']));
 	}
	//convert sang json
	echo json_encode($arrayWaste); 
?>