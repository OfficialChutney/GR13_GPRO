package animal;


import domainmodel.Helper;
import domainmodel.TimeOfDay;
import foliage.Grass;
import hole.WolfHole;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Ulve klassen bliver skabt af klassen WolfPack. Ulve har forskellige adf�rd afh�ngigt af tiden p� dagen,
 * lederens position, og andre ulveflokkes tilstedev�relse. Ulven har mulighed for selv at bev�ge sig rundt,
 * men bev�ger sig altid inden for en vis afstand af en udpeget ulveleder. Ulven har mulighed for at
 * jage bytte som opdager inden for en r�kkevide. Hvis en ulv m�der en anden kommer de to ulve i duel. Ulveflokke deler
 * ulvehuller, hvor de sover og f�der nye ulve unger.
 */
public class Wolf extends Animal {

    private boolean isLeader;
    private int wolfPackID;
    private WolfPack pack;
    private Wolf leader;
    private Location myLocation;
    private World world;
    private WolfHole myWolfHole;
    private boolean attacking = false;
    private boolean hiding = false;
    protected Wolf wolfTarget = null;
    protected Location preyLocation;
    protected boolean inWolfDuel;
    protected float chanceToGetPregnant;

    Wolf(World world, int wolfPackID, WolfPack pack) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = true;
        leader = this;
        this.world = world;
        energy = 30;
        hitpoints = 10;
        maxHitpoints = hitpoints;
        chanceToGetPregnant = 0.3f;
    }

    Wolf(World world, int wolfPackID, WolfPack pack, Wolf leader) {
        super(30, world);
        this.wolfPackID = wolfPackID;
        this.pack = pack;
        isLeader = false;
        this.leader = leader;
        this.world = world;
        energy = 10;
        hitpoints = 10;
        maxHitpoints = hitpoints;
        chanceToGetPregnant = 0.3f;
    }


    @Override
    public void act(World world) {



        if (attacking) {
            huntingBehavior();

            System.out.println("attacking");
        } else {
            passiveBehavior();
            tryGetPregnant();
        }

        tryToDecreaseEnergy();

        if(status != AnimalStatus.SLEEPING) {
            eat();
        }
        die(false, 15, 100);
        if (isOnMap) {
            myLocation = world.getLocation(this);
        }
    }

    @Override
    public void die(boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        if (isLeader && hitpoints <= 0) {
            System.out.println("Find new leader");
            appointNewLeader();
        }
        super.die(mushrooms, amountOfMeat, stepsToDecompose);
    }

    @Override
    public void eat() {
        ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
        for (Location neighbor : neighborTiles) {
            Object temp = world.getTile(neighbor);
            if (temp instanceof Animal animal) {
                //if wolf
                if (animal instanceof Wolf wolf) {
                    if (wolf.getWolfPackID() != this.getWolfPackID()) {
                        wolf.takeDamage(2);
                        inWolfDuel = true;
                        wolfTarget = wolf;
                        attacking = true;
                        System.out.println("attacking another wolf");
                        if (world.isTileEmpty(neighbor)) {
                            updateEnergy(3);
                        }
                    }
                } else {  //if food
                    animal.takeDamage(2);
                }

            } else if (temp instanceof Cadavar cadavar) {
                cadavar.reduceAmountOfMeat(3);
                updateEnergy(3);
            }
        }
    }

    /**
     * Definerer den passive tilstand af objektet, p� baggrund af tidspunktet p� dagen.
     * Om dagen s�ger objektet for bytte samtidig med, at den bev�ger sig rundt.
     * N�r det bliver aften og senere nat, begynder ulven at s�ge mod sit hul. Her fors�ger den at gemme sig i hullet, hvis den er t�t nok.
     * Hvis hullet ikke findes (er lig null), fors�ger ulven s�tte sit hul.
     * N�r det bliver morgen, og ulven er i sit hul, forlader ulven s� sit hul.
     */
    protected void passiveBehavior() {
        if (isOnMap) {
            if (checktime() == TimeOfDay.MORNING) {
                roamBehaviour();
                searchForPrey();
            } else if (checktime() == TimeOfDay.EVENING || checktime() == TimeOfDay.NIGHT) {
                if (myWolfHole != null) {
                    goingHomeBehaviour();
                    tryToHide();
                } else {
                    setHole();
                }
            }
        }

        if (checktime() == TimeOfDay.MORNING && hiding && !isOnMap) {
            emerge();
        }
    }

    /**
     * roamBehaviour er en type af objektets adf�rd, som rykker p� objektet.
     * Hvis objektet kalder roamBehaviour, bliver der f�rst tjekket for, om objektet er leder.
     * Hvis dette er sandt, bev�ger objektet sig uafh�ngtigt af flokken, ved at s�tte pathfinder lig null.
     * Hvis objektet ikke er leder af flokken, og har en leder er der to m�der hvorp� objektet kan bev�ge sig.
     * Hvis dens afstand bliver udregnet til at v�re inden for en specifik radius af lederen, bev�ger objektet sig tilf�ldigt rundt.
     * Hvis objektet er for langt fra lederen, s�ger den hen i mod lederen.
     * Hvis ingen leder findes, bev�ger objektet sig tilf�ldigt rundt.
     */
    protected void roamBehaviour() {
        if (isLeader) { // pack leader
            // move independently
            pathFinder(null);
        } else if (leader != null && leader.getIsOnMap()) { // if leader exists

            float rangeFromWolfLeader = rangeFromLeader(pack.getLeaderWolf());

            if (rangeFromWolfLeader < 2) {
                pathFinder(null);

            } else { // move independently
                pathFinder(leader.getMyLocation());
            }

        } else { // no leader exists
            pathFinder(null);
        }
    }

    /**
     * N�r metoden searchForPrey leder objektet efter andre objekter inden for en designeret radius.
     * R�kkef�lgen af if-statements, g�r at det er muligt at prioriterer nogle objekter f�r andre.
     * Hvis ikke nogle af specifikationerne for prey bliver m�dt, returneres null.
     * @return Location
     */
    protected Location searchForPrey() {
        if (getNearestObject(Cadavar.class, 8) != null && checktime() != TimeOfDay.NIGHT) {
            attacking = true;
            return getNearestObject(Cadavar.class, 8);
        } else if (getNearestObject(Rabbit.class, 8) != null) {
            attacking = true;
            return getNearestObject(Rabbit.class, 8);
        } else if (getNearestObject(Bear.class, 5) != null) {
            attacking = true;
            return getNearestObject(Bear.class, 5);
        } else {
            return null;
        }
    }

    /**
     * huntingBehaviour bev�ger objektet mod en lokation.
     * Afh�ngigt af, om
     */
    protected void huntingBehavior() {
        if (!inWolfDuel) {
            preyLocation = searchForPrey();
            if (preyLocation == null) {
                attacking = false;
            } else {
                pathFinder(preyLocation);
            }
        } else if (!wolfTarget.getIsOnMap()) {
            inWolfDuel = false;
        } else {
            pathFinder(wolfTarget.getMyLocation());
            System.out.println("in wolf duel");
        }

    }

    /**
     * goingHomeBehaviour s�tter objektets vejfinder til hjem, hvis objektet er i verdenen.
     */
    protected void goingHomeBehaviour() {
        if (isOnMap) {
            pathFinder(world.getLocation(myWolfHole));
        }
    }

    /**
     *tryToHide tjekker om objektet er i verdenen. Herefter tjekker den om objektet er t�t no p� sit hul, til at g� ned i det.
     */
    private void tryToHide() {
        if (isOnMap) {
            int thisX = world.getLocation(this).getX();
            int thisY = world.getLocation(this).getY();

            int wolfHoleX = world.getLocation(myWolfHole).getX();
            int wolfHoleY = world.getLocation(myWolfHole).getY();

            if (thisX == wolfHoleX && thisY == wolfHoleY) {
                world.remove(this);
                hiding = true;
                isOnMap = false;
                sleep();
            }
        }
    }

    /**
     * emerge metoden bruges til at smide alle objekterne ud af hullet. F�rst smides lederen ud, da de andre ulve er afh�ngige af lederen.
     * Hvis lederen er ude, kan andre ulve herefter frit g� ud af hullet, via ledige tomme pladser rundt om hullet.
     */
    public void emerge() {
        if (hiding && !isOnMap) {
            Location wolfHoleLoc = world.getLocation(myWolfHole);

            //leader bliver skubbet ud af hulen f�rst.
            if (!isLeader && !leader.getIsOnMap() && leader.getIsHiding()) {
                leader.emerge();
            }

            Set<Location> set = world.getEmptySurroundingTiles(wolfHoleLoc);
            ArrayList<Location> emptyEmergeLocations = new ArrayList<>(set);

            if (emptyEmergeLocations.isEmpty()) {
                return;
            }

            if (!(world.getTile(wolfHoleLoc) instanceof Actor)) {
                Random rand = new Random();
                Location emergeLocation = emptyEmergeLocations.get(rand.nextInt(emptyEmergeLocations.size()));

                world.setTile(emergeLocation, this);
                isOnMap = true;
                hiding = false;
                birth();
                status = AnimalStatus.LOOKINGFORFOOD;
            }
        }
    }

    /**
     * Kalkulerer afstanden fra objektet selv til lederen.
     * Benytter pythagoras bevis.
     * Hvis lederen ikke er i verdenen, returneres nul.
     * @param wolfLeader
     * @return float
     */
    private float rangeFromLeader(Wolf wolfLeader) {
        if (wolfLeader.isOnMap) {
            // Get the coordinates of "this" wolf
            int thisX = world.getLocation(this).getX();
            int thisY = world.getLocation(this).getY();

            // Get the coordinates of the leader wolf
            int leaderX = world.getLocation(wolfLeader).getX();
            int leaderY = world.getLocation(wolfLeader).getY();

            // Calculate the distance using the Pythagorean theorem
            int deltaX = Math.abs(leaderX - thisX);
            int deltaY = Math.abs(leaderY - thisY);

            System.out.println((float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY)));
            return (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        } else return 0;

    }

    @Override
    public LifeStage getLifeStage() {
        if (age < 2) {
            return LifeStage.CHILD;
        } else {
            return LifeStage.ADULT;
        }
    }


    /**
     * setHole har to forskellige funktionaliteter p� baggrund af, om objektet, som kalder det er leder.
     * F�rst tjekker metoden tidspunktet af dagen.
     * Hvis det er aften eller nat, tjekker metoden efterf�lgende om objektet er leder.
     * Hvis objektet er en leder, har den ansvar for at placerer et hul til flokken.
     * Hvis ikke objektet er leder, skal den tage floklederens hul.
     */
    protected void setHole() {
        if ((checktime() == TimeOfDay.EVENING || checktime() == TimeOfDay.NIGHT)) {
            if (isLeader) {
                digHole();
            } else {
                myWolfHole = leader.getPackHole();
            }

        }
    }

    /**
     * digHole har til ansvar, at placerer et hul i verdenen.
     * F�rst tjekkes om objektet i forvejen har et hul. Metoden fors�ger derefter at placere et hul oven p� objektets placering.
     * Hvis et non-blocking objekt i forvejen findes, som hverken er gr�s eller et kadaver, bev�ger objektet sig til en anden n�rl�ggende lokation
     * Hvis et non-blocking objekt ikke findes i lokationen, eller best�r af enten gr�s eller et kadaver, placeres hullet, efter at have fjernet det eventuelt eksisterende non-blocking objekt.
     * Objektets reference til eget hul bliver her sat til det nyligt skabte hul.
     */
    protected void digHole() {
        if (myWolfHole == null) {
            Location locOfWolf = world.getLocation(this);
            Object objectOnWolf = world.getNonBlocking(locOfWolf);
            if (!(objectOnWolf instanceof Grass || objectOnWolf instanceof Cadavar) && objectOnWolf != null) {
                Random rd = new Random();

                for (int i = 1; i <= world.getSize(); i++) {
                    Set<Location> emptyTilesSet = Helper.getEmptySurroundingTiles(world, locOfWolf, i);
                    ArrayList<Location> emptyTiles = new ArrayList<>(emptyTilesSet);
                    if (!emptyTiles.isEmpty()) {
                        pathFinder(emptyTiles.get(rd.nextInt(emptyTiles.size())));
                        break;
                    }
                }

            } else {

                if (objectOnWolf != null) {
                    world.delete(objectOnWolf);
                }

                WolfHole newHole = new WolfHole(world, getMyLocation(), wolfPackID);
                world.setTile(getMyLocation(), newHole);
                myWolfHole = newHole;
            }
        }
    }

    /**
     * tryToDecreaseEnergy fors�ger at opdatere objektets energi med -1, hvis dyret ikke sover.
     */
    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    /**
     * appointNewLeader udpeger en ny leder for ulveflokken ved at v�lge en tilf�ldig ulv fra flokken,
     *  eksklusiv det nuv�rende objekt. Den valgte ulv markeres som leder, og
     *  alle ulve i flokken opdateres til at referere til den nye leder.
     *  Hvis flokken er tom (bortset fra det nuv�rende objekt), udskrives en besked om,
     *  at der ikke er nogen tilg�ngelige ulve at udpege som leder.
     */
    private void appointNewLeader() {
        ArrayList<Wolf> wolfArrayList = new ArrayList<>(wolfPackList());

        // Remove the current wolf (this) from the list
        wolfArrayList.removeIf(wolf -> wolf.equals(this)); // Use equals if overridden

        // Ensure the list is not empty before proceeding
        if (!wolfArrayList.isEmpty()) {
            Random random = new Random();

            // Pick a random wolf from the list
            int randomIndex = random.nextInt(wolfArrayList.size());
            Wolf nextLeader = wolfArrayList.get(randomIndex);
            nextLeader.isLeader = true; // Mark the new leader

            // Update all wolves in the pack
            for (Wolf wolf : wolfArrayList) {
                wolf.leader = nextLeader;
                wolf.setHole(); // Assume this updates their state/location
            }
            System.out.println(nextLeader + " is the new leader!");
        } else {
            System.out.println("No wolves left to appoint as leader.");
        }
    }

    /**
     * wolfPackList tager alle enheder i verdenen, sorterer objekterne baseret p� deres instanstype.
     * Hvis enheden er af typen ulv og har samme flok ID som objektet der kalder metoden, bliver den tilf�jet til en ArrayListe.
     * Denne ArrayListe af ulve med samme flok ID bliver returneret.
     * @return ArrayList<Wolf>
     */
    private ArrayList<Wolf> wolfPackList() {
        HashMap<Object, Location> map = (HashMap<Object, Location>) world.getEntities();
        ArrayList<Wolf> wolfArrayList = new ArrayList<>();

        // Collect all wolves in the same pack
        for (Object obj : map.keySet()) {
            if (obj instanceof Wolf wolf) {
                if (wolf.getWolfPackID() == getWolfPackID()) {
                    wolfArrayList.add(wolf);
                }
            }
        }
        System.out.println("Wolf Pack List: " + wolfArrayList);
        return wolfArrayList;
    }

    @Override
    public DisplayInformation getInformation() {
        if (getLifeStage() == LifeStage.CHILD) {
            return new DisplayInformation(Color.red, "wolf-small");
        } else {
            return new DisplayInformation(Color.red, "wolf");
        }

    }

    /**
     * tryGetPregnant metoden fors�ger at s�tte sandhedsv�rdien "pregnant" til sand, hvis specifikke krav er opn�et.
     * Metoden kontrollerer, om objektet er en voksen hunulv, der ikke allerede er gravid. Derudover
     * kaldes en tilf�ldig v�rdi, for at skabe tilf�ldighed til om graviditet kan ske. Derefter unders�ges
     * n�rliggende felter for en voksen hanulv fra samme ulveflok. Hvis alle betingelser er
     * opfyldt, markeres objektet som gravid.
     */
    protected void tryGetPregnant() {
        Random rd = new Random();

        if (getSex() == Sex.FEMALE && !pregnant && rd.nextFloat(1) < chanceToGetPregnant && getLifeStage() == LifeStage.ADULT) {
            System.out.println("single an rdy to mingle");
            ArrayList<Location> neighborTiles = new ArrayList<>(surrondingLocationsList());
            for (Location neighbor : neighborTiles) {
                Object temp = world.getTile(neighbor);
                if (temp instanceof Wolf wolf) {
                    if (wolf.getWolfPackID() == this.getWolfPackID() && wolf.getSex() == Sex.MALE && wolf.getLifeStage() == LifeStage.ADULT) {
                        pregnant = true;
                        System.out.println("pregnant");
                    }
                }
            }
        }
    }

    /**
     * Indstiller sandsynligheden for, at objektet kan blive gravid.
     * @param chance float
     */
    public void setChanceToGetPregnant(float chance) {
        chanceToGetPregnant = chance;
    }

    /**
     * Indstiller lokationen for objektet som kalder den.
     * @param myLocation Location
     */
    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    /**
     * Returnerer lokationen for objektet som kalder den.
     * @return Location
     */
    public Location getMyLocation() {
        return myLocation;
    }

    /**
     * Returnerer flok ID, for den flok som objektet er i.
     * @return int
     */
    public int getWolfPackID() {
        return wolfPackID;
    }

    /**
     * Returnerer objektets leder.
     * @return Wolf
     */
    public Wolf getLeader() {
        return leader;
    }

    /**
     * Returnerer et objekt af typen WolfPack
     * @return WolfPack
     */
    public WolfPack getWolfPack() {
        return pack;
    }

    /**
     * Returnerer det WolfHole som objektet er knyttet til.
     * @return WolfHole
     */
    public WolfHole getPackHole() {
        return myWolfHole;
    }

    /**
     * Returnerer en sandhedsv�rdi omkring hvorvidt objektet gemmer sig.
     * @return boolean
     */
    public boolean getIsHiding() {
        return hiding;
    }
}
