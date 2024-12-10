package information_containers;

import animal.carnivores.Bear;
import animal.Cadavar;
import animal.herbivore.Rabbit;
import animal.carnivores.Wolf;
import foliage.BerryBush;
import foliage.Grass;
import hole.RabbitHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse der indeholder alle informationer om en verden, ved endt simulation. Benyttes til UnitTests, for at kunne se de parametre såsom antal af objekter,
 * antallet af typerne af objekter, selve {@link World} og {@link Program} objekterne som simulationen blev kørt på.
 */
public class TestPackage {
    World world;
    Program program;
    Map<Object, Location> entities;
    Map<Rabbit, Location> rabbits;
    Map<Bear, Location> bears;
    Map<Wolf, Location> wolves;
    Map<Grass, Location> grass;
    Map<BerryBush, Location> berrybushes;
    Map<RabbitHole, Location> rabbitHoles;
    Map<Cadavar, Location> cadaversWithoutFungi;
    Map<Cadavar, Location> cadaversWithFungi;

    public TestPackage(World world, Program program, Map<Object, Location> entities) {
        this.world = world;
        this.program = program;
        this.entities = entities;
        rabbits = new HashMap<>();
        bears = new HashMap<>();
        wolves = new HashMap<>();
        grass = new HashMap<>();
        rabbitHoles = new HashMap<>();
        berrybushes = new HashMap<>();
        cadaversWithoutFungi = new HashMap<>();
        cadaversWithFungi = new HashMap<>();
        isolateEntities();
    }

    /**
     * Returnere {@link World} fra den verden som er blevet simuleret.
     * @return {@link World}
     */
    public World getWorld() {
        return world;
    }
    /**
     * Returnere {@link Program} fra den verden som er blevet simuleret.
     * @return {@link Program}
     */
    public Program getProgram() {
        return program;
    }
    /**
     * Returnere et {@link Map} af alle {@link Object} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle objekterne på spillefladen.
     */
    public Map<Object, Location> getEntities() {
        return entities;
    }

    /**
     * Den tager listen af alle {@link Object} som er i verdenen, og sortere dem ud i individuelle {@link Map}, hvor objekterne downcastes til den
     * klasse de er instansieret som.
     */
    private void isolateEntities() {

        for(Object o : entities.keySet()) {
            Location locationOfObject = entities.get(o);

            if(o instanceof Rabbit r) {
                rabbits.put(r,locationOfObject);
            } else if(o instanceof Wolf w) {
                wolves.put(w, locationOfObject);
            } else if(o instanceof Bear b) {
                bears.put(b,locationOfObject);
            } else if(o instanceof Grass g) {
                grass.put(g,locationOfObject);
            } else if(o instanceof BerryBush b) {
                berrybushes.put(b,locationOfObject);
            } else if(o instanceof RabbitHole rb) {
                rabbitHoles.put(rb, locationOfObject );
            } else if(o instanceof Cadavar c) {
                if(c.isMushrooms()) {
                    cadaversWithFungi.put(c, locationOfObject);
                } else {
                    cadaversWithoutFungi.put(c,locationOfObject);
                }
            }
        }

    }
    /**
     * Returnere et {@link Map} af alle {@link Rabbit} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Rabbit} objekterne på spillefladen.
     */
    public Map<Rabbit, Location> getRabbits() {
        return rabbits;
    }
    /**
     * Returnere et {@link Map} af alle {@link Bear} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Bear} objekterne på spillefladen.
     */
    public Map<Bear, Location> getBears() {
        return bears;
    }
    /**
     * Returnere et {@link Map} af alle {@link Wolf} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Wolf} objekterne på spillefladen.
     */
    public Map<Wolf, Location> getWolves() {
        return wolves;
    }
    /**
     * Returnere et {@link Map} af alle {@link RabbitHole} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link RabbitHole} objekterne på spillefladen.
     */
    public Map<RabbitHole, Location> getRabbitHoles() {
        return rabbitHoles;
    }
    /**
     * Returnere et {@link Map} af alle {@link Grass} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Grass} objekterne på spillefladen.
     */
    public Map<Grass, Location> getGrass() {
        return grass;
    }
    /**
     * Returnere et {@link Map} af alle {@link BerryBush} og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link BerryBush} objekterne på spillefladen.
     */
    public Map<BerryBush, Location> getBerrybushes() {
        return berrybushes;
    }
    /**
     * Returnere et {@link Map} af alle {@link Cadavar} der ikke er inficeret af fungi, og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Cadavar} objekterne på spillefladen.
     */
    public Map<Cadavar, Location> getCadaversWithoutFungi() {
        return cadaversWithoutFungi;
    }
    /**
     * Returnere et {@link Map} af alle {@link Cadavar} der er inficeret af fungi, og deres {@link Location} som var på verdenen ved endt simulation
     * @return {@link Map} af alle {@link Cadavar} objekterne på spillefladen.
     */
    public Map<Cadavar, Location> getCadaversWithFungi() {
        return cadaversWithFungi;
    }
}
