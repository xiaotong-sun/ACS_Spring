package com.xiaotong.acs.function.fpgrowth;

public class HeaderTable {
    public int frequency;
    public FPTreeNode node;

    public HeaderTable(int frequency, FPTreeNode node) {
        this.frequency = frequency;
        this.node = node;
    }
}
