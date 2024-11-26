package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Iterator;
import java.util.Set;


public class Wolf extends Animal implements Actor {

    boolean isLeader;
    int wolfPackID;
    WolfPack pack;
    Wolf leader;
    Location myLocation;

    Wolf(int wolfPackID, WolfPack pack) {
        super(30);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = true;
        leader = this;
    }

    Wolf(int wolfPackID, WolfPack pack, Wolf leader) {
        super(30);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
    }

    @Override
    public void act(World world) {

        if (isLeader) { // pack leader
            // move independently
            pathFinder(world, null);
        } else {

            if (rangeFromLeader(pack.getWolfLeader(), world) < 4) {
                pathFinder(world, null);

            } else { // move independently
                pathFinder(world, leader.getMyLocation());
                System.out.println("Out of leader range");
                System.out.println(myLocation.toString());
            }

        }

        myLocation = world.getLocation(this);
    }

    private float rangeFromLeader(Wolf wolfLeader, World world) {
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
    protected void eat(World world) {

    }
}
