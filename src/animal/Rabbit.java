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

/**
 * Rabbit er bunden af fødekæden, de spiser græs og graver huller.
 */
public class Rabbit extends Animal {

    private RabbitHole myRabbitHole;
    private boolean hiding;

    public Rabbit(World world) {
        super(15, world);
        hitpoints = 5;
        maxHitpoints = hitpoints;
        hiding = false;
    }

    public Rabbit(World world, boolean isOnMap) {
        super(15, world, isOnMap);
        hitpoints = 5;
        maxHitpoints = hitpoints;
        hiding = false;
    }

    /**
     * act styre Rabbits adfær, hvis det er morning leder de efter mad og parrer med hinanden.
     * hvis det er evening eller nat løber Rabbits tilbage til deres hul så de kan sove.
     * hvis det er nat og de gemmer sig i sit hul sover de.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
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

    /**
     * eat tjekker om der er gras under kaninen, hvis der er græs fjerne den det og giver kaninen noget energy.
     */
    @Override
    public void eat() {
        if (isItGrass()) {
            Grass victim = (Grass) world.getNonBlocking(world.getLocation(this));
            victim.deleteGrass();
            updateEnergy(4);
        }
    }

    /**
     * digHole, hvis kaninen ikke har et hul graver den et nyt.
     * hvis der ikke er plads under kaninen til et hul sletter den det under, hvis der allerede er et hul der hvor den står
     * rykker den sig egang til et tomt felt inden for range 1 og graver sit hul der, hvis der ikke er plads heller gør den intet.
     */
    private void digHole() {
        if (myRabbitHole == null) {
            Location locOfRabbit = world.getLocation(this);
            Object objectOnRabbit = world.getNonBlocking(locOfRabbit);
            if (!(objectOnRabbit instanceof Grass || objectOnRabbit instanceof Cadavar) && objectOnRabbit != null) {
                Random rd = new Random();

                for (int i = 1; i <= world.getSize(); i++) {
                    Set<Location> emptyTilesSet = Helper.getEmptySurroundingTiles(world, locOfRabbit, i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if (!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }


            } else {
                if (isItGrass()) {
                    eat();
                } else if(objectOnRabbit instanceof Cadavar c) {
                    world.delete(c);
                }
                RabbitHole newHole = new RabbitHole(world, world.getLocation(this));
                world.setTile(locOfRabbit, newHole);
                myRabbitHole = newHole;
                newHole.addRabbit(this);
            }

        }
    }

    /**
     * findHoleWithoutOwner tjekker om der er nogen huller uden en ejer, hvis ja retuner den Rabbithole.
     * @return
     */
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

    /**
     * tryToDecreaseEnergy  kalde updateEnergy hvis kaninen IKKE sover.
     */
    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    /**
     * isItGrass tjekker om det felt kaninen står på også indeholder græs.
     * @return
     */
    private boolean isItGrass() {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
    }

    /**
     * tryTohide tjekker først om kaninen har et hul, hvis ne kalder den digHole metoden, hvis ja tjekker den om den står på sit hul,
     * hvis den gør gemmer den sig i hullet.
     */
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

    /**
     * emerge, hvis kaninen har sit hiding = true og den ikke er på kortet, kravler den ud af sit hul, og birth bliver kaldt til sidst.
     */
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

    /**
     * getStatus retunere status.
     * @return
     */
    public AnimalStatus getStatus() {
        return status;
    }

    /**
     * lookingForFoodBehaviour kalder pathfinder med getNearestObject i forhold til Grass.
     */
    private void lookingForFoodBehaviour() {
        if (isOnMap) {
            this.status = AnimalStatus.LOOKINGFORFOOD;
            pathFinder(getNearestObject(Grass.class, 10));
            if (isItGrass()) {
                eat();
            }
        }
    }

    /**
     * goingHomeBehaviour, hvis kaninen ikke har et hul prøver den at finde et, hvis den ikke kan finde et graver den et nyt.
     * når kaninen har et hul pathfinder den imod det.
     */
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

    /**
     * getLifeStage retunere LifeStage.
     * @return
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
     * getInformation retunerer den korrekt DisplayInformation i forhold til kaninens status.
     * @return
     */
    @Override
    public DisplayInformation getInformation() {
        if (getLifeStage() == LifeStage.CHILD) {
            return new DisplayInformation(Color.red, "rabbit-small");
        } else {
            return new DisplayInformation(Color.red, "rabbit-large");
        }

    }

    /**
     * getMyRabbitHole retunerer kaninens RabbitHole.
     * @return
     */
    public RabbitHole getMyRabbitHole() {
        return myRabbitHole;
    }
}
