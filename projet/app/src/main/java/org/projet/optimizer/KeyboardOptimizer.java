package org.projet.optimizer;

import org.projet.model.KeyboardLayout;
import org.projet.model.KeyboardDisplay;
import org.projet.evaluator.LayoutEvaluator;

import java.util.*;

/**
 * Optimise une disposition de clavier en utilisant un algorithme génétique.
 */
public class KeyboardOptimizer {
    private final LayoutEvaluator evaluator;
    private final Random random;
    private final int populationSize;
    private final int maxGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    
    /**
     * Crée un nouvel optimiseur de disposition.
     */
    public KeyboardOptimizer(LayoutEvaluator evaluator) {
        // Réduire la taille de population et le nombre de générations
        // Augmenter les taux de mutation et croisement pour une convergence plus rapide
        this(evaluator, 50, 100, 0.2, 0.9);
    }
    
    /**
     * Crée un nouvel optimiseur avec des paramètres personnalisés.
     */
    public KeyboardOptimizer(
        LayoutEvaluator evaluator,
        int populationSize,
        int maxGenerations,
        double mutationRate,
        double crossoverRate
    ) {
        this.evaluator = evaluator;
        this.random = new Random();
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
    }
    
    /**
     * Optimise une disposition de départ.
     * @return la meilleure disposition trouvée
     */
    public KeyboardLayout optimize(KeyboardLayout initial) {
        // Initialiser la population avec des variations de la disposition initiale
        List<KeyboardLayout> population = initializePopulation(initial);
        KeyboardLayout bestLayout = initial;
        double bestScore = evaluator.evaluateLayout(initial);
        
        System.out.printf("Score initial : %.2f%n%n", bestScore);
        
        int generationsWithoutImprovement = 0;
        double previousBestScore = bestScore;
        
        for (int generation = 0; generation < maxGenerations; generation++) {
            // Évaluer la population
            Map<KeyboardLayout, Double> scores = new HashMap<>();
            for (KeyboardLayout layout : population) {
                scores.put(layout, evaluator.evaluateLayout(layout));
            }
            
            // Trouver le meilleur de cette génération
            KeyboardLayout generationBest = Collections.min(
                scores.entrySet(),
                Map.Entry.comparingByValue()
            ).getKey();
            
            double generationBestScore = scores.get(generationBest);
            if (generationBestScore < bestScore) {
                bestScore = generationBestScore;
                bestLayout = generationBest;
                generationsWithoutImprovement = 0;
            } else {
                generationsWithoutImprovement++;
            }
            
            // Arrêter si pas d'amélioration depuis 20 générations
            if (generationsWithoutImprovement > 20) {
                System.out.println("Convergence atteinte après " + generation + " générations");
                break;
            }
            
            // Créer la nouvelle génération
            List<KeyboardLayout> newPopulation = new ArrayList<>();
            
            while (newPopulation.size() < populationSize) {
                // Sélection des parents
                KeyboardLayout parent1 = selectParent(population, scores);
                KeyboardLayout parent2 = selectParent(population, scores);
                
                // Croisement
                if (random.nextDouble() < crossoverRate) {
                    var children = crossover(parent1, parent2);
                    newPopulation.add(children.get(0));
                    if (newPopulation.size() < populationSize) {
                        newPopulation.add(children.get(1));
                    }
                } else {
                    newPopulation.add(parent1);
                    if (newPopulation.size() < populationSize) {
                        newPopulation.add(parent2);
                    }
                }
            }
            
            // Mutation
            for (int i = 0; i < newPopulation.size(); i++) {
                if (random.nextDouble() < mutationRate) {
                    newPopulation.set(i, mutate(newPopulation.get(i)));
                }
            }
            population = newPopulation;
        }
        
        System.out.println("=== Changements de touches ===");
        System.out.println("Liste des échanges effectués :");
        
        // Créer une Map des positions initiales
        Map<KeyboardLayout.Key, Character> initialPositions = new HashMap<>();
        for (Map.Entry<Character, KeyboardLayout.Key> entry : initial.characterToKeyMap().entrySet()) {
            initialPositions.put(entry.getValue(), entry.getKey());
        }
        
        // Pour chaque position, voir quelle touche a changé
        for (Map.Entry<Character, KeyboardLayout.Key> entry : bestLayout.characterToKeyMap().entrySet()) {
            char newChar = entry.getKey();
            KeyboardLayout.Key position = entry.getValue();
            char originalChar = initialPositions.get(position);
            
            if (newChar != originalChar) {
                System.out.printf("%c -> %c%n", originalChar, newChar);
            }
        }
        
        System.out.printf("%nScore final : %.2f%n", bestScore);
        
        return bestLayout;
    }
    
    private List<KeyboardLayout> initializePopulation(KeyboardLayout initial) {
        List<KeyboardLayout> population = new ArrayList<>();
        population.add(initial); // Garder la disposition initiale
        
        // Créer des variations par mutation
        for (int i = 1; i < populationSize; i++) {
            population.add(mutate(initial));
        }
        
        return population;
    }
    
    private KeyboardLayout selectParent(
        List<KeyboardLayout> population,
        Map<KeyboardLayout, Double> scores
    ) {
        // Sélection par tournoi
        int tournamentSize = 3;
        KeyboardLayout best = null;
        double bestScore = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < tournamentSize; i++) {
            KeyboardLayout candidate = population.get(
                random.nextInt(population.size())
            );
            double score = scores.get(candidate);
            
            if (score < bestScore) {
                best = candidate;
                bestScore = score;
            }
        }
        
        return best;
    }
    
    private List<KeyboardLayout> crossover(
        KeyboardLayout parent1,
        KeyboardLayout parent2
    ) {
        // Point de croisement aléatoire
        List<Character> chars = new ArrayList<>(parent1.characterToKeyMap().keySet());
        int crossPoint = random.nextInt(chars.size());
        
        // Créer deux nouveaux mappings
        Map<Character, KeyboardLayout.Key> child1Keys = new HashMap<>();
        Map<Character, KeyboardLayout.Key> child2Keys = new HashMap<>();
        
        // Première partie : copier directement
        for (int i = 0; i < crossPoint; i++) {
            char c = chars.get(i);
            child1Keys.put(c, parent1.characterToKeyMap().get(c));
            child2Keys.put(c, parent2.characterToKeyMap().get(c));
        }
        
        // Deuxième partie : échanger
        for (int i = crossPoint; i < chars.size(); i++) {
            char c = chars.get(i);
            child1Keys.put(c, parent2.characterToKeyMap().get(c));
            child2Keys.put(c, parent1.characterToKeyMap().get(c));
        }
        
        return List.of(
            new KeyboardLayout("Optimized-1", child1Keys),
            new KeyboardLayout("Optimized-2", child2Keys)
        );
    }
    
    private KeyboardLayout mutate(KeyboardLayout layout) {
        Map<Character, KeyboardLayout.Key> newKeys = new HashMap<>(
            layout.characterToKeyMap()
        );
        
        // Sélectionner deux caractères aléatoires et échanger leurs positions
        List<Character> chars = new ArrayList<>(newKeys.keySet());
        int idx1 = random.nextInt(chars.size());
        int idx2 = random.nextInt(chars.size());
        
        char c1 = chars.get(idx1);
        char c2 = chars.get(idx2);
        
        KeyboardLayout.Key key1 = newKeys.get(c1);
        KeyboardLayout.Key key2 = newKeys.get(c2);
        
        newKeys.put(c1, key2);
        newKeys.put(c2, key1);
        
        return new KeyboardLayout(
            layout.name() + "-mutated",
            newKeys
        );
    }
}
