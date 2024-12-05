package animal;


import domainmodel.Helper;
import domainmodel.TimeOfDay;
import foliage.Grass;
import hole.WolfHole;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class Wolf extends Animal {

    private boolean isLeader;
    private int wolfPackID;
    private WolfPack pack;
    private Wolf leader;
    private Location myLocation;
    private World world;
    private WolfHole myWolfHole;
    private boolean attacking = false;
    private boolean hiding = false;
    protected Wolf wolfTarget = null;
    protected Location preyLocation;
    protected boolean inWolfDuel;

    Wolf(World world, int wolfPackID, WolfPack pack) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = true;
        leader = this;
        this.world = world;
        isOnMap = true;
        energy = 30;
        hitpoints = 10;
        maxHitpoints = hitpoints;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
        isOnMap = true;
        energy = 10;
        hitpoints = 10;
        maxHitpoints = hitpoints;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader, boolean isOnMap) {
        super(30, world, isOnMap);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
        energy = 30;
        hitpoints = 10;
        maxHitpoints = hitpoints;
    }

    @Override
    public void act(World world) {

        if (isOnMap) {
            myLocation = world.getLocation(this);
        }

        if (attacking) {
            huntingBehavior();
            eat();
            System.out.println("attacking");
        } else {
            passiveBehavior();
            tryGetPregnant();
        }

        tryToDecreaseEnergy();

        die(false, 15, 100);
    }

    @Override
    public void die(boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        if (isLeader && hitpoints <= 0) {
            System.out.println("Find new leader");
            appointNewLeader();
        }
        super.die(mushrooms, amountOfMeat, stepsToDecompose);
    }

    @Override
    public void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {

                //if wolf
                if (animal instanceof Wolf wolf) {

                    if (wolf.getWolfPackID() != this.getWolfPackID()) {

                        wolf.takeDamage(2);
                        inWolfDuel = true;
                        wolfTarget = wolf;
                        attacking = true;
                        System.out.println("attacking another wolf");
                        if (world.isTileEmpty(neighbor)) {
                            updateEnergy(3);
                        }
                    }
                } else {  //if food
                    animal.takeDamage(2);
                }

            } else if (temp instanceof Cadavar cadavar) {
                cadavar.reduceAmountOfMeat(3);
                updateEnergy(3);
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
        } else if (leader != null && leader.getIsOnMap()) { // if leader exists

            if (rangeFromLeader(pack.getWolfLeader()) < 2) {
                pathFinder(null);

            } else { // move independently
                pathFinder(leader.getMyLocation());
            }

        } else { // no leader exists
            pathFinder(null);
        }
    }


    protected Location searchForPrey() {
        //if rabbit found

        if (getNearestObject(Cadavar.class, 8) != null && checktime() != TimeOfDay.NIGHT) {
            attacking = true;
            return getNearestObject(Cadavar.class, 8);
        } else if (getNearestObject(Rabbit.class, 8) != null) {
            attacking = true;
            return getNearestObject(Rabbit.class, 8);
        } else if (getNearestObject(Bear.class, 5) != null) {
            attacking = true;
            return getNearestObject(Bear.class, 5);
        } else {
            return null;
        }
    }

    protected void huntingBehavior() {
        if (!inWolfDuel) {
            preyLocation = searchForPrey();
            if (preyLocation == null) {
                attacking = false;
            } else {
                pathFinder(preyLocation);
            }
        } else if (!wolfTarget.getIsOnMap()) {
            inWolfDuel = false;
        } else {
            pathFinder(wolfTarget.getMyLocation());
            System.out.println("in wolf duel");
        }

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

            int wolfHoleX = world.getLocation(myWolfHole).getX();
            int wolfHoleY = world.getLocation(myWolfHole).getY();

            if (thisX == wolfHoleX && thisY == wolfHoleY) {
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
        if (wolfLeader.isOnMap) {
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
        } else return 0;

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
    public LifeStage getLifeStage() {
        if (age < 100) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }


    protected void setHole() {
        if ((checktime() == TimeOfDay.EVENING || checktime() == TimeOfDay.NIGHT)) {
            if (isLeader) {
                digHole();
            } else {
                myWolfHole = leader.getPackHole();
            }

        }
    }

    protected void digHole() {
        if (myWolfHole == null) {
            Location locOfWolf = world.getLocation(this);
            Object objectOnWolf = world.getNonBlocking(locOfWolf);
            if (!(objectOnWolf instanceof Grass || objectOnWolf instanceof Cadavar) && objectOnWolf != null) {
                Random rd = new Random();

                for (int i = 1; i <= world.getSize(); i++) {
                    Set<Location> emptyTilesSet = Helper.getEmptySurroundingTiles(world, locOfWolf, i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if (!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }

            } else {

                if (objectOnWolf != null) {
                    world.delete(objectOnWolf);
                }

                WolfHole newHole = new WolfHole(world, getMyLocation(), wolfPackID);
                world.setTile(getMyLocation(), newHole);
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

    private void appointNewLeader() {
        ArrayList<Wolf> wolfArrayList = new ArrayList<>(wolfPackList());

        // Remove the current wolf (this) from the list
        wolfArrayList.removeIf(wolf -> wolf.equals(this)); // Use equals if overridden

        // Ensure the list is not empty before proceeding
        if (!wolfArrayList.isEmpty()) {
            Random random = new Random();

            // Pick a random wolf from the list
            int randomIndex = random.nextInt(wolfArrayList.size());
            Wolf nextLeader = wolfArrayList.get(randomIndex);
            nextLeader.isLeader = true; // Mark the new leader

            // Update all wolves in the pack
            for (Wolf wolf : wolfArrayList) {
                wolf.leader = nextLeader;
                wolf.setHole(); // Assume this updates their state/location
            }
            System.out.println(nextLeader + " is the new leader!");
        } else {
            System.out.println("No wolves left to appoint as leader.");
        }
    }

    private ArrayList<Wolf> wolfPackList() {
        HashMap<Object, Location> map = (HashMap<Object, Location>) world.getEntities();
        ArrayList<Wolf> wolfArrayList = new ArrayList<>();

        // Collect all wolves in the same pack
        for (Object obj : map.keySet()) {
            if (obj instanceof Wolf wolf) {
                if (wolf.getWolfPackID() == getWolfPackID()) {
                    wolfArrayList.add(wolf);
                }
            }
        }
        System.out.println("Wolf Pack List: " + wolfArrayList);
        return wolfArrayList;
    }

    @Override
    public DisplayInformation getInformation() {
        if (getLifeStage() == LifeStage.CHILD) {
            return new DisplayInformation(Color.red, "wolf-small");
        } else {
            return new DisplayInformation(Color.red, "wolf-large");
        }

    }

    protected void tryGetPregnant() {
        Random rd = new Random();

        if (this.getSex() == Sex.FEMALE && !pregnant && rd.nextFloat(1) < 0.3) {
            System.out.println("single an rdy to mingle");
            ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
            for (Location neighbor : neighborTiles) {
                Object temp = world.getTile(neighbor);
                if (temp instanceof Wolf wolf) {
                    if (wolf.getWolfPackID() == this.getWolfPackID() && wolf.getSex() == Sex.MALE) {
                        pregnant = true;
                        System.out.println("pregnant");
                    }
                }
            }
        }
    }
}
