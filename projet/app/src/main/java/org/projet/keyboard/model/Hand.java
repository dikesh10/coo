package org.projet.keyboard.model;

/**
 * Énumération des mains.
 */
public enum Hand {
    LEFT,
    RIGHT;

    /**
     * Retourne la main opposée.
     * @return l'autre main
     */
    public Hand opposite() {
        return this == LEFT ? RIGHT : LEFT;
    }
}
