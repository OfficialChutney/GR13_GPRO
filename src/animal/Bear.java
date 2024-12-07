package animal;

import domainmodel.Helper;
import domainmodel.TimeOfDay;
import foliage.BerryBush;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Bear extends Animal {
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
        if (territoryTopLeftCornor == null) {
            setTerritory(world.getLocation(this));
        }

        behavior();
        ageAnimal();
        die(false, 60, 160);
    }

    @Override
    public void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {
                animal.takeDamage(5);

            } else if (temp instanceof BerryBush bush) {
                bush.eatBerries();
                updateEnergy(3);
            } else if (temp instanceof Cadavar cadavar) {
                cadavar.reduceAmountOfMeat(3);
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
        int tSize = 2;
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
                if (!(world.getTile(temp) instanceof NonBlocking && !(world.getTile(temp) instanceof Rabbit) && !(world.getTile(temp) == this))) {
                    bearBehavior = BearBehavior.GETOFMYLAWN;
                    status = AnimalStatus.LOOKINGFORFOOD;
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

                    if (entity instanceof BerryBush bush) {

                        if (bush.BerryState()) {
                            return loc;
                        }
                    }

                    if (entity instanceof Cadavar) {
                        System.out.println(loc);
                        return loc;

                    } else if ((entity instanceof Animal && !(entity instanceof Bear))) {
                        System.out.println(loc);
                        return loc;
                    }

                }
            }
        }
        return null;
    }

    protected void behavior() {
        isItBabyMakingSeason();

        if (bearBehavior != BearBehavior.TIMETOSEX) {
            isThereSomeoneInMyTerritory();
        }

        if (bearBehavior == BearBehavior.TIMETOSEX) {
            status = AnimalStatus.LOOKINGFORFOOD;
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

        } else if (checktime() == TimeOfDay.NIGHT && bearBehavior != BearBehavior.GETOFMYLAWN) {
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
            ArrayList<Location> tempList = new ArrayList<>(world.getSurroundingTiles(world.getLocation(this)));
            for (Location loc : tempList) {
                if (loc == locateMaid()) {
                    bearBehavior = BearBehavior.PASSIVE;
                }
            }
        }
    }

    protected void isItBabyMakingSeason() {
        if (Helper.getSteps() % 40 == 0) {
            bearBehavior = BearBehavior.TIMETOSEX;
        }
    }

    @Override
    public DisplayInformation getInformation() {

        if (status == AnimalStatus.SLEEPING) {
            if (getLifeStage() == LifeStage.CHILD) {
                return new DisplayInformation(Color.BLACK, "bear-small-sleeping");
            } else {
                return new DisplayInformation(Color.BLACK, "bear-sleeping");
            }
        } else {

            if (getLifeStage() == LifeStage.CHILD) {
                return new DisplayInformation(Color.BLACK, "bear-small");
            } else {
                return new DisplayInformation(Color.BLACK, "bear");
            }
        }
    }
}
