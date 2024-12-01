package domainmodel;

import animal.Bear;
import animal.Rabbit;
import animal.Wolf;
import foliage.BerryBush;
import foliage.Grass;
import hole.Hole;
import hole.RabbitHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashMap;
import java.util.Map;

public class TestPackage {
    World world;
    Program program;
    Map<Object, Location> entities;
    Map<Rabbit, Location> rabbits;
    Map<Bear, Location> bears;
    Map<Wolf, Location> wolves;
    Map<Grass, Location> grass;
    Map<BerryBush, Location> berrybushes;
    Map<Hole, Location> rabbitHoles;

    public TestPackage(World world, Program program, Map<Object, Location> entities) {
        this.world = world;
        this.program = program;
        this.entities = entities;
        rabbits = new HashMap<>();
        bears = new HashMap<>();
        wolves = new HashMap<>();
        grass = new HashMap<>();
        rabbitHoles = new HashMap<>();
        isolateEntities();
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
            } else if(o instanceof Hole rb) {
                rabbitHoles.put(rb, locationOfObject );
            }
        }

    }

    public Map<Rabbit, Location> getRabbits() {
        return rabbits;
    }

    public Map<Bear, Location> getBears() {
        return bears;
    }

    public Map<Wolf, Location> getWolves() {
        return wolves;
    }

    public Map<Hole, Location> getRabbitHoles() {
        return rabbitHoles;
    }

    public Map<Grass, Location> getGrass() {
        return grass;
    }

    public Map<BerryBush, Location> getBerrybushes() {
        return berrybushes;
    }
}
