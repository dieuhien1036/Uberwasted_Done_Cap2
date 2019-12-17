package com.example.ggmap_getlocationtextview;

public class JoinClean {
    private String join_id;
    private String waste_id;
    private double wasteLatitude;
    private double wasteLongtitude;
    private String wasteAddress;

    JoinClean(String join_id, String waste_id, double wasteLatitude, double wasteLongtitude, String wasteAddress){
        this.join_id = join_id;
        this.waste_id= waste_id;
        this.wasteLatitude = wasteLatitude;
        this.wasteLongtitude = wasteLongtitude;
        this.wasteAddress = wasteAddress;
    }
    public String getJoin_id() {
        return join_id;
    }

    public String getWaste_id() {
        return waste_id;
    }

    public double getWasteLatitude() {
        return wasteLatitude;
    }


    public double getWasteLongtitude() {
        return wasteLongtitude;
    }
    public String getWasteAddress() {
        return wasteAddress;
    }
}
