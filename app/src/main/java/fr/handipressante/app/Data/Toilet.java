package fr.handipressante.app.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements Parcelable, ClusterItem {
    private Integer _id;
    private Boolean _adapted;
    private String _name;
    private LatLng _position;
    private Boolean _charged;

    private String _description;

    private Float _rankCleanliness;
    private Float _rankFacilities;
    private Float _rankAccessibility;
    private Integer _rateWeight;

    // Computed data members
    LatLng mLastRef = null;
    double mDistance = -1;

    public Toilet() {
        _id = 0;
        _adapted = false;
        _charged = false;
        _name = "Undefined";
        _position = new LatLng(0, 0);

        _description = "";
        _rankCleanliness = 0.f;
        _rankFacilities = 0.f;
        _rankAccessibility = 0.f;
        _rateWeight = 0;
    }

    public Toilet(Integer id, Boolean adapted, Boolean charged, String address, LatLng position) {
        _id = id;
        _adapted = adapted;
        _charged = charged;
        _name = address;
        _position = position;

        _description = "";
        _rankCleanliness = 0.f;
        _rankFacilities = 0.f;
        _rankAccessibility = 0.f;
        _rateWeight = 0;
    }

    public Toilet(Parcel in) {
        _id = in.readInt();
        _adapted = in.readByte() != 0;
        _charged = in.readByte() != 0;
        _name = in.readString();
        _position = new LatLng(in.readDouble(), in.readDouble());

        _description = in.readString();
        _rankCleanliness = in.readFloat();
        _rankFacilities = in.readFloat();
        _rankAccessibility = in.readFloat();
        _rateWeight = in.readInt();

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
        dest.writeByte((byte) (_charged ? 1 : 0));
        dest.writeString(_name);
        dest.writeDouble(_position.latitude);
        dest.writeDouble(_position.longitude);

        dest.writeString(_description);
        dest.writeFloat(_rankCleanliness);
        dest.writeFloat(_rankFacilities);
        dest.writeFloat(_rankAccessibility);
        dest.writeInt(_rateWeight);

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
        _charged = t._charged;
        _name = t._name;
        _position = t._position;
        _description = t._description;
        _rankCleanliness = t._rankCleanliness;
        _rankFacilities = t._rankFacilities;
        _rankAccessibility = t._rankAccessibility;
        _rateWeight = t._rateWeight;
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

    public void setCharged(Boolean charged) {
        _charged = charged;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public Float getRankAverage() {
        return (_rankAccessibility + _rankCleanliness + _rankFacilities) / 3.f;
    }

    public Float getRankCleanliness() {
        return _rankCleanliness;
    }

    public void setRankCleanliness(Float rank) {
        _rankCleanliness = rank;
    }

    public Float getRankFacilities() {
        return _rankFacilities;
    }

    public void setRankFacilities(Float rank) {
        _rankFacilities = rank;
    }

    public Float getRankAccessibility() {
        return _rankAccessibility;
    }

    public void setRankAccessibility(Float rank) {
        _rankAccessibility = rank;
    }

    public Integer getRateWeight() {
        return _rateWeight;
    }

    public void setRateWeight(Integer weight) {
        _rateWeight = weight;
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

    @Override
    public LatLng getPosition() {
        return _position;
    }

    public void setPosition(LatLng position) {
        _position = position;
    }

    public void setDistanceWith(LatLng ref) {
        if (mLastRef != ref) {
            mDistance = distanceWGS84(_position.latitude, _position.longitude,
                    ref.latitude, ref.longitude);
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
