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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etUsername, etPassword, etConfirmPassword, etNickname;
    private Button btnRegister;
    private TextView tvLogin;
    private UserDbHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化数据库帮助类
        userDbHelper = UserDbHelper.getInstance(this);

        // 初始化视图
        initView();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etNickname = findViewById(R.id.et_nickname);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // 设置点击事件
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.btn_register) {
            // 注册
            register();
        } else if (id == R.id.tv_login) {
            // 返回登录
            finish();
        }
    }

    private void register() {
        // 获取输入
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        // 注册
        long result = userDbHelper.register(username, password, nickname);
        if (result > 0) {
            // 注册成功
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            
            // 获取用户信息并设置为当前登录用户
            UserInfo userInfo = userDbHelper.login(username, password);
            if (userInfo != null) {
                UserInfo.setUserInfo(userInfo);
            }
            
            // 跳转到主页
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else if (result == -1) {
            // 用户已存在
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
        } else {
            // 注册失败
            Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
        }
    }
} 