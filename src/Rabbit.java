import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.sql.Time;
import java.util.*;

public class Rabbit implements Actor {
    int age = 0;
    int maxEnergy = 15;
    int energy;
    RabbitHole myRabbitHole;
    boolean isInRabbitHole;
    Sex sex;
    boolean pregnant;
    RabbitStatus status;
    boolean hiding = false;


    Rabbit() {
        this.setSex();
    }

    @Override
    public void act(World world) {

        TimeOfDay currentTime = this.checktime(world);
        System.out.println("TEEEEEEEST JEG KØRER");
        if (currentTime == TimeOfDay.MORNING) {
            System.out.println("It is day!");
            if (hiding) {
                System.out.println("i will emerge");
                emerge(world);
                if (pregnant) {
                    birth(world);
                }
            }


            this.status = RabbitStatus.LOOKINGFORFOOD;


            this.pathFinder(world, this.getNearestGrass(world));
            if (this.isItGrass(world)) {
                this.eat(world);
            }
            this.updateEnergy(-1);

            if (sex == Sex.FEMALE) {
                this.reproduce(world);
            }

        } else if ((currentTime == TimeOfDay.EVENING || currentTime == TimeOfDay.NIGHT) && !hiding) {
            this.status = RabbitStatus.GOINGHOME;
            if (this.myRabbitHole == null) {
                this.digHole(world);
            } else if (this.myRabbitHole != null) {
                this.pathFinder(world, world.getLocation(myRabbitHole));
            }

            if (world.getLocation(this) == world.getLocation(myRabbitHole)) {
                hide(world);
            }

            this.updateEnergy(-1);

            if (this.energy <= 0) {
                this.die(world);
            }

        } else if (currentTime == TimeOfDay.NIGHT && hiding) {
            this.sleep(world);
        }
    }

    private void reproduce(World world) {
        Location myTempLoc = world.getLocation(this);

        if (this.isNeighbourMale(world)) {
            this.pregnant = true;
        }
    }

    //fejl
    private void eat(World world) {
        if (this.isItGrass(world)) {
            Grass victim = (Grass) world.getNonBlocking(world.getLocation(this));
            victim.deleteGrass();
            this.updateEnergy(4);
        }
    }

    private void pathFinder(World world, Location destination) {
        if (destination == null) {
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
        world.delete(this);
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

    private void sleep(World world) {
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

    private Location getNearestGrass(World world) {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(world, i);
            for (Location loc : temp) {
                world.containsNonBlocking(loc);
                if (world.getTile(loc) instanceof Grass) {
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
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
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


    private void hide(World world) {
        if (this.checktime(world) == TimeOfDay.NIGHT) {
            this.isInRabbitHole = true;
            world.remove(this);
            this.hiding = true;
        }
    }

    private void emerge(World world) {
        this.isInRabbitHole = false;
        Random r = new Random();
        world.setTile(world.getLocation(myRabbitHole), this);
        this.hiding = false;
        this.ageRabbit();
    }

    public RabbitStatus getStatus() {
        return this.status;
    }

    private void ageRabbit(){
        age++;
        maxEnergy--;
    }

}
