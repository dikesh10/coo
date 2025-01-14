package org.projet.config;

import java.util.Map;

/**
 * Configuration d'une disposition de clavier chargée depuis un fichier JSON.
 */
public record KeyboardConfig(
    String name,
    String description,
    Map<Character, KeyConfig> keys
) {
    /**
     * Configuration d'une touche individuelle.
     */
    public record KeyConfig(
        int row,
        int column,
        String finger,
        String shiftProduces,
        String altgrProduces
    ) {}
}
