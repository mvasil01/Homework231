package ID1376829.ID1367186;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class DictionaryLoader{
    private CompressedTrie trie;

    public DictionaryLoader(){
        trie = new CompressedTrie();
    }

    public void loadDictionary(String dictionaryFile){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
            String word = reader.readLine();
            while(word != null){
                word = word.trim().toLowerCase();
                if(!word.isEmpty()){
                    trie.insert(word);
                }
                word = reader.readLine();
            }

            reader.close();
            System.out.println("Dictionary loaded Successfully");
        } 
        catch(IOException e){
            System.out.println("Error reading Dictionary: " + e.getMessage());
        }   
    }

    private String cleanToken(String token){
        if(token == null){
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

    public void updateFrequenciesFromText(String textFIle){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(textFIle));
            String line = reader.readLine();

            while(line != null){
                StringTokenizer st = new StringTokenizer(line);
                while(st.hasMoreElements()){
                    String rawToken = st.nextToken();

                    String cleaned = cleanToken(rawToken);
                    if(cleaned == null){
                        continue;
                    }

                    cleaned = cleaned.toLowerCase();

                    if(cleaned.isEmpty()) continue;

                    CompressedTrieNode node = trie.getNode(cleaned);
                    if(node != null){
                        node.importance++;
                    }
                }


                line = reader.readLine();
            }
            reader.close();
            System.out.println("Frequencies updated from text file: " + textFIle);
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java AutocompleteApp <dictionary_file> <text_file>");
            return;
        }

        DictionaryLoader app = new DictionaryLoader();

        // STEP 2: load dictionary
        app.loadDictionary(args[0]);

        // STEP 3: update frequencies from text
        app.updateFrequenciesFromText(args[1]);

        // Quick sanity check: print importance of a few words
        System.out.println("=== Sample frequency check ===");
        String[] testWords = {"apple", "banana", "java", "hello"};
        for (String w : testWords) {
            CompressedTrieNode node = app.trie.getNode(w);
            int freq = (node == null ? 0 : node.importance);
            System.out.println("  " + w + " -> " + freq);
        }
    }

}
