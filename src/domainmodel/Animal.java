package domainmodel;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public abstract class Animal {
    protected int age;
    protected int energy;
    protected int maxEnergy;
    protected int hitpoints;
    protected int maxHitpoints;
    protected Sex sex;
    protected boolean pregnant;
    protected AnimalStatus status;
    protected World world;
    protected Random rd;
    protected boolean isOnMap;
    protected Helper helper;

    Animal(int maxEnergy, World world) {
        this.maxEnergy = maxEnergy;
        this.world = world;
        rd = new Random();
        isOnMap = true;
        pregnant = false;
        helper = new Helper(world);
    }

    Animal(int maxEnergy, World world, boolean isOnMap) {
        this.maxEnergy = maxEnergy;
        this.world = world;
        rd = new Random();
        this.isOnMap = isOnMap;
        pregnant = false;
        helper = new Helper(world);
    }

    protected abstract void eat();

    protected abstract LifeStage getLifeStage();

    protected void reproduce() {
        if (isOnMap) {
            if (sex == Sex.FEMALE && getLifeStage() == LifeStage.ADULT) {
                if (isNeighbourMale(this)) {
                    pregnant = true;
                }
            }
        }
    }

    protected void pathFinder(Location destination) {

        if (destination == null) {
            Set<Location> sorroundingLocations = world.getEmptySurroundingTiles(world.getLocation(this));
            ArrayList<Location> sourroundingLocationsAsList = new ArrayList<>(sorroundingLocations);

            if (!sourroundingLocationsAsList.isEmpty()) {
                Random rd = new Random();

                world.move(this, sourroundingLocationsAsList.get(rd.nextInt(sourroundingLocationsAsList.size())));
            }
            return;

        }
        Location start = world.getLocation(this);

        int movingX = start.getX();
        int movingY = start.getY();

        int destinationY = destination.getY();
        int destinationX = destination.getX();

        if (movingX > destinationX) {
            movingX--;
        } else if (movingX < destinationX) {
            movingX++;
        }

        if (movingY > destinationY) {
            movingY--;
        } else if (movingY < destinationY) {
            movingY++;
        }

        Location onTheMove = new Location(movingX, movingY);

        if (world.isTileEmpty(onTheMove)) {
            world.move(this, onTheMove);
        } else {
            ArrayList<Location> alternativeLocations = new ArrayList<>(helper.getEmptySurroundingTiles(start, 1));
            if (!alternativeLocations.isEmpty()) {
                world.move(this, alternativeLocations.get(rd.nextInt(alternativeLocations.size())));
            }

        }
    }

    protected void die() {
        if (energy <= 0) {
            System.out.println("I died");
            world.delete(this);
            isOnMap = false;
        }
    }

    protected void sleep() {
        status = AnimalStatus.SLEEPING;
        updateEnergy(1);

        //Resolves a bug where sometimes, the rabbit is not removed. Possibly an issue with the library
        //Trying to remove several rabbits at once. This forces the rabbit to be removed in the next step.
        try {
            world.getLocation(this);
            world.remove(this);
        } catch (IllegalArgumentException e) {
            //Do nothing
        }
    }

    protected void updateEnergy(int num) {
        energy += num;
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    protected TimeOfDay checktime() {
        if (world.getCurrentTime() < 7) {
            return TimeOfDay.MORNING;
        } else if (world.getCurrentTime() >= 7 && world.getCurrentTime() < 10) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }

    protected Location getNearestObject(Class<?> object) {

        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);
            for (Location loc : temp) {

                Object objectOnTile;

                if (object.isAssignableFrom(NonBlocking.class)) {
                    objectOnTile = world.getNonBlocking(loc);

                } else {
                    objectOnTile = world.getTile(loc);
                }

                if (object.isInstance(objectOnTile)) {
                    return loc;
                }

            }
        }
        return null;
    }


    protected ArrayList<Location> surrondingEmptyLocationsList() {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
    }

    protected ArrayList<Location> surrondingLocationsList() {
        return surrondingLocationsList(1);
    }

    protected ArrayList<Location> surrondingLocationsList(int range) {
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this), range);
        return new ArrayList<>(neighbours);

    }

    protected boolean isNeighbourMale(Animal animal) {
        ArrayList<Location> neighbours = surrondingLocationsList();
        for (Location neighbor : neighbours) {

            Object objectOnTile = world.getTile(neighbor);

            try {
                if (animal.getClass() == objectOnTile.getClass()) {
                    return ((Animal) objectOnTile).getSex() == Sex.MALE;
                }
            } catch (NullPointerException e) {
                return false;
            }


        }
        return false;
    }

    protected void setSex() {
        Random r = new Random();
        switch (r.nextInt(2)) {
            case 0 -> this.sex = Sex.MALE;
            case 1 -> this.sex = Sex.FEMALE;
        }
    }

    protected Sex getSex() {
        return sex;
    }

    protected void birth() {
        if (pregnant) {
            Random r = new Random();
            ArrayList<Location> tiles = surrondingEmptyLocationsList();

            if (tiles.isEmpty()) {
                return;
            }

            Animal child = null;
            if (this instanceof Rabbit) {
                child = new Rabbit(world, false);
            } else if (this instanceof Wolf w) {
                child = new Wolf(world, w.getWolfPackID(), w.getWolfPack(), w.getLeader(), false);
            }

            if (child != null) {
                System.out.println("I birthed");
                world.setTile(tiles.get(r.nextInt(tiles.size())), child);
                child.setOnMap(true);
                pregnant = false;
            }
        }
    }

    protected void setOnMap(boolean isOnMap) {
        this.isOnMap = isOnMap;
    }
}
