package domainmodel;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Map;

public class TestPackage {
    World world;
    Program program;
    Map<Object, Location> entities;

    public TestPackage(World world, Program program, Map<Object, Location> entities) {
        this.world = world;
        this.program = program;
        this.entities = entities;
    }

    public World getWorld() {
        return world;
    }

    public Program getProgram() {
        return program;
    }

    public Map<Object, Location> getEntities() {
        return entities;
    }
}
