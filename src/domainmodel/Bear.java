package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Collection;
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
    }

    @Override
    protected void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if(temp instanceof Animal animal) {
                animal.takeDamage(5);
                if(world.isTileEmpty(neighbor)){
                    updateEnergy(3);
                }
            } else if(temp instanceof BerryBush bush) {
                bush.eatBerries();
                updateEnergy(3);
            }
        }
    }

    @Override
    protected LifeStage getLifeStage() {
        if (age < 2) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }

    protected void setTerritory(Location loc) {
        int startX = loc.getX();
        int startY = loc.getY();
        territoryTopLeftCornor = new Location(startX - 3, startY - 3);
        territoryLowerRightCornor = new Location(startX + 3, startY + 3);

        territoryTileList = new ArrayList<>(world.getSurroundingTiles(3));
    }

    protected void isThereSomeoneInMyTerritory() {
        for (int i = 0; i < territoryTileList.size(); i++) {
            Location temp = territoryTileList.get(i);
            if (!world.isTileEmpty(temp)) {
                if (!(world.getTile(temp) instanceof NonBlocking)) {
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
        isThereSomeoneInMyTerritory();

        if (bearBehavior == BearBehavior.TIMETOSEX) {
            timeToSexBehavior();

        } else if (bearBehavior == BearBehavior.GETOFMYLAWN) {
            chaseIntruder();
            eat();

        } else {
            normalBehavior();
        }

    }

    protected void normalBehavior() {
        if (checktime() == TimeOfDay.MORNING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestBearFood());
            eat();
            //eat or attack
        } else if (checktime() == TimeOfDay.EVENING) {
            status = AnimalStatus.GOINGHOME;
            pathFinder(getNearestBearFood());
            eat();

        } else if (checktime() == TimeOfDay.NIGHT) {
            status = AnimalStatus.SLEEPING;

        }
    }

    protected Location locateMaid() {
        Map<Object, Location> entitiesOnMap = world.getEntities();
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = entitiesOnMap.get(loc);

                if ((entity instanceof Bear maidBear)) {

                    if (maidBear.getSex() != sex) {
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
        surrondingLocationsList();
        if (sex != Sex.MALE && !pregnant && isNeighbourMale(this)) {
            reproduce();
            bearBehavior = BearBehavior.PASSIVE;
        }
    }

    protected void babyMakingSeason() {
        //a methoed that calls all bears to get horny!
        //steps
    }

}
