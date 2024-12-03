package animal;

import domainmodel.TimeOfDay;
import foliage.BerryBush;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class Bear extends Animal implements Actor {
    protected Location territoryTopLeftCornor;
    protected Location territoryLowerRightCornor;
    protected ArrayList<Location> territoryTileList;
    protected BearBehavior bearBehavior;
    protected Actor bearTarget;


    public Bear(World world) {
        super(103, world);
        maxHitpoints = 20;
        hitpoints = maxHitpoints;
    }

    @Override
    public void act(World world) {
        try {
            if (territoryTopLeftCornor == null) {
                setTerritory(world.getLocation(this));
            }

            beheavior();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        die(false, 60, 160);
    }

    @Override
    public void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {
                animal.takeDamage(5);
                if (world.isTileEmpty(neighbor)) {
                    updateEnergy(3);
                }
            } else if (temp instanceof BerryBush bush) {
                bush.eatBerries();
                updateEnergy(3);
            }
        }
    }

    @Override
    public LifeStage getLifeStage() {
        if (age < 2) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }

    protected void setTerritory(Location loc) {
        int tSize = 1;
        int startX = loc.getX();
        int startY = loc.getY();
        territoryTopLeftCornor = new Location(startX - tSize, startY - tSize);
        territoryLowerRightCornor = new Location(startX + tSize, startY + tSize);

        territoryTileList = new ArrayList<>(world.getSurroundingTiles(tSize));
    }

    protected void isThereSomeoneInMyTerritory() {
        for (int i = 0; i < territoryTileList.size(); i++) {
            Location temp = territoryTileList.get(i);
            if (!world.isTileEmpty(temp)) {
                if (!(world.getTile(temp) instanceof NonBlocking && !(world.getTile(temp) == this))) {
                    bearBehavior = BearBehavior.GETOFMYLAWN;
                    bearTarget = (Actor) world.getTile(temp);
                    return;
                }
            }
        }
        bearBehavior = BearBehavior.PASSIVE;
    }

    protected void chaseIntruder() {
        pathFinder(world.getLocation(bearTarget));
    }

    protected Location getNearestBearFood() {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = world.getTile(loc);

                if (entity != null) {


                    if ((entity instanceof Animal && !(entity instanceof Bear))) {
                        System.out.println(loc);
                        return loc;

                    } else if (entity instanceof BerryBush bush) {
                        if (bush.BerryState()) {
                            return loc;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected void beheavior() {
        isItBabyMakingSeason();

        if (bearBehavior != BearBehavior.TIMETOSEX) {
            isThereSomeoneInMyTerritory();
        }

        if (bearBehavior == BearBehavior.TIMETOSEX) {
            timeToSexBehavior();
            updateEnergy(-1);

        } else if (bearBehavior == BearBehavior.GETOFMYLAWN) {
            chaseIntruder();
            eat();
            updateEnergy(-1);

        } else {
            normalBehavior();
        }

    }

    protected void normalBehavior() {
        if (checktime() == TimeOfDay.MORNING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestBearFood());
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.EVENING) {
            status = AnimalStatus.GOINGHOME;
            pathFinder(getNearestBearFood());
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.NIGHT) {
            status = AnimalStatus.SLEEPING;
            updateEnergy(1);
            healHitPoints(1);
        }
    }

    protected Location locateMaid() {
        Map<Object, Location> entitiesOnMap = world.getEntities();
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = world.getTile(loc);

                if ((entity instanceof Bear maidBear)) {

                    if (maidBear.getSex() != sex && maidBear != this) {
                        return loc;
                    }
                }
            }
        }
        return null;
    }

    protected void timeToSexBehavior() {
        if (locateMaid() == null) {
            bearBehavior = BearBehavior.PASSIVE;
            return;
        }
        pathFinder(locateMaid());
        if (sex != Sex.MALE && !pregnant && isNeighbourMale(this)) {
            reproduce();
            bearBehavior = BearBehavior.PASSIVE;
        }
        if (sex == Sex.MALE) {
            ArrayList<Location> tempList = (ArrayList<Location>) world.getSurroundingTiles(world.getLocation(this));
            for (Location loc : tempList) {
                if (loc == locateMaid()) {
                    bearBehavior = BearBehavior.PASSIVE;
                }
            }
        }
    }

    protected void isItBabyMakingSeason() {
        if (steps % 40 == 0) {
            bearBehavior = BearBehavior.TIMETOSEX;
        }
    }

}
