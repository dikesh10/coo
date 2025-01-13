package org.projet.keyboard.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Charge les configurations de clavier depuis des fichiers JSON.
 */
public class KeyboardConfig {
    private final ObjectMapper mapper;

    public KeyboardConfig() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Charge une disposition de clavier depuis un fichier JSON.
     * Format attendu:
     * {
     *   "name": "AZERTY",
     *   "description": "Standard French AZERTY layout",
     *   "keys": {
     *     "a": {
     *       "row": 2,
     *       "column": 0,
     *       "finger": "LEFT_PINKY",
     *       "shiftProduces": "A",
     *       "altgrProduces": null
     *     },
     *     ...
     *   }
     * }
     * 
     * @param path Chemin vers le fichier JSON
     * @return La disposition de clavier chargée
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public Optional<KeyboardLayout> loadLayout(Path path) throws IOException {
        JsonNode root = mapper.readTree(path.toFile());
        
        String name = root.get("name").asText();
        JsonNode keys = root.get("keys");
        
        Map<Character, Key> characterToKeyMap = new HashMap<>();
        
        keys.fields().forEachRemaining(entry -> {
            String character = entry.getKey();
            JsonNode keyInfo = entry.getValue();

            int row = keyInfo.get("row").asInt();
            int column = keyInfo.get("column").asInt();
            Finger finger = Finger.valueOf(keyInfo.get("finger").asText());
            
            Character shiftProduces = keyInfo.has("shiftProduces") 
                ? keyInfo.get("shiftProduces").asText().charAt(0)
                : null;
                
            Character altgrProduces = keyInfo.has("altgrProduces")
                ? keyInfo.get("altgrProduces").asText().charAt(0)
                : null;

            characterToKeyMap.put(
                character.charAt(0),
                new Key(row, column, finger, shiftProduces, altgrProduces)
            );
        });
        
        return Optional.of(new KeyboardLayout(name, characterToKeyMap));
    }
}
