package org.projet.evaluator;

import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

/**
 * Évaluateur de mouvements pour un agencement de clavier.
 * Cette classe analyse et évalue les différents types de mouvements
 * possibles lors de la frappe sur un clavier.
 * 
 * Évalue les différents types de mouvements sur un clavier.
 * 
 * <p>Cette classe fournit des méthodes pour détecter différents types de mouvements
 * problématiques ou favorables lors de la frappe sur un clavier. Elle est utilisée
 * par {@link LayoutEvaluator} pour évaluer l'ergonomie d'une disposition de clavier.
 * 
 * <p>Les mouvements détectés incluent :
 * <ul>
 *   <li>Utilisation du même doigt (à éviter)</li>
 *   <li>Extension latérale des doigts (à éviter)</li>
 *   <li>Mouvement en ciseaux (à éviter)</li>
 *   <li>Roulements intérieurs (favorables)</li>
 *   <li>Redirections et mauvaises redirections</li>
 * </ul>
 * 
 * <p>Exemple d'utilisation :
 * <pre>{@code
 * MovementEvaluator evaluator = new MovementEvaluator();
 * 
 * // Vérifier si deux touches utilisent le même doigt
 * boolean sameFinger = evaluator.isSameFinger(key1, key2);
 * 
 * // Vérifier si un mouvement est une extension latérale
 * boolean isStretch = evaluator.isLateralStretch(key1, key2);
 * }</pre>
 */
public class MovementEvaluator {
    
    /**
     * Vérifie si une touche est tapée avec le même doigt qu'une autre.
     * 
     */
    public boolean isSameFinger(Key key1, Key key2) {
        return key1.finger() == key2.finger();
    }
    
    /**
     * Vérifie si deux touches sont tapées avec la même main.
     * 
     */
    public boolean isSameHand(Key key1, Key key2) {
        return isLeftHand(key1.finger()) == isLeftHand(key2.finger());
    }
    
    /**
     * Vérifie si un doigt est de la main gauche.
     * 
     */
    public boolean isLeftHand(Finger finger) {
        return finger.name().startsWith("LEFT_");
    }
    
    /**
     * Vérifie si le mouvement est une extension latérale (stretch).
     * Une extension latérale est considérée comme problématique car elle
     * nécessite un effort important du doigt.
     * 
     */
    public boolean isLateralStretch(Key key1, Key key2) {
        if (!isSameHand(key1, key2)) return false;
        
        int columnDiff = Math.abs(key1.column() - key2.column());
        return columnDiff >= 3;
    }
    
    /**
     * Vérifie si le mouvement est un roulement intérieur.
     * Un roulement intérieur est un mouvement favorable où les doigts
     * se déplacent naturellement de l'extérieur vers l'intérieur de la main.
     * 
     */
    public boolean isInwardRoll(Key key1, Key key2) {
        if (isSameHand(key1, key2)) {
            boolean isLeft = isLeftHand(key1.finger());
            int col1 = key1.column();
            int col2 = key2.column();
            
            return isLeft ? col1 < col2 : col1 > col2;
        }
        return false;
    }
    
    /**
     * Vérifie si le mouvement est un mouvement de ciseaux.
     * Un mouvement en ciseaux se produit lorsque deux doigts adjacents
     * se croisent, ce qui est généralement inconfortable.
     * 
     */
    public boolean isScissors(Key key1, Key key2) {
        if (!isSameHand(key1, key2)) return false;
        
        // Les doigts doivent être adjacents
        if (Math.abs(getFingerIndex(key1.finger()) - getFingerIndex(key2.finger())) != 1) {
            return false;
        }
        
        // Le mouvement en ciseaux implique un croisement des doigts
        // sur des rangées différentes
        return Math.abs(key1.row() - key2.row()) >= 2;
    }
    
    /**
     * Vérifie si le mouvement est une redirection (changement de direction).
     * Une redirection se produit lorsque le mouvement change de direction
     * entre trois touches consécutives.
     * 
     */
    public boolean isRedirection(Key key1, Key key2, Key key3) {
        if (!isSameHand(key1, key2) || !isSameHand(key2, key3)) {
            return false;
        }
        
        int dir1 = Integer.compare(key2.column(), key1.column());
        int dir2 = Integer.compare(key3.column(), key2.column());
        
        return dir1 != 0 && dir2 != 0 && dir1 != dir2;
    }
    
    /**
     * Vérifie si le mouvement est une mauvaise redirection.
     * Une mauvaise redirection est une redirection qui implique
     * des mouvements latéraux importants.
     * 
     */
    public boolean isBadRedirection(Key key1, Key key2, Key key3) {
        if (!isRedirection(key1, key2, key3)) {
            return false;
        }
        
        // Une redirection est considérée comme mauvaise si elle implique
        // des mouvements latéraux importants
        return isLateralStretch(key1, key2) || 
               isLateralStretch(key2, key3);
    }
    
    /**
     * Vérifie si un mouvement est un roulement vers l'extérieur.
     * 
     */
    public boolean isOutwardRoll(Key key1, Key key2) {
        if (!isSameHand(key1, key2)) {
            return false;
        }
        
        // Pour la main gauche : index -> majeur -> annulaire -> auriculaire
        // Pour la main droite : index -> majeur -> annulaire -> auriculaire
        return switch (key1.finger()) {
            case LEFT_INDEX -> key2.finger() == Finger.LEFT_MIDDLE;
            case LEFT_MIDDLE -> key2.finger() == Finger.LEFT_RING;
            case LEFT_RING -> key2.finger() == Finger.LEFT_PINKY;
            case RIGHT_INDEX -> key2.finger() == Finger.RIGHT_MIDDLE;
            case RIGHT_MIDDLE -> key2.finger() == Finger.RIGHT_RING;
            case RIGHT_RING -> key2.finger() == Finger.RIGHT_PINKY;
            default -> false;
        };
    }
    
    /**
     * Retourne l'index d'un doigt (0 pour l'auriculaire, 3 pour l'index).
     * 
     */
    private int getFingerIndex(Finger finger) {
        return switch (finger) {
            case LEFT_PINKY, RIGHT_PINKY -> 0;
            case LEFT_RING, RIGHT_RING -> 1;
            case LEFT_MIDDLE, RIGHT_MIDDLE -> 2;
            case LEFT_INDEX, RIGHT_INDEX -> 3;
        };
    }
    
    /**
     * Évalue le type de mouvement pour un bigramme donné.
     * 
     */
    public MovementType evaluateBigramMovement(KeyboardLayout layout, String bigram) {
        if (bigram == null || bigram.length() != 2) {
            return null;
        }

        Key key1 = layout.getKey(bigram.charAt(0));
        Key key2 = layout.getKey(bigram.charAt(1));

        if (key1 == null || key2 == null) {
            return null;
        }

        // Vérifier les différents types de mouvements dans l'ordre de priorité
        if (isSameFinger(key1, key2)) {
            return MovementType.SAME_FINGER;
        }
        
        if (isLateralStretch(key1, key2)) {
            return MovementType.LATERAL_STRETCH;
        }
        
        if (isScissors(key1, key2)) {
            return MovementType.SCISSORS;
        }

        if (!isSameHand(key1, key2)) {
            return MovementType.HAND_ALTERNATION;
        }

        if (isInwardRoll(key1, key2)) {
            return MovementType.INWARD_ROLL;
        }

        if (isOutwardRoll(key1, key2)) {
            return MovementType.OUTWARD_ROLL;
        }

        // Si aucun mouvement spécifique n'est détecté
        return MovementType.REDIRECTION;
    }
}
