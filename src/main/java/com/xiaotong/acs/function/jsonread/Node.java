package com.xiaotong.acs.function.jsonread;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Node {
    private String id;
    private String label;
    private String keywords;
    private int coreNum;
    private Set<Integer> cluster = new HashSet<>();
}
