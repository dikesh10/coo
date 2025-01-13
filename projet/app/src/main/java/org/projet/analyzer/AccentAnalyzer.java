package org.projet.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour analyser les caractères accentués et leurs séquences de touches.
 */
public class AccentAnalyzer {
    private final Map<Character, String> keySequences;
    private final TextAnalyzer textAnalyzer;
    private static final String SHIFT = "⇧";  // Symbole pour la touche Shift
    private static final String ALTGR = "⌥";  // Symbole pour la touche AltGr
    private static final String ALT = "⎇";    // Symbole pour la touche Alt

    public AccentAnalyzer(TextAnalyzer textAnalyzer) {
        this.textAnalyzer = textAnalyzer;
        this.keySequences = new HashMap<>();
        initializeKeySequences();
    }

    private void initializeKeySequences() {
        // Accents circonflexes
        keySequences.put('â', "^a");
        keySequences.put('ê', "^e");
        keySequences.put('î', "^i");
        keySequences.put('ô', "^o");
        keySequences.put('û', "^u");

        // Accents aigus
        keySequences.put('é', "´e");
        keySequences.put('á', "´a");
        keySequences.put('í', "´i");
        keySequences.put('ó', "´o");
        keySequences.put('ú', "´u");

        // Accents graves
        keySequences.put('à', "`a");  // Séquence accent grave + a
        keySequences.put('è', "`e");
        keySequences.put('ì', "`i");
        keySequences.put('ò', "`o");
        keySequences.put('ù', "`u");

        // Tréma
        keySequences.put('ë', "¨e");
        keySequences.put('ï', "¨i");
        keySequences.put('ü', "¨u");
        keySequences.put('ÿ', "¨y");

        // Cédille
        keySequences.put('ç', "c,");

        // Chiffres (nécessitent Shift sur AZERTY)
        keySequences.put('1', SHIFT + "&");
        keySequences.put('2', SHIFT + "é");
        keySequences.put('3', SHIFT + "\"");
        keySequences.put('4', SHIFT + "'");
        keySequences.put('5', SHIFT + "(");
        keySequences.put('6', SHIFT + "-");
        keySequences.put('7', SHIFT + "è");
        keySequences.put('8', SHIFT + "_");
        keySequences.put('9', SHIFT + "ç");
        keySequences.put('0', SHIFT + "à");

        // Caractères spéciaux nécessitant Shift
        keySequences.put('§', SHIFT + "!");
        keySequences.put('/', SHIFT + ":");
        keySequences.put('*', SHIFT + "$");
        keySequences.put('+', SHIFT + "=");
        keySequences.put('?', SHIFT + ",");
        keySequences.put('>', SHIFT + ".");
        keySequences.put('<', SHIFT + ",");
        keySequences.put('£', SHIFT + "$");

        // Caractères avec AltGr
        keySequences.put('€', ALTGR + "E");
        keySequences.put('#', ALTGR + "\"");
        keySequences.put('{', ALTGR + "'");
        keySequences.put('}', ALTGR + "=");
        keySequences.put('[', ALTGR + "(");
        keySequences.put(']', ALTGR + ")");
        keySequences.put('|', ALTGR + "-");
        keySequences.put('\\', ALTGR + "_");
        keySequences.put('@', ALTGR + "à");
        keySequences.put('~', ALTGR + "é");
        keySequences.put('¤', ALTGR + "$");

        // Caractères avec Alt
        keySequences.put('æ', ALT + "f");
        keySequences.put('œ', ALT + "o");
        keySequences.put('±', ALT + "+");
        keySequences.put('≤', ALT + "<");
        keySequences.put('≥', ALT + ">");
        keySequences.put('÷', ALT + ":");
        keySequences.put('×', ALT + "*");
        keySequences.put('≠', ALT + "=");
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
