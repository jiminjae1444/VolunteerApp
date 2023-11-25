package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyinfoModifyActivity extends AppCompatActivity {
    private EditText usernameText, passwordText, phoneText, nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_modify);
        Button logout = findViewById(R.id.logoutButton);
        initializeViews();

        String username = getIntent().getStringExtra("username");

        Button modifyButton = findViewById(R.id.button_modifiy);
        modifyButton.setOnClickListener(view -> updateUser(username));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyinfoModifyActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeViews() {
        nameText = findViewById(R.id.name);
        usernameText = findViewById(R.id.idText);
        passwordText = findViewById(R.id.passwordText);
        phoneText = findViewById(R.id.phoneNumber);
    }

    private void updateUser(String username) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 스프링 부트 API 엔드포인트
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<User> getUserCall = apiService.getUserByUsername(username);
        getUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User existingUser = response.body();

                    // 변경된 필드 가져오기
                    User changedFieldsUser = new User();

                    // 비교 후 변경된 값만 가져오기
                    if (!usernameText.getText().toString().equals(existingUser.getUsername())) {
                        changedFieldsUser.setUsername(usernameText.getText().toString());
                    } else {
                        changedFieldsUser.setUsername(existingUser.getUsername());
                    }

                    if (!passwordText.getText().toString().equals(existingUser.getPassword())) {
                        changedFieldsUser.setPassword(passwordText.getText().toString());
                    } else {
                        changedFieldsUser.setPassword(existingUser.getPassword());
                    }

                    if (!nameText.getText().toString().equals(existingUser.getName())) {
                        changedFieldsUser.setName(nameText.getText().toString());
                    } else {
                        changedFieldsUser.setName(existingUser.getName());
                    }

                    if (!phoneText.getText().toString().equals(existingUser.getPhoneNumber())) {
                        changedFieldsUser.setPhoneNumber(phoneText.getText().toString());
                    } else {
                        changedFieldsUser.setPhoneNumber(existingUser.getPhoneNumber());
                    }
                    // 사용자 정보 업데이트
                    updateUserFields(changedFieldsUser, existingUser, username);
                } else {
                    // 오류 처리
                    Toast.makeText(getApplicationContext(), "사용자 정보 가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserFields(User changedFieldsUser, User existingUser, String username) {
        // id와 userType은 기존 값으로 유지
        changedFieldsUser.setId(existingUser.getId());
        changedFieldsUser.setUserType(existingUser.getUserType());

        Log.d("MyInfoModifyActivity", "Changed Fields - Name: " + changedFieldsUser.getName());
        Log.d("MyInfoModifyActivity", "Changed Fields - UserName: " + changedFieldsUser.getUsername());
        Log.d("MyInfoModifyActivity", "Changed Fields - Phone: " + changedFieldsUser.getPhoneNumber());
        Log.d("MyInfoModifyActivity", "Changed Fields - Password: " + changedFieldsUser.getPassword());

        // 변경된 값이 있는지 확인
        if (changedFieldsUser.hasChanges(existingUser)) {
            // 변경된 필드만 업데이트 요청으로 전달
            sendUpdateRequest(username, changedFieldsUser);
        } else {
            // 변경된 값이 없을 경우 처리
            Toast.makeText(getApplicationContext(), "변경된 값이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendUpdateRequest(String username, User changedFieldsUser) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 스프링 부트 API 엔드포인트
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<User> updateUserCall = apiService.updateUser(username, changedFieldsUser);
        updateUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User updatedUser = response.body();
                    handleUpdateSuccess(username, updatedUser);
                } else {
                    // 오류 처리
                    Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateSuccess(String username, User updatedUser) {
        // 업데이트된 사용자 정보를 처리
        Toast.makeText(getApplicationContext(), "사용자 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MyinfoModifyActivity.this, MainHomeActivity.class);
        String newUsername = usernameText.getText().toString();
        if (!username.equals(newUsername)) {
            intent.putExtra("username", newUsername);
        } else {
            intent.putExtra("username", username);
        }
        startActivity(intent);
    }
}