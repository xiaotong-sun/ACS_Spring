package com.xiaotong.acs.controller;

import com.xiaotong.acs.function.graph.AdjacencyList;
import com.xiaotong.acs.function.graph.EdgeNode;
import com.xiaotong.acs.function.graph.Vertex;
import com.xiaotong.acs.function.index.AdvancedIndex;
import com.xiaotong.acs.function.index.TNode;
import com.xiaotong.acs.function.kcore.Decomposition;
import com.xiaotong.acs.function.kcore.NullDegException;
import com.xiaotong.acs.function.query.DecQuery;
import com.xiaotong.acs.function.query.ErrorInputException;
import com.xiaotong.acs.function.query.NullSubtreeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/graph")
public class SearchController {
    private static DecQuery decQuery;

    @RequestMapping("/init")
    public static void init() {
        int vertexNum = 10;
        AdjacencyList graph = new AdjacencyList(vertexNum);
        graph.insertVertex(new Vertex("w, x, y"));
        graph.insertVertex(new Vertex("x"));
        graph.insertVertex(new Vertex("x, y"));
        graph.insertVertex(new Vertex("x, y, z"));
        graph.insertVertex(new Vertex("x, y, z"));
        graph.insertVertex(new Vertex("y"));
        graph.insertVertex(new Vertex("x, y"));
        graph.insertVertex(new Vertex("y, z"));
        graph.insertVertex(new Vertex("x"));
        graph.insertVertex(new Vertex("x"));

        graph.insertEdge(new EdgeNode(0, 1));
        graph.insertEdge(new EdgeNode(0, 2));
        graph.insertEdge(new EdgeNode(0, 3));
        graph.insertEdge(new EdgeNode(0, 4));
        graph.insertEdge(new EdgeNode(1, 4));
        graph.insertEdge(new EdgeNode(4, 6));
        graph.insertEdge(new EdgeNode(3, 1));
        graph.insertEdge(new EdgeNode(2, 1));
        graph.insertEdge(new EdgeNode(3, 2));
        graph.insertEdge(new EdgeNode(3, 5));
        graph.insertEdge(new EdgeNode(7, 8));


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
    public static Map<Set<String>, Set<Integer>> search(
            @PathVariable int queryVertex,
            @PathVariable int queryK,
            @PathVariable String queryS) throws ErrorInputException, NullSubtreeException, NullDegException {
        if (decQuery == null) {
            init();
        }
        return decQuery.query(queryVertex, queryK, queryS);
    }
}
