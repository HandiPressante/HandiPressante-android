package fr.handipressante.app;

/**
 * Created by Nico on 06/03/2016.
 */
public class Memo {
    private Integer mId;
    private String mTitle;
    private String mFilename;
    private String mRemoteUrl;

    Memo() {
        mId = 0;
        mTitle = "Undefined";
        mFilename = "Undefined";
        mRemoteUrl = "Undefined";
    }

    Memo(Integer id, String title, String localPath, String remotePath) {
        mId = id;
        mTitle = title;
        mFilename = localPath;
        mRemoteUrl = remotePath;
    }

    public  void updateData(Memo m) {
        if (!mId.equals(m.mId)) return;

        mTitle = m.mTitle;
        mFilename = m.mFilename;
        mRemoteUrl = m.mRemoteUrl;
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
        return getFolder() + "/" + mFilename;
    }

    public String getRemoteUrl() {
        return mRemoteUrl;
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
