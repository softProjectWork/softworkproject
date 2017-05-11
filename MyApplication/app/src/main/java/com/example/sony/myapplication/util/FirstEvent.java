package com.example.sony.myapplication.util;

import org.json.JSONObject;

public class FirstEvent {
    private JSONObject jsonData;

    public FirstEvent(JSONObject js) {
        jsonData = js;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }
}
