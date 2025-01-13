package org.projet.analyzer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe thread-safe pour stocker les résultats d'analyse de texte
 */
public class AnalysisResult {
    private final ConcurrentHashMap<String, AtomicLong> ngramFrequencies;
    private final AtomicLong totalCharacters;

    public AnalysisResult() {
        this.ngramFrequencies = new ConcurrentHashMap<>();
        this.totalCharacters = new AtomicLong(0);
    }

    public void incrementNGramCount(String ngram) {
        ngramFrequencies.computeIfAbsent(ngram, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void addToTotalCharacters(long count) {
        totalCharacters.addAndGet(count);
    }

    /**
     * Réinitialise le compteur total de caractères.
     */
    public void resetTotalCharacters() {
        totalCharacters.set(0);
    }

    /**
     * Définit le nombre total de caractères.
     */
    public void setTotalCharacters(long count) {
        totalCharacters.set(count);
    }

    public long getNGramCount(String ngram) {
        AtomicLong count = ngramFrequencies.get(ngram);
        return count != null ? count.get() : 0;
    }

    public long getTotalCharacters() {
        return totalCharacters.get();
    }

    public Map<String, Long> getNGramFrequencies() {
        return ngramFrequencies.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().get()
            ));
    }

    public void merge(AnalysisResult other) {
        other.ngramFrequencies.forEach((ngram, count) -> {
            ngramFrequencies.computeIfAbsent(ngram, k -> new AtomicLong(0))
                .addAndGet(count.get());
        });
        totalCharacters.addAndGet(other.getTotalCharacters());
    }
}
