import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {

    // Target sizes (N) to measure at. 
    private static final int[] CHECKPOINTS = {1000, 10000, 50000, 100000, 200000};

    // Filenames defined as constants
    private static final String FIXED_DICT = "dictionary_fixed_length.txt";
    private static final String NORM_DICT = "dictionary_normal_distributed.txt";
    
    // Output Files
    private static final String RESULTS_FIXED = "results_fixed_length.txt";
    private static final String RESULTS_NORM = "results_variable_length.txt";

    public static void main(String[] args) {
        System.out.println("Starting Experiment (Part B)...");

        // --- SCENARIO 1: FIXED LENGTH ---
        System.out.println("\n--- Processing Fixed Length Dictionary ---");
        runExperiment(FIXED_DICT, RESULTS_FIXED);

        // --- SCENARIO 2: VARIABLE LENGTH ---
        System.out.println("\n--- Processing Variable Length Dictionary ---");
        runExperiment(NORM_DICT, RESULTS_NORM);
        
        System.out.println("\nExperiments completed. Results saved to .txt files.");
    }

    /**
     * Reads a dictionary file line-by-line, inserts into Trie/CompressedTrie,
     * and records memory usage at specific milestones (CHECKPOINTS).
     */
    private static void runExperiment(String inputFileName, String outputFileName) {
        // 1. Create the Data Structures
        Trie trie = new Trie();
        CompressedTrie compTrie = new CompressedTrie();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

            // Write Header (Space separated)
            writer.write("N Trie_Bytes CompressedTrie_Bytes Ratio\n");

            String line;
            int count = 0;
            int checkpointIndex = 0;
            int nextTarget = CHECKPOINTS[0];

            // 2. Read file line by line (Single Pass)
            while ((line = br.readLine()) != null) {
                String word = line.trim();
                
                if (word.isEmpty()) continue;

                // Insert into both structures
                trie.insert(word);
                compTrie.insert(word);
                count++;

                // 3. Check if we reached a milestone
                if (count == nextTarget) {
                    // Measure Memory
                    long trieMem = trie.estimateMemory();
                    long compMem = compTrie.estimateMemory();
                    
                    // Calculate Ratio
                    double ratio = (compMem == 0) ? 0 : (double) trieMem / compMem;

                    // Log to Console
                    System.out.printf("  Reached N=%-7d | Trie: %-10d | Comp: %-10d | Ratio: %.2f\n", 
                                      count, trieMem, compMem, ratio);

                    // Write to File (Space separated)
                    writer.write(count + " " + trieMem + " " + compMem + " " + String.format("%.2f", ratio));
                    writer.newLine();

                    // Update to next target
                    checkpointIndex++;
                    if (checkpointIndex < CHECKPOINTS.length) {
                        nextTarget = CHECKPOINTS[checkpointIndex];
                    } else {
                        break;
                    }
                }
            }

            if (count < CHECKPOINTS[0]) {
                System.out.println("  [Warning] File " + inputFileName + " had fewer words (" + count + ") than the smallest checkpoint.");
            }

        } catch (IOException e) {
            System.err.println("Error processing " + inputFileName + ": " + e.getMessage());
        }
    }
}