package fr.handipressante.app.data;

/**
 * Created by Nico on 26/05/2016.
 */
public class Comment {
    private Integer mId;
    private String mUsername;
    private String mContent;
    private String mPostdate;

    public Comment() {
        mId = 0;
        mUsername = "Undefined";
        mContent = "Undefined";
        mPostdate = "Undefined";
    }

    public Comment(Integer id, String username, String content, String postdate) {
        mId = id;
        mUsername = username;
        mContent = content;
        mPostdate = postdate;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getPostdate() {
        return mPostdate;
    }

    public void setPostdate(String postdate) {
        this.mPostdate = postdate;
    }

}
