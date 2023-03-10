package com.myfunction.acs.function.fpgrowth;

import java.util.*;

public class FPGrowth {
    /**
     * Looking for the prefix path in reverse order
     * @param leafNode the leaf node of the fp-tree
     * @param prefixPath the prefix path
     */
    private void ascendTree(FPTreeNode leafNode, Set<String> prefixPath) {
        if (leafNode.parent != null) {
            prefixPath.add(leafNode.keyword);
            ascendTree(leafNode.parent, prefixPath);
        }
    }

    /**
     * Looking for the conditional pattern base for an item.
     * @param basePat the item
     * @param headerTable the header table
     * @return the conditional pattern base
     */
    private Map<Set<String>, Integer> findPrefixPath(String basePat, Map<String, HeaderTable> headerTable) {
        Map<Set<String>, Integer> condPats = new HashMap<>();
        FPTreeNode treeNode = headerTable.get(basePat).node;
        while (treeNode != null) {
            Set<String> prefixPath = new HashSet<>();
            ascendTree(treeNode, prefixPath);
            if (prefixPath.size() > 1) {
                prefixPath.remove(treeNode.keyword);
                condPats.put(prefixPath, treeNode.frequency);
            }
            treeNode = treeNode.nodeLink;
        }
        return condPats;
    }

    /**
     * Mine the fp-tree to find the frequent itemsets.
     * @param headerTable the header table of fp-tree
     * @param minSup the minimum support
     */
    public Set<Set<String>> mineTree(Map<String, HeaderTable> headerTable, int minSup) {
        Set<Set<String>> allFrequentItems = new HashSet<>();
        List<String> bigL = orderInAsc(headerTable);
        for (String item : bigL) {
            Set<String> temp = new HashSet<>();
            temp.add(item);
            allFrequentItems.add(temp);
        }
        for (String basePat : bigL) {
            Map<Set<String>, Integer> condPatBases = findPrefixPath(basePat, headerTable);
            FPTree condTree = new FPTree();
            FPTreeNode condTreeRoot = condTree.buildFPTree(condPatBases, minSup);
            Map<String, HeaderTable> condTreeTable = condTree.getHeaderTable();
            if (condTreeTable.size() != 0) {
                obtainFrequentItems(allFrequentItems, basePat, condTreeTable.keySet());
            }
        }
        return allFrequentItems;
    }

    /**
     * Sort the elements of headerTable in ascending order by the frequency.
     * @param headerTable the header table of the fp-tree
     * @return the list of keyword after sorted.
     */
    private List<String> orderInAsc(Map<String, HeaderTable> headerTable) {
        List<Map.Entry<String, HeaderTable>> list = new ArrayList<>(headerTable.entrySet());
        list.sort(Comparator.comparingInt(o -> o.getValue().frequency));
        List<String> res = new ArrayList<>();
        for (Map.Entry<String, HeaderTable> item : list) {
            res.add(item.getKey());
        }
        return res;
    }

    /**
     * Obtain all the frequent items.
     * First, we get all the subsets of the set header,
     * then, we add basePat to each subset.
     * @param allFrequentItems to store all frequent items.
     * @param basePat the base pattern
     * @param header the headerTable.keySet().
     */
    private void obtainFrequentItems(Set<Set<String>> allFrequentItems, String basePat, Set<String> header) {
        List<String> list = new ArrayList<>(header);
        int len = list.size();
        for (int i = 0; i < (1 << len); i ++) {
            Set<String> subSet = new HashSet<>();
            for (int j = 0; j < len; j ++) {
                if ((i &(1 << j)) != 0) {
                    subSet.add(list.get(j));
                }
            }
            subSet.add(basePat);
            allFrequentItems.add(subSet);
        }
    }
}
