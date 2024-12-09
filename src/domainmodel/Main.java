package domainmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main klassen indeholder vores main method, som JVM instatiere programmet fra. Main klassens opgave er at lave én instans af {@link UserInterface}
 */
public class Main {

    public static void main(String[] args) {

        UserInterface fr = new UserInterface();

        fr.startProgram();




    }
}