<?php
	if(isset($_POST['firstname'],$_POST['lastname'],$_POST['email'],$_POST['password'])) {
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$email = $_POST['email'];
		$password = $_POST['password'];
		
		if(!empty($firstname) && !empty($lastname) && !empty($email) && !empty($password)){
			$encrypted_password = md5($password);
			define('HOST','localhost');
        	define('USER','root');
       		define('PASS','');
	    	define('DB','uberwasted');
        	$con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');
			$query = "insert into volunteer (volunteer_firstName,volunteer_lastName,volunteer_email, volunteer_password) values ( '$firstname','$lastname','$email','$encrypted_password')";
			if(mysqli_query($con,$query) ){
				$json['success'] = 'Create success';
			}else{
				//$json['error'] = 'Create fail';
				$json['error'] = 'The Email already exists';
		}
		echo json_encode($json);
		mysqli_close($con);			
		}else{
			$json['type'] = 'you must type all inputs';
			echo json_encode($json);
		}	
	}
?>
