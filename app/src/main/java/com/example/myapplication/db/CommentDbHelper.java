package com.example.myapplication.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.entity.CommentInfo;
import com.example.myapplication.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class CommentDbHelper extends SQLiteOpenHelper {
    private static CommentDbHelper commentDbHelper;
    private static final String DB_NAME = "comment.db";   //数据库名
    private static final int VERSION = 1;
    
    public CommentDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static CommentDbHelper getInstance(Context context){
        if(commentDbHelper == null)
            commentDbHelper = new CommentDbHelper(context);
        return commentDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建评论表
        sqLiteDatabase.execSQL("create table comment(id integer primary key autoincrement,"
                + "news_id text,"     // 新闻ID
                + "username text,"    // 评论用户名
                + "nickname text,"    // 用户昵称
                + "content text,"     // 评论内容
                + "time long"         // 评论时间
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 如果版本更新，可以在这里处理数据库升级
    }
    
    // 获取当前登录用户的用户名
    private String getCurrentUsername() {
        UserInfo userInfo = UserInfo.getUserInfo();
        return userInfo != null ? userInfo.getUsername() : "";
    }
    
    // 获取当前登录用户的昵称
    private String getCurrentNickname() {
        UserInfo userInfo = UserInfo.getUserInfo();
        return userInfo != null ? userInfo.getNickname() : "";
    }
    
    /**
     * 添加评论
     * @param newsId 新闻ID
     * @param content 评论内容
     * @return 插入结果
     */
    public long addComment(String newsId, String content) {
        String username = getCurrentUsername();
        String nickname = getCurrentNickname();
        
        // 未登录不能评论
        if (username.isEmpty()) {
            return -1;
        }
        
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_id", newsId);
        values.put("username", username);
        values.put("nickname", nickname);
        values.put("content", content);
        values.put("time", System.currentTimeMillis());
        long insert = sqLiteDatabase.insert("comment", null, values);
        sqLiteDatabase.close();
        return insert;
    }
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 删除结果
     */
    public int deleteComment(int commentId) {
        String username = getCurrentUsername();
        
        // 未登录不能删除评论
        if (username.isEmpty()) {
            return -1;
        }
        
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // 只能删除自己的评论
        String whereClause = "id=? AND username=?";
        String[] whereArgs = {String.valueOf(commentId), username};
        int delete = sqLiteDatabase.delete("comment", whereClause, whereArgs);
        sqLiteDatabase.close();
        return delete;
    }
    
    /**
     * 查询新闻评论
     * @param newsId 新闻ID
     * @return 评论列表
     */
    @SuppressLint("Range")
    public List<CommentInfo> getCommentsByNewsId(String newsId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<CommentInfo> commentList = new ArrayList<>();
        
        String sql = "SELECT * FROM comment WHERE news_id=? ORDER BY time DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{newsId});
        
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String news_id = cursor.getString(cursor.getColumnIndex("news_id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            
            commentList.add(new CommentInfo(id, news_id, username, nickname, content, time));
        }
        cursor.close();
        sqLiteDatabase.close();
        return commentList;
    }
    
    /**
     * 查询用户的所有评论
     * @return 评论列表
     */
    @SuppressLint("Range")
    public List<CommentInfo> getUserComments() {
        String username = getCurrentUsername();
        
        // 未登录返回空列表
        if (username.isEmpty()) {
            return new ArrayList<>();
        }
        
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<CommentInfo> commentList = new ArrayList<>();
        
        String sql = "SELECT * FROM comment WHERE username=? ORDER BY time DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{username});
        
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String news_id = cursor.getString(cursor.getColumnIndex("news_id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            
            commentList.add(new CommentInfo(id, news_id, name, nickname, content, time));
        }
        cursor.close();
        sqLiteDatabase.close();
        return commentList;
    }
} 