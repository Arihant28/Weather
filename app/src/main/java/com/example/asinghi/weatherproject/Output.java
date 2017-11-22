package com.example.asinghi.weatherproject;

/**
 * Created by asinghi on 11/7/17.
 */

public class Output {

    int id;
    String json;

    public Output(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
