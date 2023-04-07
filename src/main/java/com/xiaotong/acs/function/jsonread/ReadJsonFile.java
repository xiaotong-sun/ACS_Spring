package com.xiaotong.acs.function.jsonread;

import com.alibaba.fastjson2.JSON;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ReadJsonFile {
    /**
     * Get the graph data from Json file.
     * @return the nodes and the edges.
     * @throws FileNotFoundException when file not found.
     */
    public static GraphData getGraphData() throws FileNotFoundException {
        Gson gson = new Gson();
//        FileReader reader = new FileReader("D:\\javaproject\\ACS\\src\\main\\java\\com\\xiaotong\\acs\\data\\graphData2.json");
//        FileReader reader = new FileReader("D:\\javaproject\\ACS\\src\\main\\java\\com\\xiaotong\\acs\\data\\data.json");
//        FileReader reader = new FileReader("D:\\javaproject\\ACS\\src\\main\\java\\com\\xiaotong\\acs\\data\\graphData_lastfm.json");
        FileReader reader = new FileReader("D:\\javaproject\\ACS\\src\\main\\java\\com\\xiaotong\\acs\\data\\graphData_facebook.json");
//        JSON.parseO
//        gson.from
        return gson.fromJson(reader, GraphData.class);
    }
}

