package com.example.myapplication.entity;

public class HistoryInfo {
    private  int history_id;
    private String username;;
    private String uniquekey;
    private String new_json;
    private int isFavorite;

    public HistoryInfo(int history_id, String username, String uniquekey, String new_json,int isFavorite) {
        this.history_id = history_id;
        this.username = username;
        this.uniquekey = uniquekey;
        this.new_json = new_json;
        this.isFavorite = isFavorite;
    }

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getNew_json() {
        return new_json;
    }

    public void setNew_json(String new_json) {
        this.new_json = new_json;
    }

    public int getIsfavorite() {
        return isFavorite;
    }

    public void setIsfavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
