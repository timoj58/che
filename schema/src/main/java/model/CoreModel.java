package model;

import java.io.Serializable;

/**
 * Created by timmytime on 15/01/16.
 */
public abstract class CoreModel implements Serializable, ModelInterface {

    protected String key;

    public CoreModel() {

    }

    public CoreModel(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
