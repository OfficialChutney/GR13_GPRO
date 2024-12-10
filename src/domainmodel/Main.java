package domainmodel;

/**
 * Main klassen indeholder vores main method, som JVM instatierer programmet fra. Main klassens opgave er at lave én instans af {@link UserInterface}
 */
public class Main {

    public static void main(String[] args) {

        UserInterface fr = new UserInterface();

        fr.startProgram();




    }
}