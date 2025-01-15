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

}
