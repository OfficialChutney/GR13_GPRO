package information_containers;

import itumulator.world.Location;

/**
 * Klassen som indeholder startparametrene for 1 objekt, der skal initialiseres.
 */
public class InitialConditions {
    private String object;
    private String numberOfObjects;
    private Location coordinates;
    private boolean fungi;

    public InitialConditions(String object, String numberOfObjects, String fungi) {
        this.object = object;
        this.fungi = !(fungi == null);
        this.numberOfObjects = numberOfObjects;
        coordinates = null;
    }

    public InitialConditions(String object, String numberOfObjects, String fungi, String x, String y) {
        this.object = object;
        this.fungi = !(fungi == null);
        this.numberOfObjects = numberOfObjects;
        coordinates = new Location(Integer.parseInt(x), Integer.parseInt(y));
    }


    /**
     * Returnere objektet der �nskes at initialisering.
     * @return Den {@link String} som korrespondere til det objekt der �nskes at initialiseres.
     */
    public String getObject() {
        return object;
    }

    /**
     * Returnere antallet af objekter der �nskes at initialisering.
     * @return Den {@link String} som korrespondere til det antal af objekter der �nskes at initialiseres. S�fremt det er et range, returneres det
     * i formatet "min-max."
     */
    public String getNumberOfObjects() {
        return numberOfObjects;
    }
    /**
     * Returnere den {@link Location} der �nskes at objektet skal starte p�.
     * @return Den {@link String} som korrespondere til den {@link Location} som objektet �nskes at initialiseres p�.
     */
    public Location getCoordinates() {
        return coordinates;
    }
    /**
     * Returnere hvorvidt objektet er inficeret af fungi. Relateret til {@link foliage.Mushroom} klassen.
     * @return boolean om objektet er inficeret af fungi eller ej.
     */
    public boolean isFungi() {
        return fungi;
    }
}
