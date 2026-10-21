package com.example.proj1;

import java.util.ArrayList;
import java.util.List;

// Custom hash table that maps a key or in this case a word for how many times its been seen.
class HT {
    static final class Node {
        Object key;
        int count;
        Node next;
        Node(Object k, Node n) { key = k; count = 1; next = n; }
    }

    Node[] table = new Node[8]; // always a power of 2
    int size = 0;

    // Adds 1 to keys count and inserts new
    void increment(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1); // which bucket this key belongs in
        // Will go through the buckets looking for match
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)) {
                e.count++;
                return;
            }
        }
        // If not found insert a new node at front
        table[i] = new Node(key, table[i]);
        ++size;
        // If table is getting to full get bigger
        if ((float) size / table.length >= 0.75f)
            resize();
    }

    // Looks at how many times a key has been counted.
    // returns 0 of word isn't seen which is nice because it
    // dosen't contribute instead of causing error
    int getCount(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                return e.count;
        }
        return 0;
    }

    // Returns every key this is used when my TextAnalyzer iterates over every word on a page.
    List<Object> keys() {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < table.length; ++i)
            for (Node e = table[i]; e != null; e = e.next)
                result.add(e.key);
        return result;
    }

    // This works well because with so many words it can cause for long runtimes,
    // This allows for keeping each bucket chain short so it doubles number of buckets and
    // distributes every node into a new larger table. does automatically by increment()
    private void resize() {
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; ++i) {
            Node e = oldTable[i];
            while (e != null) {
                Node next = e.next;
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);
                e.next = newTable[j];
                newTable[j] = e;
                e = next;
            }
        }
        table = newTable;
    }

}
