package org.projet.evaluator;

import org.projet.model.KeyboardLayout;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * Évalue les dispositions de clavier selon différents critères ergonomiques.
 */
public class LayoutEvaluator {
    private final Map<String, Long> ngramFrequencies;
    private final MovementEvaluator movementEvaluator;
    private final Map<MovementType, Double> weights;
    private final Map<MovementType, Long> movementCounts;
    private long totalBigramCount;
    private long totalTrigramCount;
    private Map<KeyboardLayout.Finger, Double> fingerLoads;
    
    private static final Map<KeyboardLayout.Finger, Double> IDEAL_FINGER_LOADS;
    static {
        IDEAL_FINGER_LOADS = new EnumMap<>(KeyboardLayout.Finger.class);
        // Index (les plus forts)
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.LEFT_INDEX, 18.0);
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.RIGHT_INDEX, 18.0);
        // Majeurs (assez forts)
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.LEFT_MIDDLE, 15.0);
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.RIGHT_MIDDLE, 15.0);
        // Annulaires (plus faibles)
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.LEFT_RING, 12.0);
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.RIGHT_RING, 12.0);
        // Auriculaires (les plus faibles)
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.LEFT_PINKY, 5.0);
        IDEAL_FINGER_LOADS.put(KeyboardLayout.Finger.RIGHT_PINKY, 5.0);
    }

    public LayoutEvaluator(Map<String, Long> ngramFrequencies) {
        this.ngramFrequencies = Map.copyOf(ngramFrequencies);
        this.movementEvaluator = new MovementEvaluator();
        this.weights = initializeWeights();
        this.movementCounts = new EnumMap<>(MovementType.class);
        this.fingerLoads = new EnumMap<>(KeyboardLayout.Finger.class);
        for (MovementType type : MovementType.values()) {
            movementCounts.put(type, 0L);
        }
        for (KeyboardLayout.Finger finger : KeyboardLayout.Finger.values()) {
            fingerLoads.put(finger, 0.0);
        }
        this.totalBigramCount = 0;
        this.totalTrigramCount = 0;
    }
    
    private Map<MovementType, Double> initializeWeights() {
        Map<MovementType, Double> weights = new EnumMap<>(MovementType.class);
        
        // Pénalités pour les mouvements indésirables
        weights.put(MovementType.SAME_FINGER, 2.0);        // Forte pénalité pour même doigt
        weights.put(MovementType.LATERAL_STRETCH, 1.5);    // Pénalité pour extension
        weights.put(MovementType.SCISSORS, 1.8);           // Pénalité pour ciseaux
        weights.put(MovementType.BAD_REDIRECTION, 1.7);    // Pénalité pour mauvaise redirection
        weights.put(MovementType.REDIRECTION, 1.2);        // Pénalité légère pour redirection
        weights.put(MovementType.SAME_FINGER_SKIPGRAM, 1.6); // Pénalité pour skipgram
        
        // Bonus pour les bons mouvements
        weights.put(MovementType.HAND_ALTERNATION, -0.8);  // Bonus pour alternance
        weights.put(MovementType.INWARD_ROLL, -1.0);       // Bonus pour roulement intérieur
        weights.put(MovementType.OUTWARD_ROLL, -0.5);      // Petit bonus pour roulement extérieur
        
        return weights;
    }
    
    /**
     * Retourne les statistiques de mouvements après l'évaluation.
     */
    public Map<MovementType, Long> getMovementCounts() {
        return Map.copyOf(movementCounts);
    }
    
    /**
     * Retourne les charges de chaque doigt après l'évaluation.
     */
    public Map<KeyboardLayout.Finger, Double> getFingerLoads() {
        return Map.copyOf(fingerLoads);
    }
    
    /**
     * Évalue une disposition de clavier et retourne un score.
     * Plus le score est bas, meilleure est la disposition.
     */
    public double evaluateLayout(KeyboardLayout layout) {
        if (layout == null || layout.getKeys().isEmpty()) {
            return 0.0;
        }

        // Réinitialiser les compteurs
        movementCounts.clear();
        fingerLoads.clear();
        
        // Initialiser les compteurs à zéro
        for (MovementType type : MovementType.values()) {
            movementCounts.put(type, 0L);
        }
        for (KeyboardLayout.Finger finger : KeyboardLayout.Finger.values()) {
            fingerLoads.put(finger, 0.0);
        }
        
        totalBigramCount = 0;
        totalTrigramCount = 0;
        
        double score = 0.0;
        
        // Évaluer les bigrammes
        for (Map.Entry<String, Long> entry : ngramFrequencies.entrySet()) {
            String ngram = entry.getKey();
            if (ngram.length() == 2) {
                MovementType movement = movementEvaluator.evaluateBigramMovement(layout, ngram);
                if (movement != null) {
                    long count = entry.getValue();
                    movementCounts.merge(movement, count, Long::sum);
                    totalBigramCount += count;
                    score += weights.get(movement) * count;
                }
            }
        }

        // Si aucun bigramme n'a été trouvé, retourner 0
        if (totalBigramCount == 0) {
            return 0.0;
        }
        
        // Évaluer les trigrammes
        for (Map.Entry<String, Long> entry : ngramFrequencies.entrySet()) {
            String ngram = entry.getKey();
            if (ngram.length() == 3) {
                score += evaluateTrigram(layout, ngram, entry.getValue());
                totalTrigramCount += entry.getValue();
            }
        }
        
        // Ajouter la pénalité pour la répartition des doigts
        score += calculateFingerLoadScore();
        
        return score;
    }
    
    private KeyboardLayout.Key getKeyForCharacter(KeyboardLayout layout, char c) {
        // Gérer les caractères spéciaux
        switch (c) {
            case '`':  // accent grave
                return layout.characterToKeyMap().get('7');  // Sur AZERTY, l'accent grave est sur la touche 7
            case '^':  // accent circonflexe
                return layout.characterToKeyMap().get('9');  // Sur AZERTY, l'accent circonflexe est sur la touche 9
            case '¨':  // tréma
                return layout.characterToKeyMap().get('¨');  // Sur AZERTY, le tréma est une touche morte
            case '´':  // accent aigu
                return layout.characterToKeyMap().get('é');  // Sur AZERTY, l'accent aigu est sur la touche é
            case '⇧':  // touche Shift
                return new KeyboardLayout.Key(0, 0, KeyboardLayout.Finger.LEFT_PINKY, '⇧', '⇧');  // Position approximative de Shift
            default:
                return layout.characterToKeyMap().get(c);
        }
    }
    
    private double evaluateTrigram(KeyboardLayout layout, String trigram, long frequency) {
        if (trigram.length() != 3) return 0.0;
        
        char c1 = trigram.charAt(0);
        char c2 = trigram.charAt(1);
        char c3 = trigram.charAt(2);
        
        KeyboardLayout.Key key1 = getKeyForCharacter(layout, c1);
        KeyboardLayout.Key key2 = getKeyForCharacter(layout, c2);
        KeyboardLayout.Key key3 = getKeyForCharacter(layout, c3);
        
        if (key1 == null || key2 == null || key3 == null) return 0.0;
        
        double score = 0.0;
        
        if (movementEvaluator.isBadRedirection(key1, key2, key3)) {
            score += weights.get(MovementType.BAD_REDIRECTION);
            movementCounts.merge(MovementType.BAD_REDIRECTION, frequency, Long::sum);
        }
        else if (movementEvaluator.isRedirection(key1, key2, key3)) {
            score += weights.get(MovementType.REDIRECTION);
            movementCounts.merge(MovementType.REDIRECTION, frequency, Long::sum);
        }
        
        // Vérifier les skipgrams
        if (movementEvaluator.isSameFinger(key1, key3)) {
            score += weights.get(MovementType.SAME_FINGER_SKIPGRAM);
            movementCounts.merge(MovementType.SAME_FINGER_SKIPGRAM, frequency, Long::sum);
        }
        
        return score * frequency;
    }
    
    /**
     * Calcule la distance entre la répartition actuelle et la répartition idéale.
     * @return Le score de pénalité pour la répartition des doigts
     */
    private double calculateFingerLoadScore() {
        double score = 0.0;
        for (Map.Entry<KeyboardLayout.Finger, Double> entry : fingerLoads.entrySet()) {
            KeyboardLayout.Finger finger = entry.getKey();
            double actualLoad = entry.getValue();
            double idealLoad = IDEAL_FINGER_LOADS.get(finger);
            score += Math.abs(actualLoad - idealLoad);
        }
        return score * 0.1; // Facteur de pondération pour ne pas trop pénaliser
    }

    /**
     * Affiche les statistiques d'évaluation d'une disposition.
     */
    public void displayEvaluation(KeyboardLayout layout) {
        double score = evaluateLayout(layout);
        
        System.out.println("\nÉvaluation détaillée de la disposition " + layout.name() + " :");
        System.out.println("=".repeat(50));
        
        // Afficher la formule du score
        System.out.println("\nFormule du score :");
        System.out.println("-".repeat(30));
        System.out.println("Score = Score_bigrammes + Score_trigrammes");
        System.out.println("où :");
        System.out.println("  Score_bigrammes = Σ(occurrences_bigramme × poids_bigramme)");
        System.out.println("  Score_trigrammes = Σ(occurrences_trigramme × poids_trigramme)");
        System.out.println("\nPoids = {");
        System.out.println("  // Pénalités (mouvements indésirables) -> augmentent le score");
        System.out.println("  même_doigt          : +2.00");
        System.out.println("  extension_latérale  : +1.50");
        System.out.println("  ciseaux            : +1.80");
        System.out.println("  mauvaise_redirection: +1.70");
        System.out.println("  redirection        : +1.20");
        System.out.println("  skipgram_même_doigt : +1.60");
        System.out.println();
        System.out.println("  // Bonus (mouvements favorables) -> diminuent le score");
        System.out.println("  alternance_mains    : -0.80");
        System.out.println("  roulement_intérieur : -1.00");
        System.out.println("  roulement_extérieur : -0.50");
        System.out.println("}");
        System.out.println("\nNote : Un score négatif est meilleur car il indique plus de");
        System.out.println("mouvements favorables (bonus) que de mouvements pénalisés.");
        
        // Afficher les charges des doigts
        System.out.println("\nCharges des doigts (%) :");
        System.out.println("-".repeat(30));
        for (Map.Entry<KeyboardLayout.Finger, Double> entry : fingerLoads.entrySet()) {
            System.out.printf("%-15s : %6.2f%%\n", entry.getKey(), entry.getValue());
        }
        
        // Afficher les statistiques des mouvements avec leurs contributions au score
        System.out.println("\nContribution des mouvements au score :");
        System.out.println("-".repeat(50));
        System.out.println("Type de mouvement      Poids    Occurrences    Impact sur score");
        System.out.println("-".repeat(50));
        
        // Bigrammes
        System.out.println("\nBigrammes (" + totalBigramCount + " total) :");
        double bigramScore = 0.0;
        bigramScore += displayMovementStats(MovementType.SAME_FINGER, "Même doigt", totalBigramCount);
        bigramScore += displayMovementStats(MovementType.LATERAL_STRETCH, "Extension latérale", totalBigramCount);
        bigramScore += displayMovementStats(MovementType.SCISSORS, "Ciseaux", totalBigramCount);
        bigramScore += displayMovementStats(MovementType.HAND_ALTERNATION, "Alternance mains", totalBigramCount);
        bigramScore += displayMovementStats(MovementType.INWARD_ROLL, "Roulement intérieur", totalBigramCount);
        bigramScore += displayMovementStats(MovementType.OUTWARD_ROLL, "Roulement extérieur", totalBigramCount);
        System.out.printf("Sous-total bigrammes : %.2f\n", bigramScore);
        
        // Trigrammes
        System.out.println("\nTrigrammes (" + totalTrigramCount + " total) :");
        double trigramScore = 0.0;
        trigramScore += displayMovementStats(MovementType.BAD_REDIRECTION, "Mauvaise redirection", totalTrigramCount);
        trigramScore += displayMovementStats(MovementType.REDIRECTION, "Redirection", totalTrigramCount);
        trigramScore += displayMovementStats(MovementType.SAME_FINGER_SKIPGRAM, "Skipgram même doigt", totalTrigramCount);
        System.out.printf("Sous-total trigrammes : %.2f\n", trigramScore);
        
        System.out.println("\nScore global : " + String.format("%.2f", score));
        System.out.printf("             = %.2f (bigrammes) + %.2f (trigrammes)\n", bigramScore, trigramScore);
        System.out.println("Plus le score est bas, meilleure est la disposition.");
        System.out.println("=".repeat(50));
    }
    
    private double displayMovementStats(MovementType type, String label, long total) {
        long count = movementCounts.getOrDefault(type, 0L);
        double weight = weights.get(type);
        double impact = count * weight;
        double percentage = total > 0 ? (count * 100.0) / total : 0.0;
        
        System.out.printf("%-20s %6.2f %8d (%5.1f%%) %10.2f\n",
            label, weight, count, percentage, impact);
            
        return impact;
    }
}
