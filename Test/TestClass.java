import domainmodel.Helper;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


import java.util.LinkedList;
import java.util.Map;

/**
 * Denne klasse nedarver alle Testklasser fra. Den indeholder hjælpemetoder til UnitTests samt instansiere testverdenen.
 */
public abstract class TestClass {

    protected int worldSize;
    protected int display_size;
    protected int delay;
    protected int numberOfTiles;
    protected World world;
    protected Program program;

    public TestClass() {
        delay = 500;
        display_size = 800;
        worldSize = 15;
        numberOfTiles = worldSize * worldSize;
    }

    @BeforeEach
    public void makeWorld() {
        program = new Program(worldSize, display_size, delay);
        world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());
    }

    /**
     * Metode til at finde antallet af objekter i en verden.
     * @param objectType det objekt man gerne vil have antallet på.
     * @param world den verden man vil iterere igennem.
     * @return returnere en {@link LinkedList<Object>}.
     */
    protected LinkedList<Object> getObjectsOnMap(Class<?> objectType, World world) {
        Map<Object, Location> entities = world.getEntities();
        LinkedList<Object> objects = new LinkedList<>();

        for (Object o : entities.keySet()) {
            if (objectType.isInstance(o)) {
                objects.add(o);
            }
        }

        return objects;
    }
    /**
     * Metode til at finde antallet af objekter i en verden.
     * @param objectType det objekt man gerne vil have antallet på.
     * @return returnere en {@link LinkedList<Object>}.
     */
    protected LinkedList<Object> getObjectsOnMap(Class<?> objectType) {
        return getObjectsOnMap(objectType, world);
    }

    /**
     * Klasse der kan beregne afstanden imellem 2 {@link Location} via Pythagoras.
     * @param loc1 første {@link Location}
     * @param loc2 anden {@link Location}
     * @return returnere et float, som er afstanden i antallet af tiles.
     */
    protected float pythagoras(Location loc1, Location loc2) {
        int loc1X = loc1.getX();
        int loc1Y = loc1.getY();

        // Get the coordinates of the leader wolf
        int loc2X = loc2.getX();
        int loc2Y = loc2.getY();

        // Calculate the distance using the Pythagorean theorem
        int deltaX = Math.abs(loc2X - loc1X);
        int deltaY = Math.abs(loc2Y - loc1Y);

        return (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

}
