package com.example.ggmap_getlocationtextview.test_knn;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class KDT {
    static double currBest=Double.MAX_VALUE;
    static int k = 50176;
    Node root;

    public KDT()
    {
        root=null;
    }
    Node insertRec(Node root,byte point[], int depth)
    {
        if (root == null)
            return (new Node(point));

        int cd = depth % k;

        if (point[cd] < (root.point[cd]))
            root.left  = insertRec(root.left, point, depth + 1);
        else
            root.right = insertRec(root.right, point, depth + 1);

        return root;
    }

    void insert(byte point[])
    {
        root=insertRec(root, point, 0);
    }


    public Node nearest(Node root,byte point[],Node currentBestNode,int depth)
    {
        if(root==null) return currentBestNode;
        if(dist(root.point,point)<currBest)
        {
            currBest=dist(root.point,point);
            currentBestNode=root;
        }
        Node good=null,bad=null;
        int cd = depth % k;
        if(point[cd]<root.point[cd])
        {
            good=root.left;
            bad=root.right;
        }
        else{
            bad=root.left;
            good=root.right;
        }

        currentBestNode=nearest(good, point, currentBestNode, depth+1);

        if(Math.abs(point[cd]-root.point[cd])<currBest)
            return currentBestNode=nearest(bad, point, currentBestNode, depth+1);

        return currentBestNode;
    }

    double dist(byte []a,byte b[])
    {
        double resultEuclidean = 0.0;
        for(int i = 0; i < a.length; i++){
            resultEuclidean+= Math.pow(a[i]-b[i],2);
        }
        return Math.sqrt(resultEuclidean);
    }

    public byte[] NearestNeighbour(byte a[])
    {
        Node temp=nearest(root, a, root, 0);
        currBest=Byte.MAX_VALUE;
        return temp.point;
    }


    public static String getNearestA(byte[] input, LinkedHashMap<byte[], String> tmap , Context mycontext)
    {
        Scanner sc=new Scanner(System.in);
        KDT kd=new KDT();

        Set set = tmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            kd.insert((byte[])mentry.getKey());
        }

        byte nnb[]=kd.NearestNeighbour(input);

        Set set1 = tmap.entrySet();
        Iterator iterator1 = set.iterator();
        while(iterator1.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator1.next();
           if(Arrays.equals((byte[]) mentry.getKey(),nnb)){
               return (String) mentry.getValue();
           }
        }
        return "fail";

    }

}
