package animal.carnivores;

import animal.*;
import animal.herbivore.Rabbit;
import domainmodel.Helper;
import foliage.BerryBush;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;

/**
 * Bear er klassen til bjørnen. Denne står for adfærden af bjørnen.
 * Bear implementere {@link Actor} og nedarver fra {@link Animal}
 */

public class Bear extends Animal {
    protected Location territoryTopLeftCornor;
    protected Location territoryLowerRightCornor;
    protected ArrayList<Location> territoryTileList;
    protected BearBehavior bearBehavior;
    protected Actor bearTarget;
    protected Location centerOfTerritory;


    public Bear(World world) {
        super(103, world);
        maxHitpoints = 20;
        hitpoints = maxHitpoints;
    }

    /**
     * Starter med at sætte Bjørnenes territorie, derefter kaldes {@link #behavior()}, {@link #ageAnimal()} og metoden {@link #die(boolean, int, int)}.
     * @param world den verden som objektet befinder sig i.
     */
    @Override
    public void act(World world) {
        if (territoryTopLeftCornor == null) {
            setTerritory(world.getLocation(this));
        }

        behavior();
        ageAnimal();
        die(false, 60, 160);
    }

    /**
     * Metoden kalder {@link #takeDamage(int)} hvis der står et dyr ved siden af bjørnen, som bjørnen skal angribe, efterfulgt af at bjørnen enten
     * spiser bær fra en {@link BerryBush}, eller spiser af et {@link Cadavar}.
     */
    @Override
    public void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {
                animal.takeDamage(5);

            } else if (temp instanceof BerryBush bush) {
                bush.eatBerries();
                updateEnergy(3);
            } else if (temp instanceof Cadavar cadavar) {
                cadavar.reduceAmountOfMeat(3);
                updateEnergy(3);
            }
        }
    }

    /**
     * Retunere en lifeStage ud fra hvor mange Steps bjørnen har levet.
     * @return {@link LifeStage}
     */
    @Override
    public LifeStage getLifeStage() {
        if (age < 2) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }

    /**
     * Sætter bjørnens territorie ud fra en fixed størrelse som er 2.
     * @param loc den {@link Location} som dens territorie skal sættes ud fra.
     */
    protected void setTerritory(Location loc) {
        int tSize = 3;
        int startX = loc.getX();
        int startY = loc.getY();
        territoryTopLeftCornor = new Location(startX - tSize, startY - tSize);
        territoryLowerRightCornor = new Location(startX + tSize, startY + tSize);

        territoryTileList = new ArrayList<>(world.getSurroundingTiles(tSize));
        centerOfTerritory = world.getLocation(this);
    }

    /**
     * Tjekker om der befinder sig et {@link Animal} som IKKE er en {@link Rabbit} eller sig selv i territoriet.
     * Hvis der er nogen i territoriet sætter den {@link #bearTarget} til det {@link Animal}.
     */
    protected void isThereSomeoneInMyTerritory() {
        for (Location temp : territoryTileList) {
            if (!world.isTileEmpty(temp)) {
                if (!(world.getTile(temp) instanceof NonBlocking && !(world.getTile(temp) instanceof Rabbit))) {
                    if ((world.getTile(temp) != this)) {

                        bearBehavior = BearBehavior.GETOFMYLAWN;
                        status = AnimalStatus.LOOKINGFORFOOD;
                        bearTarget = (Actor) world.getTile(temp);
                        return;
                    }
                }
            }
        }
        bearBehavior = BearBehavior.PASSIVE;
    }

    /**
     * Kalder {@link #pathFinder(Location)} med {@link World#getLocation(Object)} til det {@link Animal} objekt som {@link #bearTarget} er sat til.
     */
    protected void chaseIntruder() {
        pathFinder(world.getLocation(bearTarget));
    }

    /**
     * Finder det nærmeste som bjørnen kan spise.
     * @return en {@link Location} såfremt der blev fundet noget mad. Ellers returneres null.
     */
    protected Location getNearestBearFood() {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = world.getTile(loc);

                if (entity != null) {

                    if (entity instanceof BerryBush bush) {

                        if (bush.berryState()) {
                            return loc;
                        }
                    }

                    if (entity instanceof Cadavar) {
                        System.out.println(loc);
                        return loc;

                    } else if ((entity instanceof Animal && !(entity instanceof Bear))) {
                        System.out.println(loc);
                        return loc;
                    }

                }
            }
        }
        return null;
    }

    /**
     * Kalder først {@link #isItBabyMakingSeason()} og {@link #isThereSomeoneInMyTerritory()}
     * efterfulgt tjekker den på {@link #bearBehavior} og kalder bestemte metoder ud fra situationen.
     * Såsom; {@link #timeToSexBehavior()}, {@link #chaseIntruder()}, {@link #eat()}  og {@link #normalBehavior()}
     */
    protected void behavior() {
        isItBabyMakingSeason();

        if (bearBehavior != BearBehavior.TIMETOSEX) {
            isThereSomeoneInMyTerritory();
        }

        if (bearBehavior == BearBehavior.TIMETOSEX) {
            status = AnimalStatus.LOOKINGFORFOOD;
            timeToSexBehavior();
            updateEnergy(-1);

        } else if (bearBehavior == BearBehavior.GETOFMYLAWN) {
            chaseIntruder();
            eat();
            updateEnergy(-1);

        } else {
            normalBehavior();
        }

    }

    /**
     * Styre dens {@link BearBehavior#PASSIVE} Behavior. Enten leder bjørnen efter mad ved kald af {@link #pathFinder(Location)} eller sover.
     * Bjørnen vil dog altid vandre tilbage til centrum af dens territorie, såfremt den bevæger sig ud (dog ved mindre dens {@link #bearBehavior} er sat til {@link BearBehavior#TIMETOSEX}.
     */
    protected void normalBehavior() {
        if (checktime() == TimeOfDay.MORNING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            if(!haveILeftHome()) {
                pathFinder(getNearestBearFood());
            } else {
                pathFinder(centerOfTerritory);
            }
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.EVENING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            if(!haveILeftHome()) {
                pathFinder(getNearestBearFood());
            } else {
                pathFinder(centerOfTerritory);
            }
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.NIGHT && bearBehavior != BearBehavior.GETOFMYLAWN) {
            status = AnimalStatus.SLEEPING;
            updateEnergy(1);
            healHitPoints(2);
        }
    }

    /**
     * Finder den nærmeste mulige {@link Bear}/parrings partner, eller returnere Null hvis der ingen er.
     * @return {@link Location} af nærmeste parrings partner {@link Bear} eller Null.
     */
    protected Location locateMaid() {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = world.getTile(loc);

                if ((entity instanceof Bear maidBear)) {

                    if (maidBear.getSex() != sex && maidBear != this) {
                        return loc;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Får bjørnen til at søge mod sin partner, hvis {@link #locateMaid()} ikke returnere Null.
     * Herefter tjekker den {@link Bear} som har sat sin {@link #sex} til {@link Sex#FEMALE} om den står ved siden af en {@link Bear} som har sat sin {@link #sex} til {@link Sex#MALE}
     * Hvis de gør; {@link #pregnant} sat til true og deres {@link #bearBehavior} bliver sat til {@link BearBehavior#PASSIVE}. Det samme gælder for bjørnens partner.
     * Hvis {@link Bear} {@link #sex} er {@link Sex#MALE} bliver {@link #bearBehavior} sat til {@link BearBehavior#PASSIVE} hvis den står ved siden af en {@link Bear} hvis {@link #sex} er {@link Sex#FEMALE}.
     */
    protected void timeToSexBehavior() {
        if (locateMaid() == null) {
            bearBehavior = BearBehavior.PASSIVE;
            return;
        }
        pathFinder(locateMaid());
        if (sex != Sex.MALE && !pregnant && isNeighbourMale(this)) {
            reproduce();
            bearBehavior = BearBehavior.PASSIVE;
            ArrayList<Location> temp = surrondingLocationsList(1);
            for (Location loc : temp) {
                Object entity = world.getTile(loc);
                if (entity instanceof Bear maidBear) {
                    if (maidBear.getSex() == Sex.MALE) {
                        maidBear.setBearBehavior(BearBehavior.PASSIVE);
                        System.out.println("near is now passive");
                    }

                }
            }
            if (pregnant) {
                birth();
            }
        }
        if (sex == Sex.MALE && isNeighbourFemale(this)) {
            bearBehavior = BearBehavior.PASSIVE;
        }
    }

    /**
     * Sætter {@link #bearBehavior} til {@link BearBehavior#TIMETOSEX} hvert 40'ende step i simulationen.
     */
    protected void isItBabyMakingSeason() {
        if (Helper.getSteps() % 40 == 0) {
            bearBehavior = BearBehavior.TIMETOSEX;
        }
    }

    /**
     * Retunere den korrekte DisplayInformation baseret på bjørnens nuværende tilstand.
     * @return {@link DisplayInformation}
     */
    @Override
    public DisplayInformation getInformation() {

        if (status == AnimalStatus.SLEEPING) {
            if (getLifeStage() == LifeStage.CHILD) {
                return new DisplayInformation(Color.BLACK, "bear-small-sleeping");
            } else {
                return new DisplayInformation(Color.BLACK, "bear-sleeping");
            }
        } else {

            if (getLifeStage() == LifeStage.CHILD) {
                return new DisplayInformation(Color.BLACK, "bear-small");
            } else {
                return new DisplayInformation(Color.BLACK, "bear");
            }
        }
    }

    /**
     * Tjekker om det {@link Object} som er nabo til bjørnen har sit {@link #sex} sat til {@link Sex#FEMALE}.
     * @param animal det animal der skal tjekkes.
     * @return boolean hvorvidt det er en {@link Bear} og om den har sit {@link #sex} sat til {@link Sex#FEMALE}.
     */
    public boolean isNeighbourFemale(Animal animal) {
        ArrayList<Location> neighbours = surrondingLocationsList();
        for (Location neighbor : neighbours) {

            Object objectOnTile = world.getTile(neighbor);

            try {
                if (animal.getClass() == objectOnTile.getClass()) {
                    return ((Animal) objectOnTile).getSex() == Sex.FEMALE;
                }
            } catch (NullPointerException e) {
                return false;
            }


        }
        return false;
    }

    /**
     * Kan bruges til at sætte bjørnens {@link #bearBehavior}.
     * @param bearBehavior er den {@link BearBehavior} man ønsker bjørnen skal have.
     */
    public void setBearBehavior(BearBehavior bearBehavior) {
        this.bearBehavior = bearBehavior;
    }

    /**
     * Tjekker om bjørnen er ud for sit territory, hvis ja retunere den TRUE.
     * @return boolean
     */
    protected boolean haveILeftHome(){
        for (Location temp : territoryTileList) {
            if (!world.isTileEmpty(temp)) {
                if ((world.getTile(temp) == this)) {
                    return false;
                }
            }
        }
        return true;
    }
}
