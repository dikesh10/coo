package org.projet.keyboard.model;

/**
 * Représente une touche sur le clavier avec ses différentes productions possibles.
 */
public class Key {
    private final int row;
    private final int column;
    private final Finger finger;
    private final String shiftProduces;
    private final String altgrProduces;

    /**
     * Crée une nouvelle touche.
     * @param row La ligne de la touche
     * @param column La colonne de la touche
     * @param finger Le doigt utilisé pour cette touche
     * @param shiftProduces Le caractère produit avec Shift
     * @param altgrProduces Le caractère produit avec AltGr
     */
    public Key(int row, int column, Finger finger, String shiftProduces, String altgrProduces) {
        this.row = row;
        this.column = column;
        this.finger = finger;
        this.shiftProduces = shiftProduces;
        this.altgrProduces = altgrProduces;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Finger getFinger() {
        return finger;
    }

    public String getShiftProduces() {
        return shiftProduces;
    }

    public String getAltgrProduces() {
        return altgrProduces;
    }
}
