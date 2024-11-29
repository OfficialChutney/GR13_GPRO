package animal;


import domainmodel.TimeOfDay;
import foliage.Grass;
import hole.WolfHole;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;


public class Wolf extends Animal implements Actor {

    private boolean isLeader;
    private int wolfPackID;
    private WolfPack pack;
    private Wolf leader;
    private Location myLocation;
    private World world;
    private WolfHole myWolfHole;
    private boolean attacking = false;
    private boolean hiding = false;
    protected Animal target = null;
    protected Location preyLocation;

    Wolf(World world, int wolfPackID, WolfPack pack) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = true;
        leader = this;
        this.world = world;
        isOnMap = true;
        energy = 30;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
        isOnMap = true;
        energy = 30;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader, boolean isOnMap) {
        super(30, world, isOnMap);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
        energy = 30;
    }

    @Override
    public void act(World world) {

        if (isOnMap) {
            myLocation = world.getLocation(this);
        }

        if (attacking) {
            huntingBehavior();
            eat();

        } else {
            passiveBehavior();
        }

        tryToDecreaseEnergy();
        //die();

    }

    @Override
    protected void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {

                //if wolf
                if (animal instanceof Wolf wolf) {

                    if (wolf.getWolfPackID() == this.getWolfPackID()) {
                        System.out.println("My homie");
                    } else {
                        wolf.takeDamage(5);
                        if (world.isTileEmpty(neighbor)) {
                            updateEnergy(3);
                        }
                    }
                } else {  //if food
                    animal.takeDamage(5);
                    System.out.println("damage" + animal.getClass());
                    if (world.isTileEmpty(neighbor)) {
                        updateEnergy(3);
                    }
                }

            }
        }
    }

    // pack behavior
    protected void passiveBehavior() {
        if (isOnMap) {
            if (checktime() == TimeOfDay.MORNING) {
                roamBehaviour();
                searchForPrey();
            } else if (checktime() == TimeOfDay.EVENING || checktime() == TimeOfDay.NIGHT) {
                if (myWolfHole != null) {
                    goingHomeBehaviour();
                    tryToHide();
                } else {
                    setHole();
                }
            }
        }


        //wakey wakey
        if (checktime() == TimeOfDay.MORNING && hiding && !isOnMap) {
            emerge();
        }
    }

    protected void roamBehaviour() {
        if (isLeader) { // pack leader
            // move independently
            pathFinder(null);
        } else {

            if (rangeFromLeader(pack.getWolfLeader()) < 2) {
                pathFinder(null);

            } else { // move independently
                pathFinder(leader.getMyLocation());
                //System.out.println("Out of leader range");
                //System.out.println(myLocation.toString());
            }

        }
    }


    protected void searchForPrey() {
        //if rabbit found
        if (getNearestObject(Rabbit.class, 8) != null) {
            System.out.println("search");
            attacking = true;
            preyLocation = getNearestObject(Rabbit.class, 8);
            System.out.println(preyLocation);
        } else {
            preyLocation = null;
        }
    }

    protected void huntingBehavior() {
        if (getNearestObject(Rabbit.class, 8) != null) {
            attacking = true;
            preyLocation = getNearestObject(Rabbit.class, 8);
            System.out.println(preyLocation);
        } else {
            preyLocation = null;
            attacking = false;
        }
        pathFinder(preyLocation);
    }


    protected void goingHomeBehaviour() {
        if (isOnMap) {
            pathFinder(world.getLocation(myWolfHole));
        }
    }

    private void tryToHide() {
        if (isOnMap) {
            int thisX = world.getLocation(this).getX();
            int thisY = world.getLocation(this).getY();

            int rabbitHoleX = world.getLocation(myWolfHole).getX();
            int rabbitHoleY = world.getLocation(myWolfHole).getY();

            if (thisX == rabbitHoleX && thisY == rabbitHoleY) {
                world.remove(this);
                hiding = true;
                isOnMap = false;
                sleep();
            }
        }
    }

    public void emerge() {
        if (hiding && !isOnMap) {
            Location wolfHoleLoc = world.getLocation(myWolfHole);

            //leader bliver skubbet ud af hulen først.
            if (!isLeader && !leader.getIsOnMap() && leader.getIsHiding()) {
                leader.emerge();
            }

            Set<Location> set = world.getEmptySurroundingTiles(wolfHoleLoc);
            ArrayList<Location> emptyEmergeLocations = new ArrayList<>(set);

            if (emptyEmergeLocations.isEmpty()) {
                return;
            }

            if (!(world.getTile(wolfHoleLoc) instanceof Actor)) {
                Random rand = new Random();
                Location emergeLocation = emptyEmergeLocations.get(rand.nextInt(emptyEmergeLocations.size()));

                world.setTile(emergeLocation, this);
                isOnMap = true;
                hiding = false;
                birth();
                status = AnimalStatus.LOOKINGFORFOOD;
            }
        }
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

    public int getWolfPackID() {
        return wolfPackID;
    }

    public Wolf getLeader() {
        return leader;
    }

    public WolfPack getWolfPack() {
        return pack;
    }

    @Override
    protected LifeStage getLifeStage() {
        if (age < 100) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }


    //Hole
    protected void setHole() {
        if ((checktime() == TimeOfDay.EVENING || checktime() == TimeOfDay.NIGHT)) {
            if (isLeader) {
                digHole();
                System.out.println("dig hole");
            } else {
                myWolfHole = leader.getPackHole();
                System.out.println("look for leaders hole");
            }

        }
    }

    protected void digHole() {
        if (myWolfHole == null) {
            Location locOfWolf = world.getLocation(this);
            Object objectOnWolf = world.getNonBlocking(locOfWolf);
            if (!(objectOnWolf instanceof Grass) && objectOnWolf != null) {
                Random rd = new Random();

                for (int i = 1; i <= world.getSize(); i++) {
                    Set<Location> emptyTilesSet = helper.getEmptySurroundingTiles(locOfWolf, i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if (!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }


            } else {
                WolfHole newHole = new WolfHole(world, getMyLocation(), wolfPackID);
                world.setTile(getMyLocation(), newHole);
                newHole.setHasAnimal(true);
                myWolfHole = newHole;
            }
        }
    }

    public WolfHole getPackHole() {
        return myWolfHole;
    }

    public boolean getIsHiding() {
        return hiding;
    }

    public boolean isAttacking() {
        return attacking;
    }

    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

}
