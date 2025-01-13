package org.projet.analyzer;

import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardLayout.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour analyser les caractères accentués et leurs séquences de touches.
 */
public class AccentAnalyzer {
    private final TextAnalyzer textAnalyzer;
    private final Map<Character, String> keySequences;
    private final KeyboardLayout keyboardLayout;

    private static final String SHIFT = "⇧";  // Symbole pour la touche Shift
    private static final String ALTGR = "⌥";  // Symbole pour la touche AltGr
    private static final String ALT = "⎇";    // Symbole pour la touche Alt
    private static final String DEAD_CIRCUMFLEX = "^";  // Touche morte pour les accents circonflexes
    private static final String DEAD_DIAERESIS = "¨";  // Touche morte pour les trémas

    public AccentAnalyzer(TextAnalyzer textAnalyzer, KeyboardLayout keyboardLayout) {
        this.textAnalyzer = textAnalyzer;
        this.keyboardLayout = keyboardLayout;
        this.keySequences = new HashMap<>();
        initializeKeySequences();
    }

    private void initializeKeySequences() {
        // Parcourir toutes les touches du clavier
        keyboardLayout.getKeys().forEach((character, key) -> {
            // Caractère de base
            keySequences.put(character, String.valueOf(character));

            // Caractère avec Shift si disponible
            if (key.shiftProduces() != null) {
                keySequences.put(key.shiftProduces(), SHIFT + character);
            }

            // Caractère avec AltGr si disponible
            if (key.altgrProduces() != null) {
                keySequences.put(key.altgrProduces(), ALTGR + character);
            }
        });

        // Caractères nécessitant une touche morte (^)
        keySequences.put('â', DEAD_CIRCUMFLEX + "a");
        keySequences.put('ê', DEAD_CIRCUMFLEX + "e");
        keySequences.put('î', DEAD_CIRCUMFLEX + "i");
        keySequences.put('ô', DEAD_CIRCUMFLEX + "o");
        keySequences.put('û', DEAD_CIRCUMFLEX + "u");

        // Caractères nécessitant une touche morte (¨)
        keySequences.put('ë', DEAD_DIAERESIS + "e");
        keySequences.put('ï', DEAD_DIAERESIS + "i");
        keySequences.put('ü', DEAD_DIAERESIS + "u");
        keySequences.put('ÿ', DEAD_DIAERESIS + "y");
    }

    /**
     * Analyse un texte en prenant en compte les séquences de touches pour les accents.
     */
    public void analyzeAccentedText(String text) {
        List<String> keyStrokes = new ArrayList<>();
        
        // Convertir le texte en séquence de frappes
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String sequence = keySequences.get(c);
            
            if (sequence != null) {
                // Pour un caractère nécessitant une séquence de touches
                for (char keystroke : sequence.toCharArray()) {
                    keyStrokes.add(String.valueOf(keystroke));
                }
            } else {
                // Pour un caractère normal, l'ajouter tel quel
                keyStrokes.add(String.valueOf(c));
            }
        }
        
        // Analyser la séquence de touches avec TextAnalyzer
        textAnalyzer.analyzeKeyStrokes(keyStrokes);
    }

    /**
     * Vérifie si un caractère nécessite une séquence de touches spéciale.
     */
    public boolean needsSpecialSequence(char c) {
        return keySequences.containsKey(c);
    }

    /**
     * Retourne la séquence de touches pour un caractère.
     */
    public String getKeySequence(char c) {
        return keySequences.get(c);
    }
}
