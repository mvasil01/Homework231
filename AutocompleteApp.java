import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Console-based autocomplete application built on top of {@link CompressedTrie}.
 *
 * <p>The application:</p>
 * <ul>
 *   <li>Loads a dictionary file (one word per line) into a {@link CompressedTrie}.</li>
 *   <li>Reads a training text file and updates word frequencies in the trie.</li>
 *   <li>Provides an interactive menu with options to:
 *     <ul>
 *       <li>Get top-k suggestions for a prefix</li>
 *       <li>Get average frequency for a prefix</li>
 *       <li>Predict the next letter after a prefix</li>
 *       <li>Search for an exact word</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Usage (from command line):</p>
 * <pre>
 *   java AutocompleteApp dictionary.txt training.txt
 * </pre>
 */
public class AutocompleteApp {

    /**
     * The compressed trie that stores all dictionary words
     * and their importance (frequency).
     */
    private CompressedTrie trie;

    /**
     * Constructs a new {@code AutocompleteApp} with an empty {@link CompressedTrie}.
     */
    public AutocompleteApp() {
        trie = new CompressedTrie();
    }

    /**
     * Loads words from a dictionary file into the trie.
     * <p>
     * Expected format: one word per line. Each word is trimmed and converted to
     * lowercase before insertion. Empty lines are ignored.
     * </p>
     *
     * @param dictionaryFile path to the dictionary file
     */
    public void loadDictionary(String dictionaryFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile))) {
            String word = reader.readLine();
            while (word != null) {
                word = word.trim().toLowerCase();
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
                word = reader.readLine();
            }

            System.out.println("Dictionary loaded successfully from: " + dictionaryFile);
        } catch (IOException e) {
            System.out.println("Error reading dictionary: " + e.getMessage());
        }
    }

    /**
     * Cleans a token by stripping leading and trailing punctuation characters.
     * <p>
     * The set of punctuation characters removed is:
     * {@code ".,;:!?\\\"'()[]{}"}.
     * Internal punctuation (e.g. in "don't") is preserved.
     * </p>
     *
     * @param token the raw token as read from the text
     * @return the cleaned token (may be empty), {@code null} if input token is null
     */
    private String cleanToken(String token) {
        if (token == null) {
            return null;
        }

        int start = 0;
        int end = token.length() - 1;

        String punct = ".,;:!?\\\"'()[]{}";

        // strip punctuation from the start
        while (start <= end && punct.indexOf(token.charAt(start)) != -1) {
            start++;
        }

        // strip punctuation from the end
        while (end >= start && punct.indexOf(token.charAt(end)) != -1) {
            end--;
        }

        if (start > end) {
            return "";
        }

        return token.substring(start, end + 1);
    }

    /**
     * Reads a training text file and updates word frequencies in the trie.
     * <p>
     * For each token found in the text:
     * <ul>
     *   <li>Leading and trailing punctuation is removed via {@link #cleanToken(String)}.</li>
     *   <li>The token is converted to lowercase.</li>
     *   <li>If the cleaned token is present in the dictionary trie, the
     *       {@code importance} counter of its node is incremented by 1.</li>
     * </ul>
     * </p>
     *
     * @param textFile path to the training text file
     */
    public void updateFrequenciesFromText(String textFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line = reader.readLine();

            while (line != null) {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreElements()) {
                    String rawToken = st.nextToken();

                    String cleaned = cleanToken(rawToken);
                    if (cleaned == null) {
                        continue;
                    }

                    cleaned = cleaned.toLowerCase();
                    if (cleaned.isEmpty()) continue;

                    CompressedTrieNode node = trie.getNode(cleaned);
                    if (node != null) {
                        node.importance++;
                    }
                }

                line = reader.readLine();
            }
            System.out.println("Frequencies updated from text file: " + textFile);

        } catch (IOException e) {
            System.out.println("Error reading text file: " + e.getMessage());
        }
    }

    /**
     * Runs an interactive console menu to query the autocomplete system.
     * <p>
     * Options:
     * <ol>
     *   <li>Get top-k suggestions for a prefix</li>
     *   <li>Get average frequency for a prefix</li>
     *   <li>Predict next letter for a prefix</li>
     *   <li>Search exact word</li>
     *   <li>Exit</li>
     * </ol>
     * </p>
     */
    public void runMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n========== AUTOCOMPLETE MENU ==========");
            System.out.println("1. Get top-k suggestions for a prefix");
            System.out.println("2. Get average frequency for a prefix");
            System.out.println("3. Predict next letter for a prefix");
            System.out.println("4. Search exact word");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choiceLine = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceLine);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Exiting. Bye!");
                break;
            }

            switch (choice) {
                case 1:
                    handleTopK(sc);
                    break;
                case 2:
                    handleAverageFrequency(sc);
                    break;
                case 3:
                    handlePredictNextLetter(sc);
                    break;
                case 4:
                    handleExactSearch(sc);
                    break;
                default:
                    System.out.println("Unknown option. Please try again.");
            }
        }

        sc.close();
    }

    /**
     * Handles menu option 1: get top-k suggestions for a prefix.
     *
     * @param sc the shared {@link Scanner} for reading user input
     */
    private void handleTopK(Scanner sc) {
        System.out.print("Enter prefix: ");
        String prefix = sc.nextLine().trim();
        if (prefix.isEmpty()) {
            System.out.println("Prefix cannot be empty.");
            return;
        }

        System.out.print("Enter k: ");
        String kLine = sc.nextLine().trim();
        int k;
        try {
            k = Integer.parseInt(kLine);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number for k.");
            return;
        }

        WordFrequency[] result = trie.getTopK(prefix, k);
        if (result.length == 0) {
            System.out.println("No suggestions found for prefix \"" + prefix + "\".");
            return;
        }

        System.out.println("Top " + result.length + " suggestions for \"" + prefix + "\":");
        for (WordFrequency wf : result) {
            System.out.println("  " + wf.word + " (freq = " + wf.importance + ")");
        }
    }

    /**
     * Handles menu option 2: compute and display the average frequency
     * of words starting with a prefix.
     *
     * @param sc the shared {@link Scanner} for reading user input
     */
    private void handleAverageFrequency(Scanner sc) {
        System.out.print("Enter prefix: ");
        String prefix = sc.nextLine().trim();
        if (prefix.isEmpty()) {
            System.out.println("Prefix cannot be empty.");
            return;
        }

        double avg = trie.getAverageFrequency(prefix);
        System.out.printf("Average frequency of words starting with \"%s\": %.2f%n", prefix, avg);
    }

    /**
     * Handles menu option 3: predict the next letter after a prefix.
     *
     * @param sc the shared {@link Scanner} for reading user input
     */
    private void handlePredictNextLetter(Scanner sc) {
        System.out.print("Enter prefix: ");
        String prefix = sc.nextLine().trim();
        if (prefix.isEmpty()) {
            System.out.println("Prefix cannot be empty.");
            return;
        }

        char c = trie.predictNextLetter(prefix);
        if (c == '\0') {
            System.out.println("No next-letter suggestion found for prefix \"" + prefix + "\".");
        } else {
            System.out.println("Suggested next letter after \"" + prefix + "\": '" + c + "'");
        }
    }

    /**
     * Handles menu option 4: check if an exact word exists in the trie.
     *
     * @param sc the shared {@link Scanner} for reading user input
     */
    private void handleExactSearch(Scanner sc) {
        System.out.print("Enter word to search: ");
        String word = sc.nextLine().trim();
        if (word.isEmpty()) {
            System.out.println("Word cannot be empty.");
            return;
        }

        boolean found = trie.search(word);
        System.out.println("Word \"" + word + "\" found? " + found);
    }

    /**
     * Entry point of the application.
     * <p>
     * Expects two command-line arguments:
     * <ol>
     *   <li>{@code dictionary_file}: path to a dictionary with one word per line</li>
     *   <li>{@code text_file}: path to a training text used to update frequencies</li>
     * </ol>
     * </p>
     *
     * @param args command-line arguments: dictionary file and training text file
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java AutocompleteApp <dictionary_file> <text_file>");
            return;
        }

        AutocompleteApp app = new AutocompleteApp();

        app.loadDictionary(args[0]);
        app.updateFrequenciesFromText(args[1]);
        app.runMenu();
    }
}
