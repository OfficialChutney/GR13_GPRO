package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Rabbit extends Animal implements Actor {

    private Hole myRabbitHole;
    private boolean hiding = false;


    public Rabbit(World world) {
        super(10000, world);
        this.setSex();
        energy = 10000;
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

        die();
        tryToDecreaseEnergy();
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
        if (this.myRabbitHole == null) {
            Location locOfRabbit = world.getLocation(this);
            Object objectOnRabbit = world.getNonBlocking(locOfRabbit);
            if(!(objectOnRabbit instanceof Grass) && objectOnRabbit != null) {
                Random rd = new Random();
                ArrayList<Location> emptyTiles = surrondingEmptyLocationsList();
                world.move(this, emptyTiles.get(rd.nextInt(emptyTiles.size())));
            } else {
                if(isItGrass()) {
                    eat();
                }
                Hole newHole = new Hole(world, world.getLocation(this),HoleType.RABBITHOLE);
                world.setTile(locOfRabbit, newHole);
                newHole.setHasAnimal(true);
                myRabbitHole = newHole;
            }

        }
    }

    private Hole findHoleWithoutOwner() {
        Map<Object, Location> allEntities = world.getEntities();

        for (Object object : allEntities.keySet()) {
            if (object instanceof Hole rh) {
                if (!rh.getHasAnimal() && rh.getHoleType() == HoleType.RABBITHOLE) {
                    return rh;
                }
            }
        }
        return null;
    }
    
    private void tryToDecreaseEnergy() {
        if(status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    private boolean isItGrass() {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
    }



    private void tryTohide() {
        int thisX = world.getLocation(this).getX();
        int thisY = world.getLocation(this).getY();

        int rabbitHoleX = world.getLocation(myRabbitHole).getX();
        int rabbitHoleY = world.getLocation(myRabbitHole).getY();

        if (thisX == rabbitHoleX && thisY == rabbitHoleY) {
            world.remove(this);
            hiding = true;

        }
    }

    private void emerge() {
        if (hiding) {
            Location rabbitHoleLoc = world.getLocation(myRabbitHole);
            if (!(world.getTile(rabbitHoleLoc) instanceof Actor)) {
                world.setTile(rabbitHoleLoc, this);
                hiding = false;
                ageRabbit();
                birth();
            }
        }
    }

    public AnimalStatus getStatus() {
        return this.status;
    }

    private void ageRabbit() {
        age++;
        maxEnergy--;
    }

    private void lookingForFoodBehaviour() {
        this.status = AnimalStatus.LOOKINGFORFOOD;

        pathFinder(getNearestObject(Grass.class));
        if (this.isItGrass()) {
            eat();
        }
        this.updateEnergy(-1);
    }

    private void goingHomeBehaviour() {
        this.status = AnimalStatus.GOINGHOME;
        if (this.myRabbitHole == null) {
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
