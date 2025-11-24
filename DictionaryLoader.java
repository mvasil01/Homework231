import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: java AutocompleteApp <dictionary_file>");
            return;
        }

        DictionaryLoader dict = new DictionaryLoader();
        dict.loadDictionary(args[0]);

        System.out.println("Sample search test:");
        System.out.println("Contains 'apple'? " + dict.trie.search("apple"));
        System.out.println("Contains 'banana'? " + dict.trie.search("banana"));
        System.out.println("Contains 'table'? " + dict.trie.search("table"));
        System.out.println("Contains 'user'? " + dict.trie.search("user"));
        System.out.println("Contains 'test'? " + dict.trie.search("test"));
        System.out.println("Contains 'world'? " + dict.trie.search("world"));
    }
}
