package com.xiaotong.acs.function.fpgrowth;

import java.util.ArrayList;
import java.util.List;

public class FPTreeNode {
    public String keyword;
    public int frequency;
    public FPTreeNode nodeLink;
    public FPTreeNode parent;
    public List<FPTreeNode> childList;

    public FPTreeNode(String keyword, int frequency, FPTreeNode parent) {
        this.keyword = keyword;
        this.frequency = frequency;
        this.parent = parent;
        this.nodeLink = null;
        this.childList = new ArrayList<>();
    }

    public static void print(FPTreeNode root) {
        printTree(root, "\t");
    }

    private static void printTree(FPTreeNode root, String start) {
        if (root == null) {
            return;
        }
        String mid = start.substring(0, start.lastIndexOf("\t")) +  "●-";
        System.out.println(mid + root.keyword + ": " + root.frequency);
        if (root.childList == null) {
            return;
        }
        for (FPTreeNode node : root.childList) {
            printTree(node, start + "\t");
        }
    }
}
