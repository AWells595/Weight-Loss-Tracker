package com.example.weightlosstrackingapp;


import java.util.Date;

public class WeightData {
    private final Date date;
    private final int weight;
    private final long id;

    public WeightData(long id, int weight, long dateInMilliseconds) {
        this.date = new Date(dateInMilliseconds);
        this.weight = weight;
        this.id = id;
    }

    public WeightData() {
        this.date = null;
        this.weight = -1;
        this.id = -1;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getWeight() {
        return weight;
    }
}
