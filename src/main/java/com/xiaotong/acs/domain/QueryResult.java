package com.xiaotong.acs.domain;

import com.xiaotong.acs.function.jsonread.GraphData;
import lombok.Data;

import java.util.List;

@Data
public class QueryResult {
    private List<String> communityKeywords;
    private GraphData graphData;

    public static QueryResult from(List<String> communityKeywords, GraphData graphData){
        QueryResult queryResult = new QueryResult();
        queryResult.setGraphData(graphData);
        queryResult.setCommunityKeywords(communityKeywords);
        return queryResult;
    }
}
