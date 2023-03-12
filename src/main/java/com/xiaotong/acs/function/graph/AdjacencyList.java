/**
 *  Store graph data in an adjacency table structur.
 *  Create the adjacency table： com.AdjacencyList graph = new com.AdjacencyList(vertexNum, edgeNum);
 *  Insert the vertex： graph.insertVertex(new com.Vertex(data));
 *  Insert the edge： graph.insertEdge(new com.EdgeNode(vIndex1, vIndex2));
 */

package com.xiaotong.acs.function.graph;

import java.util.ArrayList;

public class AdjacencyList {

    public ArrayList<Vertex> vexs;
    public int vertexNum;
    boolean[] visited;

    public AdjacencyList(int vertexNum) {
        this.vertexNum = vertexNum;
        this.vexs = new ArrayList<>(vertexNum);
        this.visited = new boolean[vertexNum];
    }

    /**
     * Insert the vertex.
     * For each vertex, we should first create the object as follows:
     * com.Vertex vertex = new com.Vertex("data");
     * @param vertex The vertex to be inserted.
     */
    public void insertVertex(Vertex vertex) {
        vexs.add(vertex);
    }

    /**
     * Insert the edge.
     * For each edge, we should know its first node and second node,
     * which are connected by this edge.
     * @param edge The edge to be inserted.
     */
    public void insertEdge(EdgeNode edge) {
        int vIndex1 = edge.vIndex1;
        int vIndex2 = edge.vIndex2;
        Vertex vex1 = vexs.get(vIndex1);
        Vertex vex2 = vexs.get(vIndex2);

        edge.next = vex1.firstEdge;
        vex1.firstEdge = edge;

        EdgeNode temp = new EdgeNode(vIndex2, vIndex1);
        temp.next = vex2.firstEdge;
        vex2.firstEdge = temp;
    }

    /**
     * Return the index of com.Vertex u if u is a not yet
     * visited neighbor of com.Vertex v in graph G.
     * @param vertex The vertex
     * @return The index of vertex's all neighbors.
     */
    public ArrayList<Integer> getNeighbors(Vertex vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        EdgeNode node = vertex.firstEdge;
        while (node != null) {
            neighbors.add(node.vIndex2);
            node = node.next;
        }
        return neighbors;
    }

    /**
     * Show the adjacency table
     */
    public void show() {
        for (int i = 0; i < vertexNum; i ++) {
            Vertex vex = vexs.get(i);
            System.out.print("[" + vex.keywords + "]->");
            EdgeNode node = vex.firstEdge;
            while (node != null) {
                System.out.print(vexs.get(node.vIndex2).keywords + "(" + node.vIndex2 + ")->");
                node = node.next;
            }
            System.out.print("null");
            System.out.println();
        }
    }
}