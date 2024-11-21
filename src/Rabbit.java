import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Rabbit implements Actor {
    int age;
    int energy;
    RabbitHole myRabbitHole;
    boolean isInRabbitHole;
    Sex sex;
    boolean pregnant;
    boolean lookingForFood;
    boolean goingHome;


    Rabbit(Sex sex) {

    }

    @Override
    public void act(World world) {

    }

    private void reproduce(World world) {
        Location myTempLoc = world.getLocation(this);

        if(){

        }
    }

    private void eat(World world) {

    }

    private void pathFinder(World world, Location destination) {

    }

    private void die(World world) {

    }

    private void digHole(World world) {

    }

    private void sleep(World world) {

    }

    private void updateEnergy (int num){

    }

    private TimeOfDay checktime(World world){

    }

    private Location findGrass(World world) {

    }

    private Location findMyHole(World world) {

    }

    private Location getNearestGrass(World world) {

    }

    private Object getMyNeighbor(Actor actor, World world) {

    }

    private ArrayList<Location> surrondingLocationsList(World world) {

    }

    private void getSex() {
        Random r = new Random();
        switch(r.nextInt(2)){
            case 0 -> this.sex = Sex.MALE;
            case 1 -> this.sex = Sex.FEMALE;
        }
    }
}
