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

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    private Hole myRabbitHole;
    private boolean hiding = false;

    public Rabbit(World world) {
        super(10000, world);
        hitpoints = 25;
        maxHitpoints = hitpoints;
        this.setSex();
        energy = 10000;
    }

    public Rabbit(World world, boolean isOnMap) {
        super(10000, world, isOnMap);
        this.setSex();
        energy = 10000;
        hitpoints = 25;
        maxHitpoints = hitpoints;
    }

    @Override
    public void act(World world) {


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

        ageRabbit();
        tryToDecreaseEnergy();
        die();
    }

    @Override
    protected void eat() {
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
                    Set<Location> emptyTilesSet = helper.getEmptySurroundingTiles(locOfRabbit,i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if(!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }


            } else {
                if (isItGrass()) {
                    eat();
                }
                Hole newHole = new RabbitHole(world, world.getLocation(this));
                world.setTile(locOfRabbit, newHole);
                newHole.setHasAnimal(true);
                myRabbitHole = newHole;
            }

        }
    }

    private RabbitHole findHoleWithoutOwner() {
        Map<Object, Location> allEntities = world.getEntities();

        for (Object object : allEntities.keySet()) {
            if (object instanceof RabbitHole rh) {
                if (!rh.getHasAnimal()) {
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

            if(myRabbitHole == null) {
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
                isOnMap = true;
                hiding = false;
                birth();
            }
        }
    }

    public AnimalStatus getStatus() {
        return status;
    }

    private void ageRabbit() {
        age++;
        maxEnergy--;
    }

    private void lookingForFoodBehaviour() {
        if (isOnMap) {

            this.status = AnimalStatus.LOOKINGFORFOOD;

            pathFinder(getNearestObject(Grass.class, 10));
            if (isItGrass()) {
                eat();
            }
            updateEnergy(-1);
        }
    }

    private void goingHomeBehaviour() {
        if (isOnMap) {
            status = AnimalStatus.GOINGHOME;
            if (myRabbitHole == null) {
                Hole rh = findHoleWithoutOwner();

                if (rh == null) {
                    digHole();
                } else {
                    rh.setHasAnimal(true);
                    myRabbitHole = rh;
                }

            } else {
                pathFinder(world.getLocation(myRabbitHole));
            }
        }
    }

    @Override
    protected LifeStage getLifeStage() {
        if(age < 40) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if(getLifeStage() == LifeStage.CHILD) {
            return new DisplayInformation(Color.red, "rabbit-small");
        } else {
            return new DisplayInformation(Color.red, "rabbit-large");
        }



    }
}