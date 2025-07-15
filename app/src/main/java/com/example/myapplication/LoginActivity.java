package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.UserDbHelper;
import com.example.myapplication.entity.UserInfo;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private UserDbHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化数据库帮助类
        userDbHelper = UserDbHelper.getInstance(this);

        // 初始化视图
        initView();

        // 检查是否已登录
        checkLoginStatus();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // 设置点击事件
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    private void checkLoginStatus() {
        // 如果已经有用户登录信息，直接进入主页
        if (UserInfo.getUserInfo() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.btn_login) {
            // 登录
            login();
        } else if (id == R.id.tv_register) {
            // 注册
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    private void login() {
        // 获取输入
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录
        UserInfo userInfo = userDbHelper.login(username, password);
        if (userInfo != null) {
            // 登录成功
            UserInfo.setUserInfo(userInfo);
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            // 登录失败
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
} 