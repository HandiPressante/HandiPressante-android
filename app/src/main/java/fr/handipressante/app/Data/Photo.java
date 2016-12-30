package fr.handipressante.app.data;

import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 25/05/2016.
 */
public class Photo {
    private Integer mId;
    private Integer mToiletId;
    private String mFilename;
    private String mPostdate;

    public Photo() {
        mId = 0;
        mToiletId = 0;
        mFilename = "Undefined";
        mPostdate = "Undefined";
    }

    public Photo(Integer id, Integer toiletId, String filename, String postdate) {
        mId = id;
        mToiletId = toiletId;
        mFilename = filename;
        mPostdate = postdate;
    }

    public void updateData(Photo p) {
        if (!mId.equals(p.mId)) return;

        mToiletId = p.mToiletId;
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

    public String getPostdate() { return mPostdate; }

    public void setPostdate(String postdate) { mPostdate = postdate; }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public String getFolder() {
        return "pictures";
    }

    public String getLocalPath() {
        return getFolder() + "/" + mToiletId + "_" + getFilename();
    }

    public String getRemoteUrl() {
        return MyConstants.PICTURES_DIR + "/" + mToiletId + "/" + getFilename();
    }
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Photo) {
            Photo p = (Photo) o;
            return mId.equals(p.mId) &&
                    mToiletId.equals(p.mToiletId) &&
                    mFilename.equals(p.mFilename) &&
                    mPostdate.equals(p.mPostdate);
        }

        return false;
    }
}
