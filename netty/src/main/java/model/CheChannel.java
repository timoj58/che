package model;

import io.netty.channel.Channel;
import message.Acknowledge;
import message.CheMessage;
import message.HazelcastMessage;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tags;
import util.UUIDGenerator;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

/**
 * Created by timmytime on 25/01/16.
 */
public class CheChannel {

    private final LinkedHashMap<String, JSONObject> buffer = new LinkedHashMap<>();
    private Channel channel;
    private Object lock = new Object();
    private String lastSentKey = "";


    public CheChannel(Channel channel) {
        this.channel = channel;
    }

    public void updateChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public synchronized void force(Channel channel) {
        this.channel = channel;
        //we are simply going to send then first message...and then let queue take over.
        synchronized (lock) {
            if (buffer.size() > 0) {
                String nextKey = buffer.keySet().iterator().next();
                lastSentKey = nextKey;
                writeToChannel(buffer.get(nextKey).toString());
            }
        }
    }

    public synchronized void receive(Acknowledge acknowledge) throws JSONException {
        synchronized (lock) {
            buffer.remove(acknowledge.getCheAckId());
            lastSentKey = "";
            //now try and make the buffer send the reset.
            if (!buffer.isEmpty()) {
                String msg = buffer.get(buffer.keySet().iterator().next()).toString();
                writeToChannel(msg);
            }
        }
    }

    private synchronized void send(JSONObject message) throws JSONException {
        synchronized (lock) {

            System.out.println("message is " + message);

            buffer.put(message.getJSONObject(Tags.CHE).getJSONObject(Tags.CHE_ACKNOWLEDGE).getString(Tags.CHE_ACK_ID), message);
            String nextKey = buffer.keySet().iterator().next();

            System.out.println("buffer again " + lastSentKey + " " + nextKey);

            if (!lastSentKey.equals(nextKey)) {
                lastSentKey = nextKey;
                writeToChannel(buffer.get(lastSentKey).toString());
            }
        }

    }

    private void writeToChannel(String message) {

        if (channel != null) {
            System.out.println("attempt to write to channel");
            if (channel.isActive()) {
                System.out.println("writing as channel active " + message);
                channel.writeAndFlush(message);
            }
        }
    }

    private JSONObject setAcknowledge(CheMessage cheMessage) throws JSONException, NoSuchAlgorithmException {

        message.Acknowledge acknowledge = new Acknowledge(true);
        acknowledge.create();
        acknowledge.setKey(UUIDGenerator.generate());
        acknowledge.setState(Tags.CHE_ACK_ID);
        acknowledge.setValue(Tags.ACCEPT);

        cheMessage.setMessage(Tags.CHE_ACKNOWLEDGE, acknowledge.getContents());

        return cheMessage;

    }


    public void write(CheMessage cheMessage) throws JSONException, NoSuchAlgorithmException {
        send(setAcknowledge(cheMessage));
    }

    public void write(HazelcastMessage cheMessage) throws JSONException, NoSuchAlgorithmException {

        if (cheMessage.isSendToSelf() || (!cheMessage.getRemoteAddress().equals(channel.remoteAddress().toString()))) {
            send(setAcknowledge(new CheMessage(new JSONObject().put(Tags.CHE, cheMessage.getCheObject()).toString())));
        }
    }
}
