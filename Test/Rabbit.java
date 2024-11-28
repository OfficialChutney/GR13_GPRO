import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Rabbit {

    private int worldSize = 15;

    @BeforeEach
    public void makeWorld() {
        World world = new World(worldSize);
    }

    @Test
    public void maxedOutPlayingField() {

    }

}
