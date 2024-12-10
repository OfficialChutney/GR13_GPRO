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
 * Ulve klassen bliver skabt af klassen WolfPack. Ulve har forskellige adfærd afhængigt af tiden på dagen,
 * lederens position, og andre ulveflokkes tilstedeværelse. Ulven har mulighed for selv at bevæge sig rundt,
 * men bevæger sig altid inden for en vis afstand af en udpeget ulveleder. Ulven har mulighed for at
 * jage bytte som opdager inden for en rækkevide. Hvis en ulv møder en anden kommer de to ulve i duel. Ulveflokke deler
 * ulvehuller, hvor de sover og føder nye ulve unger.
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
     * Definerer den passive tilstand af objektet, på baggrund af tidspunktet på dagen.
     * Om dagen søger objektet for bytte samtidig med, at den bevæger sig rundt.
     * Når det bliver aften og senere nat, begynder ulven at søge mod sit hul. Her forsøger den at gemme sig i hullet, hvis den er tæt nok.
     * Hvis hullet ikke findes (er lig null), forsøger ulven sætte sit hul.
     * Når det bliver morgen, og ulven er i sit hul, forlader ulven så sit hul.
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
     * roamBehaviour er en type af objektets adfærd, som rykker på objektet.
     * Hvis objektet kalder roamBehaviour, bliver der først tjekket for, om objektet er leder.
     * Hvis dette er sandt, bevæger objektet sig uafhængtigt af flokken, ved at sætte pathfinder lig null.
     * Hvis objektet ikke er leder af flokken, og har en leder er der to måder hvorpå objektet kan bevæge sig.
     * Hvis dens afstand bliver udregnet til at være inden for en specifik radius af lederen, bevæger objektet sig tilfældigt rundt.
     * Hvis objektet er for langt fra lederen, søger den hen i mod lederen.
     * Hvis ingen leder findes, bevæger objektet sig tilfældigt rundt.
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
     * Når metoden searchForPrey leder objektet efter andre objekter inden for en designeret radius.
     * Rækkefølgen af if-statements, gør at det er muligt at prioriterer nogle objekter før andre.
     * Hvis ikke nogle af specifikationerne for prey bliver mødt, returneres null.
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
     * huntingBehaviour bevæger objektet mod en lokation.
     * Afhængigt af, om
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
     * goingHomeBehaviour sætter objektets vejfinder til hjem, hvis objektet er i verdenen.
     */
    protected void goingHomeBehaviour() {
        if (isOnMap) {
            pathFinder(world.getLocation(myWolfHole));
        }
    }

    /**
     *tryToHide tjekker om objektet er i verdenen. Herefter tjekker den om objektet er tæt no på sit hul, til at gå ned i det.
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
     * emerge metoden bruges til at smide alle objekterne ud af hullet. Først smides lederen ud, da de andre ulve er afhængige af lederen.
     * Hvis lederen er ude, kan andre ulve herefter frit gå ud af hullet, via ledige tomme pladser rundt om hullet.
     */
    public void emerge() {
        if (hiding && !isOnMap) {
            Location wolfHoleLoc = world.getLocation(myWolfHole);

            //leader bliver skubbet ud af hulen først.
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
     * setHole har to forskellige funktionaliteter på baggrund af, om objektet, som kalder det er leder.
     * Først tjekker metoden tidspunktet af dagen.
     * Hvis det er aften eller nat, tjekker metoden efterfølgende om objektet er leder.
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
     * Først tjekkes om objektet i forvejen har et hul. Metoden forsøger derefter at placere et hul oven på objektets placering.
     * Hvis et non-blocking objekt i forvejen findes, som hverken er græs eller et kadaver, bevæger objektet sig til en anden nærlæggende lokation
     * Hvis et non-blocking objekt ikke findes i lokationen, eller består af enten græs eller et kadaver, placeres hullet, efter at have fjernet det eventuelt eksisterende non-blocking objekt.
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
     * tryToDecreaseEnergy forsøger at opdatere objektets energi med -1, hvis dyret ikke sover.
     */
    private void tryToDecreaseEnergy() {
        if (status != AnimalStatus.SLEEPING) {
            updateEnergy(-1);
        }
    }

    /**
     * appointNewLeader udpeger en ny leder for ulveflokken ved at vælge en tilfældig ulv fra flokken,
     *  eksklusiv det nuværende objekt. Den valgte ulv markeres som leder, og
     *  alle ulve i flokken opdateres til at referere til den nye leder.
     *  Hvis flokken er tom (bortset fra det nuværende objekt), udskrives en besked om,
     *  at der ikke er nogen tilgængelige ulve at udpege som leder.
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
     * wolfPackList tager alle enheder i verdenen, sorterer objekterne baseret på deres instanstype.
     * Hvis enheden er af typen ulv og har samme flok ID som objektet der kalder metoden, bliver den tilføjet til en ArrayListe.
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
     * tryGetPregnant metoden forsøger at sætte sandhedsværdien "pregnant" til sand, hvis specifikke krav er opnået.
     * Metoden kontrollerer, om objektet er en voksen hunulv, der ikke allerede er gravid. Derudover
     * kaldes en tilfældig værdi, for at skabe tilfældighed til om graviditet kan ske. Derefter undersøges
     * nærliggende felter for en voksen hanulv fra samme ulveflok. Hvis alle betingelser er
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
     * Returnerer en sandhedsværdi omkring hvorvidt objektet gemmer sig.
     * @return boolean
     */
    public boolean getIsHiding() {
        return hiding;
    }
}
