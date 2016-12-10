package fr.handipressante.app.data;

import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 06/03/2016.
 */
public class Memo {
    private Integer mId;
    private String mTitle;
    private String mFilename;
    private String mSalt;

    public Memo() {
        mId = 0;
        mTitle = "Undefined";
        mFilename = "Undefined";
        mSalt = "Undefined";
    }

    public Memo(Integer id, String title, String filename, String salt) {
        mId = id;
        mTitle = title;
        mFilename = filename;
        mSalt = salt;
    }

    public void updateData(Memo m) {
        if (!mId.equals(m.mId)) return;

        mTitle = m.mTitle;
        mFilename = m.mFilename;
        mSalt = m.mSalt;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public String getSalt() {
        return mSalt;
    }

    public void setSalt(String salt) {
        mSalt = salt;
    }



    public String getFolder() {
        return "memos";
    }

    public String getLocalPath() {
        return getFolder() + "/" + getFilename();
    }

    public String getRemoteUrl() {
        return MyConstants.MEMOS_DIR + "/" + getFilename();
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Memo) {
            Memo m = (Memo) o;
            return this.mId.equals(m.mId);
        }

        return false;
    }
    */
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Memo) {
            Memo m = (Memo) o;
            return mId.equals(m.mId) &&
                    mTitle.equals(m.mTitle) &&
                    mFilename.equals(m.mFilename) &&
                    mSalt.equals(m.mSalt);
        }

        return false;
    }
}
