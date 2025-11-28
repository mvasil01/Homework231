import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {

    // Τα μεγέθη πρέπει να ταιριάζουν με αυτά της Generator
    private static final int[] SIZES = {1000, 10000, 50000, 100000, 200000};

    // Αρχεία Εξόδου (Αποτελέσματα)
    private static final String RESULTS_FIXED = "results_fixed_length.txt";
    private static final String RESULTS_NORM = "results_variable_length.txt";

    public static void main(String[] args) {
        System.out.println("Starting Experiment (Part B)...");

        // --- SCENARIO 1: FIXED LENGTH ---
        // Θα ψάξει αρχεία με όνομα: dictionary_fixed_1000.txt, dictionary_fixed_10000.txt ...
        runScenario("fixed", RESULTS_FIXED);

        // --- SCENARIO 2: VARIABLE LENGTH ---
        // Θα ψάξει αρχεία με όνομα: dictionary_normal_1000.txt, dictionary_normal_10000.txt ...
        runScenario("normal", RESULTS_NORM);
        
        System.out.println("\nExperiments completed. Results saved to .txt files.");
    }

    private static void runScenario(String type, String outputFile) {
        System.out.println("\n--- Running Scenario: " + type + " ---");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Header
            writer.write("N Trie_Bytes CompressedTrie_Bytes Ratio\n");

            for (int n : SIZES) {
                // Κατασκευή ονόματος αρχείου εισόδου βάσει του τύπου και του Ν
                String inputFileName = "dictionary_" + type + "_" + n + ".txt";
                
                Trie trie = new Trie();
                CompressedTrie compTrie = new CompressedTrie();
                
                int count = 0;

                // Διάβασμα του αρχείου
                try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String word = line.trim();
                        if (!word.isEmpty()) {
                            trie.insert(word);
                            compTrie.insert(word);
                            count++;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("  [Error] Could not read file " + inputFileName + ": " + e.getMessage());
                    continue; // Skip this N if file missing
                }

                // Επαλήθευση ότι διαβάσαμε όσες λέξεις περιμέναμε
                if (count != n) {
                    System.out.println("  [Warning] File " + inputFileName + " contained " + count + " words (expected " + n + ").");
                }

                // Μέτρηση Μνήμης
                long trieMem = trie.estimateMemory();
                long compMem = compTrie.estimateMemory();
                
                double ratio = (compMem == 0) ? 0 : (double) trieMem / compMem;

                // Εκτύπωση και Εγγραφή
                System.out.printf("  Processed N=%-7d | Trie: %-10d | Comp: %-10d | Ratio: %.2f\n", n, trieMem, compMem, ratio);
                
                writer.write(n + " " + trieMem + " " + compMem + " " + String.format("%.2f", ratio));
                writer.newLine();
                
                // Καθαρισμός για το επόμενο N
                trie = null;
                compTrie = null;
                System.gc(); // Προαιρετική υπόδειξη στον Garbage Collector
            }

        } catch (IOException e) {
            System.err.println("Error writing to output file " + outputFile + ": " + e.getMessage());
        }
    }
}