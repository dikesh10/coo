package org.projet.keyboard.model;

/**
 * Représente la position physique d'une touche sur le clavier.
 * Record immuable contenant les coordonnées et les informations de la touche.
 */
public record KeyPosition(
    int row,           // Rangée (0 = supérieure, 1 = home row, 2 = inférieure)
    int column,        // Colonne (0 = gauche à droite)
    Finger finger,     // Doigt assigné à cette touche
    Hand hand,         // Main utilisée pour cette touche
    boolean isHomeKey  // True si c'est une touche de repos (home position)
) {
    /**
     * Vérifie si la position est valide.
     */
    public KeyPosition {
        if (row < 0 || row > 2) {
            throw new IllegalArgumentException("La rangée doit être entre 0 et 2");
        }
        if (column < 0) {
            throw new IllegalArgumentException("La colonne doit être positive");
        }
        if (finger == null || hand == null) {
            throw new IllegalArgumentException("Le doigt et la main ne peuvent pas être null");
        }
    }
}
