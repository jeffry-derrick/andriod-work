package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.db.UserDbHelper;
import com.example.myapplication.entity.UserInfo;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etUsername, etNickname, etPassword;
    private Button btnSave, btnLogout;
    private UserDbHelper userDbHelper;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 初始化控件
        toolbar = findViewById(R.id.toolbar);
        etUsername = findViewById(R.id.et_username);
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        btnSave = findViewById(R.id.btn_save);
        btnLogout = findViewById(R.id.btn_logout);

        // 初始化数据库
        userDbHelper = UserDbHelper.getInstance(this);

        // 设置toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 检查登录状态
        userInfo = UserInfo.getUserInfo();
        if (userInfo == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 填充用户信息
        etUsername.setText(userInfo.getUsername());
        etNickname.setText(userInfo.getNickname());
        etPassword.setText(userInfo.getPassword());

        // 保存修改
        btnSave.setOnClickListener(v -> saveUserInfo());

        // 退出登录
        btnLogout.setOnClickListener(v -> logout());
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新用户信息
        userInfo.setNickname(nickname);
        userInfo.setPassword(password);

        // 更新数据库
        int result = userDbHelper.updateUserInfo(userInfo);
        if (result > 0) {
            // 更新全局用户信息
            UserInfo.setUserInfo(userInfo);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        // 清除全局用户信息
        UserInfo.setUserInfo(null);
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        // 跳转到登录界面
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 