package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class NewDetailActivity extends AppCompatActivity {
    private NewsInfo.ResultBean.DataBean dataBean;
    private Toolbar toolbar;
    private WebView webView;
    private ImageButton favoriteButton;
    private int historyId;
    private ProgressBar progressBar;
    
    // 评论相关
    private RecyclerView recyclerComments;
    private EditText etComment;
    private Button btnPostComment;
    private CommentAdapter commentAdapter;
    private CommentDbHelper commentDbHelper;
    private List<CommentInfo> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);
        dataBean = (NewsInfo.ResultBean.DataBean) getIntent().getSerializableExtra("dataBean");

        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webView);
        favoriteButton = findViewById(R.id.favorite);
        progressBar = findViewById(R.id.progressBar);
        
        // 初始化评论相关控件
        recyclerComments = findViewById(R.id.recycler_comments);
        etComment = findViewById(R.id.et_comment);
        btnPostComment = findViewById(R.id.btn_post_comment);
        
        // 初始化评论数据库
        commentDbHelper = CommentDbHelper.getInstance(this);

        // 配置WebView
        setupWebView();

        //设置数据
        if(null != dataBean) {
            HistoryInfo historyInfo = HistoryDbHelper.getInstance(NewDetailActivity.this).getFavoriteStatus(dataBean.getUniquekey());
            if(historyInfo != null) {
                if(historyInfo.getIsfavorite() == 1)
                    favoriteButton.setColorFilter(getResources().getColor(R.color.yellow));
                else
                    favoriteButton.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                historyId = historyInfo.getHistory_id();
            } else {
                favoriteButton.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            }
            webView.loadUrl(dataBean.getUrl());
            String new_json = new Gson().toJson(dataBean);
            //准备添加到历史记录
            String username = null;
            UserInfo userInfo = UserInfo.getUserInfo();
            if(null != userInfo)
                username = userInfo.getUsername();
            long i = HistoryDbHelper.getInstance(NewDetailActivity.this).addHistroyNew(username, dataBean.getUniquekey(), new_json);
            if(i != 0)
                historyId = (int) i;
                
            // 加载评论数据
            loadComments();
        }

        //返回
        toolbar.setNavigationOnClickListener(view -> finish());
        
        //是否收藏
        favoriteButton.setOnClickListener(v -> {
            HistoryInfo historyInfo = HistoryDbHelper.getInstance(NewDetailActivity.this).getFavoriteStatus(dataBean.getUniquekey());
            if (historyInfo == null || historyInfo.getIsfavorite() == 0) {
                // 未收藏，设置为收藏
                HistoryDbHelper.getInstance(NewDetailActivity.this).setFavorite(historyId, 1);
                favoriteButton.setColorFilter(getResources().getColor(R.color.yellow));
                Toast.makeText(NewDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                // 已收藏，取消收藏
                HistoryDbHelper.getInstance(NewDetailActivity.this).setFavorite(historyId, 0);
                favoriteButton.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                Toast.makeText(NewDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 发表评论
        btnPostComment.setOnClickListener(v -> postComment());
    }

    private void setupWebView() {
        // 启用JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        
        // 设置WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        // 设置WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }
    
    /**
     * 加载评论数据
     */
    private void loadComments() {
        if (dataBean != null) {
            // 从数据库中加载评论
            commentList = commentDbHelper.getCommentsByNewsId(dataBean.getUniquekey());
            
            // 初始化RecyclerView
            recyclerComments.setLayoutManager(new LinearLayoutManager(this));
            commentAdapter = new CommentAdapter(commentList);
            recyclerComments.setAdapter(commentAdapter);
            
            // 设置评论操作监听器
            commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
                @Override
                public void onDeleteComment(int position, CommentInfo comment) {
                    showDeleteCommentDialog(position, comment);
                }

                @Override
                public void onCommentClick(CommentInfo comment) {
                    // 评论点击事件，这里不需要实现
                }
            });
        }
    }
    
    /**
     * 发表评论
     */
    private void postComment() {
        // 获取评论内容
        String content = etComment.getText().toString().trim();
        
        // 判断用户是否登录
        if (UserInfo.getUserInfo() == null) {
            Toast.makeText(this, "请先登录再评论", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证评论内容
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 添加评论到数据库
        long result = commentDbHelper.addComment(dataBean.getUniquekey(), content);
        
        if (result > 0) {
            // 清空输入框
            etComment.setText("");
            
            // 重新加载评论数据
            commentList = commentDbHelper.getCommentsByNewsId(dataBean.getUniquekey());
            commentAdapter.updateComments(commentList);
            
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show();
        }
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
                        Toast.makeText(NewDetailActivity.this, "评论已删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}