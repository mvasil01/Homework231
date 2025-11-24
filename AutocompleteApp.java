import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AutocompleteApp {
    private CompressedTrie trie;

    public AutocompleteApp(){
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

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: java AutocompleteApp <dictionary_file>");
            return;
        }

        AutocompleteApp app = new AutocompleteApp();
        app.loadDictionary(args[0]);

        // simple check (not Step 3 yet)
        System.out.println("Sample search test:");
        System.out.println("Contains 'apple'? " + app.trie.search("apple"));
        System.out.println("Contains 'banana'? " + app.trie.search("banana"));
        System.out.println("Contains 'table'? " + app.trie.search("apple"));
        System.out.println("Contains 'user'? " + app.trie.search("banana"));
        System.out.println("Contains 'test'? " + app.trie.search("apple"));
        System.out.println("Contains 'banana'? " + app.trie.search("banana"));
    }
}
