package org.projet.analyzer;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Classe responsable de l'analyse statistique des textes.
 * Cette classe permet d'analyser un corpus de texte pour en extraire :
 * - Les fréquences de caractères individuels
 * - Les fréquences de bigrammes (séquences de 2 caractères)
 * - Les fréquences de trigrammes (séquences de 3 caractères)
 * 
 * <p>
 * Exemple d'utilisation :
 * 
 * <pre>{@code
 * TextAnalyzer analyzer = new TextAnalyzer();
 * analyzer.analyzeText("texte");
 * 
 * // Obtenir les fréquences
 * long charFreq = analyzer.getFrequency("a");
 * long bigramFreq = analyzer.getFrequency("ab");
 * long trigramFreq = analyzer.getFrequency("abc");
 * }</pre>
 * 
 * @see TextLoader
 */
public class TextAnalyzer {
    // attention on aurait pu utiliser le pattern du singleton
    private final AnalysisResult result;
    private final ExecutorService executor;

    /**
     * Constructeur initialisant les structures de données pour l'analyse.
     */
    public TextAnalyzer() {
        this.result = new AnalysisResult();
        this.executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
    }

    /**
     * Analyse un texte donné et met à jour les statistiques de manière thread-safe.
     */
    public void analyzeText(String text) {
        synchronized (result) {
            // Update total characters
            result.addToTotalCharacters(text.length());

            // Analyse des caractères individuels
            for (int i = 0; i < text.length(); i++) {
                result.incrementNGramCount(String.valueOf(text.charAt(i)));
            }

            // Analyse des bigrammes
            for (int i = 0; i < text.length() - 1; i++) {
                String bigram = text.substring(i, i + 2);
                result.incrementNGramCount(bigram);
            }

            // Analyse des trigrammes
            for (int i = 0; i < text.length() - 2; i++) {
                String trigram = text.substring(i, i + 3);
                result.incrementNGramCount(trigram);
            }
        }
    }

    /**
     * Analyse une liste de textes en parallèle.
     * 
     * @param texts Liste des textes à analyser
     */
    public void analyzeTexts(List<String> texts) {
        try {
            // on soummet une tache + ajout immediat a la liste de future
            // une référence à un résultat qui peut être disponible plus tard, mais qui
            // n'est pas nécessairement disponible immédiatement.
            List<Future<?>> futures = texts.stream()
                    .map(text -> executor.submit(() -> analyzeText(text)))
                    .collect(Collectors.toList()); // rassembler les resultats dans une nouvelle liste

            // Attendre que toutes les tâches soient terminées
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'analyse parallèle", e);
        }
    }

    /**
     * Obtient la fréquence d'un n-gramme.
     * 
     * @param ngram Le n-gramme dont on veut la fréquence
     * @return La fréquence du n-gramme
     */
    public long getFrequency(String ngram) {
        return result.getNGramCount(ngram);
    }

    /**
     * Obtient le pourcentage d'apparition d'un n-gramme.
     * 
     * @param ngram Le n-gramme dont on veut le pourcentage
     * @return Le pourcentage d'apparition du n-gramme
     */
    public double getPercentage(String ngram) {
        long total = result.getTotalCharacters();
        if (total == 0)
            return 0.0;
        return (getFrequency(ngram) * 100.0) / total;
    }

    /**
     * Obtient le nombre total de caractères analysés.
     * 
     * @return Le nombre total de caractères
     */
    public long getTotalCharacters() {
        return result.getTotalCharacters();
    }

    /**
     * Obtient toutes les fréquences des n-grammes.
     * 
     * @return Une Map contenant les n-grammes et leurs fréquences
     */
    public Map<String, Long> getAllFrequencies() {
        return result.getNGramFrequencies();
    }

    /**
     * Réinitialise le compteur total de caractères.
     */
    public void resetTotalCharacters() {
        result.resetTotalCharacters();
    }

    /**
     * Définit le nombre total de caractères.
     * 
     * @param count Le nouveau nombre total de caractères
     */
    public void setTotalCharacters(long count) {
        result.setTotalCharacters(count);
    }

    /**
     * Ferme l'ExecutorService et libère les ressources.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
