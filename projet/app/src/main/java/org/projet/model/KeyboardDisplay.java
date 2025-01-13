package org.projet.model;

import java.util.Map;

/**
 * Classe utilitaire pour afficher une disposition de clavier dans le terminal.
 */
public class KeyboardDisplay {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    /**
     * Affiche une disposition de clavier dans le terminal avec une mise en forme colorée.
     * @param layout La disposition à afficher
     */
    public static void displayLayout(KeyboardLayout layout) {
        System.out.println("\nDisposition du clavier : " + layout.name());
        System.out.println("┌─────────────────────────────────┐");
        
        // Première rangée (nombres)
        System.out.print("│ ");
        displayRow(layout, "1234567890");
        System.out.println(" │");
        
        // Deuxième rangée
        System.out.print("│  ");
        displayRow(layout, "azertyuiop");
        System.out.println("  │");
        
        // Troisième rangée
        System.out.print("│   ");
        displayRow(layout, "qsdfghjklm");
        System.out.println("   │");
        
        // Quatrième rangée
        System.out.print("│    ");
        displayRow(layout, "wxcvbn");
        System.out.println("      │");
        
        System.out.println("└─────────────────────────────────┘");
    }

    private static void displayRow(KeyboardLayout layout, String keys) {
        for (char key : keys.toCharArray()) {
            String color = getColorForFinger(key);
            System.out.print(color + key + " " + ANSI_RESET);
        }
    }

    private static String getColorForFinger(char key) {
        // Couleurs selon le doigt utilisé
        switch (Character.toLowerCase(key)) {
            // Main gauche
            case 'a': case 'q': case '1':  // Auriculaire gauche
                return ANSI_RED;
            case 'z': case 's': case '2':  // Annulaire gauche
                return ANSI_GREEN;
            case 'e': case 'd': case '3':  // Majeur gauche
                return ANSI_BLUE;
            case 'r': case 'f': case '4':  // Index gauche
                return ANSI_RED;
            case 't': case 'g': case '5':  // Index gauche
                return ANSI_GREEN;
            // Main droite
            case 'y': case 'h': case '6':  // Index droit
                return ANSI_BLUE;
            case 'u': case 'j': case '7':  // Index droit
                return ANSI_RED;
            case 'i': case 'k': case '8':  // Majeur droit
                return ANSI_GREEN;
            case 'o': case 'l': case '9':  // Annulaire droit
                return ANSI_BLUE;
            case 'p': case 'm': case '0':  // Auriculaire droit
                return ANSI_RED;
            case 'w': case 'x': case 'c': case 'v': case 'b': case 'n':  // Touches du bas
                return ANSI_GREEN;
            default:
                return ANSI_RESET;
        }
    }
}
