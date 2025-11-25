import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class AutocompleteApp {
    private CompressedTrie trie;

    public AutocompleteApp() {
        trie = new CompressedTrie();
    }

    public void loadDictionary(String dictionaryFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
            String word = reader.readLine();
            while (word != null) {
                word = word.trim().toLowerCase();
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
                word = reader.readLine();
            }

            reader.close();
            System.out.println("Dictionary loaded successfully from: " + dictionaryFile);
        } catch (IOException e) {
            System.out.println("Error reading dictionary: " + e.getMessage());
        }
    }

    private String cleanToken(String token) {
        if (token == null) {
            return null;
        }

        int start = 0;
        int end = token.length() - 1;

        String punct = ".,;:!?\\\"'()[]{}";

        while (start <= end && punct.indexOf(token.charAt(start)) != -1) {
            start++;
        }

        while (end >= start && punct.indexOf(token.charAt(end)) != -1) {
            end--;
        }

        if (start > end) {
            return "";
        }

        return token.substring(start, end + 1);
    }

    public void updateFrequenciesFromText(String textFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(textFile));
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
            reader.close();
            System.out.println("Frequencies updated from text file: " + textFile);

        } catch (IOException e) {
            System.out.println("Error reading text file: " + e.getMessage());
        }
    }

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