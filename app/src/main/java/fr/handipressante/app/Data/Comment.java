package fr.handipressante.app.Data;

/**
 * Created by Nico on 26/05/2016.
 */
public class Comment {
    private String mUsername;
    private String mContent;
    private String mPostdate;

    public Comment() {
        mUsername = "Undefined";
        mContent = "Undefined";
        mPostdate = "Undefined";
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
