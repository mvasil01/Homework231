import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {

    private static final int[] SIZES = {1000, 10000, 50000, 100000, 200000};
    private static final int[] FIXED_LENGTHS = {7, 10, 31};

    public static void main(String[] args) {
        System.out.println("Starting Experiment (Part B)...");

        // --- 1. Run Fixed Length Scenarios ---
        for (int len : FIXED_LENGTHS) {
            String prefix = "dictionary_fixed_" + len + "_";
            String output = "results_fixed_" + len + ".txt";
            
            System.out.println("\n--- Processing Fixed Length (" + len + ") ---");
            runScenario(prefix, output);
        }

        // --- 2. Run Variable Length Scenario ---
        System.out.println("\n--- Processing Variable Length (Normal Dist) ---");
        runScenario("dictionary_normal_", "results_variable.txt");
        
        System.out.println("\nExperiments completed. Results saved to .txt files.");
    }

    private static void runScenario(String filePrefix, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Header
            writer.write("N Trie_Bytes CompressedTrie_Bytes Ratio\n");

            for (int n : SIZES) {
                // Construct filename: e.g., "dictionary_fixed_7_" + "1000" + ".txt"
                String inputFileName = filePrefix + n + ".txt";
                
                Trie trie = new Trie();
                CompressedTrie compTrie = new CompressedTrie();
                
                int count = 0;

                // Read File
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
                    continue; 
                }

                if (count != n) {
                    System.out.println("  [Warning] File " + inputFileName + " contained " + count + " words (expected " + n + ").");
                }

                // Measure
                long trieMem = trie.estimateMemory();
                long compMem = compTrie.estimateMemory();
                double ratio = (compMem == 0) ? 0 : (double) trieMem / compMem;

                // Log & Write
                System.out.printf("  N=%-7d | Trie: %-10d | Comp: %-10d | Ratio: %.2f\n", n, trieMem, compMem, ratio);
                
                writer.write(n + " " + trieMem + " " + compMem + " " + String.format("%.2f", ratio));
                writer.newLine();
                
                // Cleanup
                trie = null;
                compTrie = null;
                System.gc(); 
            }

        } catch (IOException e) {
            System.err.println("Error writing to output file " + outputFile + ": " + e.getMessage());
        }
    }
}