package com.xiaotong.acs.domain;

import com.xiaotong.acs.function.jsonread.GraphData;
import com.xiaotong.acs.function.worldcloud.CloudData;
import lombok.Data;

import java.util.List;

@Data
public class InitResult {
    private List<CloudData> cloudDatas;
    private GraphData graphData;

    public static InitResult from(List<CloudData> cloudDatas, GraphData graphData) {
        InitResult initResult = new InitResult();
        initResult.setCloudDatas(cloudDatas);
        initResult.setGraphData(graphData);
        return initResult;
    }
}
