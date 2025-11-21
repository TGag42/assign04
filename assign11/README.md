# Random Phrase Generator

## Compilation

To compile the project, open your terminal, navigate to the project root directory (`pulls/`), and run the following command:

```bash
javac -d target/classes assign11/RandomPhraseGenerator.java
```

If you want to compile the experiments as well, use:

```bash
javac -d target/classes assign11/*.java
```

## Running the Generator

The `RandomPhraseGenerator` program requires two arguments:

1. The path to the grammar file (from command root e.g., `poetic_sentence.g`).
2. The number of phrases to generate (an integer).

**Usage:**

```bash
java -cp target/classes assign11.RandomPhraseGenerator <grammar_file> <number_of_phrases>
```

## Running Experiments

To run the performance comparison experiment:

1. Compile all files:

```bash
javac -d target/classes assign11/*.java
```

2. Run the experiment class:

```bash
java -cp target/classes assign11.FinalComparisonExperiment
```
