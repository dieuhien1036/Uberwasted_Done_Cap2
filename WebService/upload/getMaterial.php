<?php

require_once 'include/db_connection.php';

$myfile = fopen("Pos.txt", "r") or die("Unable to open file!");
$Inum = fgets($myfile);
fclose($myfile);

$query = 'select waste_material from waste_location where waste_id = "'.$Inum.'"';

//$query = "select material from imageswasted where id = 1";

$result = $connection->query($query);

$arrayofstats = array();

//array_push($arrayofstats, $row["people"],$row["size"],$row["material"]);


if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		array_push($arrayofstats, $row["waste_material"]);
    }
}
//unlink('uploads/'.$LastestPicId.'.jpg');
json_encode($arrayofstats);

echo $arrayofstats[0];

?>