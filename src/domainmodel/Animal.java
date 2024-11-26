package domainmodel;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public abstract class  Animal {
    protected int age;
    protected int energy;
    protected int maxEnergy;
    protected int hitpoints;
    protected int maxHitpoints;
    protected Sex sex;
    protected boolean pregnant = false;
    protected AnimalStatus status;
    protected World world;

    Animal(int maxEnergy){
        this.maxEnergy = maxEnergy;

    }

    protected abstract void eat(World world);

    protected void reproduce(World world) {
        if (sex == Sex.FEMALE) {
            if (this.isNeighbourMale(this, world)) {
                this.pregnant = true;
            }
        }
    }

    protected void pathFinder(World world, Location destination) {
        if (destination == null) {
            Set<Location> sorroundingLocations = world.getEmptySurroundingTiles(world.getLocation(this));
            ArrayList<Location> sourroundingLocationsAsList = new ArrayList<>(sorroundingLocations);

            if(!sourroundingLocationsAsList.isEmpty()){
                Random rd = new Random();

                world.move(this, sourroundingLocationsAsList.get(rd.nextInt(sourroundingLocationsAsList.size())));
            }
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
        if(energy <= 0) {
            System.out.println("I died");
            world.delete(this);
        }
    }

    protected void sleep(World world) {
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
        this.energy += num;
        if(energy > maxEnergy) {
            energy = maxEnergy;
        }
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



    protected ArrayList<Location> surrondingEmptyLocationsList(World world) {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
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

            try {
                if(animal.getClass() == objectOnTile.getClass()) {
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
