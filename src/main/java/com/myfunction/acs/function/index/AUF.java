/**
 * The Anchored Union-find data structure.
 * Including four functions.
 */

package com.myfunction.acs.function.index;


public class AUF {
    int[] parent;
    int[] anchor;
    int[] rank;

    /**
     * Declare an com.AUF
     * @param size InitialCapacity.
     */
    public AUF(int size) {
        parent = new int[size];
        anchor = new int[size];
        rank = new int[size];

        for (int i = 0; i < size; i ++) {
            parent[i] = i;
            anchor[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Find element belong to which set.
     * @param element The target element.
     * @return The set that contains this element.
     */
    public int find(int element) {
        while (element != parent[element]) {
            element = parent[element];
        }
        return element;
    }

    /**
     * Judge if these two elements belong to the same set.
     * @param firstElement The first target element.
     * @param secondElement The second target element.
     * @return {@code true} if belong to the same set.
     */
    public boolean isConnected(int firstElement, int secondElement) {
        return find(firstElement) == find(secondElement);
    }

    /**
     * Union the sets that contain the two elements.
     * @param firstElement The first target element.
     * @param secondElement The second target element.
     */
    public void unionElements(int firstElement, int secondElement) {
        int firstRoot = find(firstElement);
        int secondRoot = find(secondElement);

        if (firstRoot == secondRoot) {
            return;
        }
        if (rank[firstRoot] > rank[secondRoot]) {
            parent[secondRoot] = firstRoot;
        } else if (rank[firstRoot] < rank[secondRoot]) {
            parent[firstRoot] = secondRoot;
        } else {
            parent[firstRoot] = secondRoot;
            rank[secondRoot] += 1;
        }
    }
}