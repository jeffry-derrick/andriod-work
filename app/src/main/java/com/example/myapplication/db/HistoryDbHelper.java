package com.example.myapplication.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.entity.HistoryInfo;
import com.example.myapplication.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class HistoryDbHelper extends SQLiteOpenHelper {
    private static HistoryDbHelper historyDbHelper;
    private static final String DB_NAME = "history.db";   //数据库名
    private static final int VERSION = 1;
    public HistoryDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static HistoryDbHelper getInstance(Context context){
        if(historyDbHelper == null)
            historyDbHelper = new HistoryDbHelper(context);
        return historyDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建histroy表
        sqLiteDatabase.execSQL("create table history(id integer primary key autoincrement,"
                +"new_id text,"   //新闻id
                +"username text,"    //用户名
                +"new_json text,"   //新闻数据
                +"is_favorite int"    //判断是否为收藏
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    
    // 获取当前登录用户的用户名
    private String getCurrentUsername() {
        UserInfo userInfo = UserInfo.getUserInfo();
        return userInfo != null ? userInfo.getUsername() : "guest";
    }
    
    @SuppressLint("Range")
    public List<HistoryInfo> queryAllHistory(String username){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<HistoryInfo> historyInfos = new ArrayList<>();
        
        // 如果没有传入用户名，则获取当前登录用户的历史记录
        if (username == null || username.isEmpty()) {
            username = getCurrentUsername();
        }
        
        String sql = "select * from history where username=?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{username});
        
        while (cursor.moveToNext()) {
            int history_id= cursor.getInt(cursor.getColumnIndex("id"));
            String uniquekey = cursor.getString(cursor.getColumnIndex("new_id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String new_json = cursor.getString(cursor.getColumnIndex("new_json"));
            int isFavorite = cursor.getInt(cursor.getColumnIndex("is_favorite"));
            historyInfos.add(new HistoryInfo(history_id, name, uniquekey, new_json, isFavorite));
        }
        cursor.close();
        sqLiteDatabase.close();
        return historyInfos;
    }
    
    @SuppressLint("Range")
    public List<HistoryInfo> queryAllFavorite(String username){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<HistoryInfo> favoritelist = new ArrayList<>();
        
        // 如果没有传入用户名，则获取当前登录用户的收藏
        if (username == null || username.isEmpty()) {
            username = getCurrentUsername();
        }
        
        String sql = "select * from history where username=? and is_favorite=1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{username});
        
        while (cursor.moveToNext()) {
            int history_id = cursor.getInt(cursor.getColumnIndex("id"));
            String uniquekey = cursor.getString(cursor.getColumnIndex("new_id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String new_json = cursor.getString(cursor.getColumnIndex("new_json"));
            int isFavorite = cursor.getInt(cursor.getColumnIndex("is_favorite"));
            favoritelist.add(new HistoryInfo(history_id, name, uniquekey, new_json, isFavorite));
        }
        cursor.close();
        sqLiteDatabase.close();
        return favoritelist;
    }
    
    public long addHistroyNew(String username, String new_id, String new_json){
        // 如果没有传入用户名，则使用当前登录用户的用户名
        if (username == null || username.isEmpty()) {
            username = getCurrentUsername();
        }
        
        if(!isHistory(new_id, username)) {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("username", username);
            values.put("new_id", new_id);
            values.put("new_json", new_json);
            values.put("is_favorite", 0);
            long insert = sqLiteDatabase.insert("history", null, values);
            sqLiteDatabase.close();

            return insert;
        }
        return 0;
    }
    
    //判断浏览的是否已经添加到当前用户的历史记录中
    public boolean isHistory(String new_id, String username) {
        // 如果没有传入用户名，则使用当前登录用户的用户名
        if (username == null || username.isEmpty()) {
            username = getCurrentUsername();
        }
        
        //获取SQLiteDatabase实例
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select id from history where new_id=? and username=?";
        String[] selectionArgs = {new_id, username};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        boolean exist = cursor.moveToNext();
        cursor.close();
        db.close();
        return exist;
    }
    
    //重载方法，使用当前登录用户名
    public boolean isHistory(String new_id) {
        return isHistory(new_id, getCurrentUsername());
    }
    
    //设置为收藏
    public int setFavorite(int id, int isFavorite){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite);
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(id)};
        int update = sqLiteDatabase.update("history", values, whereClause, whereArgs);
        sqLiteDatabase.close();
        return update;
    }
    
    //获取是否为收藏
    @SuppressLint("Range")
    public HistoryInfo getFavoriteStatus(String new_id, String username){
        // 如果没有传入用户名，则使用当前登录用户的用户名
        if (username == null || username.isEmpty()) {
            username = getCurrentUsername();
        }
        
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select id, new_id, username, new_json, is_favorite from history where new_id=? and username=?";
        String[] selectionArgs = {new_id, username};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            int historyId = cursor.getInt(cursor.getColumnIndex("id"));
            int isFavorite = cursor.getInt(cursor.getColumnIndex("is_favorite"));
            return new HistoryInfo(historyId, username, new_id, null, isFavorite);
        }
        cursor.close();
        db.close();
        return null;
    }
    
    //重载方法，使用当前登录用户名
    @SuppressLint("Range")
    public HistoryInfo getFavoriteStatus(String new_id){
        return getFavoriteStatus(new_id, getCurrentUsername());
    }

    @SuppressLint("Range")
    public HistoryInfo getHistoryByNewsId(String newsId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from history where new_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{newsId});
        
        if (cursor.moveToNext()) {
            int historyId = cursor.getInt(cursor.getColumnIndex("id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String newJson = cursor.getString(cursor.getColumnIndex("new_json"));
            int isFavorite = cursor.getInt(cursor.getColumnIndex("is_favorite"));
            
            cursor.close();
            db.close();
            return new HistoryInfo(historyId, username, newsId, newJson, isFavorite);
        }
        
        cursor.close();
        db.close();
        return null;
    }
}
