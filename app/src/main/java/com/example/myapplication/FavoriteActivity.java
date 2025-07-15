package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NewsListAdapter;
import com.example.myapplication.db.HistoryDbHelper;
import com.example.myapplication.entity.HistoryInfo;
import com.example.myapplication.entity.NewsInfo;
import com.example.myapplication.entity.UserInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private List<NewsInfo.ResultBean.DataBean> dataBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerView = findViewById(R.id.recyclerView);
        //退出
        findViewById(R.id.toolbar).setOnClickListener(view -> finish());
        newsListAdapter = new NewsListAdapter(this);
        recyclerView.setAdapter(newsListAdapter);

        //获取收藏的数据
        Gson gson = new Gson();
        String username;
        UserInfo userInfo = UserInfo.getUserInfo();
        if (userInfo != null) {
            username = userInfo.getUsername();
        } else {
            username = null;
        }
        List<HistoryInfo> favoriteNews = HistoryDbHelper.getInstance(FavoriteActivity.this).queryAllHistory(username);
        favoriteNews.forEach(historyInfo -> {
            int favoriteState = historyInfo.getIsfavorite();
            if(favoriteState == 1)
                dataBeans.add(gson.fromJson(historyInfo.getNew_json(),NewsInfo.ResultBean.DataBean.class));
        });
        newsListAdapter.setListData(dataBeans);

        //收藏记录，新闻的点击事件和fragment中的一样
        newsListAdapter.setOnItemClickListener((dataBean, position) -> {
            Intent intent = new Intent(this,NewDetailActivity.class);
            //传递对象必须实现可序列化接口
            intent.putExtra("dataBean",dataBean);
            startActivity(intent);
        });
    }
}