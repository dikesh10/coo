package org.projet.analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

/**
 * Tests unitaires pour la classe TextAnalyzer.
 */
public class TextAnalyzerTest {
    private TextAnalyzer analyzer;
    private KeyboardLayout testLayout;

    @BeforeEach
    void setUp() {
        // Créer une disposition de test simple
        Map<Character, Key> keys = new HashMap<>();
        keys.put('a', new Key(0, 0, Finger.LEFT_PINKY, null, null));
        keys.put('e', new Key(0, 1, Finger.LEFT_RING, null, null));
        keys.put('h', new Key(0, 2, Finger.LEFT_MIDDLE, null, null));
        keys.put('l', new Key(0, 3, Finger.LEFT_INDEX, null, null));
        keys.put('o', new Key(0, 4, Finger.RIGHT_INDEX, null, null));
        
        testLayout = new KeyboardLayout("TEST", keys);
        analyzer = new TextAnalyzer(testLayout);
    }

    @AfterEach
    void tearDown() {
        analyzer.shutdown();
    }

    @Test
    @DisplayName("Test de l'analyse des caractères individuels")
    void testAnalyzeSimpleText() {
        String text = "hello";
        analyzer.analyzeText(text);
        
        // Vérifier les fréquences individuelles
        assertEquals(1L, analyzer.getFrequency("h"));
        assertEquals(1L, analyzer.getFrequency("e"));
        assertEquals(2L, analyzer.getFrequency("l"));
        assertEquals(1L, analyzer.getFrequency("o"));
        
        // Vérifier le total des caractères
        assertEquals(5L, analyzer.getTotalCharacters());
    }

    @Test
    @DisplayName("Test de l'analyse des bigrammes")
    void testAnalyzeBigrams() {
        String text = "hello";
        analyzer.analyzeText(text);
        
        // Vérifier les bigrammes
        assertEquals(1L, analyzer.getFrequency("he"));
        assertEquals(1L, analyzer.getFrequency("el"));
        assertEquals(1L, analyzer.getFrequency("ll"));
        assertEquals(1L, analyzer.getFrequency("lo"));
        
        // Vérifier qu'un bigramme inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("xx"));
    }

    @Test
    @DisplayName("Test de l'analyse des trigrammes")
    void testAnalyzeTrigrams() {
        String text = "hello";
        analyzer.analyzeText(text);
        
        // Vérifier les trigrammes
        assertEquals(1L, analyzer.getFrequency("hel"));
        assertEquals(1L, analyzer.getFrequency("ell"));
        assertEquals(1L, analyzer.getFrequency("llo"));
        
        // Vérifier qu'un trigramme inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("xyz"));
    }

    @Test
    @DisplayName("Test avec un texte vide")
    void testEmptyText() {
        analyzer.analyzeText("");
        
        // Vérifier que le texte vide est géré correctement
        assertEquals(0L, analyzer.getTotalCharacters());
        assertEquals(0L, analyzer.getFrequency("a")); // N'importe quel caractère devrait retourner 0
    }

    @Test
    @DisplayName("Test avec plusieurs analyses successives")
    void testMultipleAnalysis() {
        // Analyser deux textes successivement
        analyzer.analyzeTexts(Arrays.asList("hello", "world"));
        
        // Vérifier le total des caractères
        assertEquals(10L, analyzer.getTotalCharacters());
        
        // Vérifier les fréquences individuelles
        assertEquals(1L, analyzer.getFrequency("h"));
        assertEquals(1L, analyzer.getFrequency("e"));
        assertEquals(3L, analyzer.getFrequency("l"));
        assertEquals(2L, analyzer.getFrequency("o"));
        assertEquals(1L, analyzer.getFrequency("w"));
        assertEquals(1L, analyzer.getFrequency("r"));
        assertEquals(1L, analyzer.getFrequency("d"));
        
        // Vérifier qu'un caractère inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("x"));
    }

    @Test
    @DisplayName("Test du traitement parallèle")
    void testParallelProcessing() {
        // Créer une liste de textes à analyser en parallèle
        var texts = Arrays.asList(
            "hello",      // 5 caractères
            "world",      // 5 caractères
            "test",       // 4 caractères
            "parallel",   // 8 caractères
            "processing" // 10 caractères
        );
        
        analyzer.analyzeTexts(texts);
        
        // Vérifier le total des caractères (5 + 5 + 4 + 8 + 10 = 32)
        assertEquals(32L, analyzer.getTotalCharacters());
        
        // Vérifier les caractères qui apparaissent un nombre spécifique de fois
        assertTrue(analyzer.getFrequency("h") == 1L); // uniquement dans "hello"
        assertTrue(analyzer.getFrequency("w") == 1L); // uniquement dans "world"
        assertTrue(analyzer.getFrequency("t") >= 2L); // dans "test" et peut-être ailleurs
        assertTrue(analyzer.getFrequency("l") >= 3L); // dans "hello", "parallel"
        assertTrue(analyzer.getFrequency("e") >= 2L); // dans "hello", "test"
        assertTrue(analyzer.getFrequency("s") >= 2L); // dans "test", "processing"
        
        // Vérifier que des caractères inexistants retournent 0
        assertEquals(0L, analyzer.getFrequency("x"));
        assertEquals(0L, analyzer.getFrequency("y"));
        assertEquals(0L, analyzer.getFrequency("z"));
    }

    @Test
    @DisplayName("Test des pourcentages")
    void testPercentages() {
        String text = "hello";
        analyzer.analyzeText(text);
        
        // Vérifier les pourcentages
        assertEquals(20.0, analyzer.getPercentage("h")); // 1/5 = 20%
        assertEquals(20.0, analyzer.getPercentage("e")); // 1/5 = 20%
        assertEquals(40.0, analyzer.getPercentage("l")); // 2/5 = 40%
        assertEquals(20.0, analyzer.getPercentage("o")); // 1/5 = 20%
        assertEquals(0.0, analyzer.getPercentage("x")); // caractère inexistant
    }
}
