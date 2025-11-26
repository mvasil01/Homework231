/* This class is out dated 
*
* It needs updates
*
* Write it based on the Trie.java and CompressedTrie.java classes.
* Dont use any java libs or too complicated OOP java utilities.
*
*
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {

    private static final String FIXED_DICT = "dictionary_fixed_length.txt";
    private static final String NORM_DICT = "dictionary_normal_distributed.txt";
    
    private static final String RESULTS_FIXED = "results_fixed_length.txt";
    private static final String RESULTS_NORM = "results_normal_distributed.txt";

    private static final int[] SIZES = {1000, 10000, 50000, 100000, 200000};

    public static void main(String[] args) {
        // --- SCENARIO 1: FIXED LENGTH ---
        System.out.println("Running Scenario 1 (Fixed Length)...");
        runScenario(FIXED_DICT, RESULTS_FIXED);

        // --- SCENARIO 2: VARIABLE LENGTH ---
        System.out.println("Running Scenario 2 (Variable Length)...");
        runScenario(NORM_DICT, RESULTS_NORM);
        
        System.out.println("Experiments completed. Check the .txt files.");
    }

    private static void runScenario(String inputDictFile, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write Header
            writer.write("N fmem ndmem\n");

            for (int n : SIZES) {
                long[] results = runTest(n, inputDictFile);
                if (results != null) {
                    // Convert bytes to MB
                    double trieMB = results[0] / (1024.0 * 1024.0);
                    double compTrieMB = results[1] / (1024.0 * 1024.0);
                    
                    // Write row: N, ClassicMem(MB), CompressedMem(MB)
                    writer.write(String.format("%d %.4f %.4f\n", n, trieMB, compTrieMB));
                    System.out.printf("  Processed N=%d\n", n);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + outputFile + ": " + e.getMessage());
        }
    }

    private static long[] runTest(int n, String dictionaryFile) {
        // Direct instantiation - No Reflection
        Trie trie = new Trie();
        CompressedTrie compTrie = new CompressedTrie();

        try (BufferedReader br = new BufferedReader(new FileReader(dictionaryFile))) {
            String line;
            int count = 0;
            while (count < n && (line = br.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    trie.insert(word);
                    compTrie.insert(word);
                    count++;
                }
            }
            
            if (count < n) {
                System.out.println("  Warning: Requested N=" + n + " but file only had " + count + " words.");
            }
        } catch (IOException e) {
            System.out.println("  Error reading file: " + e.getMessage());
            return null;
        }

        long trieBytes = MemoryController.estimate(trie);
        long compressedBytes = MemoryController.estimate(compTrie);

        return new long[]{trieBytes, compressedBytes};
    }
}