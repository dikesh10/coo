package org.projet.evaluator;

/**
 * Types de mouvements à évaluer sur un clavier.
 */
public enum MovementType {
    // Mouvements à une touche
    SAME_FINGER,           // Même doigt
    LATERAL_STRETCH,       // Extension latérale
    
    // Bigrammes
    HAND_ALTERNATION,      // Alternance des mains
    INWARD_ROLL,          // Roulement vers l'intérieur
    OUTWARD_ROLL,         // Roulement vers l'extérieur
    SCISSORS,             // Mouvement en ciseaux
    
    // Trigrammes
    BAD_REDIRECTION,      // Mauvaise redirection (sans index)
    REDIRECTION,          // Redirection normale
    SAME_FINGER_SKIPGRAM  // Même doigt avec une touche intermédiaire
}
