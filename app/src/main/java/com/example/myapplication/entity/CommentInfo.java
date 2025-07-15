package com.example.myapplication.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentInfo implements Serializable {
    private int id;
    private String newsId;    // 新闻ID
    private String username;  // 评论用户
    private String nickname;  // 用户昵称
    private String content;   // 评论内容
    private long time;        // 评论时间
    
    public CommentInfo() {
    }
    
    public CommentInfo(int id, String newsId, String username, String nickname, String content, long time) {
        this.id = id;
        this.newsId = newsId;
        this.username = username;
        this.nickname = nickname;
        this.content = content;
        this.time = time;
    }
    
    // 不含ID的构造方法，用于新增评论
    public CommentInfo(String newsId, String username, String nickname, String content, long time) {
        this.newsId = newsId;
        this.username = username;
        this.nickname = nickname;
        this.content = content;
        this.time = time;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNewsId() {
        return newsId;
    }
    
    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    // 获取格式化的时间
    public String getFormattedTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(time));
    }
} 