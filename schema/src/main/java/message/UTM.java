package message;

import model.CoreModel;

/**
 * Created by timmytime on 15/01/16.
 */
public class UTM extends CoreMessage {
    public UTM(String message) {
        super(message);
    }

    public UTM(CoreModel model) {
        super(model.getMessage());
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

}