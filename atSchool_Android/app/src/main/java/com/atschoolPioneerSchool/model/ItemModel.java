package com.atschoolPioneerSchool.model;

import java.io.Serializable;

public class ItemModel implements Serializable {
    long id;
    int img;
    String name;

    long price;
    String category;
    long likes;
    int total = 1;
    String description;
    public boolean isKid = false;
    public int StudentsId;
    public String StudentImageName;

    public ItemModel(long id, int img, String name, long price, String category, long likes, String description) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.price = price;
        this.category = category;
        this.likes = likes;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public int getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getStrPrice() {
        return "$ " + getPrice();
    }

    public long getPrice() {
        return (price);
    }

    public long getSumPrice() {
        return (price * total);
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLikes() {
        if (likes > 100) {
            return "100+ Likes";
        } else {
            return likes + " Likes";
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
