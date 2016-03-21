package fr.handipressante.app.Data;

import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 06/03/2016.
 */
public class Memo {
    private Integer mId;
    private String mTitle;
    private String mFilename;

    public Memo() {
        mId = 0;
        mTitle = "Undefined";
        mFilename = "Undefined";
    }

    public Memo(Integer id, String title, String filename) {
        mId = id;
        mTitle = title;
        mFilename = filename;
    }

    public void updateData(Memo m) {
        if (!mId.equals(m.mId)) return;

        mTitle = m.mTitle;
        mFilename = m.mFilename;
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

    public String getFolder() {
        return "memos";
    }

    public String getLocalPath() {
        return getFolder() + "/" + getFilename();
    }

    public String getRemoteUrl() {
        return MyConstants.BASE_URL + "memos/" + getFilename();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Memo) {
            Memo m = (Memo) o;
            return this.mId.equals(m.mId);
        }

        return false;
    }
}
