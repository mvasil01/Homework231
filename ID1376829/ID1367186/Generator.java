package ID1376829.ID1367186;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator {

    private static final String CHAR_FREQ = "eeeeeeeeettttttaaaaaaaaaoooooiiiiiinnnnnnssssssrrrrrrhhhhhhddddllluuucmmywfgpbvkxqjz";
    private static final Random random = new Random();
    
    // The dictionary sizes required by the assignment
    private static final int[] SIZES = {1000, 10000, 50000, 100000, 200000};
    
    // The specific fixed lengths you requested
    private static final int[] FIXED_LENGTHS = {7, 10, 31};

    public static void main(String[] args) {
        System.out.println("Generating dictionaries...");

        for (int n : SIZES) {
            System.out.println("Processing N = " + n + "...");
            
            // --- 1. Fixed Length Dictionaries (7, 10, 31) ---
            for (int len : FIXED_LENGTHS) {
                // Set deterministic seed based on N and Length
                random.setSeed(n * len * 12345L); 
                
                String fixedName = "dictionary_fixed_" + len + "_" + n + ".txt";
                String[] fixedWords = generateSameLength(n, len);
                
                saveToFile(fixedName, fixedWords);
                System.out.println("  -> Created " + fixedName);
            }

            // --- 2. Variable Length (Normal Distribution) ---
            // Set deterministic seed independent of the fixed loop
            random.setSeed(n * 67890L); 
            
            String normName = "dictionary_normal_" + n + ".txt";
            String[] variableWords = generateNormal(n);
            
            // Visual Check for EVERY dictionary size
            System.out.println("\n  [Visual Check for Normal Dist (N=" + n + ")]");
            checkNormalDistribution(variableWords);
            
            saveToFile(normName, variableWords);
            System.out.println("  -> Created " + normName);
            System.out.println("--------------------------------------------------");
        }
        
        System.out.println("All dictionaries generated successfully.");
    }

    public static void checkNormalDistribution(String[] words) {
        if (words == null || words.length == 0) {
            System.out.println("Dictionary is empty.");
            return;
        }

        int[] lengthCounts = new int[40]; // Increased buffer for longer words if needed
        long totalLength = 0;
        long totalLengthSq = 0;
        int minLen = Integer.MAX_VALUE;
        int maxLen = Integer.MIN_VALUE;

        for (String w : words) {
            int len = w.length();
            if (len < lengthCounts.length) {
                lengthCounts[len]++;
            }
            totalLength += len;
            totalLengthSq += (long) len * len;
            
            if (len < minLen) minLen = len;
            if (len > maxLen) maxLen = len;
        }

        double mean = (double) totalLength / words.length;
        double variance = ((double) totalLengthSq / words.length) - (mean * mean);
        double stdDev = Math.sqrt(variance);

        System.out.printf("  Stats: Mean=%.2f (Target ~11.0) | StdDev=%.2f | Range=[%d, %d]%n", 
                          mean, stdDev, minLen, maxLen);
        System.out.println("  Histogram:");

        int maxCount = 0;
        for (int c : lengthCounts) maxCount = Math.max(maxCount, c);

        for (int i = minLen; i <= maxLen; i++) {
            if (i >= lengthCounts.length) break;
            int count = lengthCounts[i];
            int barLen = (maxCount == 0) ? 0 : (int) ((double) count / maxCount * 40); 
            
            StringBuilder bar = new StringBuilder();
            for (int k = 0; k < barLen; k++) bar.append("*");
            
            System.out.printf("  %2d | %-40s (%d)%n", i, bar.toString(), count);
        }
        System.out.println();
    }

    private static void saveToFile(String filename, String[] words) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String word : words) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
        }
    }

    public static String[] generateSameLength(int n, int length) {
        String[] words = new String[n];
        for (int i = 0; i < n; i++) {
            words[i] = generateWord(length);
        }
        return words;
    }

    public static String[] generateNormal(int n) {
        String[] words = new String[n];
        for (int i = 0; i < n; i++) {
            double val = random.nextGaussian() * 4 + 11;
            int len = (int) Math.round(val);
            
            if (len < 4) len = 4;
            if (len > 20) len = 20;
            
            words[i] = generateWord(len);
        }
        return words;
    }

    public static String generateWord(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_FREQ.length());
            sb.append(CHAR_FREQ.charAt(index));
        }
        return sb.toString();
    }
}