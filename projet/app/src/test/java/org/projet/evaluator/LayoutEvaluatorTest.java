package org.projet.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.projet.model.KeyboardLayout;
import java.util.Map;
import java.util.HashMap;

/**
 * Tests unitaires pour la classe LayoutEvaluator.
 */
public class LayoutEvaluatorTest {
    private LayoutEvaluator evaluator;
    private KeyboardLayout layout;
    private Map<String, Long> ngramFrequencies;

    @BeforeEach
    void setUp() {
        // Initialiser les fréquences de test
        ngramFrequencies = new HashMap<>();
        
        // Ajouter quelques fréquences de test
        ngramFrequencies.put("e", 100L);
        ngramFrequencies.put("t", 90L);
        ngramFrequencies.put("a", 80L);
        ngramFrequencies.put("th", 50L);
        ngramFrequencies.put("he", 45L);
        ngramFrequencies.put("the", 40L);
        
        evaluator = new LayoutEvaluator(ngramFrequencies);
        
        // Créer une disposition de test (AZERTY simplifiée)
        Map<Character, KeyboardLayout.Key> characterToKeyMap = new HashMap<>();
        characterToKeyMap.put('a', new KeyboardLayout.Key(2, 0, KeyboardLayout.Finger.LEFT_PINKY, 'A', null));
        characterToKeyMap.put('z', new KeyboardLayout.Key(2, 1, KeyboardLayout.Finger.LEFT_RING, 'Z', null));
        characterToKeyMap.put('e', new KeyboardLayout.Key(2, 2, KeyboardLayout.Finger.LEFT_MIDDLE, 'E', '€'));
        characterToKeyMap.put('r', new KeyboardLayout.Key(2, 3, KeyboardLayout.Finger.LEFT_INDEX, 'R', null));
        characterToKeyMap.put('t', new KeyboardLayout.Key(2, 4, KeyboardLayout.Finger.LEFT_INDEX, 'T', null));
        characterToKeyMap.put('y', new KeyboardLayout.Key(2, 5, KeyboardLayout.Finger.RIGHT_INDEX, 'Y', null));
        
        layout = new KeyboardLayout("Test AZERTY", characterToKeyMap);
    }

    @Test
    @DisplayName("Test de l'évaluation globale")
    void testEvaluateLayout() {
        double score = evaluator.evaluateLayout(layout);
        
        // Le score ne devrait pas être négatif
        assertTrue(score >= 0);
        
        // Vérifier que l'évaluation est déterministe
        assertEquals(score, evaluator.evaluateLayout(layout));
    }

    @Test
    @DisplayName("Test des statistiques de mouvements")
    void testMovementStats() {
        evaluator.evaluateLayout(layout);
        Map<MovementType, Long> stats = evaluator.getMovementCounts();
        
        // Vérifier que nous avons des statistiques pour chaque type de mouvement
        for (MovementType type : MovementType.values()) {
            assertNotNull(stats.get(type));
        }
    }

    @Test
    @DisplayName("Test de l'équilibre des mains")
    void testHandBalance() {
        evaluator.evaluateLayout(layout);
        Map<KeyboardLayout.Finger, Double> fingerLoads = evaluator.getFingerLoads();
        
        // Vérifier que nous avons des charges pour chaque doigt
        assertFalse(fingerLoads.isEmpty());
        
        // Vérifier que les charges sont entre 0 et 100%
        for (Double load : fingerLoads.values()) {
            assertTrue(load >= 0 && load <= 100);
        }
    }

    @Test
    @DisplayName("Test avec une disposition vide")
    void testEmptyLayout() {
        KeyboardLayout emptyLayout = new KeyboardLayout("Empty", new HashMap<>());
        double score = evaluator.evaluateLayout(emptyLayout);
        
        // Une disposition vide devrait avoir un score de 0
        assertEquals(0.0, score);
    }

    @Test
    @DisplayName("Test avec des n-grammes inconnus")
    void testUnknownNgrams() {
        // Ajouter des n-grammes qui n'existent pas dans la disposition
        Map<String, Long> unknownNgrams = new HashMap<>();
        unknownNgrams.put("xyz", 100L);
        
        LayoutEvaluator testEvaluator = new LayoutEvaluator(unknownNgrams);
        double score = testEvaluator.evaluateLayout(layout);
        
        // Le score devrait être 0 car aucun n-gramme ne correspond
        assertEquals(0.0, score);
    }
}
