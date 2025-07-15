package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.NewDetailActivity;
import com.example.myapplication.entity.NewsInfo;
import com.example.myapplication.R;
import com.example.myapplication.adapter.NewsListAdapter;
import com.example.myapplication.entity.juhe;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//推荐
public class GuojiFragment extends Fragment {
    private String URL="http://v.juhe.cn/toutiao/index?key="+juhe.getKey()+"&type=guoji";
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 100) {
                String data = (String) msg.obj;
                NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                //刷新适配器
                if (null!=newsListAdapter){
                    newsListAdapter.setListData(newsInfo.getResult().getData());
                }
            }
        }
    };
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_guoji,
                container, false);
        recyclerView = view.findViewById(R.id.GuojirecyclerView);
        newsListAdapter = new NewsListAdapter(getContext());
        recyclerView.setAdapter(newsListAdapter);
        //recyclerView列表点击事件
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.ResultBean.DataBean dataBean, int position) {
                //跳转到详情页
                Intent intent = new Intent(getActivity(), NewDetailActivity.class);
                //传递参数，该类一定要实现Serializable
                intent.putExtra("dataBean",dataBean);
                startActivity(intent);
            }
        });
        getHttpData();
        return view;
    }

    private void getHttpData() {
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //构构造Request对象
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        //通过OkHttpClient和Request对象来构建Call对象
        Call call = okHttpClient.newCall(request);
        //通过Call对象的enqueue(Callback)方法来执行异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("-------------", "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Log.d("--------------", "onResponse: " + response.body().string());
                String data = response.body().string();
                Message message = new Message();
                //指定一个标识符
                message.what = 100;
                message.obj = data;
                mHandler.sendMessage(message);
            }
        });
    }
}