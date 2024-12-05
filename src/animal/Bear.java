package animal;

import domainmodel.Helper;
import domainmodel.TimeOfDay;
import foliage.BerryBush;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Bear er klassen til bjørnen, denne står for adfæren af bjørnen.
 * Bear implimentere Actor og arver fra Animal
 */

public class Bear extends Animal {
    protected Location territoryTopLeftCornor;
    protected Location territoryLowerRightCornor;
    protected ArrayList<Location> territoryTileList;
    protected BearBehavior bearBehavior;
    protected Actor bearTarget;


    public Bear(World world) {
        super(103, world);
        maxHitpoints = 20;
        hitpoints = maxHitpoints;
    }

    /**
     * act starter med at sætte Bjørnenes territorie, derefter starter den bjørnenes behavior, kalder ageAnimal og metoden die.
     * @param world providing details of the position on which the actor is currently located and much more.
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
     * eat metoden kalder animal.takeDamage(5) hvis der står et dyr ved siden af bjørnen, efterfulgt af at bjørnen enden
     * spiser bær fra en busk, eller spiser af et Cadavar.
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
     * GetLifeStage retunere en lifeStage ud fra hvor mange Steps bjørnen har levet.
     * @return LifeStage.CHILD or LifeStage.ADULT
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
     * setTerritory sætter bjørnens territorie ud fra en fixed størrelse som er 2
     * @param loc
     */
    protected void setTerritory(Location loc) {
        int tSize = 2;
        int startX = loc.getX();
        int startY = loc.getY();
        territoryTopLeftCornor = new Location(startX - tSize, startY - tSize);
        territoryLowerRightCornor = new Location(startX + tSize, startY + tSize);

        territoryTileList = new ArrayList<>(world.getSurroundingTiles(tSize));
    }

    /**
     * isThereSomeoneInMyTerritory tjekker om der befinder sig et Animal som IKKE er en Rabbit eller sig selv i territoriet.
     * Hvis der er nogen i territoriet sætter den BearTarget til det Animal.
     */
    protected void isThereSomeoneInMyTerritory() {
        for (int i = 0; i < territoryTileList.size(); i++) {
            Location temp = territoryTileList.get(i);
            if (!world.isTileEmpty(temp)) {
                if (!(world.getTile(temp) instanceof NonBlocking && !(world.getTile(temp) instanceof Rabbit) && !(world.getTile(temp) == this))) {
                    bearBehavior = BearBehavior.GETOFMYLAWN;
                    status = AnimalStatus.LOOKINGFORFOOD;
                    bearTarget = (Actor) world.getTile(temp);
                    return;
                }
            }
        }
        bearBehavior = BearBehavior.PASSIVE;
    }

    /**
     * chaseIntruder kalder pathFinder med World.getLocation til BearTarget.
     */
    protected void chaseIntruder() {
        pathFinder(world.getLocation(bearTarget));
    }

    /**
     * getNearestBearFood finder det nærmeste som bjørnen kan spise.
     * @return Location or Null
     */
    protected Location getNearestBearFood() {
        for (int i = 1; i < 11; i++) {
            ArrayList<Location> temp = surrondingLocationsList(i);

            for (Location loc : temp) {

                Object entity = world.getTile(loc);

                if (entity != null) {

                    if (entity instanceof BerryBush bush) {

                        if (bush.BerryState()) {
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
     * behavior kalder først isItBabyMakingSeason og isThereSomeoneInMyTerritory,
     * efterfulgt tjekker den på bearBehavior og kalder det bestemte metoder ud fra situationen.
     * såsom; timeToSexBehavior, chaseIntruder, eat og normalBehavior.
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
     * normalBehavior styre den Passive Behavior, enden leder bjørnen efter mad eller sover.
     */
    protected void normalBehavior() {
        if (checktime() == TimeOfDay.MORNING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestBearFood());
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.EVENING) {
            status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestBearFood());
            eat();
            updateEnergy(-1);

        } else if (checktime() == TimeOfDay.NIGHT && bearBehavior != BearBehavior.GETOFMYLAWN) {
            status = AnimalStatus.SLEEPING;
            updateEnergy(1);
            healHitPoints(2);
        }
    }

    /**
     * locateMaid finder den nærmeste mulige parrings partner, eller retunere Null hvis der ingen er
     * @return Location or Null
     */
    protected Location locateMaid() {
        Map<Object, Location> entitiesOnMap = world.getEntities();
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
     * timeToSexBehavior får bjørnen til at søge mod sin partner, hvis locateMaid ikke retunered Null.
     * her efter tjekker den kvindelige bjørne om de står ved siden af en mand hver step, hvis de gør;
     * bliver de gravide og deres bearBehavior bliver sat til PASSIVE, samme gælder for bjørnens partner.
     * hvis bjørnen er en mand bliver bearBehavior sat til PASSIVE hvis den står ved siden af en kvinde.
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
                if( entity instanceof Bear maidBear) {
                    if(maidBear.getSex() == Sex.MALE){
                        maidBear.setBearBehavior(BearBehavior.PASSIVE);
                        System.out.println("near is now passive");
                    }

                }
            }
            if (pregnant){
                birth();
            }
        }
        if (sex == Sex.MALE && isNeighbourFemale(this)) {
            bearBehavior = BearBehavior.PASSIVE;
        }
    }

    /**
     * isItBabyMakingSeason sætter bearBehavior til TIMETOSEX hver 40'ende step i simulationen.
     */
    protected void isItBabyMakingSeason() {
        if (Helper.getSteps() % 40 == 0) {
            bearBehavior = BearBehavior.TIMETOSEX;
        }
    }

    /**
     * getInformation retunere den korrekte DisplayInformation baseret på bjørnens nuværende tilstand.
     * @return DisplayInformation
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
     * isNeighbourFemale tjekker om naboen er en Female.
     * @param animal
     * @return boolean
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
     * setBearBehavior kan bruges til at sætte bjørnens bearBehavior.
     * @param bearBehavior
     */
    public void setBearBehavior(BearBehavior bearBehavior) {
        this.bearBehavior = bearBehavior;
    }
}
