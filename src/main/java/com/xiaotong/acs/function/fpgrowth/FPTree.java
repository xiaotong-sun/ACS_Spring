package com.xiaotong.acs.function.fpgrowth;

import java.util.*;

public class FPTree {
    private Map<String, HeaderTable> headerTable;

    /**
     * Get the headerTable.
     * @return header table.
     */
    public Map<String, HeaderTable> getHeaderTable() {
        return headerTable;
    }

    /**
     * Build the FP-Tree
     * @param itemsetsMap The map, whose Key is the itemset and Value is the itemset's frequency.
     * @param minSup The minimum support.
     * @return The root of the FP-Tree.
     */
    public FPTreeNode buildFPTree(Map<Set<String>, Integer> itemsetsMap, int minSup) {
        // Traverse the dataset, recording the frequency of each data item
        Set<Set<String>> itemsets = itemsetsMap.keySet();
        this.headerTable = new HashMap<>();
        for (Set<String> set : itemsets) {
            for (String kw : set) {
                int value = 0;
                if (this.headerTable.containsKey(kw)) {
                    value = this.headerTable.get(kw).frequency;
                }
                HeaderTable table = new HeaderTable(value + itemsetsMap.get(set), null);
                this.headerTable.put(kw, table);
            }
        }

        // Filter based on minimum support
        Set<String> toDelete = new HashSet<>();
        for (String kw : this.headerTable.keySet()) {
            if (this.headerTable.get(kw).frequency < minSup) {
                toDelete.add(kw);
            }
        }
        for (String elem : toDelete) {
            headerTable.remove(elem);
        }

        // If all data not satisfied the minium support, then return null.
        Set<String> freqItemSet = this.headerTable.keySet();
        if (freqItemSet.size() == 0) {
            return null;
        }

        // Traverse the dataset, build the fp-tree.
        FPTreeNode root = new FPTreeNode("∅", 1, null);
        for (Set<String> set : itemsetsMap.keySet()) {
            Map<String, Integer> localD = new HashMap<>();
            for (String kw : set) {
                if (freqItemSet.contains(kw)) {
                    localD.put(kw, this.headerTable.get(kw).frequency);
                }
            }
            if (localD.size() > 0) {
                List<String> orderedItems = orderByFrequency(localD);
                updateTree(orderedItems, root, this.headerTable, itemsetsMap.get(set));
            }
        }
        return root;
    }

    /**
     * Sort the data of each itemset in descending order by frequency.
     *
     * @param localD A map of elements that satisify the minimum level of support.
     * @return The list of string, which is sorted in
     * descending order by the string's frequency.
     */
    private List<String> orderByFrequency(Map<String, Integer> localD) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(localD.entrySet());
        list.sort((o1, o2) -> -((o1.getValue()).compareTo(o2.getValue())));
        List<String> res = new ArrayList<>();
        for (Map.Entry<String, Integer> item : list) {
            res.add(item.getKey());
        }
        return res;
    }

    /**
     * Update the tree node level by level.
     * @param items The string list, which is sorted in descending order by the string's frequency.
     * @param node The root of current child FP-Tree.
     * @param headerTable The header table.
     * @param frequency The firequency of the string list(items).
     */
    private void updateTree(List<String> items, FPTreeNode node, Map<String, HeaderTable> headerTable, int frequency) {
        String firstItem = items.get(0);
        FPTreeNode nextRoot = null;
        for (FPTreeNode child : node.childList) {
            if ((child.keyword).equals(firstItem)) {
                child.frequency += frequency;
                nextRoot = child;
                break;
            }
        }
        if (nextRoot == null) {
            nextRoot = new FPTreeNode(firstItem, frequency, node);
            node.childList.add(nextRoot);
            if (headerTable.get(firstItem).node == null) {
                headerTable.get(firstItem).node = nextRoot;
            } else {
                updateHeader(headerTable.get(firstItem).node, nextRoot);
            }
        }
        if (items.size() > 1) {
            items.remove(0);
            updateTree(items, nextRoot, headerTable, frequency);
        }
    }

    /**
     * Update the header table.
     * @param nodeToTest The node that needs to test.
     * @param targetNode The target node that needs to be connected.
     */
    private void updateHeader(FPTreeNode nodeToTest, FPTreeNode targetNode) {
        while (nodeToTest.nodeLink != null) {
            nodeToTest = nodeToTest.nodeLink;
        }
        nodeToTest.nodeLink = targetNode;
    }
}
