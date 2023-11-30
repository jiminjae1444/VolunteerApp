package com.example.myapplication_login_test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerApplicationList extends AppCompatActivity {

    private RecyclerView volunteerRecyclerView;
    private VolunteerApplictionAdapter adapter;
    private ApiService volunteerApi;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliction_list);

        volunteerRecyclerView = findViewById(R.id.volunteerRecyclerView);
        volunteerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonToMain = findViewById(R.id.buttonToMain);
        buttonToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 받아온 username
        username = getIntent().getStringExtra("username");

        if (username == null) {
            // username이 null인 경우에 대한 처리 (예: 기본값 설정)
            username = "defaultUsername";
        }

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        volunteerApi = retrofit.create(ApiService.class);

        // 서버에서 해당 username에 대한 infoid를 가져오는 요청
        getVolunteerApplications(username);
    }

    private void getVolunteerApplications(String username) {
        // 서버에서 해당 username에 대한 infoid를 가져오는 요청
        Call<UserInfo> call = volunteerApi.getUserInfoByUsername(username);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        // 서버에서 infoid를 받아왔으면, 이를 이용하여 봉사 신청 목록을 가져오는 요청
                        long infoId = userInfo.getId();
                        getVolunteerApplicationsForInfo(infoId);
                    } else {
                        // userInfo가 null인 경우에 대한 처리
                        Toast.makeText(VolunteerApplicationList.this, "사용자 정보를 받아올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 서버에서 오류 응답이 왔을 경우에 대한 처리
                    Toast.makeText(VolunteerApplicationList.this, "서버에서 응답을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                // 네트워크 오류 또는 예외 발생 시에 대한 처리
                Log.e("VolunteerListActivity", "Error getting user info: " + t.getMessage());
                Toast.makeText(VolunteerApplicationList.this, "네트워크 오류 또는 예외 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVolunteerApplicationsForInfo(long infoId) {
        // 서버에서 해당 infoid에 대한 봉사 신청 목록을 가져오는 요청
        Call<List<VolunteerApplication>> call = volunteerApi.getVolunteerApplicationsForInfo(infoId);
        call.enqueue(new Callback<List<VolunteerApplication>>() {
            @Override
            public void onResponse(Call<List<VolunteerApplication>> call, Response<List<VolunteerApplication>> response) {
                if (response.isSuccessful()) {
                    List<VolunteerApplication> applications = response.body();
                    if (applications != null && !applications.isEmpty()) {
                        // 서버에서 봉사 신청 목록을 받아왔으면, RecyclerView에 표시
                        displayVolunteerApplications(applications);
                    } else {
                        // 봉사 신청이 없는 경우에 대한 처리
                        Toast.makeText(VolunteerApplicationList.this, "봉사 신청이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 서버에서 오류 응답이 왔을 경우에 대한 처리
                    Toast.makeText(VolunteerApplicationList.this, "서버에서 응답을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VolunteerApplication>> call, Throwable t) {
                // 네트워크 오류 또는 예외 발생 시에 대한 처리
                Log.e("VolunteerListActivity", "Error getting volunteer applications: " + t.getMessage());
                Toast.makeText(VolunteerApplicationList.this, "네트워크 오류 또는 예외 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayVolunteerApplications(List<VolunteerApplication> applications) {
        // 어댑터를 생성하고 RecyclerView에 설정
        adapter = new VolunteerApplictionAdapter(applications);
        volunteerRecyclerView.setAdapter(adapter);
    }
}
