package fr.handipressante.app;

import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet {
    private Integer _id;
    private Boolean _adapted;
    private String _address;
    private GeoPoint _coord;

    private String _description;

    private Integer _rankAverage; // TODO : Move to a method ..
    private Integer _rankCleanliness;
    private Integer _rankFacilities;
    private Integer _rankAccessibility;

    private Double _distance;

    Toilet() {
        _id = 0;
        _adapted = false;
        _address = "Undefined";
        _coord = new GeoPoint(0, 0);
        _distance = 0.0;

        _description = "";
        _rankAverage = -1;
        _rankCleanliness = -1;
        _rankFacilities = -1;
        _rankAccessibility = -1;
    }

    Toilet(Integer id, Boolean adapted, String address, GeoPoint coord, Double distance) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coord = coord;
        _distance = distance;

        _description = "";
        _rankAverage = -1;
        _rankCleanliness = -1;
        _rankFacilities = -1;
        _rankAccessibility = -1;
    }

    public  void updateData(Toilet t) {
        if (_id != t._id) return;

        _adapted = t._adapted;
        _address = t._address;
        _coord = t._coord;
        _distance = t._distance;
        _description = t._description;
        _rankAverage = t._rankAverage;
        _rankCleanliness = t._rankCleanliness;
        _rankFacilities = t._rankFacilities;
        _rankAccessibility = t._rankAccessibility;
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

    public Integer getRankAverage() {
        return _rankAverage;
    }

    public void setRankAverage(Integer rank) {
        _rankAverage = rank;
    }

    public Integer getRankCleanliness() {
        return _rankCleanliness;
    }

    public void setRankCleanliness(Integer rank) {
        _rankCleanliness = rank;
    }

    public Integer getRankFacilities() {
        return _rankFacilities;
    }

    public void setRankFacilities(Integer rank) {
        _rankFacilities = rank;
    }

    public Integer getRankAccessibility() {
        return _rankAccessibility;
    }

    public void setRankAccessibility(Integer rank) {
        _rankAccessibility = rank;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    // TODO : Check if needed
    public String getMarkerName() {
        return "NoName";
    }

    public GeoPoint getCoordinates() {
        return _coord;
    }

    public Double getDistance() {
        return _distance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Toilet) {
            Toilet t = (Toilet) o;
            return this._id.equals(t._id);
        }

        return false;
    }
}
