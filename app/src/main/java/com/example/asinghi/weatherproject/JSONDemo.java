package com.example.asinghi.weatherproject;

import java.util.List;


public class JSONDemo {

    private String name;

    public JSONDemo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NAME : " + name;
    }
}
