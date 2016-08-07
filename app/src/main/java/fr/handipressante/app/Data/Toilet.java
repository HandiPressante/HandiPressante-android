package fr.handipressante.app.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements Parcelable {
    private Integer _id;
    private Boolean _adapted;
    private String _address;
    private GeoPoint _coord;
    private Boolean _charged;

    private String _description;

    private Integer _rankCleanliness;
    private Integer _rankFacilities;
    private Integer _rankAccessibility;

    // Computed data members
    GeoPoint mLastRef = null;
    double mDistance = -1;

    public Toilet() {
        _id = 0;
        _adapted = false;
        _address = "Undefined";
        _coord = new GeoPoint(0, 0);
        _charged = false;

        _description = "";
        _rankCleanliness = -1;
        _rankFacilities = -1;
        _rankAccessibility = -1;
    }

    public Toilet(Integer id, Boolean adapted, String address, GeoPoint coord) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coord = coord;
        _charged = false;

        _description = "";
        _rankCleanliness = -1;
        _rankFacilities = -1;
        _rankAccessibility = -1;
    }

    public Toilet(Parcel in) {
        _id = in.readInt();
        _adapted = in.readByte() != 0;
        _address = in.readString();
        _coord = new GeoPoint(in.readDouble(), in.readDouble());
        _charged = false;

        _description = in.readString();
        _rankCleanliness = in.readInt();
        _rankFacilities = in.readInt();
        _rankAccessibility = in.readInt();

        mDistance = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeByte((byte) (_adapted ? 1 : 0));
        dest.writeString(_address);
        dest.writeDouble(_coord.getLatitude());
        dest.writeDouble(_coord.getLongitude());

        dest.writeString(_description);
        dest.writeInt(_rankCleanliness);
        dest.writeInt(_rankFacilities);
        dest.writeInt(_rankAccessibility);

        dest.writeDouble(mDistance);
    }

    public static final Creator<Toilet> CREATOR = new Creator<Toilet>() {
        @Override
        public Toilet createFromParcel(Parcel in) {
            return new Toilet(in);
        }

        @Override
        public Toilet[] newArray(int size) {
            return new Toilet[size];
        }
    };

    public  void updateData(Toilet t) {
        if (_id != t._id) return;

        _adapted = t._adapted;
        _address = t._address;
        _coord = t._coord;
        _description = t._description;
        _rankCleanliness = t._rankCleanliness;
        _rankFacilities = t._rankFacilities;
        _rankAccessibility = t._rankAccessibility;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        _id = id;
    }

    public Boolean isAdapted() {
        return _adapted;
    }

    public Boolean isCharged(){
        return _charged;
    }

    public void setAdapted(Boolean adapted) {
        _adapted = adapted;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public Integer getRankAverage() {
        float average = (_rankAccessibility + _rankCleanliness + _rankFacilities) / 3.f;
        return Math.round(average);
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

    public void setCoordinates(GeoPoint coord) {
        _coord = coord;
    }

    public void setDistanceWith(GeoPoint ref) {
        if (mLastRef != ref) {
            mDistance = distanceWGS84(_coord.getLatitude(), _coord.getLongitude(),
                    ref.getLatitude(), ref.getLongitude());
            mLastRef = ref;
        }
    }

    public double getDistance() {
        return mDistance;
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

    public static void sortListByDistance(List<Toilet> list) {
        for (int i=0; i+1<list.size(); i++) {
            Toilet nearest = list.get(i);
            int nearestIndex = i;

            for (int j=i+1; j<list.size(); j++) {
                Toilet t = list.get(j);
                if (t.getDistance() < nearest.getDistance()) {
                    nearest = t;
                    nearestIndex = j;
                }
            }

            list.set(nearestIndex, list.get(i));
            list.set(i, nearest);
        }
    }

    public static Double distanceWGS84(Double lat1, Double lon1, Double lat2, Double lon2) {
        int earthRadius = 6371;

        double lat1Rad = (lat1 * Math.PI) / 180;
        double lon1Rad = (lon1 * Math.PI)/ 180;
        double lat2Rad = (lat2 * Math.PI) / 180;
        double lon2Rad = (lon2 * Math.PI)/ 180;

        double x1 = earthRadius * Math.cos(lat1Rad) * Math.cos(lon1Rad);
        double y1 = earthRadius * Math.cos(lat1Rad) * Math.sin(lon1Rad);
        double z1 = earthRadius * Math.sin(lat1Rad);

        double x2 = earthRadius * Math.cos(lat2Rad) * Math.cos(lon2Rad);
        double y2 = earthRadius * Math.cos(lat2Rad) * Math.sin(lon2Rad);
        double z2 = earthRadius * Math.sin(lat2Rad);

        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        double distanceKm = Math.sqrt(dx*dx + dy*dy + dz*dz);

        return distanceKm * 1000;
    }
}
