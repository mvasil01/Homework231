package ID1376829.ID1367186;
public class AutocompleteEngine {

    private final CompressedTrie trie = new CompressedTrie();

    public AutocompleteEngine(String dictionaryFile, String trainingFile) {
        loadDictionary(dictionaryFile);
        updateFrequencies(trainingFile);
    }

    private void loadDictionary(String dictionaryFile) {
        DictionaryLoader loader = new DictionaryLoader();
        // If DictionaryLoader currently has static methods, adapt accordingly.
        try {
            java.io.BufferedReader reader =
                new java.io.BufferedReader(new java.io.FileReader(dictionaryFile));
            String word = reader.readLine();
            while (word != null) {
                word = word.trim().toLowerCase();
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
                word = reader.readLine();
            }
            reader.close();
            System.out.println("Dictionary loaded from " + dictionaryFile);
        } catch (java.io.IOException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    private void updateFrequencies(String trainingFile) {
        AutocompleteApp helper = new AutocompleteApp();
        // We only want its cleanToken logic; you can move that logic here instead if you prefer

        try {
            java.io.BufferedReader reader =
                new java.io.BufferedReader(new java.io.FileReader(trainingFile));
            String line = reader.readLine();
            while (line != null) {
                java.util.StringTokenizer st = new java.util.StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    String raw = st.nextToken();
                    String cleaned = helperCleanToken(raw);
                    if (cleaned == null) continue;
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
            System.out.println("Frequencies updated from " + trainingFile);
        } catch (java.io.IOException e) {
            System.out.println("Error updating frequencies: " + e.getMessage());
        }
    }

    // copy the logic of cleanToken from AutocompleteApp here:
    private String helperCleanToken(String token) {
        if (token == null) return null;
        int start = 0;
        int end = token.length() - 1;
        String punct = ".,;:!?\\\"'()[]{}";

        while (start <= end && punct.indexOf(token.charAt(start)) != -1) {
            start++;
        }

        while (end >= start && punct.indexOf(token.charAt(end)) != -1) {
            end--;
        }

        if (start > end) return "";
        return token.substring(start, end + 1);
    }

    // === Methods exposed to the HTTP layer ===

    public WordFrequency[] topK(String prefix, int k) {
        return trie.getTopK(prefix, k);
    }

    public double avgFreq(String prefix) {
        return trie.getAverageFrequency(prefix);
    }

    public char nextLetter(String prefix) {
        return trie.predictNextLetter(prefix);
    }

    public boolean search(String word) {
        return trie.search(word);
    }
}