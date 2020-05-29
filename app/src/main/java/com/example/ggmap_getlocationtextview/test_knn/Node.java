package com.example.ggmap_getlocationtextview.test_knn;

class Node {
    static int k = 50176;
    byte point[]=new byte[50176]; // To store k dimensional point
    Node left, right;

    public Node(byte arr[])
    {
        for(int i = 0; i < arr.length; i++){
            point[i]=arr[i];
        }
        left=right=null;
    }

}