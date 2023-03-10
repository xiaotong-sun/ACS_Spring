/**
 * The advanced method, which is used to build the CL-tree index.
 */
package com.myfunction.acs.function.index;

import com.myfunction.acs.function.graph.AdjacencyList;
import com.myfunction.acs.function.graph.Vertex;
import com.myfunction.acs.function.kcore.Decomposition;

import java.util.*;

public class AdvancedIndex {
    /**
     * The advanced method builds the
     * CL-tree level by lever in a bottom-up manner
     * @param graph The Graph which is stored in adjacency lsit.
     * @return The root of the CL-tree.
     */
    public TNode buildIndex(AdjacencyList graph) {
        Decomposition de = new Decomposition(graph);
        int[] core = de.coresDecomposition();
        int size = graph.vertexNum;
        AUF auf = new AUF(size);
        int k = de.obtainMaxCore();
        int minCore = de.obtainMinCore();
        Map<Integer, List<Integer>> mapV = getMapV(core, size);
        Map<Integer, TNode> map0 = new HashMap<>();
        // build index
        while (k >= minCore) {
            List<Integer> listV = mapV.get(k);
            // union the connected vertices in listV.
            Map<Integer, Set<Integer>> anchorTemp = new HashMap<>();
            for (int i : listV) {
                ArrayList<Integer> neighbors = graph.getNeighbors(graph.vexs.get(i));
                for (int neighbor : neighbors) {
                    if (core[neighbor] == core[i]) {
                        auf.unionElements(neighbor, i);
                    } else if (core[neighbor] > core[i]) {
                        int anchorN = auf.anchor[auf.find(neighbor)];
                        auf.unionElements(neighbor, i);
                        Set<Integer> value = new HashSet<>();
                        int neighborRoot = auf.find(neighbor);
                        if (anchorTemp.containsKey(neighborRoot)) {
                            value = anchorTemp.get(neighborRoot);
                        }
                        value.add(anchorN);
                        anchorTemp.put(neighborRoot, value);
                    }
                }
            }
            // compute the connected components
            Map<Integer, Set<Integer>> map1 = new HashMap<>();
            for (int i : listV) {
                int root = auf.find(i);
                Set<Integer> value = new HashSet<>();
                if (map1.containsKey(root)) {
                    value = map1.get(root);
                }
                value.add(i);
                map1.put(root, value);
            }
            // Create a TNode for each connected component, and then add the TNode to the CLTree.
            Collection<Set<Integer>> connectedComponents = map1.values();
            for (Set<Integer> component : connectedComponents) {
                TNode node1 = new TNode(k, component);
                // build invertde list for this tree node.
                node1.invertedList = buildInvertedList(component, graph.vexs);
                for (int v : component) {
                    map0.put(v, node1);
                }
                Iterator<Integer> iterator = component.iterator();
                int vertex = iterator.next();
                int vRoot = auf.find(vertex);
                Set<Integer> anchorSet = anchorTemp.get(vRoot);
                if (anchorSet == null) {
                    continue;
                }
                for (Integer anchor : anchorSet) {
                    node1.childList.add(map0.get(anchor));
                }
                auf.anchor[vRoot] = vertex;
            }
            k --;
        }
        // build root node.
        Set<Integer> core0Set = new HashSet<>();
        for (int i = 0; i < size; i ++) {
            if (core[i] == 0) {
                core0Set.add(i);
            }
        }
        TNode root = new TNode(0, core0Set);
        root.invertedList = buildInvertedList(core0Set, graph.vexs);
        Set<Integer> anchorTemp = new HashSet<>();
        for (int i = 0; i < size; i ++) {
            if (core0Set.contains(i)) {
                continue;
            }
            anchorTemp.add(auf.anchor[auf.find(i)]);
        }
        for (int i : anchorTemp) {
            root.childList.add(map0.get(i));
        }
        return root;
    }

    /**
     * Group all the vertices into sets, where set
     * V_k contains vertices with core numbers being exactly k.
     * @param core The set that contains core number of each vertex.
     * @param size The size of the set core.
     * @return A map, whose key is the core-number and value is the list of vertices.
     */
    private Map<Integer, List<Integer>> getMapV(int[] core, int size) {
        Map<Integer, List<Integer>> mapV = new HashMap<>();
        for (int i = 0; i < size; i ++) {
            List<Integer> listV = new ArrayList<>();
            if (mapV.containsKey(core[i])) {
                listV = mapV.get(core[i]);
            }
            listV.add(i);
            mapV.put(core[i], listV);
        }
        return mapV;
    }

    /**
     * Build the inverted list.
     * @param nodeList The list of vertices, which is contained in the Tree Node.
     * @param vexs The arraylist that contains all the vertices of the Graph.
     * @return A map of <key, value> pairs, where the key is a keyword contained by
     * vertices in vertexSet and the value is the list of vertices in vertexSet containing key;
     */
    private Map<String, Set<Integer>> buildInvertedList(Set<Integer> nodeList, ArrayList<Vertex> vexs) {
        Map<String, Set<Integer>> invertedList = new HashMap<>();
        for (int i : nodeList) {
            Set<String> keywords = vexs.get(i).data;
            for (String keyword : keywords) {
                Set<Integer> vertexSet = new HashSet<>();
                if (invertedList.containsKey(keyword)) {
                    vertexSet = invertedList.get(keyword);
                }
                vertexSet.add(i);
                invertedList.put(keyword, vertexSet);
            }
        }
        return invertedList;
    }
}