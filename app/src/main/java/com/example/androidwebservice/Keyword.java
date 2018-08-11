package com.example.androidwebservice;

public class Keyword {
    private String name;
    private String value;
    private boolean synced;

    public Keyword(String keyword, String value, boolean synced) {
        this.name = keyword;
        this.value = value;
        this.synced = synced;
    }

    public String getKeyword() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean getSynced() {
        return synced;
    }
}
