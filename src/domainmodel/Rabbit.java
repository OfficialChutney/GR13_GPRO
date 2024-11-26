package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public class Rabbit implements Actor {
    private int age = 0;
    private int maxEnergy = 15;
    private int energy;
    private RabbitHole myRabbitHole;
    private boolean isInRabbitHole;
    private Sex sex;
    private boolean pregnant;
    private RabbitStatus status;
    private boolean hiding = false;


    public Rabbit() {
        this.setSex();
        energy = 15;
    }

    @Override
    public void act(World world) {

        TimeOfDay currentTime = this.checktime(world);
        if (currentTime == TimeOfDay.MORNING) {
            emerge(world);
            lookingForFoodBehaviour(world);
            tryToReproduce(world);
        } else if ((currentTime == TimeOfDay.EVENING || currentTime == TimeOfDay.NIGHT) && !hiding) {
            goingHomeBehaviour(world);
            tryTohide(world);
        } else if (currentTime == TimeOfDay.NIGHT && hiding) {
            sleep(world);
        }

        tryTodie(world);
        tryToDecreaseEnergy();
    }

    private void tryToReproduce(World world) {
        if (sex == Sex.FEMALE) {
            if (this.isNeighbourMale(world)) {
                this.pregnant = true;
            }
        }
    }

    private void eat(World world) {
        if (this.isItGrass(world)) {
            Grass victim = (Grass) world.getNonBlocking(world.getLocation(this));
            victim.deleteGrass();
            this.updateEnergy(4);
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

    private void tryTodie(World world) {
        if(energy <= 0) {
            System.out.println("I died");
            world.delete(this);
        }
    }

    private void digHole(World world) {
        System.out.println("digging a hole");
        if (this.myRabbitHole == null) {
            RabbitHole newHole = new RabbitHole(world, world.getLocation(this));
            world.setTile(world.getLocation(this), newHole);
            newHole.setHasRabbit(true);
            this.myRabbitHole = newHole;
        }
    }

    private RabbitHole findHoleWithoutOwner(World world) {
        Map<Object, Location> allEntities = world.getEntities();

        for (Object object : allEntities.keySet()) {
            if (object instanceof RabbitHole rh) {
                if (!rh.getHasRabbit()) {
                    return rh;
                }
            }
        }

        return null;


    }

    private void sleep(World world) {
        status = RabbitStatus.SLEEPING;
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

    private void updateEnergy(int num) {
        this.energy += num;
        if(energy > maxEnergy) {
            energy = maxEnergy;
        }
    }
    
    private void tryToDecreaseEnergy() {
        if(status != RabbitStatus.SLEEPING) {
            updateEnergy(-1);
        }
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


    private Location findMyHole(World world) {
        if (this.myRabbitHole == null) {
            return null;
        } else {
            return world.getLocation(this.myRabbitHole);
        }

    }

    private boolean isItGrass(World world) {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
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


    private ArrayList<Location> surrondingEmptyLocationsList(World world) {
        return surrondingEmptyLocationsList(world, this);
    }

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


    private boolean isNeighbourMale(World world) {
        ArrayList<Location> neighbours = this.surrondingLocationsList(world);
        for (Location neighbor : neighbours) {

            if (world.getTile(neighbor) instanceof Rabbit rabbit) {
                return rabbit.getSex() == Sex.MALE;
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


    private void tryTohide(World world) {
        int thisX = world.getLocation(this).getX();
        int thisY = world.getLocation(this).getY();

        int rabbitHoleX = world.getLocation(myRabbitHole).getX();
        int rabbitHoleY = world.getLocation(myRabbitHole).getY();

        if (thisX == rabbitHoleX && thisY == rabbitHoleY) {
            this.isInRabbitHole = true;
            world.remove(this);
            this.hiding = true;

        }


    }

    private void emerge(World world) {
        if (hiding) {
            Location rabbitHoleLoc = world.getLocation(myRabbitHole);
            if (!(world.getTile(rabbitHoleLoc) instanceof Actor)) {
                world.setTile(rabbitHoleLoc, this);
                this.isInRabbitHole = false;
                Random r = new Random();
                this.hiding = false;
                this.ageRabbit();
                birth(world);
            }
        }
    }

    public RabbitStatus getStatus() {
        return this.status;
    }

    private void ageRabbit() {
        age++;
        maxEnergy--;
    }

    private void lookingForFoodBehaviour(World world) {
        this.status = RabbitStatus.LOOKINGFORFOOD;

        this.pathFinder(world, this.getNearestObject(Grass.class, world));
        if (this.isItGrass(world)) {
            this.eat(world);
        }
        this.updateEnergy(-1);
    }

    private void goingHomeBehaviour(World world) {
        this.status = RabbitStatus.GOINGHOME;
        if (this.myRabbitHole == null) {
            RabbitHole rh = findHoleWithoutOwner(world);

            if (rh == null) {
                this.digHole(world);
            } else {
                rh.setHasRabbit(true);
                myRabbitHole = rh;
            }

        } else {
            this.pathFinder(world, world.getLocation(myRabbitHole));
        }
    }

}
