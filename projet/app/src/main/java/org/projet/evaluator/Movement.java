package org.projet.evaluator;

/**
 * Énumération des différents types de mouvements possibles sur le clavier.
 */
public enum Movement {
    // Mouvements à une touche
    SINGLE_KEY,             // Une touche simple
    HAND_BALANCE,          // Équilibre entre les mains
    FINGER_BALANCE,        // Équilibre entre les doigts

    // Bigrammes (mouvements à deux touches)
    SAME_FINGER_BIGRAM,    // Même doigt (SFB)
    LATERAL_STRETCH,       // Extension latérale (LSB)
    SCISSORS,              // Ciseaux (rangée sup -> inf ou inv.)
    HAND_ALTERNATION,      // Alternance des mains
    INWARD_ROLL,          // Roulement vers l'intérieur
    OUTWARD_ROLL,         // Roulement vers l'extérieur

    // Trigrammes (mouvements à trois touches)
    BAD_REDIRECT,         // Mauvaise redirection (sans index)
    REDIRECT,             // Redirection normale
    SAME_FINGER_SKIPGRAM; // Skipgram même doigt (SKS)

    /**
     * Retourne le poids par défaut pour ce mouvement.
     * Les valeurs négatives sont pour les mouvements à maximiser.
     */
    public double getDefaultWeight() {
        return switch (this) {
            // Mouvements à une touche
            case SINGLE_KEY -> 0.5;      // Léger malus pour les touches difficiles
            case HAND_BALANCE -> 2.0;    // Important pour l'équilibre
            case FINGER_BALANCE -> 1.5;  // Important mais moins que l'équilibre des mains

            // Bigrammes - À minimiser
            case SAME_FINGER_BIGRAM -> 5.0;  // Très mauvais
            case LATERAL_STRETCH -> 4.0;     // Mauvais
            case SCISSORS -> 3.0;            // À éviter

            // Bigrammes - À maximiser
            case HAND_ALTERNATION -> -2.0;   // Très bon
            case INWARD_ROLL -> -1.5;        // Bon
            case OUTWARD_ROLL -> -1.0;       // Acceptable

            // Trigrammes - Tous à minimiser
            case BAD_REDIRECT -> 6.0;        // Le pire
            case REDIRECT -> 3.0;            // À éviter
            case SAME_FINGER_SKIPGRAM -> 4.0; // Très mauvais
        };
    }
}
