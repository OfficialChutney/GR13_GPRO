package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Iterator;
import java.util.Set;


public class Wolf implements Actor {

    boolean isLeader;
    int wolfPackID;
    WolfPack pack;
    Wolf leader;
    Location myLocation;

    Wolf(boolean isLeader, int wolfPackID, WolfPack pack){
        this.isLeader = isLeader;
        this.wolfPackID = wolfPackID;
        this.pack = pack;
    }

    @Override
    public void act(World world) {

        if(isLeader){ // pack leader
            // move independently

        } else {
            // move in direction of leader
            if(rangeFromLeader(pack.getWolfLeader(), world) < 3){

            } else { // move independently

            }

        }
    }

    private float rangeFromLeader(Wolf wolfLeader, World world){

        // Get the coordinates of "this" wolf
        int thisX = world.getLocation(this).getX();
        int thisY = world.getLocation(this).getY();

        // Get the coordinates of the leader wolf
        int leaderX = world.getLocation(wolfLeader).getX();
        int leaderY = world.getLocation(wolfLeader).getY();

        // Calculate the distance using the Pythagorean theorem
        int deltaX = Math.abs(leaderX - thisX);
        int deltaY = Math.abs(leaderY - thisY);

        return (float)Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }


}
