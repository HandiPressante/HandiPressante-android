package com.handipressante.handipressante;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements IMarker {
    private Integer _id;
    private Boolean _adapted;
    private String _address;
    private GPSCoordinates _coord;
    private Integer _rank; // from 1 to 5

    Toilet(Integer id, Boolean adapted, String address, GPSCoordinates coord, Integer rank) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coord = coord;
        _rank = rank;
    }

    public Integer getId() {
        return _id;
    }

    public Boolean isAdapted() {
        return _adapted;
    }

    public String getAddress() {
        return _address;
    }

    public Integer getRank() {
        return _rank;
    }

    public int getIcon(){
        if(_adapted){
            return R.drawable.handicap_icon;
        }else{
            return R.drawable.not_handicap_icon;
        }
    }

    public int getRankIcon(){

        switch (_rank) {
            case 1:
                return R.drawable.star_one;
            case 2:
                return R.drawable.star_two;
            case 3:
                return R.drawable.star_three;
            case 4:
                return R.drawable.star_four;
            case 5:
                return R.drawable.star_five;
            default:
                return R.drawable.star_zero;

        }

    }

    // return distance in meters
    public  Double getDistance(GPSCoordinates ref) {
        // TODO : compute dist between _coord and ref
        return new Double(520);
    }

    // return a string to display the distance
    public String getDistanceToString(GPSCoordinates ref) {
        Double dist = this.getDistance(ref);
        if(dist>1000){
            // Kilometers
            dist = dist/1000;
            return "à " + dist +" Km.";
        }else{
            // meters
            return "à " + dist.intValue() +" m.";
        }
    }

    public String getMarkerName() {
        return "NoName";
    }

    public GPSCoordinates getCoordinates() {
        return new GPSCoordinates();
    }
}
