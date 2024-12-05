import domainmodel.Helper;
import itumulator.executable.Program;
import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;

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
    }

}
