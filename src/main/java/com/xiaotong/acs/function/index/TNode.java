/**
 * The Cl-tree node.
 */
package com.xiaotong.acs.function.index;

import java.util.*;

public class TNode {
    public int coreNum;
    public Set<Integer> nodeList;
    public List<TNode> childList;
    public Map<String, Set<Integer>> invertedList;

    public TNode(int coreNum, Set<Integer> nodeList) {
        this.coreNum = coreNum;
        this.nodeList = nodeList;
        this.childList = new ArrayList<>();
    }

    public static void print(TNode root) {
        printTree(root, "\t");
    }

    private static void printTree(TNode root, String start) {
        if (root == null) {
            return;
        }
        String mid = start.substring(0, start.lastIndexOf("\t")) +  "●-";
        System.out.println(mid + root.coreNum + ": " + root.nodeList + "---->" + root.invertedList);
        if (root.childList == null) {
            return;
        }
        for (TNode node : root.childList) {
            printTree(node, start + "\t");
        }
    }

    public static void traverseTree(TNode root) {
        if (root == null) {
            return;
        }
        Deque<TNode> stack = new LinkedList<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TNode node = stack.pop();
            for (int i = 0; i < node.childList.size(); i ++) {
                stack.push(node.childList.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return "TNode{" +
                "coreNum=" + coreNum +
                ", nodeList=" + nodeList +
                ", childList=" + childList +
                ", invertedList=" + invertedList +
                '}';
    }
}