package com.xiaotong.acs.function.query;

import com.xiaotong.acs.function.fpgrowth.FPGrowth;
import com.xiaotong.acs.function.fpgrowth.FPTree;
import com.xiaotong.acs.function.fpgrowth.FPTreeNode;
import com.xiaotong.acs.function.fpgrowth.HeaderTable;
import com.xiaotong.acs.function.graph.AdjacencyList;
import com.xiaotong.acs.function.graph.Vertex;
import com.xiaotong.acs.function.index.AUF;
import com.xiaotong.acs.function.index.TNode;
import com.xiaotong.acs.function.kcore.Decomposition;
import com.xiaotong.acs.function.kcore.NullDegException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

class Flag {
    boolean sign = false;
}

@Slf4j
public class DecQuery {
    private final AdjacencyList graph;
    private final Decomposition de;
    private final TNode root;

    public DecQuery(AdjacencyList graph, Decomposition de, TNode root) {
        this.graph = graph;
        this.de = de;
        this.root = root;
    }

    /**
     * Query algorithm
     * @param q query vertex
     * @param k a positive integer, it is the minimum support also.
     * @param string a keyword set
     * @return all the qualified community that we found.
     * @throws NullDegException when have not run the coresDecomposition.
     */
    public Map<Set<String>, Set<Integer>> query(int q, int k, String string) throws NullDegException, NullSubtreeException, ErrorInputException {
        if (this.de.getDeg()[q] < k)
            throw new ErrorInputException("The input parameter is incorrect. Please re-enter again!");
        String[] split = string.split(",");
        Set<String> S = new HashSet<>(Arrays.asList(split));
        // Get frequent itemsets, and group these sets by its size.
        List<Set<String>> itemsets = getItemsets(q, k, S);
        Map<Set<String>, Integer> itemsetsMap = new HashMap<>();
        for (Set<String> itemset : itemsets) {
            int frequency = 0;
            if (itemsetsMap.containsKey(itemset)) {
                frequency = itemsetsMap.get(itemset);
            }
            itemsetsMap.put(itemset, frequency + 1);
        }
        FPTree fpTree = new FPTree();
        FPTreeNode fpTreeRoot = fpTree.buildFPTree(itemsetsMap, k);
        Map<String, HeaderTable> headerTable = fpTree.getHeaderTable();
        FPGrowth mineFrequency = new FPGrowth();
        Set<Set<String>> frequentItems = mineFrequency.mineTree(headerTable, k);
        if (frequentItems.size() == 0) {
            throw new ErrorInputException("The input parameter is incorrect. Please re-enter again!");
        }
        Map<Integer, Set<Set<String>>> groupItems = groupFrequentItems(frequentItems);
        // Find the subtre, which contains the query vertex and the core-number of its root is k.
        TNode rootOfCoreK = findSubtreeRk(this.root, q, k);
        log.info("Find Subtree Root");
        // Find vertices which share keywords with q in the subtree. And group by the number of sharing keyword with S.
        Map<Integer, Set<Integer>> verticesSet = new HashMap<>();
        obtainVerticesSet(rootOfCoreK, S, verticesSet);
        log.info("Obtain Vertices Set");
        // Find the object communties.
        int var = Collections.max(verticesSet.keySet());
        int loop = Collections.max(groupItems.keySet());
        Set<Integer> bigVerticesSet = new HashSet<>();
        for (int i = loop ;i <= var; i++) {
            if (verticesSet.containsKey(i)) {
                bigVerticesSet.addAll(verticesSet.get(i));
            }
        }
        Map<Set<String>, Set<Integer>> allCommunities = new HashMap<>();
        while (loop >= 1) {
            for (Set<String> stringSet : groupItems.get(loop)) {
                Set<Integer> community = new HashSet<>();
                community.add(q);
                for (int vertex : bigVerticesSet) {
                    if (vertex == q) {
                        continue;
                    }
                    Set<String> keywords = new HashSet<>(this.graph.vexs.get(vertex).keywords);
                    if (!keywords.addAll(stringSet)) {
                        community.add(vertex);
                    }
                }
                if (community.size() != 1) {
                    allCommunities.put(stringSet, community);
                }
            }
            if (allCommunities.size() == 0) {
                loop --;
                if (verticesSet.containsKey(loop)) {
                    bigVerticesSet.addAll(verticesSet.get(loop));
                }
            } else {
                break;
            }
        }
        return getConnectedCommunities(allCommunities, this.graph, q);
    }

    /**
     * For each vertex of q's neighbors(whose core number is at least k),
     * we only select the keywords that are contained by S. Then we use these
     * selected keywords to form an itemset, in which each item is a keyword.
     * Finally, we obtain a list of itemsets.
     * @param q A vertex q ∈ V.
     * @param k A positive integer k.
     * @param S A set of keywordk S ⊆ W(q).
     * @return The list of itemsets.
     * @throws NullDegException if array deg is null.
     */
    private List<Set<String>> getItemsets(int q, int k, Set<String> S) throws NullDegException {
        ArrayList<Integer> neighbors = this.graph.getNeighbors(this.graph.vexs.get(q));
         int[] deg = this.de.getDeg();
         neighbors.removeIf(i -> deg[i] < k);
        neighbors.add(q);
        List<Set<String>> itemsets = new ArrayList<>();
        for (Integer i : neighbors) {
            Vertex vertex = this.graph.vexs.get(i);
            Set<String> commonKeywords = new HashSet<>(vertex.keywords);
            commonKeywords.retainAll(S);
            if (commonKeywords.isEmpty()) {
                continue;
            }
            itemsets.add(commonKeywords);
        }
        return itemsets;
    }

    /**
     * Group the frequent items by its size.
     * @param frequentItems the frequent items
     * @return the map, whose Key is the size and Value is the frequent itemsets of this size.
     */
    private Map<Integer, Set<Set<String>>> groupFrequentItems(Set<Set<String>> frequentItems) {
        Map<Integer, Set<Set<String>>> groupItems = new HashMap<>();
        for (Set<String> item : frequentItems) {
            int size = item.size();
            if (groupItems.containsKey(size)) {
                groupItems.get(size).add(item);
            } else {
                Set<Set<String>> temp = new HashSet<>();
                temp.add(item);
                groupItems.put(size, temp);
            }
        }
        return groupItems;
    }

    /**
     * Find the subtree root node Rk.
     * @param root the root of CL-tree
     * @param q the query vertex q
     * @param k the positive integer k
     * @return the root of subtree
     * @throws NullSubtreeException when not found the subtree.
     */
    private TNode findSubtreeRk(TNode root, int q, int k) throws NullSubtreeException {
        if (root == null) {
            return null;
        }

        TNode temp = null;
        Flag flag = new Flag();

        if (root.coreNum == k) {
            temp = root;
        }
        for (int i : root.nodeList) {
            if (i == q) {
                flag.sign = true;
                break;
            }
        }
        for (TNode node : root.childList) {
            temp = traverse(node, q, k, temp, flag);
        }
        if (temp == null) {
            throw new NullSubtreeException("No matching subtree found!");
        }
        return temp;
    }

    private TNode traverse(TNode root, int q, int k, TNode temp, Flag flag) {
        if (root == null || flag.sign) {
            return temp;
        }
        if (root.coreNum == k) {
            temp = root;
        }
        for (int i : root.nodeList) {
            if (i == q) {
                flag.sign = true;
                break;
            }
        }
        for (TNode node : root.childList) {
            temp = traverse(node, q, k, temp, flag);
        }
        return temp;
    }

    /**
     * Find vertices which share keywords with q in the subtree.
     * And group by the number of sharing keyword with S.
     * @param root the root of the subtree
     * @param S the query keywords set
     * @param verticesSet the vertices Map.
     */
    private void obtainVerticesSet(TNode root, Set<String> S, Map<Integer, Set<Integer>> verticesSet) {
        if (root == null) {
            return;
        }
        Deque<TNode> stack = new LinkedList<>();
        stack.push(root);
        while (!stack.isEmpty()){
            TNode node = stack.pop();
            Set<Integer> nodeList = node.nodeList;
            for (int vertex : nodeList) {
                Set<String> keywords = new HashSet<>(this.graph.vexs.get(vertex).keywords);
                keywords.retainAll(S);
                int size = keywords.size();
                if (size == 0) {
                    continue;
                }
                Set<Integer> value = new HashSet<>();
                if (verticesSet.containsKey(size)) {
                    value = verticesSet.get(size);
                }
                value.add(vertex);
                verticesSet.put(size, value);
            }
            for (TNode child : node.childList) {
                stack.push(child);
            }
        }
    }

    /**
     * Get Gk[S'], which is the largest connected subgraph of G
     * s.t. q is included in Gk[S'] and every vertex in Gk[S'] its deg >= k, meanwhile S' ⊆ W(v)
     * @param allCommunities all possible communities, however the vertices in each community may not connected.
     * @param graph the graph G
     * @param q the query vertex
     * @return Gk[S']
     */
    private Map<Set<String>, Set<Integer>> getConnectedCommunities(Map<Set<String>, Set<Integer>> allCommunities, AdjacencyList graph, int q) {
        Map<Set<String>, Set<Integer>> allConnectedCommunitits = new HashMap<>();
        for (Set<String> keywords : allCommunities.keySet()) {
            Set<Integer> community = allCommunities.get(keywords);
            AUF auf = new AUF(graph.vertexNum);
            for (int vertex : community) {
                ArrayList<Integer> neighbors = graph.getNeighbors(graph.vexs.get(vertex));
                for (int neighbor : neighbors) {
                    if (community.contains(neighbor)) {
                        auf.unionElements(vertex, neighbor);
                    }
                }
            }
            int root = auf.find(q);
            Set<Integer> temp = new HashSet<>(community);
            for (int vertex : community) {
                if (auf.find(vertex) != root) {
                    temp.remove(vertex);
                }
            }
            if (temp.size() > 1) {
                allConnectedCommunitits.put(keywords, temp);
            }
        }
        return allConnectedCommunitits;
    }
}
