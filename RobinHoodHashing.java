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

    private static int hashFunction(String label, Edge hashTable[]){
        return (label.charAt(0) - 'a') % hashTable.length;
    }

    private int probeLength(int pos){
        int expected = hashFunction(hashTable[pos].label, hashTable);

        if(pos < expected){
            return capacity - expected + pos;
        }
        else{
            return pos - expected;
        }
    }

    public void insert(Edge edge){
        size++;
        if(needsRehash()){
            this.rehash();
        }

        int pos = hashFunction(edge.label, hashTable);
        if(!hashTable[pos].occupied){
            hashTable[pos] = edge;
        }
        else{
            int newProbeLength = 0;
            while(newProbeLength < probeLength(pos)){
                Edge conflict = hashTable[pos];
                newProbeLength = probeLength(pos);
                hashTable[pos] = edge;
                if(pos + 1 < capacity){
                    pos++;
                }
                else{
                    pos = 0;
                }
                edge = conflict;
                if(newProbeLength > maxProbeLength){
                    maxProbeLength = newProbeLength;
                }
            }
            hashTable[pos] = edge;
        }
    
    }
    
    public boolean getEdge(String label){
        int pos = hashFunction(label, hashTable);

        for (int i = pos; i <= maxProbeLength; i++){
            if (hashTable[i].label.equals(label)){
                return true;
            }
        }

        return false;
    }

    private boolean needsRehash(){
        return ((size * 1.0 / capacity )> 0.9);
    }

    private void rehash(){
        
        int newSize = 0;
        while (newSize < PRIMES.length){
            if (capacity == PRIMES[newSize++]){
                break;
            }
        
        }

        Edge newHashTable[] = new Edge[PRIMES[newSize]];

        
    }
}