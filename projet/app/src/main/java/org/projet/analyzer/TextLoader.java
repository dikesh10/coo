package org.projet.analyzer;

import org.projet.keyboard.model.KeyboardLayout;
import org.projet.config.KeyboardConfigLoader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cette classe permet de charger et d'analyser des fichiers texte.
 */
public class TextLoader {
    
    public static void analyzeFile(TextAnalyzer analyzer, Path filePath) throws IOException {
        String content = Files.readString(filePath);
        System.out.println("Analyse du fichier : " + filePath.getFileName());
        
        // Créer l'analyseur d'accents
        AccentAnalyzer accentAnalyzer = new AccentAnalyzer(analyzer, analyzer.getKeyboardLayout());
        
        // Analyser les caractères individuels et les séquences de touches
        accentAnalyzer.analyzeAccentedText(content);
    }
    
    public static List<String> loadFromDirectory(Path directory) throws IOException {
        List<String> texts = new ArrayList<>();
        
        // Charger la disposition du clavier
        try {
            KeyboardConfigLoader configLoader = new KeyboardConfigLoader();
            var layoutOpt = configLoader.loadLayout(
                Path.of(TextLoader.class.getClassLoader()
                    .getResource("layouts/azerty.json")
                    .toURI())
            );
            
            if (layoutOpt.isEmpty()) {
                throw new IllegalStateException("Impossible de charger la disposition du clavier");
            }
            
            TextAnalyzer analyzer = new TextAnalyzer(layoutOpt.get());
            AccentAnalyzer accentAnalyzer = new AccentAnalyzer(analyzer, analyzer.getKeyboardLayout());
            try (Stream<Path> paths = Files.walk(directory)) {
                texts = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .parallel()
                    .map(path -> {
                        try {
                            return Files.readString(path);
                        } catch (IOException e) {
                            System.err.println("Erreur lors de la lecture de " + path + ": " + e.getMessage());
                            return "";
                        }
                    })
                    .filter(content -> !content.isEmpty())
                    .collect(Collectors.toList());
            }
        } catch (URISyntaxException e) {
            throw new IOException("Erreur lors du chargement de la disposition du clavier", e);
        }
        
        return texts;
    }

    public static void analyzeDirectory(TextAnalyzer analyzer, Path directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        analyzeFile(analyzer, path);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'analyse du fichier " + path + ": " + e.getMessage());
                    }
                });
        }
    }
}
