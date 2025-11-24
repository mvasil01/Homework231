public class WordFrequency {
    public String word;
    public int importance;

    public WordFrequency(String word, int importance){
        this.word = word;
        this.importance = importance;
    }

    @Override
    public String toString(){
        return word + " (" + importance + ")";
    }
}
