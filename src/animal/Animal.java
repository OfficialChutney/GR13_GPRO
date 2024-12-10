package animal;

import animal.carnivores.Bear;
import animal.carnivores.Wolf;
import animal.herbivore.Rabbit;
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
 * Animal er den klasse som alle dyrene nedarver fra. Her findes metoder som bliver brugt på tværs af de forskellige dyr.
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
     * Sætter {@link #pregnant} til true hvis en Animal står ved siden af har sin {@link #sex} sat til {@link Sex#MALE}.
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
     * tager en {@link Location} og bevæger det given Animal et fælt i retningen af den {@link Location}.
     * Hvis der ikke er plads på det felt som den ville rykke sig til rykker den sig til et tilfældigt felt.
     * Hvis {@link Location} er Null bevæger dyret sig tilfældigt rundt.
     * @param destination den {@link Location} som Animal skal bevæge sig imod.
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
     * Sletter dyret fra {@link World} og indsætter et {@link Cadavar} istedet. Input parameterne bestemmer visse kvaliteter af det Cadavar der bliver spawnet.
     * @param mushrooms boolean der bestemmer, hvorvidt {@link Cadavar} er inficeret med fungi, og skal kunne spawn en {@link foliage.Mushroom}
     * @param amountOfMeat int der bestemmer mængden af kød {@link Cadavar} indeholder.
     * @param stepsToDecompose int der bestemer hvor mange steps fra det er spawned, der skal gå før {@link Cadavar} despawner.
     */
    public void die(boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        if ((energy <= 0 || hitpoints <= 0) && canDie && isOnMap) {
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
     * Bruges til dyr som sover i {@link Hole}.
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
     * Trækker fra eller ligger til dyrs nuværende {@link #energy}. {@link #energy} kan ikke være over {@link #maxEnergy}
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
     * Returnere et enum baseret på {@link World#getCurrentTime()}
     * @return {@link TimeOfDay} som er et enum baseret på hvilken tid på dagen der er, baseret på {@link World#getCurrentTime()}.
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
     * Returnere {@link Location} af det nærmeste {@link Object} af en valgfri object type, inden for en valgfri range.
     * @param object det {@link Object} man ønsker at finde
     * @param range den range (int) man ønsker at søge indenfor.
     * @return Returnere den {@link Location} for det nærmeste objekt. Blev der ikke fundet ét objekt indenfor den angivne range, returneres Null.
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
     * Konvertere et {@link Set<Location>} fra metoden {@link World#getEmptySurroundingTiles()} om til en {@link ArrayList<Location>} ud fra dette objekts {@link Location}.
     * @return En {@link ArrayList<Location>}
     */
    public ArrayList<Location> surrondingEmptyLocationsList() {
        Set<Location> neighbours = world.getEmptySurroundingTiles(world.getLocation(this));
        return new ArrayList<>(neighbours);
    }

    /**
     * Retunerer en {@link ArrayList<Location>} fra {@link #surrondingLocationsList(int)} inde for range 1.
     * @return {@link ArrayList<Location>} som er listen af {@link Location} rundt om dette objekt.
     */
    public ArrayList<Location> surrondingLocationsList() {
        return surrondingLocationsList(1);
    }

    /**
     * Retunerer en {@link ArrayList<Location>} fra {@link #surrondingLocationsList(int)} inde for valgfri range.
     * @param range den range man ønsker at søge fra.
     * @return {@link ArrayList<Location>} som er listen af {@link Location} rundt om dette objekt.
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
     * Tjekker alle tiles inde for range 1 og returnere true hvis det er samme instans af {@link Animal} og det pågældende objekt har sin {@link #sex} sat til {@link Sex#MALE}.
     * @param animal det {@link Animal} objekt der skal tjekkes, om det er {@link Sex#MALE}.
     * @return boolean som er true, såfremt nabo objektet er af samme nedarvning af {@link Animal} som den selv, og den har sin {@link #sex} sat til {@link Sex#MALE}.
     *
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
     * Sætter {@link #sex} til enten at være {@link Sex#MALE} eller {@link Sex#FEMALE}, med en 50% chance for hver.
     */
    public void setRandomSex() {
        switch (rd.nextInt(2)) {
            case 0 -> sex = Sex.MALE;
            case 1 -> sex = Sex.FEMALE;
        }
    }

    /**
     * Sætter {@link #sex} til det input parameter man giver.
     * @param sex det {@link Sex} man vil sætte.
     */
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * Returnere det {@link Sex} som objektets {@link #sex} er sat til.
     * @return
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Tjekker om objektets {@link #pregnant} er sat til true. Hvis ja giver det fødsel.
     * Ud fra hvilken nedarvning af {@link Animal} objektet har, placeres et objekt af samme type.
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
     * Sætter {@link #canDie}. Parametre bruges til at sige, om objektet kan dø. Bruges i UnitTests.
     * @param canDie boolean der sættes.
     */
    public void setCanDie(boolean canDie) {
        this.canDie = canDie;
    }

    /**
     * Sætter {@link #canGetPregnant}. Parametre bruges til at sige, om objektet kan blive gravid og reproducere. Bruges i UnitTests.
     * @param canGetPregnant
     */
    public void setCanGetPregnant(boolean canGetPregnant) {
        this.canGetPregnant = canGetPregnant;
    }

    /**
     * isPregnant retunere boolean: {@link #pregnant}.
     * @return
     */
    public boolean isPregnant() {
        return pregnant;
    }

    /**
     * setAge sætter dyrets {@link #age}.
     * @param age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Reducere dyret {@link #hitpoints}, hvis dyret kommer under 1 liv kalder den {@link #die(boolean, int, int)} metoden.
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
     * Gør dyret ældre hver gange der er gået 1 dag (20 steps) ved kald til {@link World#getCurrentTime()}
     */
    public void ageAnimal() {
        if (world.getCurrentTime() == 0) {
            age++;
        }
        maxEnergy = startMaxEnergy - age;
    }

    /**
     * Ligger et antal af {@link #hitpoints} til dyrets nuværende {@link #hitpoints}, den kan ikke nå over {@link #maxHitpoints}.
     * @param heal det int som skal ligges til {@link #hitpoints}
     */
    protected void healHitPoints(int heal) {
        hitpoints = hitpoints + heal;

        if (hitpoints > maxHitpoints) {
            hitpoints = maxHitpoints;
        }
    }

    /**
     * Returnere {@link #maxEnergy}.
     * @return int {@link #maxEnergy}.
     */
    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Returnere {@link #age}.
     * @return int {@link #age}.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sætter {@link #energy}.
     * @param energy den energi som {@link #energy} skal sættes til.
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * Returnere true eller false baseret på om {@link Animal} er på mappet.
     * @return boolean
     */
    public boolean getIsOnMap() {
        return isOnMap;
    }
    /**
     * Sætter {@link #hitpoints}
     * @param hitpoints de hitpoints som {@link #hitpoints} skal sættes til.
     */
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }
    /**
     * Sætter {@link #canEnergyDecrease}. Hvis den er false, kan hitpoints ikke decreases.
     * @param canEnergyDecrease boolean som skal sættes.
     */
    public void setCanEnergyDecrease(boolean canEnergyDecrease) {
        this.canEnergyDecrease = canEnergyDecrease;
    }
    /**
     * Returnere {@link #energy}.
     * @return int {@link #energy}.
     */
    public int getEnergy() {
        return energy;
    }
}


