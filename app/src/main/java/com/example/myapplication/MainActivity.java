package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.adapter.BasicFragmentAdpter;
import com.example.myapplication.entity.UserInfo;
import com.example.myapplication.fragment.CaijingFragment;
import com.example.myapplication.fragment.GuojiFragment;
import com.example.myapplication.fragment.GuoneiFragment;
import com.example.myapplication.fragment.JiankangFragment;
import com.example.myapplication.fragment.JunshiFragment;
import com.example.myapplication.fragment.KejiFragment;
import com.example.myapplication.fragment.QicheFragment;
import com.example.myapplication.fragment.TiyuFragment;
import com.example.myapplication.fragment.TopFragment;
import com.example.myapplication.fragment.YouxiFragment;
import com.example.myapplication.fragment.YuleFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String[] titleArray = new String[]{"推荐", "国内", "国际", "娱乐", "体育", "军事","科技","财经","游戏","汽车","健康"};
    //设置tabLayout栏将展示的文字
    private NavigationView view;
    private ImageView menu;
    private DrawerLayout drawerLayout;
    private TextView tvUserName; // 显示用户名
    private TextView tvLogout; // 退出登录按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);

        viewPager = findViewById(R.id.viewPager);
        view = findViewById(R.id.nav_view);
        menu = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        //打开"全国新闻"旁边的侧边栏
        menu.setOnClickListener(v ->{
            drawerLayout.openDrawer(GravityCompat.START);
        });

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TopFragment());
        fragmentList.add(new GuoneiFragment());
        fragmentList.add(new GuojiFragment());
        fragmentList.add(new YuleFragment());
        fragmentList.add(new TiyuFragment());
        fragmentList.add(new JunshiFragment());
        fragmentList.add(new KejiFragment());
        fragmentList.add(new CaijingFragment());
        fragmentList.add(new YouxiFragment());
        fragmentList.add(new QicheFragment());
        fragmentList.add(new JiankangFragment());

        BasicFragmentAdpter adpter = new BasicFragmentAdpter(getSupportFragmentManager(),fragmentList,titleArray);
        viewPager.setAdapter(adpter);
        tabLayout.setupWithViewPager(viewPager);

        // 设置导航视图的头部布局
        if (view.getHeaderCount() == 0) {
            view.inflateHeaderView(R.layout.nav_header_main);
        }
        
        // 设置NavigationView的头部视图中的用户信息
        View headerView = view.getHeaderView(0);
        if (headerView != null) {
            tvUserName = headerView.findViewById(R.id.tv_username);
            tvLogout = headerView.findViewById(R.id.tv_logout);
            
            // 设置用户名
            setUserInfo();
            
            // 设置退出登录点击事件
            if (tvLogout != null) {
                tvLogout.setOnClickListener(v -> {
                    // 退出登录
                    UserInfo.setUserInfo(null);
                    Toast.makeText(MainActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
                    // 跳转到登录界面
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                });
            }
        }

        view.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId()==R.id.nav_profile){
            //跳转到个人信息
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            } if (menuItem.getItemId()==R.id.nav_history){
            //跳转到历史记录去
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            } if (menuItem.getItemId()==R.id.nav_favorite){
            //跳转到收藏
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            } if (menuItem.getItemId()==R.id.nav_comments){
            //跳转到我的评论
                Intent intent = new Intent(MainActivity.this, MyCommentsActivity.class);
                startActivity(intent);
            } if (menuItem.getItemId()==R.id.nav_slideshow){
            //跳转到关于App
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
            return false;
        });
    }
    
    // 设置用户信息
    private void setUserInfo() {
        if (tvUserName != null) {
            UserInfo userInfo = UserInfo.getUserInfo();
            if (userInfo != null) {
                // 已登录，显示用户昵称
                tvUserName.setText(userInfo.getNickname());
            } else {
                // 未登录，显示"未登录"
                tvUserName.setText("未登录");
            }
        }
    }
    
    // 在页面从后台回到前台时刷新用户信息
    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }
}