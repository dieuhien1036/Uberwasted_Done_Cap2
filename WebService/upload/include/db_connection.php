<?php
    /**
    *Database config variables,
    */
    define("DB_HOST","localhost"); 
    define("DB_USER","root");
    define("DB_PASSWORD","");
    define("DB_DATABASE","uberwasted");
 
    $connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
 
if ($connection->connect_error) {
    die("failed: " . $connection->connect_error);
}
?>