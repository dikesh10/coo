package org.projet.keyboard.model;

/**
 * Énumération des doigts utilisés pour la frappe.
 */
public enum Finger {
    PINKY_LEFT,   
    RING_LEFT,     
    MIDDLE_LEFT,   
    INDEX_LEFT,    
    INDEX_RIGHT,   
    MIDDLE_RIGHT,  
    RING_RIGHT,   
    PINKY_RIGHT;  

    /**
     * Retourne la main à laquelle appartient le doigt.
     * @return Hand.LEFT pour les doigts de la main gauche, Hand.RIGHT pour la droite
     */
    public Hand getHand() {
        return this.ordinal() < 4 ? Hand.LEFT : Hand.RIGHT;
    }

    /**
     * Indique si ce doigt est un index.
     * @return true si c'est un index
     */
    public boolean isIndex() {
        return this == INDEX_LEFT || this == INDEX_RIGHT;
    }
}
