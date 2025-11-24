public class RobinHoodHashing{
    private Edge hashTable[];
    private int capacity;
    private int size;
    private int maxProbeLength;
    private final int PRIMES[] = {3 , 7, 11, 17, 23, 29};
    private int primeIndex;

    public RobinHoodHashing(){
        primeIndex = 0;
        hashTable = new Edge[PRIMES[0]];
        capacity = PRIMES[primeIndex];
        size = 0;
        maxProbeLength = 0;
    }

    private int hashFunction(String label){
        return (label.charAt(0) - 'a') % capacity;
    }

    /*private int probeLength(int pos){
        int expected = hashFunction(hashTable[pos].label);

        if(pos < expected){
            return capacity - expected + pos;
        }
        else{
            return pos - expected;
        }
    }*/

    public void insert(Edge edge){
        if(edge == null || edge.label == null){
            return;
        }

        if((size + 1.0) / capacity > 0.9){
            rehash();
        }
    
        insertNoRehash(edge);
    }

    private void insertNoRehash(Edge edge) {
        int home = hashFunction(edge.label.toLowerCase());
        int idx = home;
        int probeNew = 0;

        while (true) {
            Edge current = hashTable[idx];

            if (current == null || !current.occupied) {
                edge.occupied = true;
                hashTable[idx] = edge;
                size++;
                if (probeNew > maxProbeLength) {
                    maxProbeLength = probeNew;
                }
                return;
            }

            if (current.label.equals(edge.label)) {
                return;
            }

            int homeCurrent = hashFunction(current.label);
            int probeCurrent = (idx - homeCurrent + capacity) % capacity;

            if (probeNew > probeCurrent) {
                Edge temp = hashTable[idx];
                hashTable[idx] = edge;
                edge = temp;
                probeNew = probeCurrent;
            }

            idx = (idx + 1) % capacity;
            probeNew++;
        }
    }
    
    public boolean search(String label){
        int pos = hashFunction(label);
        int index;
        for(int offset = 0; offset <= maxProbeLength; offset++){
            index = (pos + offset) % capacity;
            Edge e = hashTable[index];
            if(e == null){
                return false;
            }
            if(e.occupied && e.label.equals(label)){
                return true;
            }
        }

        return false;
    }

    public Edge getEdge(char c){
        for(int i = 0; i < capacity; i++){
            if(hashTable[i] != null && hashTable[i].label.charAt(0) == c){
                return hashTable[i];
            }
        }
        return null;
    }

    /*private boolean needsRehash(){
        return ((size * 1.0 / capacity )> 0.9);
    }*/

    private void rehash(){
        int index = 0;
        while(index < PRIMES.length && PRIMES[index] != capacity){
            index++;
        }

        if(index + 1 >= PRIMES.length){
            return;         // no larger Prime number
        }

        int newCapacity = PRIMES[index + 1];
        Edge[] oldTable = hashTable;

        hashTable = new Edge[newCapacity];
        capacity = newCapacity;
        size = 0;
        maxProbeLength = 0;

        for(Edge e : oldTable){
            if(e != null && e.occupied){
                insertNoRehash(e);
            }
        }
        
    }
}