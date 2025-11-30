# Homework231

# Autocomplete System — Java Implementation

This project implements a complete **autocomplete engine** using advanced data structures:

- **Compressed Trie**  
- **Robin Hood Hash Table** (for storing outgoing edges efficiently)  
- **Min-Heap** (for top-k suggestions)  
- **HeapSort** (to sort the final k results)  

It supports:

- Word insertion  
- Word search  
- Prefix-based suggestions  
- Top-K autocomplete  
- Frequency-based ranking  
- Prefix average frequency calculation  
- Next-letter prediction  

This README explains *how to run and use* the project.

---

## 📦 Project Structure (Main Files)

```
CompressedTrie.java           — Main trie logic
CompressedTrieNode.java       — Trie node with Robin Hood hashing
RobinHoodHashing.java         — Hash table storing outgoing edges
Edge.java                     — Labeled trie edge
MinHeap.java                  — Min-heap for top-k words
HeapSort.java                 — HeapSort for sorting final suggestions
WordFrequency.java            — Word + frequency pair
DictionaryLoader.java         — Loads dictionary from file
AutocompleteApp.java          — Interactive menu-driven app
Tester.java                   — Full test suite
TestRobinHood.java            — Hash table tests
```

---

## 🛠 Requirements

- **Java 17+**
- A **dictionary file** (one word per line)
- A **training file** (arbitrary text to update word frequencies)

---

## ▶️ Running the Program

Compile everything:

```bash
javac AutocompleteApp.java

or

javac AutocompleteServer.java
```

Run the interactive menu:

```bash
java AutocompleteApp dictionary.txt training.txt

or 

java AutocompleteServer dictionary.txt training.txt (This starts the local host server. Then open the index.html in the browser)
```

---

## 📥 Loading Dictionary & Training Files

### Dictionary file format:
```
apple
application
banana
car
...
```

### Training file format:
Free-form text such as novels or articles:

```
The quick brown fox jumps over the lazy dog.
Apple apple banana!
```

Words found in the training text increase their **importance** (frequency) within the trie.

---

## 🧭 Using the Menu

After launching the program:

```
========== AUTOCOMPLETE MENU ==========
1. Get top-k suggestions for a prefix
2. Get average frequency for a prefix
3. Predict next letter for a prefix
4. Search exact word
0. Exit
Choose an option:
```

### **1️⃣ Top-K suggestions**
Enter a prefix and k:

```
Enter prefix: app
Enter k: 5
Top 5 suggestions for "app":
 1. appetite (freq 7)
 2. apple (freq 5)
 ...
```

---

### **2️⃣ Average frequency of a prefix**
Example:

```
Enter prefix: ban
Average frequency of words starting with "ban": 1.75
```

---

### **3️⃣ Predict next letter**
Based on subtree averages:

```
Enter prefix: app
Suggested next letter: 'l'
```

---

### **4️⃣ Exact word search**

```
Enter word: apple
Word found? true
```

---

## 🧪 Testing

To run all tests:

```
java Tester
```

To test the hash table:

```
java TestRobinHood
```

All major cases are covered:
- Edge splitting  
- Prefix nodes inside edges  
- Case-insensitive operations  
- Frequency updates  
- Top-K logic  
- Next-letter prediction  

---

## 📝 Notes

- The trie is **case-insensitive** (inputs are converted to lowercase).  
- The hash table uses **Robin Hood probing** for extremely stable performance.  
- Top-K suggestions use a **fixed-size MinHeap**, giving O(n log k).  
- Average frequency uses an **iterative DFS** with a custom hybrid stack.  

---

## 📚 Authors & Credits

Project developed as part of **EPL231 — Data Structures & Algorithms** (University of Cyprus).  
All algorithms written manually without using Java collections for trie/hashing/heaps.

Authors:
    Markos Vasili   UC1376829
    Ioannis Charalampous    UC1367186

---

