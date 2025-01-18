package com.zenyte.game.content.grandexchange;

import java.time.Instant;

public class JSONGEItemDefinitions {
    //All of id, name and price are used!
    private int id;
    private String name;
    private int price;
    private Instant time;//Unused entirely

    public JSONGEItemDefinitions() {
    }

    public JSONGEItemDefinitions(final int id, final String name, final int price, final Instant time) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.time = time;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public Instant getTime() {
        return this.time;
    }

    public void setTime(final Instant time) {
        this.time = time;
    }
}
