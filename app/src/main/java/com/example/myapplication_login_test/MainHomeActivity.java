package com.example.myapplication_login_test;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;


public class MainHomeActivity extends AppCompatActivity {

    private ViewPager2 mpager;
    private FragmentStateAdapter pageAdapter;
    private int num_page = 4;
    private RecyclerView recyclerView;
    private MyAdapter2 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainhome);
        mpager = findViewById(R.id.viewpager);
        pageAdapter = new MyAdapter(this, num_page);
        mpager.setAdapter(pageAdapter);
        Button logout = findViewById(R.id.logoutButton);

        mpager.setCurrentItem(1000);
        mpager.setOffscreenPageLimit(4);

        mpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mpager.setCurrentItem(position);
                }
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, LinearLayoutManager.VERTICAL));
        List<MyData> dataList = new ArrayList<>();
        dataList.add(new MyData(R.drawable.ic_baseline_account_circle_24, "내 정보 관리"));
        dataList.add(new MyData(R.drawable.ic_baseline_edit_note_24, "봉사 모집글 작성"));
        dataList.add(new MyData(R.drawable.ic_baseline_check_24, "봉사 신청 하기"));
        dataList.add(new MyData(R.drawable.ic_baseline_text_snippet_24, "신청 목록 보기"));

        adapter = new MyAdapter2(dataList);
        recyclerView.setAdapter(adapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainHomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 클릭된 아이템의 위치(position)에 따라 다른 작업을 수행하도록 설정
                    // 클릭된 아이템의 위치(position)에 따라 다른 작업을 수행하도록 설정
                    String username = getIntent().getStringExtra("username");
                    if (username == null) {
                        // username이 null인 경우에 대한 처리 (예: 기본값 설정)
                        username = "defaultUsername";
                    }
                switch (position) {
                    case 0:
                        // 첫 번째 아이템을 클릭한 경우
                        // 해당 아이템에 대한 작업 수행 (예: 내 정보 관리 액티비티 시작)
                        Intent intent = new Intent(MainHomeActivity.this, MyInfo.class);
                        if (username == null) {
                            // username이 null인 경우에 대한 처리 (예: 기본값 설정)
                            username = "defaultUsername";
                        }
                        intent.putExtra("username", username);
                        startActivity(intent);
                        break;
                    case 1:
                        // 두 번째 아이템을 클릭한 경우
                        // 해당 아이템에 대한 작업 수행 (예: 봉사 모집글 작성 액티비티 시작)
                        Intent intent2 = new Intent(MainHomeActivity.this, VolunteerFormActivity.class);
                        if (username == null) {
                            // username이 null인 경우에 대한 처리 (예: 기본값 설정)
                            username = "defaultUsername";
                        }
                        intent2.putExtra("username", username);
                        startActivity(intent2);
                        break;
                    // 나머지 아이템들에 대한 작업도 추가 가능
                    // ...
                    case 2:
                        Intent intent3 = new Intent(MainHomeActivity.this, VolunteerListActivity.class);
                        if (username == null) {
                            // username이 null인 경우에 대한 처리 (예: 기본값 설정)
                            username = "defaultUsername";
                        }
                        intent3.putExtra("username", username);
                        startActivity(intent3);
                        break;
                    case 3:
                        Intent intent4 = new Intent(MainHomeActivity.this, MyInfo.class);
                        startActivity(intent4);
                        break;
                }
            }
        }));


    }
}

