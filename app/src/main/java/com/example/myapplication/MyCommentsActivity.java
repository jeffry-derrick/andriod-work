package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.CommentAdapter;
import com.example.myapplication.db.CommentDbHelper;
import com.example.myapplication.db.HistoryDbHelper;
import com.example.myapplication.entity.CommentInfo;
import com.example.myapplication.entity.HistoryInfo;
import com.example.myapplication.entity.NewsInfo;
import com.example.myapplication.entity.UserInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyCommentsActivity extends AppCompatActivity {
    
    private Toolbar toolbar;
    private RecyclerView recyclerMyComments;
    private CommentAdapter commentAdapter;
    private CommentDbHelper commentDbHelper;
    private List<CommentInfo> commentList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comments);
        
        // 初始化控件
        toolbar = findViewById(R.id.toolbar);
        recyclerMyComments = findViewById(R.id.recycler_my_comments);
        
        // 初始化数据库
        commentDbHelper = CommentDbHelper.getInstance(this);
        
        // 设置toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 检查登录状态
        if (UserInfo.getUserInfo() == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 加载评论数据
        loadMyComments();
    }
    
    /**
     * 加载我的评论
     */
    private void loadMyComments() {
        // 获取当前用户的所有评论
        commentList = commentDbHelper.getUserComments();
        
        // 如果没有评论，提示用户
        if (commentList.isEmpty()) {
            Toast.makeText(this, "您还没有发表过评论", Toast.LENGTH_SHORT).show();
        }
        
        // 初始化RecyclerView
        recyclerMyComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        recyclerMyComments.setAdapter(commentAdapter);
        
        // 设置评论操作监听器
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onDeleteComment(int position, CommentInfo comment) {
                showDeleteCommentDialog(position, comment);
            }

            @Override
            public void onCommentClick(CommentInfo comment) {
                // 从历史记录中获取新闻信息
                HistoryInfo historyInfo = HistoryDbHelper.getInstance(MyCommentsActivity.this)
                        .getHistoryByNewsId(comment.getNewsId());
                
                if (historyInfo != null) {
                    // 解析新闻数据
                    NewsInfo.ResultBean.DataBean dataBean = new Gson().fromJson(
                            historyInfo.getNew_json(), NewsInfo.ResultBean.DataBean.class);
                    
                    // 跳转到新闻详情页
                    Intent intent = new Intent(MyCommentsActivity.this, NewDetailActivity.class);
                    intent.putExtra("dataBean", dataBean);
                    startActivity(intent);
                } else {
                    Toast.makeText(MyCommentsActivity.this, "该新闻已不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    /**
     * 显示删除评论确认对话框
     */
    private void showDeleteCommentDialog(int position, CommentInfo comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定要删除这条评论吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 从数据库中删除评论
                    int result = commentDbHelper.deleteComment(comment.getId());
                    if (result > 0) {
                        // 从列表中移除评论
                        commentAdapter.removeComment(position);
                        if (commentList.isEmpty()) {
                            Toast.makeText(MyCommentsActivity.this, "没有更多评论了", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyCommentsActivity.this, "评论已删除", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyCommentsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 