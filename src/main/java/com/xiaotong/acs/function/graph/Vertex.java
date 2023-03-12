package com.xiaotong.acs.function.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Vertex {
    public Set<String> data = new HashSet<>(); // Store vertex information
    public EdgeNode firstEdge; // The first edge from the adjacency table

    public Vertex(String rawData) {
        String[] split = rawData.split(", ");
        this.data.addAll(Arrays.asList(split));
        this.firstEdge = null;
    }
}
