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

/**
 * Animal er den klasse som alle dyrene bygger på,her i finder du metoder som bliver brugt på tværs af de forskellige dyr.
 */
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

    /**
     * reproduce sætter pregnant til true hvis en kvinde står ved siden af en mand.
     */
    public void reproduce() {
        if (isOnMap && canGetPregnant) {
            if (sex == Sex.FEMALE && getLifeStage() == LifeStage.ADULT) {
                if (isNeighbourMale(this)) {
                    pregnant = true;
                }
            }
        }
    }

    /**
     * pathFinder tager en Location og bevæger det given Animal et fælt i retningen af den Location.
     * hvis der ikke er plads på det felt som den ville rykke sig til rykker den sig til et tilfældigt felt.
     * hvis destination er Null bevæger dyret sig tilfældigt rundt.
     * @param destination
     */
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

    /**
     * die sletter dyret fra world og indsætter et Cadavar istedet. input parameterne bestemmer hvisse kvaliteter af det Cadavar der bliver spawnet.
     * @param mushrooms
     * @param amountOfMeat
     * @param stepsToDecompose
     */
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

    /**
     * sleep metoden bruges til dyr som sover i huller.
     */
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

    /**
     * updateEnergy trækker fra eller ligger til dyres nuværende Energy, energy kan ikke være over maxEnergy
     * @param num
     */
    public void updateEnergy(int num) {
        if(num < 0 && !canEnergyDecrease) {
            return;
        }

        energy += num;
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    /**
     * checktime retunere et enum baseret på world.getCurrentTime.
     * @return TimeOfDay...
     */
    public TimeOfDay checktime() {
        if (world.getCurrentTime() < 7) {
            return TimeOfDay.MORNING;
        } else if (world.getCurrentTime() >= 7 && world.getCurrentTime() < 10) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }

    /**
     * getNearestObject retunerer Location af det tætteste object af en valgfri object type, inden for en valgfri range.
     * @param object
     * @param range
     * @return loc
     */
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

    /**
     * surrondingEmptyLocationsList retunerer en ArrayList af getEmptySurroundingTiles.
     * @return
     */
    public ArrayList<Location> surrondingEmptyLocationsList() {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
    }

    /**
     * surrondingLocationsList retunere en ArrayList af getSurroundingTiles inde for range 1.
     * @return
     */
    public ArrayList<Location> surrondingLocationsList() {
        return surrondingLocationsList(1);
    }

    /**
     * surrondingLocationsList retunere en ArrayList af getSurroundingTiles inde for en valgfri range.
     * @param range
     * @return
     */
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

    /**
     * isNeighbourMale tjekker alle tiles inde for range 1 og retunere true hvis der en en mand af samme type af Animal, såsom en Bear.
     * @param animal
     * @return
     */
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

    /**
     * setRandomSex sætter køndet af dyren til enden MALE eller FEMALE, tilfældigt.
     */
    public void setRandomSex() {
        switch (rd.nextInt(2)) {
            case 0 -> sex = Sex.MALE;
            case 1 -> sex = Sex.FEMALE;
        }
    }

    /**
     * setSex setter dyres køn til det input parameter man giver.
     * @param sex
     */
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * retunere kønnet af Animal.
     * @return
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * birth tjekker om det givet dyr er pregnant, hvis ja giver det fødlse.
     * ud fra hvilket dype dyr det er spawner den nu en unge af samme type.
     */
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

    /**
     * setOnMap sætter isOnMap.
     * @param isOnMap
     */
    public void setOnMap(boolean isOnMap) {
        this.isOnMap = isOnMap;
    }

    /**
     * setCanDie sætter canDie.
     * @param canDie
     */
    public void setCanDie(boolean canDie) {
        this.canDie = canDie;
    }

    /**
     * setCanGetPregnant sætter setCanGetPregnant.
     * @param canGetPregnant
     */
    public void setCanGetPregnant(boolean canGetPregnant) {
        this.canGetPregnant = canGetPregnant;
    }

    /**
     * isPregnant retunere boolean: pregnant.
     * @return
     */
    public boolean isPregnant() {
        return pregnant;
    }

    /**
     * setAge sætter dyret age.
     * @param age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * takeDamage reducere dyret hitpoints, hvis dyret kommer under 1 liv kalder den die metoden.
     * @param damage
     */
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

    /**
     * ageAnimal gør dyret ældre hver gange world.getCurrentTime == 0;
     */
    public void ageAnimal() {
        if (world.getCurrentTime() == 0) {
            age++;
        }
        maxEnergy = startMaxEnergy - age;
    }

    /**
     * healHitPoints ligger et antal af hitpoints til dyrets nuværende hitpoints, den kan ikke nå over maxHitpoints.
     * @param heal
     */
    protected void healHitPoints(int heal) {
        hitpoints = hitpoints + heal;

        if (hitpoints > maxHitpoints) {
            hitpoints = maxHitpoints;
        }
    }

    /**
     * getMaxEnergy retunere maxEnergy.
     * @return
     */
    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * getAge retunere age.
     * @return
     */
    public int getAge() {
        return age;
    }

    /**
     * setEnergy sætter Energy.
     * @param energy
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * getIsOnMap retunere true eller false baseret på om dyret er på mappet.
     * @return
     */
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


