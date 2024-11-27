package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Iterator;
import java.util.Set;


public class Wolf extends Animal implements Actor {

    private boolean isLeader;
    private int wolfPackID;
    private WolfPack pack;
    private Wolf leader;
    private Location myLocation;
    private World world;

    Wolf(World world, int wolfPackID, WolfPack pack) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = true;
        leader = this;
        this.world = world;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
    }

    @Override
    public void act(World world) {

        if (isLeader) { // pack leader
            // move independently
            pathFinder(null);
        } else {

            if (rangeFromLeader(pack.getWolfLeader()) < 4) {
                pathFinder(null);

            } else { // move independently
                pathFinder(leader.getMyLocation());
                System.out.println("Out of leader range");
                System.out.println(myLocation.toString());
            }

        }

        myLocation = world.getLocation(this);
    }

    private float rangeFromLeader(Wolf wolfLeader) {
        // Get the coordinates of "this" wolf
        int thisX = world.getLocation(this).getX();
        int thisY = world.getLocation(this).getY();

        // Get the coordinates of the leader wolf
        int leaderX = world.getLocation(wolfLeader).getX();
        int leaderY = world.getLocation(wolfLeader).getY();

        // Calculate the distance using the Pythagorean theorem
        int deltaX = Math.abs(leaderX - thisX);
        int deltaY = Math.abs(leaderY - thisY);

        return (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    @Override
    protected void eat() {

    }
}
