package com.xiaotong.acs.function.graph;

public class EdgeNode {
    int vIndex1; // The node index at the end of the edge
    public int vIndex2; // The node index at the another end of the edge. Besides, it is the node's index in AdjTable also.
    public EdgeNode next;

    public EdgeNode(int vIndex1, int vIndex2) {
        this.vIndex1 = vIndex1;
        this.vIndex2 = vIndex2;
        this.next = null;
    }
}
