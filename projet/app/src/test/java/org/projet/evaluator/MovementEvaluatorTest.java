package org.projet.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

/**
 * Tests unitaires pour la classe MovementEvaluator.
 */
public class MovementEvaluatorTest {
    private MovementEvaluator evaluator;
    private Key leftPinkyKey;
    private Key leftRingKey;
    private Key leftMiddleKey;
    private Key leftIndexKey;
    private Key rightIndexKey;
    private Key rightMiddleKey;
    private Key rightRingKey;
    private Key rightPinkyKey;

    @BeforeEach
    void setUp() {
        evaluator = new MovementEvaluator();
        
        // Créer des touches de test
        leftPinkyKey = new Key(1, 0, Finger.LEFT_PINKY, 'A', null);
        leftRingKey = new Key(1, 1, Finger.LEFT_RING, 'Z', null);
        leftMiddleKey = new Key(1, 2, Finger.LEFT_MIDDLE, 'E', '€');
        leftIndexKey = new Key(1, 3, Finger.LEFT_INDEX, 'R', null);
        rightIndexKey = new Key(1, 4, Finger.RIGHT_INDEX, 'T', null);
        rightMiddleKey = new Key(1, 5, Finger.RIGHT_MIDDLE, 'Y', null);
        rightRingKey = new Key(1, 6, Finger.RIGHT_RING, 'U', null);
        rightPinkyKey = new Key(1, 7, Finger.RIGHT_PINKY, 'I', null);
    }

    @Test
    @DisplayName("Test de détection du même doigt")
    void testSameFinger() {
        assertTrue(evaluator.isSameFinger(leftPinkyKey, leftPinkyKey));
        assertFalse(evaluator.isSameFinger(leftPinkyKey, leftRingKey));
    }

    @Test
    @DisplayName("Test de détection de la même main")
    void testSameHand() {
        assertTrue(evaluator.isSameHand(leftPinkyKey, leftRingKey));
        assertTrue(evaluator.isSameHand(rightIndexKey, rightPinkyKey));
        assertFalse(evaluator.isSameHand(leftPinkyKey, rightPinkyKey));
    }

    @Test
    @DisplayName("Test de détection de la main gauche")
    void testLeftHand() {
        assertTrue(evaluator.isLeftHand(Finger.LEFT_PINKY));
        assertTrue(evaluator.isLeftHand(Finger.LEFT_RING));
        assertFalse(evaluator.isLeftHand(Finger.RIGHT_INDEX));
    }

    @Test
    @DisplayName("Test de détection d'extension latérale")
    void testLateralStretch() {
        // Extension latérale sur la main gauche
        assertFalse(evaluator.isLateralStretch(leftPinkyKey, leftRingKey)); // Adjacent
        assertTrue(evaluator.isLateralStretch(leftPinkyKey, leftIndexKey)); // 3 colonnes d'écart

        // Extension latérale sur la main droite
        assertFalse(evaluator.isLateralStretch(rightIndexKey, rightMiddleKey)); // Adjacent
        assertTrue(evaluator.isLateralStretch(rightIndexKey, rightPinkyKey)); // 3 colonnes d'écart

        // Pas d'extension latérale entre les mains
        assertFalse(evaluator.isLateralStretch(leftPinkyKey, rightPinkyKey));
    }

    @Test
    @DisplayName("Test de détection de roulement intérieur")
    void testInwardRoll() {
        // Roulement intérieur sur la main gauche
        assertTrue(evaluator.isInwardRoll(leftPinkyKey, leftRingKey));
        assertTrue(evaluator.isInwardRoll(leftRingKey, leftMiddleKey));

        // Roulement intérieur sur la main droite
        assertTrue(evaluator.isInwardRoll(rightPinkyKey, rightRingKey));
        assertTrue(evaluator.isInwardRoll(rightRingKey, rightMiddleKey));

        // Pas de roulement intérieur entre les mains
        assertFalse(evaluator.isInwardRoll(leftPinkyKey, rightPinkyKey));
    }

    @Test
    @DisplayName("Test de détection de mouvement en ciseaux")
    void testScissors() {
        // Créer des touches sur différentes rangées
        Key leftPinkyUpper = new Key(0, 0, Finger.LEFT_PINKY, 'A', null);
        Key leftRingLower = new Key(2, 1, Finger.LEFT_RING, 'Z', null);
        Key rightPinkyUpper = new Key(0, 7, Finger.RIGHT_PINKY, 'I', null);
        Key rightRingLower = new Key(2, 6, Finger.RIGHT_RING, 'U', null);

        // Mouvement en ciseaux sur la main gauche
        assertTrue(evaluator.isScissors(leftPinkyUpper, leftRingLower));

        // Mouvement en ciseaux sur la main droite
        assertTrue(evaluator.isScissors(rightPinkyUpper, rightRingLower));

        // Pas de ciseaux entre les mains
        assertFalse(evaluator.isScissors(leftPinkyUpper, rightPinkyUpper));
    }

    @Test
    @DisplayName("Test de détection de redirection")
    void testRedirection() {
        // Créer des touches pour tester la redirection
        Key leftPinkyKey = new Key(1, 0, Finger.LEFT_PINKY, 'A', null);
        Key leftMiddleKey = new Key(1, 2, Finger.LEFT_MIDDLE, 'E', '€');
        Key leftRingKey = new Key(1, 1, Finger.LEFT_RING, 'Z', null);

        // Redirection sur la main gauche
        assertTrue(evaluator.isRedirection(leftPinkyKey, leftMiddleKey, leftRingKey));

        // Pas de redirection si même direction
        assertFalse(evaluator.isRedirection(leftPinkyKey, leftRingKey, leftMiddleKey));

        // Pas de redirection entre les mains
        assertFalse(evaluator.isRedirection(leftPinkyKey, leftRingKey, rightPinkyKey));
    }

    @Test
    @DisplayName("Test de détection de mauvaise redirection")
    void testBadRedirection() {
        // Créer des touches pour tester la mauvaise redirection
        Key leftPinkyKey = new Key(1, 0, Finger.LEFT_PINKY, 'A', null);
        Key leftIndexKey = new Key(1, 3, Finger.LEFT_INDEX, 'R', null);
        Key leftMiddleKey = new Key(1, 2, Finger.LEFT_MIDDLE, 'E', '€');

        // Mauvaise redirection avec extension latérale
        assertTrue(evaluator.isBadRedirection(leftPinkyKey, leftIndexKey, leftMiddleKey));

        // Pas de mauvaise redirection si mouvement court
        assertFalse(evaluator.isBadRedirection(leftPinkyKey, leftMiddleKey, leftIndexKey));
    }
}
