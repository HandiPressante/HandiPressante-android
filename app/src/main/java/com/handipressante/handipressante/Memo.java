package com.handipressante.handipressante;

/**
 * Created by Nico on 06/03/2016.
 */
public class Memo {
    private Integer mId;
    private String mTitle;
    private String mLocalPath;
    private String mRemotePath;

    Memo() {
        mId = 0;
        mTitle = "Undefined";
        mLocalPath = "Undefined";
        mRemotePath = "Undefined";
    }

    Memo(Integer id, String title, String localPath, String remotePath) {
        mId = id;
        mTitle = title;
        mLocalPath = localPath;
        mRemotePath = remotePath;
    }

    public  void updateData(Memo m) {
        if (!mId.equals(m.mId)) return;

        mTitle = m.mTitle;
        mLocalPath = m.mLocalPath;
        mRemotePath = m.mRemotePath;
    }

    public Integer getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLocalPath() {
        return mLocalPath;
    }

    public void setLocalPath(String localPath) {
        mLocalPath = localPath;
    }

    public String getRemotePath() {
        return mRemotePath;
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
