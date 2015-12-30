package model;

import org.json.JSONException;
import org.json.JSONObject;
import util.Tags;

import java.io.Serializable;

/**
 * Created by timmytime on 10/12/15.
 */
public class User extends JSONObject implements Serializable{

    private String uid;

    public User(String user) throws JSONException {
        super(user);
        setUid(this.getString(Tags.UID));
    }

    public User(JSONObject user) throws JSONException {
        super(user);
        setUid(this.getString(Tags.UID));
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
