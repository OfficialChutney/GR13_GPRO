import animal.carnivores.Bear;
import animal.Cadavar;
import animal.herbivore.Rabbit;
import animal.carnivores.Wolf;
import information_containers.TestPackage;
import domainmodel.UserInterface;
import foliage.BerryBush;
import foliage.Grass;
import hole.RabbitHole;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link UserInterface} klassen.
 */
public class FileReaderTest extends TestClass {

    public FileReaderTest() {
        super();
    }
    /**
     * Test af om {@link Rabbit} placeres når inputfilen kræver dette.
     * Svare til krav K1-2a.
     */
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
    /**
     * Test af om {@link Grass} placeres når inputfilen kræver dette.
     * Svare til krav K1-1a.
     */
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
    /**
     * Test af om {@link Wolf} placeres når inputfilen kræver dette.
     * Svare til krav K2-1a.
     */
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
    /**
     * Test af om {@link Bear} placeres når inputfilen kræver dette.
     * Svare til krav K2-4a.
     */
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
    /**
     * Test af om {@link Bear} placeres når inputfilen kræver dette, og placeres på den {@link Location} som inputfilen beskriver.
     * Svare til krav K2-4a og K2-5a.
     */
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
    /**
     * Test af om {@link RabbitHole} placeres når inputfilen kræver dette.
     * Svare til krav K1-3a.
     */
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

    /**
     * Test af om {@link BerryBush} placeres når inputfilen kræver dette.
     * Svare indirekte til krav K2-6a.
     */
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

    /**
     * Test der svare til krav K3-1a. {@link Cadavar} uden fungi.
     */
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
    /**
     * Test der svare til krav K3-1a. {@link Cadavar} med fungi.
     */
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
    /**
     * Hjælpemetode til at placere {@link Object} på spillefladen, ved at oprette en midlertidig fil.
     * @param entityToPlace Klassen på det {@link Object} der skal placeres
     * @param numOfEntities Antallet af entities der skal placeres
     * @return returnere den {@link TestPackage} fra {@link UserInterface}, hvor der kan se som de korrekte dyr er blevet placeret i verdenen ud fra inputfilerne.
     */
    private TestPackage placeEntityFromFile(Class<?> entityToPlace, int numOfEntities) {
        return placeEntityFromFile(entityToPlace, numOfEntities, null, false);
    }

    /**
     * Hjælpemetode til at placere {@link Object} på spillefladen, ved at oprette en midlertidig fil.
     * @param entityToPlace Klassen på det {@link Object} der skal placeres
     * @param numOfEntities Antallet af entities der skal placeres
     * @param locOfEntity Den {@link Location} hvor {@link Object} skal placeres
     * @param fungi en boolean hvorvidt hvis det er et {@link Cadavar}, om det har et {@link foliage.Mushroom} i sig
     * @return returnere den {@link TestPackage} fra {@link UserInterface}, hvor der kan se som de korrekte dyr er blevet placeret i verdenen ud fra inputfilerne.
     */
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
