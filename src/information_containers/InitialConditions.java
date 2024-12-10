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
     * Returnere objektet der ønskes at initialisering.
     * @return Den {@link String} som korrespondere til det objekt der ønskes at initialiseres.
     */
    public String getObject() {
        return object;
    }

    /**
     * Returnere antallet af objekter der ønskes at initialisering.
     * @return Den {@link String} som korrespondere til det antal af objekter der ønskes at initialiseres. Såfremt det er et range, returneres det
     * i formatet "min-max."
     */
    public String getNumberOfObjects() {
        return numberOfObjects;
    }
    /**
     * Returnere den {@link Location} der ønskes at objektet skal starte på.
     * @return Den {@link String} som korrespondere til den {@link Location} som objektet ønskes at initialiseres på.
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
