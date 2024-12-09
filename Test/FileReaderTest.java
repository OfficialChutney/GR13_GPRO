import animal.Bear;
import animal.Cadavar;
import animal.Rabbit;
import animal.Wolf;
import domainmodel.Helper;
import domainmodel.TestPackage;
import domainmodel.UserInterface;
import foliage.BerryBush;
import foliage.Grass;
import hole.Hole;
import hole.RabbitHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest extends TestClass {

    public FileReaderTest() {
        super();
    }

    @Test
    public void rabbitPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfRabbits = 15;
        TestPackage tp = placeEntityFromFile(Rabbit.class, expectedNumOfRabbits);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getRabbits().size();

        assertEquals(expectedNumOfRabbits,actual);

    }

    @Test
    public void GrassPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfGrass = 15;
        TestPackage tp = placeEntityFromFile(Grass.class, expectedNumOfGrass);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getGrass().size();

        assertEquals(expectedNumOfGrass,actual);

    }

    @Test
    public void wolvesPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfWolves = 8;
        TestPackage tp = placeEntityFromFile(Wolf.class, expectedNumOfWolves);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getWolves().size();

        assertEquals(expectedNumOfWolves,actual);

    }

    @Test
    public void BearsPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfBears = 7;
        TestPackage tp = placeEntityFromFile(Bear.class, expectedNumOfBears);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getBears().size();

        assertEquals(expectedNumOfBears,actual);

    }

    @Test
    public void BearsPlacedFromFileWithCoordinate() {
        //ARRANGE & ACT
        int expectedNumOfBears = 1;
        Location locOfBearStart = new Location(4,4);

        TestPackage tp = placeEntityFromFile(Bear.class, expectedNumOfBears, locOfBearStart, false);


        //ASSERT
        assertNotNull(tp);

        Map<Bear, Location> bears = tp.getBears();
        ArrayList<Bear> bearsInWorld = new ArrayList<>(bears.keySet());
        Location locOfBearEnd = bears.get(bearsInWorld.get(0));
        int actual = tp.getBears().size();

        assertEquals(expectedNumOfBears,actual);
        assertEquals(locOfBearStart, locOfBearEnd);

    }

    @Test
    public void BurrowPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfBurrows = 7;
        TestPackage tp = placeEntityFromFile(RabbitHole.class, expectedNumOfBurrows);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getRabbitHoles().size();

        assertEquals(expectedNumOfBurrows,actual);

    }

    @Test
    public void BerryBushPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfBerryBush = 7;
        TestPackage tp = placeEntityFromFile(BerryBush.class, expectedNumOfBerryBush);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getBerrybushes().size();

        assertEquals(expectedNumOfBerryBush,actual);

    }

    @Test
    public void CadaverWithoutFungiPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfCadavers = 7;
        TestPackage tp = placeEntityFromFile(Cadavar.class, expectedNumOfCadavers);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getCadaversWithoutFungi().size();

        assertEquals(expectedNumOfCadavers,actual);

    }

    @Test
    public void CadaverWithFungiPlacedFromFile() {
        //ARRANGE & ACT
        int expectedNumOfCadavers = 7;
        TestPackage tp = placeEntityFromFile(Cadavar.class, expectedNumOfCadavers, null, true);


        //ASSERT
        assertNotNull(tp);
        int actual = tp.getCadaversWithFungi().size();

        assertEquals(expectedNumOfCadavers,actual);

    }

    private TestPackage placeEntityFromFile(Class<?> entityToPlace, int numOfEntities) {
        return placeEntityFromFile(entityToPlace, numOfEntities, null, false);
    }

    private TestPackage placeEntityFromFile(Class<?> entityToPlace, int numOfEntities, Location locOfEntity, boolean fungi) {
        //ASSERT
        UserInterface ui = new UserInterface();
        String directory = ui.getInputFileDirectory();
        ui.setTest(true);
        File testFile = new File(directory+"/testFile.txt");
        ui.setSpecifiedFileToRun("testFile.txt");

        Map<Class<?>, String> entityMap = Map.of(
                Rabbit.class, "rabbit",
                Bear.class, "bear",
                Wolf.class, "wolf",
                Grass.class, "grass",
                RabbitHole.class, "burrow",
                Cadavar.class, "carcass",
                BerryBush.class, "berry"
        );
        String loc = "";
        if(locOfEntity != null) {
            loc = "(" + locOfEntity.getX() + "," + locOfEntity.getX() + ")";
        }


        try {
            testFile.createNewFile();
            try (FileWriter fw = new FileWriter(testFile)) {
                fw.write(String.valueOf(worldSize)+"\n");

                String fungiString = (fungi) ? " fungi " : " ";

                String stringToWrite = entityMap.get(entityToPlace)+fungiString+String.valueOf(numOfEntities) + " " + loc;
                stringToWrite = stringToWrite.trim();
                fw.write(stringToWrite);

            }



            TestPackage tp = ui.startProgram();

            testFile.delete();

            return tp;

        } catch (IOException e) {
            assertDoesNotThrow(() -> {
                throw e;
            });
        }

        return null;
    }



}
