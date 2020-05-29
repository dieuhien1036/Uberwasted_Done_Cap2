<?php
 
require_once 'include/db_connection.php';
global $lastest_pic_id;
global $connection;
$upload_path = 'uploads/'; //this is our upload folder
$server_ip = gethostbyname(gethostname()); //Getting the server ip
$upload_url = 'http://'.$server_ip.'/upload/'.$upload_path; //upload url
 
//response array
$response = array();
 
 
if($_SERVER['REQUEST_METHOD']=='POST'){
 
    //checking the required parameters from the request
    if(isset($_POST['caption']))
    {
         
        $caption = $_POST['caption'];
        $fileinfo = pathinfo($_FILES['image']['name']);//getting file info from the request
        $extension = $fileinfo['extension']; //getting the file extension
        $image_url = $upload_url . getFileName() . '.' . 'jpg'; //file url to store in the database
        $file_path = $upload_path . getFileName() . '.'. 'jpg'; //file path to upload in the server
        $image_name = getFileName() . '.'. $extension; //file name to store in the database
        $size = $_POST['size'];
        $material = $_POST['material'];
        $people = $_POST['people'];
        $wasteLocation_latitude = $_POST['wasteLocation_latitude'];
        $wasteLocation_longtitude = $_POST['wasteLocation_longtitude'];
        $wasteLocation_address = $_POST['wasteLocation_address'];
        $waste_date = $_POST['waste_date'];
 
        
        try{
            move_uploaded_file($_FILES['image']['tmp_name'],$file_path); //saving the file to the uploads folder;
           
            //adding the path and name to database

            //if some error occurred
        }catch(Exception $e){
            $response['error']=true;
            $response['message']=$e->getMessage();
        }
        //displaying the response
        echo json_encode($response);
 
        //closing the connection
        mysqli_close($connection);
    }else{
        $response['error'] = true;
        $response['message']='Please choose a file';
    }
}
 
/*
We are generating the file name
so this method will return a file name for the image to be uploaded
*/
function getFileName(){
    global $connection;
    global $lastest_pic_id;
     
    $sql = "SELECT max(waste_id) as id FROM waste_location";
    $result = mysqli_fetch_array(mysqli_query($connection, $sql));
 
    if($result['id']== null)
        return 1;
    else
        $lastest_pic_id = ++$result['id'];
        return $lastest_pic_id;
    mysqli_close($connection);
}

$myfile = fopen("Count.txt", "w") or die("Unable to open file!");
fwrite($myfile, $lastest_pic_id);
fclose($myfile);

shell_exec('C:\Users\Administrator\AppData\Local\Programs\Python\Python37\python.exe C:\xampp\htdocs\upload\hello.py');

    unlink('uploads/'.$lastest_pic_id.'.jpg');

//Mo file Output ra get ID voi % chinh xac bo vao 1,2 bien, sau do tim toi buc anh co id nhu the tren database, sau do lay value tu tren database xuong bo vao android la xong
// Doc file tu file txt ra duoc bien $idmax
//$idveclient = Select id from imageswasted where id=$idmax;

?>