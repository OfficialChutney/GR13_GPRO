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
    World world;

    Animal(World world){
        this.world = world;
    }

    protected abstract void eat();

    private void reproduce(World world) {
        if (sex == Sex.FEMALE) {
            if (this.isNeighbourMale(this ,world)) {
                this.pregnant = true;
            }
        }
    }

    private void pathFinder(World world, Location destination) {
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

    private void die(World world) {
        System.out.println("I died");
        world.delete(this);
    }

    private void sleep(World world) {
        status = RabbitStatus.SLEEPING;
        updateEnergy(1);
    }

    private void updateEnergy(int num) {
        this.energy += num;
    }

    private TimeOfDay checktime(World world) {
        if (world.getCurrentTime() < 7) {
            return TimeOfDay.MORNING;
        } else if (world.getCurrentTime() >= 7 && world.getCurrentTime() < 10) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }

    }

    private Location getNearestObject(Class<?> object, World world) {
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

    protected abstract ArrayList<Location> surrondingEmptyLocationsList(World world);

    private ArrayList<Location> surrondingEmptyLocationsList(World world, Actor actor) {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(actor));
        return new ArrayList<>(neighbours);
    }

    private ArrayList<Location> surrondingLocationsList(World world) {
        return surrondingLocationsList(world, 1);
    }

    private ArrayList<Location> surrondingLocationsList(World world, int range) {
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this), range);
        return new ArrayList<>(neighbours);
    }

    private boolean isNeighbourMale(Animal animal, World world) {
        ArrayList<Location> neighbours = this.surrondingLocationsList(world);
        for (Location neighbor : neighbours) {

            Object objectOnTile = world.getTile(neighbor);

            if(animal.getClass() == objectOnTile.getClass()) {
                return ((Animal) objectOnTile).getSex() == Sex.MALE;
            }


        }
        return false;
    }

    private void setSex() {
        Random r = new Random();
        switch (r.nextInt(2)) {
            case 0 -> this.sex = Sex.MALE;
            case 1 -> this.sex = Sex.FEMALE;
        }
    }

    private Sex getSex() {
        return sex;
    }

    private void birth(World world) {
        if (pregnant) {
            Random r = new Random();
            ArrayList<Location> tiles = surrondingEmptyLocationsList(world);

            Rabbit child = new Rabbit();
            world.setTile(tiles.get(r.nextInt(tiles.size())), child);
            pregnant = false;
        }
    }
}
