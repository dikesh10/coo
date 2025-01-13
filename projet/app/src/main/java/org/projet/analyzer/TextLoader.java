package org.projet.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TextLoader {
    
   
    public static void analyzeFile(TextAnalyzer analyzer, Path filePath) throws IOException {
        String content = Files.readString(filePath);
        System.out.println("Analyse du fichier : " + filePath.getFileName());
        
        // Créer l'analyseur d'accents
        AccentAnalyzer accentAnalyzer = new AccentAnalyzer(analyzer);
        
        // Analyser les caractères individuels et les séquences de touches
        accentAnalyzer.analyzeAccentedText(content);
    }
    
   
    public static List<String> loadFromDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
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
