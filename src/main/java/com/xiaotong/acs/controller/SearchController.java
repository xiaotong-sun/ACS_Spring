package com.xiaotong.acs.controller;

import com.google.gson.Gson;
import com.xiaotong.acs.domain.QueryResult;
import com.xiaotong.acs.function.graph.AdjacencyList;
import com.xiaotong.acs.function.graph.EdgeNode;
import com.xiaotong.acs.function.graph.Vertex;
import com.xiaotong.acs.function.index.AdvancedIndex;
import com.xiaotong.acs.function.index.TNode;
import com.xiaotong.acs.function.jsonread.Edge;
import com.xiaotong.acs.function.jsonread.GraphData;
import com.xiaotong.acs.function.jsonread.Node;
import com.xiaotong.acs.function.jsonread.ReadJsonFile;
import com.xiaotong.acs.function.kcore.Decomposition;
import com.xiaotong.acs.function.kcore.NullDegException;
import com.xiaotong.acs.function.query.DecQuery;
import com.xiaotong.acs.function.query.ErrorInputException;
import com.xiaotong.acs.function.query.NullSubtreeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/graph")
public class SearchController {
    private static DecQuery decQuery;
    private static Map<String, Integer> nodeToindex;
    private static String graphJson;

    @RequestMapping("/init")
    public static void init() throws FileNotFoundException {
        GraphData graphData = ReadJsonFile.getGraphData();
        List<Node> nodes = graphData.getNodes();
        List<Edge> edges = graphData.getEdges();

        Gson gson = new Gson();
        graphJson = gson.toJson(graphData);

        int vertexNum = nodes.size();
        nodeToindex = new HashMap<>();
        AdjacencyList graph = new AdjacencyList(vertexNum);
        for (int i = 0; i < vertexNum; i ++) {
            String id = nodes.get(i).getId();
            String keywords = nodes.get(i).getKeywords();
            graph.insertVertex(new Vertex(id, keywords));
            nodeToindex.put(id, i);
        }
        for (Edge edge : edges) {
            graph.insertEdge(new EdgeNode(nodeToindex.get(edge.getSource()), nodeToindex.get(edge.getTarget())));
        }

        Decomposition de = new Decomposition(graph);
        int[] deg = de.coresDecomposition();
        for (int i = 1; i <= vertexNum; i++) {
            System.out.printf("%2d  ", i);
        }
        System.out.println();
        for (int i : deg) {
            System.out.printf("%2d  ", i);
        }

        System.out.println();
        AdvancedIndex adv = new AdvancedIndex();
        TNode root = adv.buildIndex(graph);
        TNode.print(root);
        decQuery = new DecQuery(graph, de, root);
    }

    @RequestMapping("/search/{queryVertex}/{queryK}/{queryS}")
    public static QueryResult search(
            @PathVariable String queryVertex,
            @PathVariable int queryK,
            @PathVariable String queryS) throws ErrorInputException, NullSubtreeException, NullDegException, FileNotFoundException {
        if (decQuery == null) {
            init();
        }
        Gson gson = new Gson();
        GraphData copyGraph = gson.fromJson(graphJson, GraphData.class);

        Map<Set<String>, Set<Integer>> finalResult = decQuery.query(nodeToindex.get(queryVertex), queryK, queryS);
        List<String> communityKeywords = new ArrayList<>();

        int clusterID = 1;
        for (Map.Entry<Set<String>, Set<Integer>> entry : finalResult.entrySet()) {
            Set<String> keywords = entry.getKey();
            Set<Integer> vertices = entry.getValue();

            communityKeywords.add(keywords.toString());
            for (Integer i : vertices) {
                copyGraph.getNodes().get(i).setCluster(Integer.toString(clusterID));
            }
            clusterID ++;
        }
        return QueryResult.from(communityKeywords, copyGraph);
    }
}
