package animal.herbivore;

import animal.*;
import foliage.Grass;
import domainmodel.*;
import hole.RabbitHole;
import itumulator.executable.DisplayInformation;
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

    /**
     * Styrer Rabbits adfærd. Hvis det er morgen leder de efter mad og parrer med hinanden. Her kalder de, i rækkefølge, {@link #emerge()}, {@link #lookingForFoodBehaviour()} og {@link #tryTohide()}
     * Hvis det er evening eller nat løber Rabbits tilbage til deres hul så de kan sove. Her kalder de, i rækkefølge, {@link #goingHomeBehaviour()}, {@link #tryTohide()}
     * Hvis det er nat og de gemmer sig i sit hul og sover. Her kalder de {@link #sleep()}
     * @param world world den verden som objektet befinder sig i.
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
     * Tjekker om der er {@link Grass} under kaninen. Hvis der er græs fjerner den det ved kald af {@link Grass#deleteGrass()} og giver kaninen noget energi ved kald af {@link #updateEnergy(int)}.
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
     * Hvis kaninen ikke har et {@link RabbitHole} graver den et nyt.
     * Hvis der ikke er plads under kaninen til et hul sletter den det under, hvis der allerede er et hul der hvor den står
     * rykker den sig ved at kalde {@link #pathFinder(Location)} én gang til et tomt felt inden for range af 1 og graver sit hul der. Hvis der ikke er nogle tomme felter, gør den intet.
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
     * Tjekker om der er nogle huller uden en ejer, hvis ja returner den et {@link RabbitHole}. Ellers returnere den null.
     * @return det specifikke {@link RabbitHole} der ikke har en ejer. Returnere null hvis det ikke kunne findes.
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
     * Kalder {@link #updateEnergy(int)} hvis kaninen IKKE sover. Forsøger at sænke kaninens energi.
     */
    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    /**
     * Tjekker om det felt kaninen står på også indeholder et {@link Grass} objekt.
     * @return en boolean. True såfremt der er {@link Grass} på den {@link Location}, ellers false
     */
    private boolean isItGrass() {
        Location temp = world.getLocation(this);

        return world.getNonBlocking(temp) instanceof Grass;
    }

    /**
     * tryTohide tjekker først om kaninen har et {@link RabbitHole}. Hvis den ikke har, kaldes {@link #digHole()}.
     * Hvis den har et {@link RabbitHole}, men der er mere end 6 kaniner, graver den et nyt hul alligeven ved at kalde {@link #digHole()}.
     * Hvis ikke ovenstående er sandt, fjernes (ikke slettes) kaninen fra hullet, når den står på sit hul.
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
     * Tjekker om kaninen er i sit {@link RabbitHole}. Såfremt den er, kravler den ud af sit hul.
     * Når en kanin kravler ud af sit hul, vil den i samme øjeblik kalde {@link #birth()}.
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
     * Retunere status.
     * @return {@link AnimalStatus}
     */
    public AnimalStatus getStatus() {
        return status;
    }

    /**
     * Kalder {@link #pathFinder(Location)} med {@link #getNearestObject(Class, int)} hvor class er {@link Grass} samt en range på 10.
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
     * Hvis kaninen ikke har et {@link RabbitHole} prøver den at finde et. Hvis den ikke kan finde et graver den et nyt hul via {@link #digHole()}.
     * når kaninen har et hul kaldes {@link #pathFinder(Location)} for at finde imod sit hul.
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
     * Retunere LifeStage.
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
     * Retunerer den korrekt DisplayInformation i forhold til kaninens status.
     * @return {@link DisplayInformation}
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
     * Retunerer kaninens {@link RabbitHole}.
     * @return {@link RabbitHole}
     */
    public RabbitHole getMyRabbitHole() {
        return myRabbitHole;
    }
}
