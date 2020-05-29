<?php
    $ch=curl_init("https://fcm.googleapis.com/fcm/send");
    $header=array("Content-Type:application/json","Authorization:key=AAAAP6MLpk0:APA91bEXMSBGr3N9fhROnlmFPL1Nx23gZY4cA5LsJsonjTUEwnDM9KEJ-nI-7VXdoOsYWUBEktSqMNY33hNummXGWjX7sxDI_COVDAsvXKI2eDT4JwcRSjODcRuYDFXgB_-VlbBqseiv");
    //to topic means using all device
    // $data=json_encode(array("to"=>"/topics/allDevices"
    //                         ,"notification"=>array("title"=>$_REQUEST['title'],"message"=>$_REQUEST['message'])));

    //see data message
    $data=json_encode(array("to"=>"/topics/allDevices","data"=>array("title"=>$_REQUEST['title'],"message"=>$_REQUEST['message'])));
    curl_setopt($ch,CURLOPT_HTTPHEADER,$header);
    curl_setopt($ch,CURLOPT_SSL_VERIFYPEER,false);
    curl_setopt($ch,CURLOPT_POST,1);
    curl_setopt($ch,CURLOPT_POSTFIELDS,$data);
    curl_exec($ch);