package controller.handler.che;

import core.HazelcastManagerInterface;
import factory.CheChannelFactory;
import message.CheMessage;
import message.HazelcastMessage;
import model.Alliance;
import model.Player;
import org.json.JSONException;
import org.json.JSONObject;
import util.Configuration;
import util.Tags;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by timmytime on 13/01/16.
 */
public class AllianceHandler {

    private final HazelcastManagerInterface hazelcastManagerInterface;
    private final Configuration configuration;

    public AllianceHandler(HazelcastManagerInterface hazelcastManagerInterface, Configuration configuration) {
        this.hazelcastManagerInterface = hazelcastManagerInterface;
        this.configuration = configuration;
    }

    public void handle(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException, JSONException {


        switch (alliance.state) {
            case Tags.ALLIANCE_CREATE:
                allianceCreate(player, alliance);
                break;
            case Tags.ALLIANCE_POST:
                alliancePost(player, alliance);
                break;
            case Tags.ALLIANCE_JOIN:
                allianceJoin(player, alliance);
                break;
            case Tags.ALLIANCE_INVITE:
                allianceInvite(player, alliance);
                break;
            case Tags.ALLIANCE_LEAVE:
                allianceLeave(player, alliance);
                break;
        }
    }

    private void allianceCreate(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException, JSONException {

        alliance.setKey(configuration.getUuidGenerator().generateKey("alliance " + alliance.name));

        alliance.getMembers().add(player);
        player.getAlliances().add(alliance.getKey());

        hazelcastManagerInterface.put(Tags.ALLIANCE_MAP, alliance.getKey(), alliance);

        player.getTopicSubscriptions().addSubscription(alliance.getKey(), player.getKey(),
                hazelcastManagerInterface.subscribe(alliance.getKey(), player.getKey(), player.getKey()));
        alliance.value = Tags.SUCCESS;

        configuration.getLogger().debug("created alliance");

        CheChannelFactory.write(player.getKey(), new CheMessage(Tags.ALLIANCE, new message.Alliance(alliance.getMessage())));
    }

    private void allianceInvite(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException {


        //   the bt one is local, this is to publish to other people sends the alliance key to person.....could do some trick shit with emails, phones numbers etc.
        //even virus style, ie mesage can launch intent to appstore if not installed etc...that would be clever...

    }

    //note testing wise, you can post withouth joining.  not a major issue presumably.
    private void allianceJoin(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException, JSONException {

        Object object = hazelcastManagerInterface.get(Tags.ALLIANCE_MAP, alliance.getKey());

        if (object != null) {
            Alliance serverAlliance = (Alliance) object;

            serverAlliance.getMembers().add(player);
            player.getAlliances().add(serverAlliance.getKey());

            hazelcastManagerInterface.put(Tags.ALLIANCE_MAP, serverAlliance.getKey(), serverAlliance);

            player.getTopicSubscriptions().addSubscription(serverAlliance.getKey(), player.getKey(), hazelcastManagerInterface.subscribe(serverAlliance.getKey(), player.getKey(), player.getKey()));
            serverAlliance.value = Tags.SUCCESS;
            serverAlliance.state = Tags.ALLIANCE_JOIN;

            CheChannelFactory.write(player.getKey(), new CheMessage(Tags.ALLIANCE, new message.Alliance(serverAlliance.getMessage())));
        }

    }

    private void allianceLeave(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException, JSONException {

        Object object = hazelcastManagerInterface.get(Tags.ALLIANCE_MAP, alliance.getKey());

        if (object != null) {
            Alliance serverAlliance = (Alliance) object;

            serverAlliance.getMembers().remove(player); //need to ensure the correct player is removed..  ie hashcode wont be same. (actually it will be as it came from server).  to test
            player.getAlliances().remove(serverAlliance.getKey());
            hazelcastManagerInterface.unSubscribe(serverAlliance.getKey(), player.getKey(), player.getKey());
            player.getTopicSubscriptions().removeSubscription(serverAlliance.getKey());

            hazelcastManagerInterface.put(Tags.ALLIANCE_MAP, serverAlliance.getKey(), serverAlliance);
            serverAlliance.value = Tags.SUCCESS;
            serverAlliance.state = Tags.ALLIANCE_LEAVE;

            CheChannelFactory.write(player.getKey(), new CheMessage(Tags.ALLIANCE, new message.Alliance(serverAlliance.getMessage())));
        }


    }

    private void alliancePost(Player player, Alliance alliance) throws RemoteException, NoSuchAlgorithmException, JSONException {

        HazelcastMessage hazelcastMessage = new HazelcastMessage(CheChannelFactory.getCheChannel(player.getKey()).getChannel().remoteAddress().toString(), true,
                new JSONObject().put(Tags.ALLIANCE, new message.Alliance(alliance.getMessage())));

        hazelcastMessage.getCheObject().put(Tags.PLAYER, new message.Player(player.getMessage()));
        configuration.getLogger().debug("sending a post " + hazelcastMessage.toString());
        hazelcastManagerInterface.publish(alliance.getKey(), hazelcastMessage.toString());

    }

}
