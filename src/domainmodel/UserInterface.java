package domainmodel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;

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
        for (File file : inputFileDirectory.listFiles()) {
            if (file.getName().equals(specifiedFileToRun) || specifiedFileToRun == null) {


                try {
                    int worldSize;
                    HashMap<String, String> initialConditions = new HashMap<>();


                    FileReader fr = new FileReader(file.toString());

                    BufferedReader br = new BufferedReader(fr);
                    List<String> lines = new LinkedList<>(br.lines().toList());

                    worldSize = Integer.parseInt(lines.getFirst());


                    for (int i = 1; i < lines.size(); i++) {
                        String line = lines.get(i);

                        if (!line.isBlank()) {
                            String[] splitted = line.split(" ");
                            initialConditions.put(splitted[0].toLowerCase(), splitted[1]);
                        }

                    }

                    testPackage = controller.initiateSimulation(worldSize, initialConditions, isTest);
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
