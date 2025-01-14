# Binome
- PRENOM NOM
  - DARINA MERAH
  - DIKESH UPRETY


# Optimiseur de Disposition de Clavier

Ce projet est un outil d'analyse et d'optimisation de dispositions de clavier. Il permet d'analyser des textes pour déterminer les fréquences d'utilisation des caractères et d'évaluer l'ergonomie des dispositions de clavier selon divers critères.

## Fonctionnalités

- Analyse de texte :
  - Fréquences des caractères individuels
  - Fréquences des bigrammes (paires de caractères)
  - Fréquences des trigrammes (triplets de caractères)
  
- Évaluation de disposition :
  - Détection des mouvements problématiques (même doigt, extension latérale, ciseaux)
  - Analyse de l'équilibre des mains
  - Calcul des charges par doigt
  - Détection des roulements (intérieurs et extérieurs)
  
- Optimisation de disposition :
  - Algorithme génétique pour trouver des dispositions optimales
  - Critères d'optimisation personnalisables
  - Conservation des meilleures dispositions trouvées



### Compilation

```bash
./gradlew build
```

## Guide d'Utilisation

### Analyse de Texte

```java
// Charger et analyser un texte
TextAnalyzer analyzer = new TextAnalyzer();
analyzer.analyzeFile("chemin/vers/fichier.txt");

// Obtenir les fréquences
Map<String, Long> charFreq = analyzer.getCharacterFrequencies();
Map<String, Long> bigramFreq = analyzer.getBigramFrequencies();
Map<String, Long> trigramFreq = analyzer.getTrigramFrequencies();
```

### Évaluation d'une Disposition

```java
// Créer une disposition
Map<Character, KeyboardLayout.Key> layout = new HashMap<>();
layout.put('a', new KeyboardLayout.Key(2, 0, KeyboardLayout.Finger.LEFT_PINKY, 'A', null));
layout.put('z', new KeyboardLayout.Key(2, 1, KeyboardLayout.Finger.LEFT_RING, 'Z', null));
// ... ajouter les autres touches

KeyboardLayout keyboardLayout = new KeyboardLayout("AZERTY", layout);

// Évaluer la disposition
LayoutEvaluator evaluator = new LayoutEvaluator(analyzer.getAllFrequencies());
double score = evaluator.evaluateLayout(keyboardLayout);

// Afficher les statistiques détaillées
evaluator.displayEvaluation(keyboardLayout);
```

### Optimisation de Disposition

```java
// Créer un optimiseur
KeyboardOptimizer optimizer = new KeyboardOptimizer(analyzer.getAllFrequencies());

// Configurer les paramètres
optimizer.setPopulationSize(100);
optimizer.setGenerations(1000);
optimizer.setMutationRate(0.1);

// Lancer l'optimisation
KeyboardLayout bestLayout = optimizer.optimize();
```

## Critères d'Évaluation

L'évaluateur de disposition (`LayoutEvaluator`) prend en compte plusieurs critères :

1. **Mouvements problématiques** :
   - Même doigt (pénalité : 2.0)
   - Extension latérale (pénalité : 1.5)
   - Ciseaux (pénalité : 1.8)
   - Mauvaise redirection (pénalité : 1.7)
   - Redirection simple (pénalité : 1.2)
   - Skipgram même doigt (pénalité : 1.6)

2. **Mouvements favorables** :
   - Alternance des mains (bonus : -0.8)
   - Roulement intérieur (bonus : -1.0)
   - Roulement extérieur (bonus : -0.5)

3. **Équilibre** :
   - Répartition entre les mains
   - Charge de travail par doigt

