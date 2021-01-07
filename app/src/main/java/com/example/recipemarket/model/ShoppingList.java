package com.example.recipemarket.model;

import java.util.List;

public class ShoppingList {
    private List<String> items;
    private String userId;

    public ShoppingList() {
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
