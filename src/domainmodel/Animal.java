package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public abstract class  Animal {
    int age;
    int energy;
    int maxEnergy;
    int hitpoints;
    int maxHitpoints;
    Sex sex;
    boolean pregnant = false;
    RabbitStatus status;

    Animal(){
    }

    protected abstract void eat();

    protected void reproduce(World world) {
        if (sex == Sex.FEMALE) {
            if (this.isNeighbourMale(this ,world)) {
                this.pregnant = true;
            }
        }
    }

    protected void pathFinder(World world, Location destination) {
        if (destination == null) {
            Set<Location> sorroundingLocations = world.getEmptySurroundingTiles(world.getLocation(this));
            ArrayList<Location> sourroundingLocationsAsList = new ArrayList<>(sorroundingLocations);

            Random rd = new Random();

            world.move(this, sourroundingLocationsAsList.get(rd.nextInt(sourroundingLocationsAsList.size())));
            return;


        }
        Location start = world.getLocation(this);
        int movingX = start.getX();
        int movingY = start.getY();

        if (start.getX() > destination.getX()) {
            movingX--;
        } else if (start.getX() < destination.getX()) {
            movingX++;
        }

        if (start.getY() > destination.getY()) {
            movingY--;
        } else if (start.getY() < destination.getY()) {
            movingY++;
        }

        Location onTheMove = new Location(movingX, movingY);

        if (world.isTileEmpty(onTheMove)) {
            world.move(this, onTheMove);
        }
    }

    protected void die(World world) {
        System.out.println("I died");
        world.delete(this);
    }

    protected void sleep(World world) {
        status = RabbitStatus.SLEEPING;
        updateEnergy(1);
    }

    protected void updateEnergy(int num) {
        this.energy += num;
    }

    protected TimeOfDay checktime(World world) {
        if (world.getCurrentTime() < 7) {
            return TimeOfDay.MORNING;
        } else if (world.getCurrentTime() >= 7 && world.getCurrentTime() < 10) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }

    }

    protected Location getNearestObject(Class<?> object, World world) {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(world, i);
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


    protected ArrayList<Location> surrondingLocationsList(World world) {
        return surrondingLocationsList(world, 1);
    }

    protected ArrayList<Location> surrondingLocationsList(World world, int range) {
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this), range);
        return new ArrayList<>(neighbours);
    }

    protected boolean isNeighbourMale(Animal animal, World world) {
        ArrayList<Location> neighbours = this.surrondingLocationsList(world);
        for (Location neighbor : neighbours) {

            Object objectOnTile = world.getTile(neighbor);

            if(animal.getClass() == objectOnTile.getClass()) {
                return ((Animal) objectOnTile).getSex() == Sex.MALE;
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

    protected void birth(World world) {
        if (pregnant) {
            Random r = new Random();
            ArrayList<Location> tiles = surrondingEmptyLocationsList(world);

            Rabbit child = new Rabbit();
            world.setTile(tiles.get(r.nextInt(tiles.size())), child);
            pregnant = false;
        }
    }
}
