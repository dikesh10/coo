package org.projet.keyboard.model;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Représente une disposition de clavier complète.
 * Cette classe est immuable une fois créée.
 */
public class KeyboardLayout {
    private final Map<Character, KeyPosition> characterToPosition;
    private final Map<KeyPosition, Character> positionToCharacter;
    private final String name;

    /**
     * Crée une nouvelle disposition de clavier.
     */
    public KeyboardLayout(Map<Character, KeyPosition> layout, String name) {
        this.characterToPosition = Collections.unmodifiableMap(new HashMap<>(layout));
        
        // Créer la map inverse
        Map<KeyPosition, Character> inverse = new HashMap<>();
        layout.forEach((character, position) -> inverse.put(position, character));
        this.positionToCharacter = Collections.unmodifiableMap(inverse);
        
        this.name = name;
    }

    /**
     * Retourne la position d'un caractère sur le clavier.
     */
    public KeyPosition getPosition(char c) {
        return characterToPosition.get(c);
    }

    /**
     * Retourne le caractère à une position donnée.
     */
    public Character getCharacter(KeyPosition position) {
        return positionToCharacter.get(position);
    }

    /**
     * Retourne le nom de la disposition.
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne toutes les positions de touches utilisées dans cette disposition.
     */
    public Map<Character, KeyPosition> getAllPositions() {
        return characterToPosition;
    }
}
