package com.example.myapplication.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.entity.UserInfo;

public class UserDbHelper extends SQLiteOpenHelper {
    private static UserDbHelper userDbHelper;
    private static final String DB_NAME = "user.db";   //数据库名
    private static final int VERSION = 1;

    public UserDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static UserDbHelper getInstance(Context context) {
        if (userDbHelper == null)
            userDbHelper = new UserDbHelper(context);
        return userDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建用户表
        sqLiteDatabase.execSQL("create table user(user_id integer primary key autoincrement,"
                + "username text,"    //用户名
                + "password text,"    //密码
                + "nickname text"     //昵称
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 如果版本更新，可以在这里处理数据库升级
    }

    /**
     * 注册用户
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @return 插入结果
     */
    public long register(String username, String password, String nickname) {
        if (isUserExists(username)) {
            return -1; // 用户已存在
        }
        
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("nickname", nickname);
        long insert = sqLiteDatabase.insert("user", null, values);
        sqLiteDatabase.close();
        return insert;
    }

    /**
     * 检查用户是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean isUserExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select user_id from user where username=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @SuppressLint("Range")
    public UserInfo login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        UserInfo userInfo = null;
        String sql = "select user_id, username, password, nickname from user where username=? and password=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToNext()) {
            int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String pwd = cursor.getString(cursor.getColumnIndex("password"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            userInfo = new UserInfo(userId, name, pwd, nickname);
        }
        cursor.close();
        db.close();
        return userInfo;
    }

    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @return 更新结果
     */
    public int updateUserInfo(UserInfo userInfo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", userInfo.getNickname());
        values.put("password", userInfo.getPassword());
        String whereClause = "user_id=?";
        String[] whereArgs = {String.valueOf(userInfo.get_id())};
        int update = sqLiteDatabase.update("user", values, whereClause, whereArgs);
        sqLiteDatabase.close();
        return update;
    }
} 