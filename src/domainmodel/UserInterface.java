package domainmodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public TestPackage startProgram() {
        TestPackage testPackage = null;
        LinkedList<InitialConditions> icList = new LinkedList<>();
        for (File file : inputFileDirectory.listFiles()) {
            if (file.getName().equals(specifiedFileToRun) || specifiedFileToRun == null) {
                InitialConditions ic = null;

                try (FileReader fr = new FileReader(file.toString())) {
                    int worldSize;

                    BufferedReader br = new BufferedReader(fr);
                    List<String> lines = new LinkedList<>(br.lines().toList());

                    worldSize = Integer.parseInt(lines.getFirst());


                    for (int i = 1; i < lines.size(); i++) {

                        String regex = "(\\w+)(?:\\s+(fungi))?(?:\\s+(\\d+(?:-\\d+)?))?(?:\\s+\\((\\d+),(\\d+)\\))?";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(lines.get(i));


                        while (matcher.find()) {

                            String entity = matcher.group(1);
                            String fungi = matcher.group(2);
                            String countOrRange = matcher.group(3);
                            String coordX = matcher.group(4);
                            String coordY = matcher.group(5);

                            if(coordX == null && coordY == null) {
                                ic = new InitialConditions(entity, countOrRange, fungi);
                            } else {
                                ic = new InitialConditions(entity, countOrRange, fungi, coordX, coordY);
                            }
                            icList.add(ic);




                        }



//                        String line = lines.get(i);
//
//                        if (!line.isBlank()) {
//                            String[] splitted = line.split(" ");
//
//                            if(splitted.length > 2) {
//                                ic = new InitialConditions(splitted[0].toLowerCase(), splitted[1].toLowerCase(), splitted[2].toLowerCase());
//                            } else {
//                                ic = new InitialConditions(splitted[0].toLowerCase(), splitted[1].toLowerCase());
//                            }
//
//                            if(ic == null) {
//                                throw new NullPointerException("InitialConditions are null!");
//                            } else {
//                                icList.add(ic);
//                            }
//
//                        }

                    }




                    testPackage = controller.initiateSimulation(worldSize, isTest, icList);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        if(isTest) {
            return testPackage;
        } else {
            System.exit(0);
        }
        return null;


    }

    public void setSpecifiedFileToRun(String file) {
        specifiedFileToRun = file;
    }

    public Controller getController() {
        return controller;
    }

    public String getInputFileDirectory() {
        return inputFileDirectoryString;
    }

    public void setTest(boolean isTest) {
        this.isTest = isTest;
    }




}
