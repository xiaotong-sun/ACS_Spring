package com.xiaotong.acs.function.worldcloud;

import java.util.Map;

public class GetCloudData {
    public static void getData(Map<String, Integer> cloudMap, String keywords) {
        String[] split = keywords.split(", ");
        for (String s : split) {
            int value = 0;
            if (cloudMap.containsKey(s)) {
                value = cloudMap.get(s);
            }
            value ++;
            cloudMap.put(s, value);
        }
    }
}
