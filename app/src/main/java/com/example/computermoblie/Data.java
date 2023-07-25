package com.example.computermoblie;

public class Data {
    private String OldData;
    private String NowData;

    public Data(String oldData, String nowData) {
        OldData = oldData;
        NowData = nowData;
    }

    public String getOldData() {
        return OldData;
    }

    public String getNowData() {
        return NowData;
    }
}
