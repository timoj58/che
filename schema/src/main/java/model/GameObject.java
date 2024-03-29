package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timmytime on 15/01/16.
 */
public class GameObject extends CoreModel {

    public String state, value;
    public int type, subType, quantity;
    //where are we located now...rest is calculated see physics shit. ie we can only be in 1 place.
    public UTMLocation utmLocation, destinationUTMLocation;

    //general physics variables.
    public double mass, acceleration, velocity, range, maxSpeed, strength, impactRadius, force;
    //is it fixed / hit / destroyed / located
    public boolean fixed, hit, destroyed, located;
    //contains a list of missiles.  perhaps supports holding all sorts. (no all missiles are same, just different settings).
    private List<Missile> missiles = new ArrayList<>();
    private List<UTM> destinationValidator = new ArrayList<>();

    private double distanceBetweenPoints; //our total distance to move.


    public GameObject(message.GameObject gameObject) {

        this.key = gameObject.getKey();
        this.state = gameObject.getState();
        this.value = gameObject.getValue();
        this.mass = gameObject.getMass();
        this.velocity = gameObject.getVelocity();
        this.acceleration = gameObject.getAcceleration();
        this.force = gameObject.getForce();
        this.strength = gameObject.getStrength();
        this.range = gameObject.getRange();
        this.impactRadius = gameObject.getImpactRadius();
        this.maxSpeed = gameObject.getMaxSpeed();
        this.fixed = gameObject.isFixed();
        this.hit = gameObject.isHit();
        this.located = gameObject.isLocated();
        this.destroyed = gameObject.isDestroyed();
        this.type = gameObject.getType();
        this.subType = gameObject.getSubType();
        this.quantity = gameObject.getQuantity();
        for (message.Missile missile : gameObject.getMissiles()) {
            missiles.add(new Missile(missile));
        }
        for (message.UTM utm : gameObject.getDestinationValidator()) {
            destinationValidator.add(new UTM(utm));
        }
        this.utmLocation = new UTMLocation(gameObject.getUtmLocation());
        this.destinationUTMLocation = new UTMLocation(gameObject.getDestinationUtmLocation());


    }

    public GameObject(String key) {
        super(key);
        utmLocation = new UTMLocation();
        destinationUTMLocation = new UTMLocation();
        this.state = "";
        this.value = "";
        this.mass = 0;
        this.velocity = 0;
        this.acceleration = 0;
        this.strength = 0;
        this.maxSpeed = 0;
        this.range = 0;
        this.impactRadius = 0;
        this.force = 0;
        this.fixed = false;
        this.hit = false;
        this.located = false;
        this.destroyed = false;
        this.type = -1;
        this.subType = -1;
        this.quantity = 1;
    }

    public GameObject(GameObject gameObject) {
        super(gameObject.getKey());
        utmLocation = new UTMLocation(gameObject.utmLocation);
        destinationUTMLocation = new UTMLocation(gameObject.destinationUTMLocation);
        this.state = gameObject.state;
        this.value = gameObject.value;
        this.mass = gameObject.mass;
        this.velocity = gameObject.velocity;
        this.acceleration = gameObject.acceleration;
        this.strength = gameObject.strength;
        this.maxSpeed = gameObject.maxSpeed;
        this.range = gameObject.range;
        this.impactRadius = gameObject.impactRadius;
        this.force = gameObject.force;
        this.fixed = gameObject.fixed;
        this.hit = gameObject.hit;
        this.located = gameObject.located;
        this.destroyed = gameObject.destroyed;
        this.type = gameObject.type;
        this.subType = gameObject.subType;
        this.quantity = gameObject.quantity;
        this.missiles = new ArrayList<>();
        for (Missile missile : gameObject.missiles) {
            this.missiles.add(missile);
        }
        this.distanceBetweenPoints = gameObject.distanceBetweenPoints;
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    public double getDistanceBetweenPoints() {
        return distanceBetweenPoints;
    }

    public void setDistanceBetweenPoints(double distanceBetweenPoints) {
        this.distanceBetweenPoints = distanceBetweenPoints;
    }

    public List<UTM> getDestinationValidator() {
        return destinationValidator;
    }


    @Override
    public String getMessage() {
        message.GameObject gameObject = new message.GameObject();
        gameObject.create();

        //need to create message then all done....thank fuck.
        gameObject.setKey(this.key);
        gameObject.setState(state);
        gameObject.setValue(value);
        gameObject.setAcceleration(acceleration);
        gameObject.setVelocity(velocity);
        gameObject.setMass(mass);
        gameObject.setForce(force);
        gameObject.setMaxSpeed(maxSpeed);
        gameObject.setRange(range);
        gameObject.setImpactRadius(impactRadius);
        gameObject.setStrength(strength);
        gameObject.setHit(hit);
        gameObject.setDestroyed(destroyed);
        gameObject.setLocated(located);
        gameObject.setFixed(fixed);
        gameObject.setType(type);
        gameObject.setSubType(subType);
        gameObject.setQuantity(quantity);

        List<message.Missile> temp = new ArrayList<>();
        for (Missile missile : missiles) {
            temp.add(new message.Missile(missile.getMessage()));
        }

        gameObject.setMissiles(temp);

        List<message.UTM> temp2 = new ArrayList<>();
        for (UTM utm : destinationValidator) {
            temp2.add(new message.UTM(utm.getMessage()));
        }

        gameObject.setMissiles(temp);

        gameObject.setUtmLocation(new message.UTMLocation(utmLocation.getMessage()));
        gameObject.setDestinationUtmLocation(new message.UTMLocation(destinationUTMLocation.getMessage()));


        return gameObject.toString();
    }
}
