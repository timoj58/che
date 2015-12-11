package model;

import model.generic.GenericModel;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tags;

/**
 * Created by timmytime on 10/12/15.
 */
public class Core extends JSONObject {


    public Core(String core) throws JSONException {
        super(core);
    }

    public String getAckId() throws JSONException {
        return this.getString(Tags.ACK_ID);
    }

    public User getUser() throws JSONException {
        return new User(this.getJSONObject(Tags.USER_OBJECT));
    }

    public Location getLocation() throws JSONException {
        return new Location(this.getJSONObject(Tags.LOCATION_OBJECT));
    }

    public GenericModel getGeneric() throws JSONException {
        return new GenericModel(this.getJSONObject(Tags.GENERIC_OBJECT));
    }

    public Acknowledge getAcknowledge() throws JSONException {
        return (new Acknowledge(this.getJSONObject(Tags.ACKNOWLEDGE_OBJECT)));
    }

}