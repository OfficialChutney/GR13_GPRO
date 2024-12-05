package animal;

import domainmodel.*;
import hole.Hole;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public abstract class Animal implements Actor, DynamicDisplayInformationProvider {
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
    protected boolean canDie;
    protected boolean canGetPregnant;
    protected int startMaxEnergy;
    protected boolean canEnergyDecrease;


    public Animal(int maxEnergy, World world) {
        startMaxEnergy = maxEnergy;
        this.maxEnergy = maxEnergy;
        energy = maxEnergy;
        this.world = world;
        rd = new Random();
        isOnMap = true;
        pregnant = false;
        canDie = true;
        canGetPregnant = true;
        age = 0;
        setRandomSex();
        canEnergyDecrease = true;
    }

    public abstract void eat();

    public abstract LifeStage getLifeStage();

    public void reproduce() {
        if (isOnMap && canGetPregnant) {
            if (sex == Sex.FEMALE && getLifeStage() == LifeStage.ADULT) {
                if (isNeighbourMale(this)) {
                    pregnant = true;
                }
            }
        }
    }

    public void pathFinder(Location destination) {

        if (isOnMap) {

            if (destination == null) {
                Set<Location> sorroundingLocations = world.getEmptySurroundingTiles(world.getLocation(this));
                ArrayList<Location> sourroundingLocationsAsList = new ArrayList<>(sorroundingLocations);

                if (!sourroundingLocationsAsList.isEmpty()) {

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
                ArrayList<Location> alternativeLocations = new ArrayList<>(Helper.getEmptySurroundingTiles(world, start, 1));
                if (!alternativeLocations.isEmpty()) {
                    world.move(this, alternativeLocations.get(rd.nextInt(alternativeLocations.size())));
                }

            }
        }
    }

    public void die(boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        if ((energy <= 0 || hitpoints <= 0) && canDie) {
            Location temp = world.getLocation(this);
            System.out.println("I died");
            world.delete(this);
            isOnMap = false;

            if (this instanceof Rabbit r) {
                if (r.getMyRabbitHole() != null) {
                    r.getMyRabbitHole().removeRabbit(r);
                }

            }

            if(world.getNonBlocking(temp) instanceof NonBlocking nonBlocking) {

                if(nonBlocking instanceof Hole) {
                    Set<Location> ltsc = world.getSurroundingTiles(temp);
                    ArrayList<Location> locToSetCadaver = new ArrayList<>();

                    for (Location l : ltsc) {
                        if(!(world.containsNonBlocking(l))) {
                            locToSetCadaver.add(l);
                        }

                    }


                    if(!locToSetCadaver.isEmpty()) {
                        temp = locToSetCadaver.get(rd.nextInt(locToSetCadaver.size()));
                    } else {
                        temp = null;
                    }

                } else {
                    world.delete(nonBlocking);
                }
            }

            if(temp != null) {
                Cadavar myCadavar = new Cadavar(world,  mushrooms,  amountOfMeat,  stepsToDecompose);
                world.setTile(temp, myCadavar);
            }

        }
    }

    public void sleep() {
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

    public void updateEnergy(int num) {
        if(num < 0 && !canEnergyDecrease) {
            return;
        }

        energy += num;
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    public TimeOfDay checktime() {
        if (world.getCurrentTime() < 7) {
            return TimeOfDay.MORNING;
        } else if (world.getCurrentTime() >= 7 && world.getCurrentTime() < 10) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }

    public Location getNearestObject(Class<?> object, int range) {

        for (int i = 1; i < range + 1; i++) {
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


    public ArrayList<Location> surrondingEmptyLocationsList() {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
    }

    public ArrayList<Location> surrondingLocationsList() {
        return surrondingLocationsList(1);
    }

    public ArrayList<Location> surrondingLocationsList(int range) {
        Set<Location> neighbours;
        try {
            neighbours = world.getSurroundingTiles(world.getLocation(this), range);
        } catch (IllegalArgumentException e) {
            neighbours = new HashSet<>();
            isOnMap = false;
        }
        return new ArrayList<>(neighbours);

    }

    public boolean isNeighbourMale(Animal animal) {
        ArrayList<Location> neighbours = surrondingLocationsList();
        for (Location neighbor : neighbours) {

            Object objectOnTile = world.getTile(neighbor);

            try {
                if (animal.getClass() == objectOnTile.getClass()) {

                    Animal maleAnimal = (Animal) objectOnTile;

                    if(maleAnimal.getLifeStage() == LifeStage.ADULT) {
                        return maleAnimal.getSex() == Sex.MALE;
                    } else {
                        return false;
                    }
                }
            } catch (NullPointerException e) {
                return false;
            }


        }
        return false;
    }

    public void setRandomSex() {
        switch (rd.nextInt(2)) {
            case 0 -> sex = Sex.MALE;
            case 1 -> sex = Sex.FEMALE;
        }
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Sex getSex() {
        return sex;
    }

    public void birth() {
        if (pregnant) {
            ArrayList<Location> tiles = surrondingEmptyLocationsList();
            System.out.println("I birthed");
            if (tiles.isEmpty()) {
                return;
            }

            Animal child = null;
            if (this instanceof Rabbit) {
                child = new Rabbit(world);
            } else if (this instanceof Wolf w) {
                child = new Wolf(world, w.getWolfPackID(), w.getWolfPack(), w.getLeader());
            } else if (this instanceof Bear){
                child = new Bear(world);
            }

            if (child != null) {
                world.setTile(tiles.get(rd.nextInt(tiles.size())), child);
                pregnant = false;
            }
        }
    }

    public void setOnMap(boolean isOnMap) {
        this.isOnMap = isOnMap;
    }

    public void setCanDie(boolean canDie) {
        this.canDie = canDie;
    }

    public void setCanGetPregnant(boolean canGetPregnant) {
        this.canGetPregnant = canGetPregnant;
    }

    public boolean isPregnant() {
        return pregnant;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public void takeDamage(int damage) {
        hitpoints = hitpoints - damage;
        if(hitpoints <= 0) {
            if(this instanceof Rabbit) {
                die(false,3,80);
            } else if (this instanceof Wolf ) {
                die(false,15,100);
            } else if (this instanceof Bear){
                die(false,60,160);
            }
        }
    }

    public void ageAnimal() {
        if (world.getCurrentTime() == 0) {
            age++;
        }
        maxEnergy = startMaxEnergy - age;
    }

    protected void healHitPoints(int heal) {
        hitpoints = hitpoints + heal;

        if (hitpoints > maxHitpoints) {
            hitpoints = maxHitpoints;
        }
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getAge() {
        return age;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean getIsOnMap() {
        return isOnMap;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void setCanEnergyDecrease(boolean canEnergyDecrease) {
        this.canEnergyDecrease = canEnergyDecrease;
    }

    public int getEnergy() {
        return energy;
    }
}


