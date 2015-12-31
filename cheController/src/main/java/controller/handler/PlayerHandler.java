package controller.handler;

import controller.CheController;
import core.HazelcastManagerInterface;
import model.Core;
import model.Player;
import model.UTMLocation;
import org.json.JSONException;
import util.Configuration;

import java.rmi.RemoteException;

/**
 * Created by timmytime on 31/12/15.
 */
public class PlayerHandler {

    private final HazelcastManagerInterface hazelcastManagerInterface;
    private final Configuration configuration;
    private final UTMHandler utmHandler;

    public PlayerHandler(HazelcastManagerInterface hazelcastManagerInterface, Configuration configuration) {
        this.hazelcastManagerInterface = hazelcastManagerInterface;
        this.configuration = configuration;
        this.utmHandler = new UTMHandler(hazelcastManagerInterface, configuration);
    }

    public void handlePlayer(Core message) throws RemoteException, JSONException {
        Player player = getPlayer(message.getUser().getUid());
        UTMLocation utmLocation = utmHandler.getUTMLocation(message.getLocation());

        boolean hasUTMChanged = player.hasUTMChanged(utmLocation);
        boolean hasSubUTMChanged = player.hasSubUTMChanged(utmLocation);

        if (hasUTMChanged) {
            utmHandler.handleUTMChange(utmLocation, player.utmLocation, player.getTopicSubscriptions());
            configuration.getLogger().debug("UTM has changed");
        } else if (hasSubUTMChanged) {
            utmHandler.handleSubUTMChange(utmLocation, player.utmLocation, player.getTopicSubscriptions());
            configuration.getLogger().debug("Sub UTM has changed");
        }
        player.utmLocation = utmLocation;

        hazelcastManagerInterface.put(CheController.PLAYER_MAP, player.uid, player);
    }

    public Player getPlayer(String uid) throws RemoteException {

        Player player = (Player) hazelcastManagerInterface.get(CheController.PLAYER_MAP, uid);

        if (player == null) {
            configuration.getLogger().debug("created a new player");
            player = new Player(uid);
        }

        return player;
    }

}
