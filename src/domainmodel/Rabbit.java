package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Rabbit extends Animal implements Actor {

    private RabbitHole myRabbitHole;
    private boolean hiding = false;


    public Rabbit() {
        super(25);
        this.setSex();
        energy = 15;
    }

    @Override
    public void act(World world) {

        TimeOfDay currentTime = this.checktime(world);
        if (currentTime == TimeOfDay.MORNING) {
            emerge(world);
            lookingForFoodBehaviour(world);
            reproduce(world);
        } else if ((currentTime == TimeOfDay.EVENING || currentTime == TimeOfDay.NIGHT) && !hiding) {
            goingHomeBehaviour(world);
            tryTohide(world);
        } else if (currentTime == TimeOfDay.NIGHT && hiding) {
            sleep(world);
        }

        die(world);
        tryToDecreaseEnergy();
    }

    @Override
    protected void eat(World world) {
        if (this.isItGrass(world)) {
            Grass victim = (Grass) world.getNonBlocking(world.getLocation(this));
            victim.deleteGrass();
            this.updateEnergy(4);
        }
    }

    private void digHole(World world) {
        if (this.myRabbitHole == null) {
            Location locOfRabbit = world.getLocation(this);
            Object objectOnRabbit = world.getNonBlocking(locOfRabbit);
            if(!(objectOnRabbit instanceof Grass) && objectOnRabbit != null) {
                Random rd = new Random();
                ArrayList<Location> emptyTiles = surrondingEmptyLocationsList(world);
                world.move(this, emptyTiles.get(rd.nextInt(emptyTiles.size())));
            } else {
                if(isItGrass(world)) {
                    eat(world);
                }
                RabbitHole newHole = new RabbitHole(world, world.getLocation(this));
                world.setTile(locOfRabbit, newHole);
                newHole.setHasRabbit(true);
                this.myRabbitHole = newHole;
            }

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
    
    private void tryToDecreaseEnergy() {
        if(status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    private boolean isItGrass(World world) {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
    }



    private void tryTohide(World world) {
        int thisX = world.getLocation(this).getX();
        int thisY = world.getLocation(this).getY();

        int rabbitHoleX = world.getLocation(myRabbitHole).getX();
        int rabbitHoleY = world.getLocation(myRabbitHole).getY();

        if (thisX == rabbitHoleX && thisY == rabbitHoleY) {
            world.remove(this);
            this.hiding = true;

        }
    }

    private void emerge(World world) {
        if (hiding) {
            Location rabbitHoleLoc = world.getLocation(myRabbitHole);
            if (!(world.getTile(rabbitHoleLoc) instanceof Actor)) {
                world.setTile(rabbitHoleLoc, this);
                Random r = new Random();
                this.hiding = false;
                this.ageRabbit();
                birth(world);
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

    private void lookingForFoodBehaviour(World world) {
        this.status = AnimalStatus.LOOKINGFORFOOD;

        this.pathFinder(world, this.getNearestObject(Grass.class, world));
        if (this.isItGrass(world)) {
            this.eat(world);
        }
        this.updateEnergy(-1);
    }

    private void goingHomeBehaviour(World world) {
        this.status = AnimalStatus.GOINGHOME;
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
