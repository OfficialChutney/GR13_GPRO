package animal;

import foliage.Grass;
import hole.Hole;
import domainmodel.*;
import hole.RabbitHole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.*;

public class Rabbit extends Animal {

    private RabbitHole myRabbitHole;
    private boolean hiding;

    public Rabbit(World world) {
        super(10000, world);
        setRandomSex();
        hitpoints = 1;
        maxHitpoints = hitpoints;
        hiding = false;
    }

    public Rabbit(World world, boolean isOnMap) {
        super(15, world, isOnMap);
        this.setRandomSex();
        hitpoints = 1;
        maxHitpoints = hitpoints;
        hiding = false;
    }

    @Override
    public void act(World world) {

        try {
            TimeOfDay currentTime = checktime();
            if (currentTime == TimeOfDay.MORNING) {
                emerge();
                lookingForFoodBehaviour();
                reproduce();
            } else if ((currentTime == TimeOfDay.EVENING || currentTime == TimeOfDay.NIGHT) && !hiding) {
                goingHomeBehaviour();
                tryTohide();
            } else if (currentTime == TimeOfDay.NIGHT && hiding) {
                sleep();
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + " " + this.toString());
        }

        ageAnimal();
        tryToDecreaseEnergy();
        die(false,3,80);
    }

    @Override
    public void eat() {
        if (isItGrass()) {
            Grass victim = (Grass) world.getNonBlocking(world.getLocation(this));
            victim.deleteGrass();
            updateEnergy(4);
        }
    }

    private void digHole() {
        if (myRabbitHole == null) {
            Location locOfRabbit = world.getLocation(this);
            Object objectOnRabbit = world.getNonBlocking(locOfRabbit);
            if (!(objectOnRabbit instanceof Grass) && objectOnRabbit != null) {
                Random rd = new Random();

                for (int i = 1; i <= world.getSize(); i++) {
                    Set<Location> emptyTilesSet = helper.getEmptySurroundingTiles(world, locOfRabbit, i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if (!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }


            } else {
                if (isItGrass()) {
                    eat();
                }
                RabbitHole newHole = new RabbitHole(world, world.getLocation(this));
                world.setTile(locOfRabbit, newHole);
                myRabbitHole = newHole;
                newHole.addRabbit(this);
            }

        }
    }

    private RabbitHole findHoleWithoutOwner() {
        Map<Object, Location> allEntities = world.getEntities();

        for (Object object : allEntities.keySet()) {
            if (object instanceof RabbitHole rh) {
                if (rh.getRabbitsInHole().size() < 6) {
                    return rh;
                }
            }
        }
        return null;
    }

    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    private boolean isItGrass() {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
    }


    private void tryTohide() {
        if (isOnMap) {
            if (myRabbitHole == null) {
                digHole();
                return;
            } else if (myRabbitHole.getRabbitsInHole().size() > 6) {
                myRabbitHole.removeRabbit(this);
                myRabbitHole = null;
                digHole();
                return;
            }


            int thisX = world.getLocation(this).getX();
            int thisY = world.getLocation(this).getY();

            int rabbitHoleX = world.getLocation(myRabbitHole).getX();
            int rabbitHoleY = world.getLocation(myRabbitHole).getY();

            if (thisX == rabbitHoleX && thisY == rabbitHoleY) {
                world.remove(this);
                hiding = true;
                isOnMap = false;

            }
        }
    }

    private void emerge() {
        if (hiding && !isOnMap) {
            Location rabbitHoleLoc = world.getLocation(myRabbitHole);
            if (!(world.getTile(rabbitHoleLoc) instanceof Actor)) {
                world.setTile(rabbitHoleLoc, this);
                world.getLocation(this);
                isOnMap = true;
                hiding = false;
                birth();
            }
        }
    }

    public AnimalStatus getStatus() {
        return status;
    }


    private void lookingForFoodBehaviour() {
        if (isOnMap) {
            this.status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestObject(Grass.class, 10));
            if (isItGrass()) {
                eat();
            }
        }
    }

    private void goingHomeBehaviour() {
        if (isOnMap) {
            status = AnimalStatus.GOINGHOME;
            if (myRabbitHole == null) {
                RabbitHole rh = findHoleWithoutOwner();

                if (rh == null) {
                    digHole();
                } else {
                    myRabbitHole = rh;
                    rh.addRabbit(this);
                }

            } else {
                pathFinder(world.getLocation(myRabbitHole));
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

    @Override
    public DisplayInformation getInformation() {
        if (getLifeStage() == LifeStage.CHILD) {
            return new DisplayInformation(Color.red, "rabbit-small");
        } else {
            return new DisplayInformation(Color.red, "rabbit-large");
        }

    }

    public RabbitHole getMyRabbitHole() {
        return myRabbitHole;
    }
}
