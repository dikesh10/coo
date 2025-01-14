package org.projet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to load and save keyboard configurations.
 */
public class KeyboardConfigLoader {
    private final ObjectMapper mapper;
    
    public KeyboardConfigLoader() {
        this.mapper = new ObjectMapper();
    }
    
    /**
     * Loads a keyboard configuration from a JSON file.
     *
     * @param configFile the JSON configuration file
     * @return the keyboard layout, or empty if loading fails
     */
    public Optional<KeyboardLayout> loadLayout(Path configFile) {
        try {
            KeyboardConfig config = mapper.readValue(configFile.toFile(), KeyboardConfig.class);
            
            // Convertir la configuration en KeyboardLayout
            Map<Character, Key> keyMap = config.keys().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new Key(
                        e.getValue().row(),
                        e.getValue().column(),
                        Finger.valueOf(e.getValue().finger()),
                        e.getValue().shiftProduces() != null ? 
                            e.getValue().shiftProduces().charAt(0) : null,
                        e.getValue().altgrProduces() != null ? 
                            e.getValue().altgrProduces().charAt(0) : null
                    )
                ));
            
            return Optional.of(new KeyboardLayout(config.name(), keyMap));
            
        } catch (Exception e) {
            System.err.println("Failed to load keyboard config: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Saves a keyboard configuration to a JSON file.
     *
     */
    public boolean saveLayout(KeyboardLayout layout, Path configFile) {
        try {
            // TODO: Implement the conversion from KeyboardLayout to KeyboardConfig
            // This will be implemented once we have all the necessary data in KeyboardLayout
            throw new UnsupportedOperationException("Saving layouts not yet implemented");
        } catch (Exception e) {
            System.err.println("Failed to save keyboard config: " + e.getMessage());
            return false;
        }
    }
}
