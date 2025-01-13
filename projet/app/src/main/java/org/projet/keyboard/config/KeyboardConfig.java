package org.projet.keyboard.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.projet.keyboard.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

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
    public KeyboardLayout loadLayout(Path path) throws IOException {
        JsonNode root = mapper.readTree(path.toFile());
        
        String name = root.get("name").asText();
        JsonNode keys = root.get("keys");
        
        Map<Character, KeyPosition> layout = new HashMap<>();
        
        // Parcourir tous les caractères
        Iterator<Map.Entry<String, JsonNode>> fields = keys.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            char c = entry.getKey().charAt(0);
            JsonNode keyInfo = entry.getValue();
            
            // Convertir le nom du doigt au format de notre enum
            String fingerName = keyInfo.get("finger").asText()
                .replace("LEFT_", "")
                .replace("RIGHT_", "")
                .replace("PINKY", "PINKY_LEFT")
                .replace("RING", "RING_LEFT")
                .replace("MIDDLE", "MIDDLE_LEFT")
                .replace("INDEX", "INDEX_LEFT");
            
            if (keyInfo.get("finger").asText().startsWith("RIGHT_")) {
                fingerName = fingerName.replace("_LEFT", "_RIGHT");
            }
            
            Finger finger = Finger.valueOf(fingerName);
            
            KeyPosition position = new KeyPosition(
                keyInfo.get("row").asInt(),
                keyInfo.get("column").asInt(),
                finger,
                finger.getHand(),
                // Une touche est considérée comme "home key" si elle est sur la rangée du milieu (1)
                // et utilise le doigt par défaut pour sa colonne
                keyInfo.get("row").asInt() == 1 && isDefaultFingerForColumn(keyInfo.get("column").asInt(), finger)
            );
            
            layout.put(c, position);
        }
        
        return new KeyboardLayout(layout, name);
    }
    
    /**
     * Vérifie si le doigt est le doigt par défaut pour une colonne donnée.
     */
    private boolean isDefaultFingerForColumn(int column, Finger finger) {
        return switch (column) {
            case 0 -> finger == Finger.PINKY_LEFT;
            case 1 -> finger == Finger.RING_LEFT;
            case 2 -> finger == Finger.MIDDLE_LEFT;
            case 3 -> finger == Finger.INDEX_LEFT;
            case 4 -> finger == Finger.INDEX_LEFT;
            case 5 -> finger == Finger.INDEX_RIGHT;
            case 6 -> finger == Finger.INDEX_RIGHT;
            case 7 -> finger == Finger.MIDDLE_RIGHT;
            case 8 -> finger == Finger.RING_RIGHT;
            case 9 -> finger == Finger.PINKY_RIGHT;
            default -> false;
        };
    }
}
