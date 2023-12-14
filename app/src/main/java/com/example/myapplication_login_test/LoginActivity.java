package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class LoginActivity extends AppCompatActivity {
        private Retrofit retrofit;
        private ApiService apiService;
        private Button button;
        private EditText idText;
        private EditText passwordText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            TextView textView = findViewById(R.id.register);
            button = findViewById(R.id.button2);
            idText = findViewById(R.id.idText);
            passwordText = findViewById(R.id.passwordText);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.45.93:8040") // Spring Boot 애플리케이션의 URL로 변경해야 합니다.
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client) // 앞서 생성한 OkHttpClient를 사용
                    .build();

            apiService = retrofit.create(ApiService.class);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("RegisterActivity", "Register button clicked"); // 로그 메시지 추가
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = idText.getText().toString();
                    String password = passwordText.getText().toString();

                    LoginRequest loginRequest = new LoginRequest(username, password);
                    Call<LoginResponse> call = apiService.login(loginRequest);

                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful()) {
                                LoginResponse loginResponse = response.body();

                                if (loginResponse != null) {
                                    String message = loginResponse.getMessage();
                                    if ("로그인 성공".equals(message)) {
                                        idText.setText("");
                                        passwordText.setText("");
                                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                    } else {
                                        // 로그인 실패 시 실행할 코드 작성
                                        try {
                                            String errorResponse = response.errorBody().string();
                                            Toast.makeText(LoginActivity.this, errorResponse, Toast.LENGTH_LONG).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "응답 오류: 응답이 비어 있음", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            // 네트워크 오류 및 예외 처리
                            t.printStackTrace();
                            Toast.makeText(LoginActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }