package domainmodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UserInterface indl�ser alle input filerne. Input filerne skal ligge i den mappe kaldet "InputFiles."
 * UserInterface indl�ser alle filer, og k�rer simulationer for hver fil.
 * UserInterface opgave er ogs� at k�rer regex p� hver linje af en input fil, og omdanner tekst i filerne til individuelle inputs, som sendes videre til {@link Controller}
 * */
public class UserInterface {


    private File inputFileDirectory;
    private Controller controller;
    private String specifiedFileToRun;
    private String inputFileDirectoryString;
    private boolean isTest;

    public UserInterface() {
        inputFileDirectoryString = "InputFiles";
        inputFileDirectory = new File(inputFileDirectoryString);
        controller = new Controller();
        specifiedFileToRun = null;
        isTest = false;
    }

    /**
     * startProgram initialisere UserInterface til at begynde at indl�se filerne fra Inputfilerne. N�r der er k�rt regex p� tekstlinjerne fra input filerne,
     * s�ttes de individuelle start parametre ind i en ny instans af {@link InitialConditions}. Hver instans indeholder startparametre for 1 objekt.
     * En liste af {@link InitialConditions} sendes videre til vores {@link Controller}.
     * @return {@link TestPackage}. Slutparametrene efter en simulation er k�rt. Benyttes ved UnitTests.
     */
    public TestPackage startProgram() {
        TestPackage testPackage = null;

        for (File file : inputFileDirectory.listFiles()) {
            LinkedList<InitialConditions> icList = new LinkedList<>();
            if (file.getName().equals(specifiedFileToRun) || specifiedFileToRun == null) {
                InitialConditions ic = null;
                System.out.println("Running File: " + file.getName());

                try (FileReader fr = new FileReader(file.toString())) {
                    int worldSize;

                    BufferedReader br = new BufferedReader(fr);
                    List<String> lines = new LinkedList<>(br.lines().toList());

                    worldSize = Integer.parseInt(lines.getFirst());


                    for (int i = 1; i < lines.size(); i++) {

                        String regex = "(\\w+)(?:\\s+(fungi))?(?:\\s+(\\d+(?:-\\d+)?))?(?:\\s+\\((\\d+),(\\d+)\\))?";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(lines.get(i).toLowerCase());


                        while (matcher.find()) {

                            String entity = matcher.group(1);
                            String fungi = matcher.group(2);
                            String countOrRange = matcher.group(3);
                            String coordX = matcher.group(4);
                            String coordY = matcher.group(5);

                            System.out.println("World size: " + worldSize);
                            System.out.println("Object: "+entity);
                            System.out.println("Fungi: "+ fungi);
                            System.out.println("Number of objects: "+countOrRange);
                            System.out.println("Coordinates: ("+coordX + "," + coordY + ")");

                            if (coordX == null && coordY == null) {
                                ic = new InitialConditions(entity, countOrRange, fungi);
                            } else {
                                ic = new InitialConditions(entity, countOrRange, fungi, coordX, coordY);
                            }
                            icList.add(ic);


                        }


                    }


                    testPackage = controller.initiateSimulation(worldSize, isTest, icList);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        if (isTest) {
            return testPackage;
        } else {
            System.exit(0);
        }
        return null;


    }

    /**
     * S�tter en specifik fil til at blive k�rt, istedet for alle filerne i InputFiles mappen. Bruges kun ved UnitTests,
     * @param file tager imod en {@link String} for filnavnet for den specifikke fil, der skal k�res af UserInterface.
     */
    public void setSpecifiedFileToRun(String file) {
        specifiedFileToRun = file;
    }

    /**
     * Returnere filstien p� den mappe, som alle input filerne ligger i.
     * @return {@link String} som er navnet p� den filsti/mappe, som alle input filerne ligge i.
     */
    public String getInputFileDirectory() {
        return inputFileDirectoryString;
    }

    /**
     * Bruges til at s�tte UserInterface til om denne k�rsel er en UnitTest eller ej.
     * @param isTest boolean for om denne k�rsel er en UnitTest eller ej.
     */
    public void setTest(boolean isTest) {
        this.isTest = isTest;
    }


}
