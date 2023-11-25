package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyInfo extends AppCompatActivity {

    TextView textViewName, textViewGrade, textViewTime, textViewPhonenumber;
    Button back,modify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        String username = getIntent().getStringExtra("username");
        System.out.println(username);
        textViewGrade = findViewById(R.id.textViewGrade);
        textViewName = findViewById(R.id.textViewName);
        textViewTime = findViewById(R.id.textViewTime);
        textViewPhonenumber = findViewById(R.id.textViewPhoneNumber);
        back = findViewById(R.id.button_back);
        modify = findViewById(R.id.button_modifiy);
        Button logout = findViewById(R.id.logoutButton);
        fetchData(username);
        fetchUserInfoData(username);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfo.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void fetchData(String username){
        // OkHttpClient를 생성하고 인터셉터를 추가
        OkHttpClient client = new OkHttpClient.Builder().build();
        // Retrofit 객체 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 사용자 API 엔드포인트 URL
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 사용자 정보 가져오기
        Call<User> userCall = apiService.getUserByUsername(username);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        // 사용자 정보 가져오기 성공
                        String phoneNumber = user.getPhoneNumber();
                        String name = user.getName();
                        // 텍스트뷰에 표시
                        textViewPhonenumber.setText("전화번호 : "+phoneNumber);
                        textViewName.setText("이름 : "+name);

                        // 이제 유저 정보 테이블 정보를 가져올 수 있습니다.
                        fetchUserInfoData(username);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserInfoData(String username) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 사용자 API 엔드포인트 URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 여기서 유저 정보 테이블 정보를 가져와 텍스트뷰에 표시
        Call<UserInfo> userInfoCall = apiService.getUserInfoByUsername(username);
        userInfoCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = response.body();
                    if (userInfo != null) {
                        // 등급과 봉사 시간 정보 가져오기 성공
                        String grade = userInfo.getGrade();
                        String volunteerTime = String.valueOf(userInfo.getTotal_hours());
                        // 텍스트뷰에 표시
                        textViewGrade.setText("등급 : "+grade);
                        textViewTime.setText("봉사 시간 : "+volunteerTime);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                // 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfo.this, MainHomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfo.this, MyinfoModifyActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}


