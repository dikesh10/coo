package org.projet;

import org.projet.analyzer.TextAnalyzer;
import org.projet.analyzer.TextLoader;
import org.projet.evaluator.LayoutEvaluator;
import org.projet.config.KeyboardConfigLoader;
import org.projet.optimizer.KeyboardOptimizer;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Main application class for the keyboard layout analyzer and optimizer.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Analyseur et Optimiseur de Disposition Clavier ===\n");
        
        try {
            // Créer l'analyseur
            TextAnalyzer analyzer = new TextAnalyzer();
            
            // Obtenir le chemin des ressources
            var textsResource = App.class.getClassLoader().getResource("texts");
            if (textsResource == null) {
                throw new IllegalStateException("Le dossier 'texts' n'a pas été trouvé dans les ressources");
            }
            Path textsDir = Path.of(textsResource.toURI());
            
            System.out.println("Analyse des fichiers dans : " + textsDir);
            
            // Charger et analyser tous les fichiers en parallèle
            List<String> texts = TextLoader.loadFromDirectory(textsDir);
            analyzer.analyzeTexts(texts);
            
            // Afficher les résultats
            System.out.println("\n=== Analyse de texte ===");
            System.out.println("Nombre total de caractères : " + analyzer.getTotalCharacters());
            
            // Sauvegarder les résultats dans des fichiers
            saveFrequenciesToFile(analyzer, "unigrams.txt", 1);
            saveFrequenciesToFile(analyzer, "bigrams.txt", 2);
            saveFrequenciesToFile(analyzer, "trigrams.txt", 3);
            
            System.out.println("\nLes résultats ont été sauvegardés dans :");
            System.out.println("- unigrams.txt (touches individuelles)");
            System.out.println("- bigrams.txt (bigrammes)");
            System.out.println("- trigrams.txt (trigrammes)");

            System.out.println("\n===================");
            
            // Déterminer la disposition à utiliser
            String layoutChoice = "azerty";  // Par défaut
            if (args.length > 0) {
                String arg = args[0].toLowerCase();
                if (arg.equals("qwerty") || arg.equals("azerty")) {
                    layoutChoice = arg;
                } else {
                    System.out.println("Disposition non reconnue. Utilisation d'AZERTY par défaut.");
                }
            } else {
                System.out.println("Aucune disposition spécifiée. Utilisation d'AZERTY par défaut.");
                System.out.println("Usage: ./gradlew run --args=\"qwerty\" ou ./gradlew run --args=\"azerty\"");
            }
            
            // Charger la disposition choisie
            KeyboardConfigLoader configLoader = new KeyboardConfigLoader();
            var layoutOpt = configLoader.loadLayout(
                Path.of(App.class.getClassLoader()
                    .getResource("layouts/" + layoutChoice + ".json")
                    .toURI())
            );
            
            if (layoutOpt.isPresent()) {
                var layout = layoutOpt.get();
                
                // Créer un Map des fréquences pour l'évaluateur
                Map<String, Long> frequenciesForEvaluator = analyzer.getAllFrequencies();
                
                // Évaluer la disposition initiale
                LayoutEvaluator evaluator = new LayoutEvaluator(frequenciesForEvaluator);
                evaluator.displayEvaluation(layout);
                
                // Optimiser la disposition
                System.out.println("\nOptimisation de la disposition...");
                KeyboardOptimizer optimizer = new KeyboardOptimizer(evaluator);
                var optimizedLayout = optimizer.optimize(layout);
                
                System.out.println("\nDisposition optimisée :\n");
                evaluator.displayEvaluation(optimizedLayout);
            } else {
                System.err.println("Impossible de charger la disposition " + layoutChoice.toUpperCase());
            }
            
            // Fermer proprement l'analyseur
            analyzer.shutdown();
            
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde les fréquences des n-grammes dans un fichier.
     * 
     * @param analyzer L'analyseur contenant les résultats
     * @param filename Le nom du fichier de sortie
     * @param ngramLength La longueur des n-grammes à sauvegarder
     */
    private static void saveFrequenciesToFile(TextAnalyzer analyzer, String filename, int ngramLength) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            Map<String, Long> frequencies = analyzer.getAllFrequencies();
            long totalNGrams = frequencies.entrySet().stream()
                .filter(entry -> entry.getKey().length() == ngramLength)
                .mapToLong(Map.Entry::getValue)
                .sum();
            
            // Trier les n-grammes par fréquence décroissante
            frequencies.entrySet().stream()
                .filter(entry -> entry.getKey().length() == ngramLength)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalNGrams;
                    writer.printf("'%s' : %d (%.2f%%)\n",
                        entry.getKey(), entry.getValue(), percentage);
                });
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde dans " + filename + ": " + e.getMessage());
        }
    }
}
