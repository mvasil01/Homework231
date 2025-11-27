# Developer Documentation — Autocomplete System (Compressed Trie + Robin Hood Hashing)

This document explains the **internal architecture**, **algorithms**, and **data structures** used in this project.  
It is intended for developers extending or verifying the correctness of this implementation.

---

# 🔧 1. Architecture Overview

The autocomplete engine is built from:

1. **Compressed Trie**
2. **Robin Hood Hash Table** (for outgoing edges of each node)
3. **MinHeap** (top-k selection)
4. **HeapSort** (final sorting)
5. **Frequency model** (importance counter)

---

# 🌲 2. Compressed Trie

Each node represents:

- `boolean isEndOfWord`  
- `int importance`  
- `RobinHoodHashing edges` (mapping from first character → labeled Edge)

Each **Edge** stores:

- `String label` — compressed path segment  
- `CompressedTrieNode child` — next node  

### 📌 Key operations:

### **Insertion**
Insertion handles 4 cases:

#### Case 1 — *Exact match*
`word == edge.label`  
→ Mark child as `isEndOfWord`.

#### Case 2 — *Label is prefix of word*
`label = "car"` , `word = "carton"`  
→ Recurse with remainder `"ton"`.

#### Case 3 — *Word is prefix of label*
`word = "car"` , `label = "carton"`  
→ Split into mid-node.

#### Case 4 — *Partial overlap*
`word = "carbon"` , `label = "carton"`  
→ Split into:
- prefix `"car"`
- remainder `"bon"`
- remainder `"ton"`

All handled manually based on `commonPrefixLength`.

---

# 3️⃣ Robin Hood Hashing (Open Addressing)

Each trie node stores edges in a **custom hash table** with:

- Linear probing
- Robin Hood swapping
- Dynamic rehashing (growing through primes)

### 📌 Features:
- Equalizes probe lengths
- Prevents pathological clustering
- Very fast for trie edges (usually ≤ 10 entries)

---

# ⚖️ 4. MinHeap & HeapSort

### **MinHeap(k)**
Stores at most **k** words for Top-K:

- If heap not full → insert  
- Else if new word importance > min → pop min → insert new  

This ensures O(N log k) complexity.

### **HeapSort**
Used only at the end to sort the top-k results into descending frequency.

---

# 🔢 5. Average Frequency Computation

Runs an **iterative DFS** using `hybridStack`:

```
totalImportance += node.importance
count++
```

Result:

```
average = totalImportance / count
```

Handles large tries efficiently without recursion.

---

# 🔤 6. Next-Letter Prediction

For prefix `"app"`:

1. Get the node for `"app"`  
2. For each child edge:
   - Compute average frequency of the child subtree
   - Pick the child with the **maximum** average

Formula:

```
avg(freq | prefix + c)
```

This aligns with project requirements.

---

# 🧪 7. Testing

Included tests cover:

- Compressed trie insertion/splitting
- Prefix search
- Edge prefix inside label
- Robin Hood hashing collisions
- Frequency updates
- Top-k logic
- Next-letter prediction
- Full test suite (`Tester.java`)

---

# 📈 8. Performance Notes

- Robin Hood hashing guarantees **stable ~O(1)** edge lookup.  
- Compressed trie reduces memory usage by compressing chains.  
- MinHeap keeps Top-K efficient even for large tries.  
- DFS avoids recursion limits.

---

# 🚀 9. Possible Extensions

- Web frontend (HTML+JS)  
- Next-word prediction  
- Saving trie to disk  
- LRU cache for repeated prefix queries  
- BK-tree for approximate search  

---

# 📝 Authors

Originally implemented for **EPL231 – Data Structures**.  
All structures implemented manually without Java built-in collections (except IO).

