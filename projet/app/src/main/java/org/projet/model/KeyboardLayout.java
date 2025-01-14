package org.projet.model;

import java.util.Map;

/**
 * Represents a keyboard layout configuration.
 * This class is immutable.
 */
public record KeyboardLayout(
    String name,
    Map<Character, Key> characterToKeyMap
) {
    public record Key(
        int row,
        int column,
        Finger finger,
        Character shiftProduces,
        Character altgrProduces
    ) {}

    public enum Finger {
        LEFT_PINKY,
        LEFT_RING,
        LEFT_MIDDLE,
        LEFT_INDEX,
        RIGHT_INDEX,
        RIGHT_MIDDLE,
        RIGHT_RING,
        RIGHT_PINKY
    }

    /**
     * Returns all keys in this layout.
     */
    public Map<Character, Key> getKeys() {
        return characterToKeyMap;
    }

    /**
     * Gets the key for a specific character.
     */
    public Key getKey(char c) {
        return characterToKeyMap.get(c);
    }
}
