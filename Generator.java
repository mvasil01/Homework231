import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator {

    // Standard English letter frequency approximation
    private static final String CHAR_FREQ = "eeeeeeeeettttttaaaaaaaaaoooooiiiiiinnnnnnssssssrrrrrrhhhhhhddddllluuucmmywfgpbvkxqjz";
    private static final Random random = new Random();
    
    private static final int MAX_N = 200000; 

    public static void main(String[] args) {
        System.out.println("Generating dictionaries...");

        // 1. Fixed Length (Length 10 is safe from duplicates)
        String[] fixedWords = generateSameLength(MAX_N, 10);
        saveToFile("dictionary_fixed_length.txt", fixedWords);
        // FIX: Printed filename now matches actual filename
        System.out.println("-> Created dictionary_fixed_length.txt (" + MAX_N + " words)");

        // 2. Variable Length (Normal Distribution)
        String[] variableWords = generateNormal(MAX_N);
        checkNormalDistribution(variableWords); 
        saveToFile("dictionary_normal_distributed.txt", variableWords);
        System.out.println("-> Created dictionary_normal_distributed.txt (" + MAX_N + " words)");
        
        System.out.println("Done.");
    }

    public static void checkNormalDistribution(String[] words) {
        if (words == null || words.length == 0) {
            System.out.println("Dictionary is empty.");
            return;
        }

        int[] lengthCounts = new int[30]; // Increased size just in case
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

        System.out.println("\n=== Normal Distribution Check ===");
        System.out.printf("Total Words: %d%n", words.length);
        System.out.printf("Mean Length: %.2f (Expected ~11.0)%n", mean);
        System.out.printf("Std Dev:     %.2f (Expected ~4.0)%n", stdDev);
        System.out.println("Length Histogram:");

        int maxCount = 0;
        for (int c : lengthCounts) maxCount = Math.max(maxCount, c);

        for (int i = minLen; i <= maxLen; i++) {
            int count = lengthCounts[i];
            int barLen = (int) ((double) count / maxCount * 40); 
            
            StringBuilder bar = new StringBuilder();
            for (int k = 0; k < barLen; k++) bar.append("*");
            
            System.out.printf("%2d | %-40s (%d)%n", i, bar.toString(), count);
        }
        System.out.println("=================================\n");
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
            // FIX: 
            // 1. Shifted Mean to 11 to avoid short-word collisions (length 2-3)
            // 2. Increased StdDev to 4 for a wider spread
            // 3. Used Math.round to avoid flooring bias
            double val = random.nextGaussian() * 4 + 11;
            int len = (int) Math.round(val);
            
            // Safe clamping
            if (len < 4) len = 4;   // Minimum 4 avoids most collisions
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