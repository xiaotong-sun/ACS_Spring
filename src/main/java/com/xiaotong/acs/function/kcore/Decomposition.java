/**
 * An O(m) Algorithm for Cores com.Decomposition of Networks.
 */

package com.xiaotong.acs.function.kcore;


import com.xiaotong.acs.function.graph.AdjacencyList;

import java.util.ArrayList;

public class Decomposition{
    private final int n;
    private int[] deg;
    private final AdjacencyList graph;
    private int maxCore = -1;
    private int minCore = Integer.MAX_VALUE;

    public Decomposition(AdjacencyList graph) {
        this.graph = graph;
        this.n = graph.vertexNum;
    }

    /**
     * The Cores Algorithm for Simple Undirected Graphs.
     * @return The array which contains the core number for each vertex in Graph.
     */
    public int[] coresDecomposition() {
        this.deg = new int[this.n];
        int[] pos = new int[this.n];
        int[] vert = new int[this.n];
        int md = 0;

        // Compute this.degree for each vertex v in graph g and store it into array this.deg.
        for (int v = 0; v < this.n; v ++) {
            ArrayList<Integer> neighbors = this.graph.getNeighbors(this.graph.vexs.get(v));
            int d = neighbors.size();
            this.deg[v] = d;
            md = Math.max(d, md);
        }

        int[] bin = new int[md + 1];

        // Count how many verteices will be in each bin.
        for (int v = 0; v < this.n; v ++) {
            int index = this.deg[v];
            int value = bin[index];
            bin[index] = value + 1;
        }

        // Determine starting positions of bins in array vert.
        int start = 0;
        for (int d = 0; d <= md; d ++) {
            int temp = bin[d];
            bin[d] = start;
            start += temp;
        }

        // Put vertices of graph G into array vert.
        for (int v = 0; v < this.n; v ++) {
            int index = this.deg[v];
            int value = bin[index];
            pos[v] = value;
            vert[pos[v]] = v;
            bin[index] = value + 1;
        }

        // Recover starting positions of the bins.
        for (int d = md; d >= 1; d --) {
            bin[d] = bin[d - 1];
        }

        // Cores decomposition.
        for (int i = 0; i < this.n; i ++) {
            int v = vert[i];
            ArrayList<Integer> neighbors = this.graph.getNeighbors(this.graph.vexs.get(v));
            for (int neighbor: neighbors) {
                if (this.deg[neighbor] > this.deg[v]) {
                    int du = this.deg[neighbor];
                    int pu = pos[neighbor];
                    int pw = bin[du];
                    int w = vert[pw];
                    if (neighbor != w) {
                        pos[neighbor] = pw;
                        vert[pu] = w;
                        pos[w] = pu;
                        vert[pw] = neighbor;
                    }
                    bin[du] ++;
                    this.deg[neighbor] --;
                }
            }
        }

        // obtain max and min core number.
        for (int i = 0; i < this.n; i ++) {
            this.maxCore = Math.max(this.deg[i], this.maxCore);
            this.minCore = Math.min(this.deg[i], this.minCore);
        }
        return this.deg;
    }

    /**
     * Obtain the max core number.
      * @return Max core number.
     */
    public int obtainMaxCore() {
        return this.maxCore;
    }

    /**
     * Obtain the min core number.
     * @return Min core number.
     */
    public int obtainMinCore() {
        return this.minCore;
    }

    /**
     * Get the deg array.
     * @return the deg array.
     */
    public int[] getDeg() throws NullDegException {
        if (this.deg == null) {
            throw new NullDegException("Array deg is null, please run coresDecomposition first.");
        } else {
            return this.deg;
        }
    }
}