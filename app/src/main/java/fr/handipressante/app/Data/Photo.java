package fr.handipressante.app.Data;

import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 25/05/2016.
 */
public class Photo {
    private Integer mId;
    private Integer mToiletId;
    private String mUserId;
    private String mFilename;
    private String mPostdate;

    public Photo() {
        mId = 0;
        mToiletId = 0;
        mUserId = "uuid";
        mFilename = "Undefined";
        mPostdate = "Undefined";
    }

    public Photo(Integer id, Integer toiletId, String userId, String filename, String postdate) {
        mId = id;
        mToiletId = toiletId;
        mUserId = userId;
        mFilename = filename;
        mPostdate = postdate;
    }

    public void updateData(Photo p) {
        if (!mId.equals(p.mId)) return;

        mToiletId = p.mToiletId;
        mUserId = p.mUserId;
        mFilename = p.mFilename;
        mPostdate = p.mPostdate;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getToiletId() {
        return mToiletId;
    }

    public void setToiletId(Integer id) {
        mToiletId = id;
    }

    public String getUserId() { return mUserId; }

    public void setUserId(String userId) { mUserId = userId; }

    public String getPostdate() { return mPostdate; }

    public void setPostdate(String postdate) { mPostdate = postdate; }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public String getFolder() {
        return "photos";
    }

    public String getLocalPath() {
        return getFolder() + "/" + mToiletId + "_" + getFilename();
    }

    public String getRemoteUrl() {
        return MyConstants.BASE_URL + "images/photos/" + mToiletId + "/" + getFilename();
    }
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Photo) {
            Photo p = (Photo) o;
            return mId.equals(p.mId) &&
                    mToiletId.equals(p.mToiletId) &&
                    mUserId.equals(p.mUserId) &&
                    mFilename.equals(p.mFilename) &&
                    mPostdate.equals(p.mPostdate);
        }

        return false;
    }
}
