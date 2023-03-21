package com.xiaotong.acs.domain;

import com.xiaotong.acs.function.jsonread.GraphData;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class QueryResult {
    private Map<Set<String>, Set<Integer>> finalResult;
    private List<String> communityKeywords;
    private GraphData graphData;

    public static QueryResult from(Map<Set<String>, Set<Integer>> finalResult, List<String> communityKeywords, GraphData graphData){
        QueryResult queryResult = new QueryResult();
        queryResult.setGraphData(graphData);
        queryResult.setFinalResult(finalResult);
        queryResult.setCommunityKeywords(communityKeywords);
        return queryResult;
    }
}
