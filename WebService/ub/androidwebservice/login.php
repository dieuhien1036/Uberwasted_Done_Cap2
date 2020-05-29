<?php  

	class volunteers{
		function volunteers($volunteer_id, $volunteer_firstName, $volunteer_lastName, $volunteer_gender, 
			$volunteer_birthDate, $volunteer_email, $volunteer_password, $volunteer_job, 
			$volunteer_image, $volunteer_score){
			$this->volunteer_id = $volunteer_id;
			$this->volunteer_firstName = $volunteer_firstName;
			$this->volunteer_lastName = $volunteer_lastName;
			$this->volunteer_gender = $volunteer_gender;	
			$this->volunteer_birthDate = $volunteer_birthDate;
			$this->volunteer_email = $volunteer_email;
			$this->volunteer_password = $volunteer_password;
			$this->volunteer_job = $volunteer_job;
			$this->volunteer_image = $volunteer_image;
			$this->volunteer_score = $volunteer_score;
			
		
		}
	}

	$connect = mysqli_connect("localhost","root","","uberwasted");
	mysqli_query($connect,"SET NAMES 'utf8'");
	$query = "SELECT * FROM volunteer";
	$data = mysqli_query($connect,$query);

	$array_volunteers = array();
	
	while($row = mysqli_fetch_assoc($data)){
		array_push($array_volunteers,new volunteers($row['volunteer_id'],$row['volunteer_firstName'],$row['volunteer_lastName'],$row['volunteer_gender'],$row['volunteer_birthDate'],$row['volunteer_email'],$row['volunteer_password'],$row['volunteer_job'],$row['volunteer_image'],$row['volunteer_score']));
	}
 	

   	echo json_encode($array_volunteers);


?>
