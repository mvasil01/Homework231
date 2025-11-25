/* oi ai gemerated kati allo */
package exp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Experiment {

    private static final String FIXED_DICT = "dictionary_fixed_length.txt";
    private static final String NORM_DICT = "dictionary_normal_distributed.txt";
    private static final int[] SIZES = {1000, 10000, 50000, 100000, 200000};

    private static final String TRIE_CLASS = "Trie";
    private static final String COMPRESSED_TRIE_CLASS = "CompressedTrie";

    public static void main(String[] args) {

        System.out.println("Loading dictionaries...");
        List<String> fixedWords = loadWords(FIXED_DICT);
        List<String> varWords = loadWords(NORM_DICT);

        if (fixedWords.isEmpty() || varWords.isEmpty()) {
            System.out.println("Error: Dictionaries are empty or missing. Run Generator first.");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("SCENARIO 1: FIXED LENGTH WORDS");
        System.out.println("==================================================");
        System.out.printf("%-10s | %-15s | %-15s\n", "N", "Trie Mem", "CompTrie Mem");
        System.out.println("--------------------------------------------------");
        
        for (int n : SIZES) {
            runTest(n, fixedWords);
        }

        System.out.println("\n==================================================");
        System.out.println("SCENARIO 2: VARIABLE LENGTH WORDS (Normal Dist)");
        System.out.println("==================================================");
        System.out.printf("%-10s | %-15s | %-15s\n", "N", "Trie Mem", "CompTrie Mem");
        System.out.println("--------------------------------------------------");

        for (int n : SIZES) {
            runTest(n, varWords);
        }
    }

    private static void runTest(int n, List<String> allWords) {
        if (n > allWords.size()) {
            System.out.println("Skipping N=" + n + " (Not enough words in dictionary)");
            return;
        }

        List<String> subset = allWords.subList(0, n);

        long trieBytes = 0;
        long compressedBytes = 0;

        try {
            // 1. Measure Classic Trie
            Object trie = createInstance(TRIE_CLASS);
            for (String w : subset) {
                insertInto(trie, w);
            }
            trieBytes = MemoryController.estimateClassicTrie(trie);

            // 2. Measure Compressed Trie
            Object compTrie = createInstance(COMPRESSED_TRIE_CLASS);
            for (String w : subset) {
                insertInto(compTrie, w);
            }
            compressedBytes = MemoryController.estimateCompressedTrie(compTrie);

        } catch (Exception e) {
            System.out.println("Error running test: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.printf("%-10d | %-15s | %-15s\n", 
            n, 
            MemoryController.bytesToMB(trieBytes), 
            MemoryController.bytesToMB(compressedBytes)
        );
    }

    private static Object createInstance(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        return clazz.getDeclaredConstructor().newInstance();
    }

    private static void insertInto(Object trieInstance, String word) throws Exception {
        Method insertMethod = trieInstance.getClass().getMethod("insert", String.class);
        insertMethod.invoke(trieInstance, word);
    }

    private static List<String> loadWords(String filename) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading " + filename);
        }
        return words;
    }
}