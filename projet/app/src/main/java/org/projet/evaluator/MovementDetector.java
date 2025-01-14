package org.projet.evaluator;

import org.projet.keyboard.model.*;
import java.util.*;

/**
 * Détecte les différents types de mouvements dans une séquence de touches.
 */
public class MovementDetector {
    private final KeyboardLayout layout;

    public MovementDetector(KeyboardLayout layout) {
        this.layout = layout;
    }

    /**
     * Détecte les mouvements pour un caractère unique.
     */
    public Set<Movement> detectSingleKeyMovements(char c) {
        Set<Movement> movements = new HashSet<>();
        KeyPosition pos = layout.getPosition(c);
        
        if (pos != null) {
            movements.add(Movement.SINGLE_KEY);
        }
        
        return movements;
    }

    /**
     * Détecte les mouvements pour un bigramme.
     */
    public Set<Movement> detectBigramMovements(String bigram) {
        Set<Movement> movements = new HashSet<>();
        if (bigram.length() != 2) return movements;

        KeyPosition pos1 = layout.getPosition(bigram.charAt(0));
        KeyPosition pos2 = layout.getPosition(bigram.charAt(1));
        
        if (pos1 == null || pos2 == null) return movements;

        // Même doigt
        if (pos1.finger() == pos2.finger()) {
            movements.add(Movement.SAME_FINGER_BIGRAM);
            return movements; // Pas besoin de vérifier d'autres mouvements
        }

        // Alternance des mains
        if (pos1.hand() != pos2.hand()) {
            movements.add(Movement.HAND_ALTERNATION);
            return movements;
        }

        // Extension latérale
        if (Math.abs(pos1.column() - pos2.column()) > 1) {
            movements.add(Movement.LATERAL_STRETCH);
        }

        // Ciseaux
        if (Math.abs(pos1.row() - pos2.row()) > 1) {
            movements.add(Movement.SCISSORS);
        }

        // Roulements (même main, doigts différents)
        if (pos1.hand() == pos2.hand()) {
            boolean isInward = pos1.finger().ordinal() > pos2.finger().ordinal();
            if (pos1.hand() == Hand.RIGHT) {
                isInward = !isInward;
            }
            movements.add(isInward ? Movement.INWARD_ROLL : Movement.OUTWARD_ROLL);
        }

        return movements;
    }

    /**
     * Détecte les mouvements pour un trigramme.
     */
    public Set<Movement> detectTrigramMovements(String trigram) {
        Set<Movement> movements = new HashSet<>();
        if (trigram.length() != 3) return movements;

        KeyPosition pos1 = layout.getPosition(trigram.charAt(0));
        KeyPosition pos2 = layout.getPosition(trigram.charAt(1));
        KeyPosition pos3 = layout.getPosition(trigram.charAt(2));
        
        if (pos1 == null || pos2 == null || pos3 == null) return movements;

        // Skipgram même doigt
        if (pos1.finger() == pos3.finger() && pos1.finger() != pos2.finger()) {
            movements.add(Movement.SAME_FINGER_SKIPGRAM);
        }

        // Redirections (même main pour pos1 et pos3)
        if (pos1.hand() == pos3.hand() && pos1.finger() != pos3.finger()) {
            boolean isInward1to2 = isInwardMovement(pos1, pos2);
            boolean isInward2to3 = isInwardMovement(pos2, pos3);
            
            // Si les directions sont différentes, c'est une redirection
            if (isInward1to2 != isInward2to3) {
                // Mauvaise redirection si aucune des positions n'utilise l'index
                if (!pos1.finger().isIndex() && !pos2.finger().isIndex() && !pos3.finger().isIndex()) {
                    movements.add(Movement.BAD_REDIRECT);
                } else {
                    movements.add(Movement.REDIRECT);
                }
            }
        }

        return movements;
    }

    /**
     * Détermine si le mouvement entre deux positions est vers l'intérieur.
     */
    private boolean isInwardMovement(KeyPosition pos1, KeyPosition pos2) {
        if (pos1.hand() != pos2.hand()) return false;
        
        boolean isInward = pos1.finger().ordinal() > pos2.finger().ordinal();
        return pos1.hand() == Hand.RIGHT ? !isInward : isInward;
    }
}
