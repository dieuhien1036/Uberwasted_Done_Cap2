<?php
    $connect = mysqli_connect("localhost","root","","test");
    mysqli_query($connect,"SET NAMES 'utf8'");

    $query ="Select * from imagesWasted";
    $data= mysqli_query($connect,$query);

    class wasted{
        function wasted($id, $image_name, $image_url,$wasteLocation_latitude,$wasteLocation_longtitude,$wasteLocation_address,$people,$size,$material,$mota,$caption){
            $this->Id = $id;
            $this->Image_name = $image_name ;
            $this->Image_url = $image_url ;
            $this->WasteLocation_latitude = $wasteLocation_latitude ;
            $this->WasteLocation_longtitude = $wasteLocation_longtitude ;
            $this->WasteLocation_address = $wasteLocation_address ;
            $this->People = $people ;
            $this->Size = $size ;
            $this->Mota = $mota ;
            $this->Caption = $caption ;
        }
    }
    $arrayWasted = array();
    while($row = mysqli_fetch_assoc($data)){
        array_push($arrayWasted, new wasted($row['id'] , $row['image_name'], $row['image_url'], $row['wasteLocation_latitude'], $row['wasteLocation_longtitude']
        , $row['wasteLocation_address'] ,$row['people'], $row['size'],$row['material'] , $row['mota'] ,$row['caption'] ));
    }
    echo json_encode($arrayWasted);
?>
