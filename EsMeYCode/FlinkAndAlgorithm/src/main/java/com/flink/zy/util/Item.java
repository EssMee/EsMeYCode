package com.flink.zy.util;

import java.sql.Timestamp;

public class Item {
    public Item(Timestamp timestamp, String name, Integer price, Integer groupId) {
        this.timestamp = timestamp;
        this.name = name;
        this.price = price;
        this.groupId = groupId;
    }

    private Timestamp timestamp;
    private String name;
    private Integer price;
    private Integer groupId;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Item{" +
                "timestamp=" + timestamp +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", groupId=" + groupId +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
