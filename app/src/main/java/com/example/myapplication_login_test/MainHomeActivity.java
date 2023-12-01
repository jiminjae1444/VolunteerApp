package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainHomeActivity extends AppCompatActivity {

    private ViewPager2 mpager;
    private FragmentStateAdapter pageAdapter;
    private int num_page = 4;
    private RecyclerView recyclerView;
    private MyAdapter2 adapter;
    private ApiService volunteerApi;
    private String userType;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        volunteerApi = retrofit.create(ApiService.class);
        String username = getIntent().getStringExtra("username");
        getUserInfoAndProceed(username);

        // updateExpiredForms 호출
        updateExpiredForms();

        // updateVolunteerGrade 호출
        updateVolunteerGrade();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainHomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(View view, int position) {
                String username = getIntent().getStringExtra("username");
                if (username == null) {
                    username = "defaultUsername";
                }

                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainHomeActivity.this, MyInfo.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        break;
                    case 1:
                        if(userType.equals("ORGANIZATION")) {
                            Intent intent2 = new Intent(MainHomeActivity.this,VolunteerFormActivity.class);
                            intent2.putExtra("username", username);
                            startActivity(intent2);
                        }else{
                            Toast.makeText(MainHomeActivity.this, "기관만 접근 할 수있습니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        Intent intent3 = new Intent(MainHomeActivity.this, VolunteerListActivity.class);
                        intent3.putExtra("username", username);
                        startActivity(intent3);
                        break;
                    case 3:
                        Intent intent4 = new Intent(MainHomeActivity.this, VolunteerApplicationList.class);
                        intent4.putExtra("username", username);
                        startActivity(intent4);
                        break;
                }
            }
        }));
    }

    private void updateExpiredForms() {
        Call<Void> call = volunteerApi.updateExpiredForms();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Request successful, perform additional tasks
                } else {
                    // Server returned an error response
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Network or unexpected error occurred
                // Handle the error
            }
        });
    }

    private void updateVolunteerGrade() {
        Call<Void> call = volunteerApi.updateVolunteerGrade();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Request successful, perform additional tasks
                } else {
                    // Server returned an error response
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Network or unexpected error occurred
                // Handle the error
            }
        });
    }
    private void getUserInfoAndProceed(String username) {
        Call<User> call = volunteerApi.getUserByUsername(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        userType = user.getUserType(); // 전역 변수에 userType 저장
                        // userType을 사용하여 봉사폼 작성 버튼의 접근 여부 결정
                    } else {
                        // 사용자 정보가 null인 경우의 처리
                        Toast.makeText(MainHomeActivity.this, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 서버에서 오류 응답이 왔을 경우의 처리
                    Toast.makeText(MainHomeActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 네트워크 오류 또는 예외 발생 시의 처리
                Toast.makeText(MainHomeActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
