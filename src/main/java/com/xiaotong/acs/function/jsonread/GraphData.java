package com.xiaotong.acs.function.jsonread;

import lombok.Data;

import java.util.List;

@Data
public class GraphData {
    private List<Node> nodes;
    private List<Edge> edges;
}
