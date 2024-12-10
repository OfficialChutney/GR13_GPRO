package animal;

import java.util.ArrayList;
import java.util.Set;

import domainmodel.Helper;
import itumulator.world.Location;
import itumulator.world.World;

/**
 * WolfPack er en klasse, som står for at initialisere en ulveflok med et unikt flok-ID, samt en dedikeret ulveleder.
 * Klassen tager i sin konstruktør mod parametre afhængigt af flokkens størrelse.
 */
public class WolfPack {

    private int numberOfWolves;
    private ArrayList<Wolf> wolves;
    private int wolfPackID;
    private World world;
    private Wolf leaderWolf;

    public WolfPack(int numberOfWolves, Location spawnLocation, World world) {
        wolves = new ArrayList<>();
        this.numberOfWolves = numberOfWolves;
        this.wolfPackID = hashCode();
        this.world = world;

        createWolfList();

        spawnWolfPack(spawnLocation);
    }

    /**
     * Denne metode tilføjer ulve til atributten wolves. Den første ulv bliver her tildelt leder rollen, hvorefter de resterende
     * ulve bliver tilføjet med reference til denne ulv.
     */
    private void createWolfList() {
        leaderWolf = new Wolf(world, wolfPackID, this);


        wolves.add(leaderWolf);

        for (int i = 1; i < numberOfWolves; i++) {
            wolves.add(new Wolf(world, wolfPackID, this, wolves.getFirst()));
        }
    }

    /**
     * Denne metode sætter lederulven på en lokation, som bliver givet som argument i metoden.
     * Efterfølgende bliver en arrayListe lavet, med alle ulve udover lederen.
     * Denne liste bliver herefter brugt som argument i metoden spawnWolfsInWolfPack
     * @param spawnLocation
     */
    private void spawnWolfPack(Location spawnLocation) {
        leaderWolf.setMyLocation(spawnLocation);

        world.setTile(spawnLocation, leaderWolf);

        ArrayList<Wolf> tempWolves = new ArrayList<>(wolves);
        tempWolves.remove(leaderWolf);

        spawnWolfsInWolfPack(numberOfWolves, spawnLocation, 1, tempWolves);

    }

    /**
     * Returnere leder ulven
     * @return Wolf
     */
    Wolf getWolfLeader() {
        return wolves.getFirst();
    }


    /**
     * Denne metode benytter rekursion til at sætte ulve så tæt på lederens initielle placering som muligt.
     * Hvis der ikke er flere pladser direkte rundt om leder ulven, inkrementeres radius med 1, og ulve forsøges at sættes igen.
     * Metoden tager antallet af ulve, leder ulvens spawn lokation, radius og en flok liste som argumenter.
     * @param numberOfWolfs
     * @param spawnLocation
     * @param radius
     * @param wolfs
     */
    private void spawnWolfsInWolfPack(int numberOfWolfs, Location spawnLocation, int radius, ArrayList<Wolf> wolfs) {
        Set<Location> set = Helper.getEmptySurroundingTiles(world, spawnLocation,radius);

        ArrayList<Location> spawnLocations = new ArrayList<>(set);
        ArrayList<Wolf> tempWolfs = new ArrayList<>(wolfs);
        for (int i = 0; i < spawnLocations.size(); i++) {
            if(i > numberOfWolfs-2) {
                break;
            }
            wolfs.get(i).setMyLocation(spawnLocations.get(i));
            world.setTile(spawnLocations.get(i), wolfs.get(i));
            tempWolfs.remove(wolfs.get(i));
        }

        if(!tempWolfs.isEmpty()) {
            spawnWolfsInWolfPack(numberOfWolfs - spawnLocations.size(), spawnLocation, (radius+1), tempWolfs);
        }
    }

    /**
     * Returnere listen af ulve.
     * @return ArrayList
     */
    public ArrayList<Wolf> getWolves() {
        return wolves;
    }

    public Wolf getLeaderWolf() {
        return leaderWolf;
    }
}
