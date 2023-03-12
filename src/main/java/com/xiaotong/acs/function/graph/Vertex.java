package com.xiaotong.acs.function.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Vertex {
    public String id;
    public Set<String> keywords = new HashSet<>(); // Store vertex information
    public EdgeNode firstEdge; // The first edge from the adjacency table

    public Vertex(String id, String rawData) {
        this.id = id;
        String[] split = rawData.split(", ");
        this.keywords.addAll(Arrays.asList(split));
        this.firstEdge = null;
    }
}
