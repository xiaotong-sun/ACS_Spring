package com.xiaotong.acs.controller;

import com.alibaba.fastjson2.JSON;
import com.google.gson.Gson;
import com.xiaotong.acs.domain.InitResult;
import com.xiaotong.acs.domain.QueryParam;
import com.xiaotong.acs.domain.QueryResult;
import com.xiaotong.acs.domain.Result;
import com.xiaotong.acs.function.exception.ErrorInputException;
import com.xiaotong.acs.function.exception.NullDegException;
import com.xiaotong.acs.function.exception.NullSubtreeException;
import com.xiaotong.acs.function.graph.AdjacencyList;
import com.xiaotong.acs.function.graph.EdgeNode;
import com.xiaotong.acs.function.graph.Vertex;
import com.xiaotong.acs.function.index.AdvancedIndex;
import com.xiaotong.acs.function.index.TNode;
import com.xiaotong.acs.function.jsonread.Edge;
import com.xiaotong.acs.function.jsonread.GraphData;
import com.xiaotong.acs.function.jsonread.Node;
import com.xiaotong.acs.function.kcore.Decomposition;
import com.xiaotong.acs.function.query.DecQuery;
import com.xiaotong.acs.function.worldcloud.CloudData;
import com.xiaotong.acs.function.worldcloud.GetCloudData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/graph")
public class SearchController {

    private static DecQuery decQuery;
    private static Map<String, Integer> nodeToIndex;
    private static String graphJson;

    @PostMapping("/init")
    public static Result init(MultipartFile file) {
        Result result;
        // 加载上传的json文件
        try {
            graphJson = new String(file.getBytes());
//            log.info("graph json = \n{}", graphJson);
            result = initGraph();
        } catch (IOException e) {
            e.printStackTrace();
            result = Result.error("文件上传失败");
        } catch (NullDegException e) {
            result = Result.error("NullDegException");
        }
        return result;
    }

    public static Result initGraph() throws NullDegException {
        GraphData graphData = JSON.parseObject(graphJson, GraphData.class);
        List<Node> nodes = graphData.getNodes();
        List<Edge> edges = graphData.getEdges();

        int vertexNum = nodes.size();
        nodeToIndex = new HashMap<>();
        Map<String, Integer> cloudMap = new HashMap<>();
        AdjacencyList graph = new AdjacencyList(vertexNum);
        for (int i = 0; i < vertexNum; i++) {
            String id = nodes.get(i).getId();
            String keywords = nodes.get(i).getKeywords();
            GetCloudData.getData(cloudMap, keywords);
            graph.insertVertex(new Vertex(id, keywords));
            nodeToIndex.put(id, i);
        }
        for (Edge edge : edges) {
            graph.insertEdge(new EdgeNode(nodeToIndex.get(edge.getSource()), nodeToIndex.get(edge.getTarget())));
        }
        Decomposition de = new Decomposition(graph);
        int[] deg = de.coresDecomposition();

        for (int i = 0; i < deg.length; i++) {
            nodes.get(i).setCoreNum(deg[i]);
        }

        Gson gson = new Gson();
        graphJson = gson.toJson(graphData);

        log.info("Core Decomposition Finish!");
        AdvancedIndex adv = new AdvancedIndex();
        TNode root = adv.buildIndex(graph, de);
        log.info("Build Index Finish");
/*
        TNode.traverseTree(root);
        TNode.print(root);
*/
        decQuery = new DecQuery(graph, de, root);

        // obtain word cloud data
        List<CloudData> cloudData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cloudMap.entrySet()) {
            CloudData item = new CloudData();
            item.setKeyword(entry.getKey());
            item.setValue(entry.getValue());
            cloudData.add(item);
        }
        log.info("Word Cloud Finish");
        log.info("Init Finish");
        return Result.ok(InitResult.from(cloudData, graphData));
    }


    @PostMapping("/search")
    public static Result search(@RequestBody QueryParam param) throws ErrorInputException, NullSubtreeException, NullDegException {
        if (decQuery == null) {
            return Result.error("图未初始化");
        }
        log.info("Begin Searching");
        GraphData copyGraph = JSON.parseObject(graphJson, GraphData.class);
        Map<Set<String>, Set<Integer>> finalResult = decQuery.query(nodeToIndex.get(param.getVertex()), param.getCoreK(), param.getKeywords());
        List<String> communityKeywords = new ArrayList<>();

        int clusterID = 1;
        for (Map.Entry<Set<String>, Set<Integer>> entry : finalResult.entrySet()) {
            Set<String> keywords = entry.getKey();
            Set<Integer> vertices = entry.getValue();

            communityKeywords.add(keywords.toString());
            for (Integer i : vertices) {
                copyGraph.getNodes().get(i).getCluster().add(clusterID);
            }
            clusterID++;
        }
        log.info("Finish Searching");
        return Result.ok(QueryResult.from(finalResult, communityKeywords, copyGraph));
    }
}
