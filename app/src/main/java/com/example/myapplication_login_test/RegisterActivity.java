package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    EditText editText_Org_Address;
    EditText editText_username;
    EditText editText_phonenumber;
    EditText editText_name;
    EditText editText_Org_Number;
    EditText editText_Password;
    EditText editText_Password_Check;
    RadioGroup radioGroup;
    RadioButton radioButtonOrg;
    RadioButton radioButtonNormal;
    Button button_register;
    Button button_check;
    EditText editText_Org;
    Button button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button logout = findViewById(R.id.logoutButton);
        editText_Org_Address = findViewById(R.id.org_address);
        editText_username = findViewById(R.id.idText);
        editText_phonenumber = findViewById(R.id.phoneNumber);
        editText_name = findViewById(R.id.name);
        editText_Org_Number = findViewById(R.id.org_Number);
        editText_Password = findViewById(R.id.passwordText);
        editText_Password_Check = findViewById(R.id.passwordTextCheck);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonOrg = findViewById(R.id.radioButtonOrg);
        radioButtonNormal = findViewById(R.id.radioButtonNoraml);
        button_register = findViewById(R.id.button_register);
        button_check = findViewById(R.id.button_password_check);
        editText_Org = findViewById(R.id.org_name);
        button_back = findViewById(R.id.button_back1);
        final String[] userType = new String[1];

        button_register.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                if (checkedID == R.id.radioButtonOrg) {
                    editText_Org.setVisibility(View.VISIBLE);
                    editText_Org_Address.setVisibility(View.VISIBLE);
                    editText_Org_Number.setVisibility(View.VISIBLE);
                    userType[0] ="ORGANIZATION";
                } else if (checkedID == R.id.radioButtonNoraml) {
                    editText_Org.setVisibility(View.GONE);
                    editText_Org_Address.setVisibility(View.GONE);
                    editText_Org_Number.setVisibility(View.GONE);
                    editText_Org.setText("");
                    editText_Org_Address.setText("");
                    editText_Org_Number.setText("");
                    userType[0] ="USER";
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editText_Password.getText().toString();
                String passwordCheck = editText_Password_Check.getText().toString();

                if (!TextUtils.isEmpty(password) &&!TextUtils.isEmpty(passwordCheck) && password.equals(passwordCheck) ) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치합니다!!", Toast.LENGTH_SHORT).show();
                    button_register.setEnabled(true);
                } else {
                    editText_Password.setText("");
                    editText_Password_Check.setText("");
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않거나 필드가 비어 있습니다!!", Toast.LENGTH_LONG).show();
                }
            }
        });


        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText_username.getText().toString();
                String password = editText_Password.getText().toString();
                String name = editText_name.getText().toString();
                String phoneNumber = editText_phonenumber.getText().toString();

                String orgname = editText_Org.getText().toString();
                String orgnumber = editText_Org_Number.getText().toString();
                String orgaddress = editText_Org_Address.getText().toString();
                System.out.println(orgaddress);

                sendSignupRequest(username, password, name, phoneNumber, userType[0], orgname, orgnumber, orgaddress);
            }
        });
    }

    public void sendSignupRequest(String username, String password, String name, String phoneNumber, String userType, String orgname, String orgnumber, String orgaddress) {
        if (userType == null) {
            // 사용자 유형을 선택하지 않았습니다. 에러 메시지를 표시하고 회원 가입 요청을 보내지 않습니다.
            Toast.makeText(RegisterActivity.this, "사용자 유형을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http:/192.168.45.93:8040") // 스프링 부트 서버의 주소로 변경해야 합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        SignupRequest signupRequest = new SignupRequest(username, password, name, phoneNumber, userType, orgname, orgnumber, orgaddress);

        Call<Void> call = apiService.signup(signupRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 회원가입 성공 처리
                    // 로그인 화면으로 이동 또는 기타 작업 수행
                    clearFields();
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    // 로그인 화면으로 이동
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // 현재 회원가입 화면을 종료
                } else {
                    // 회원가입 실패 처리
                    // 에러 메시지 표시 등의 작업 수행
                    clearFields();

                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            Toast.makeText(RegisterActivity.this, errorResponse, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call,Throwable t) {
                // 네트워크 오류 등의 실패 처리
                // 에러 메시지 표시 등의 작업 수행
                clearFields();

                Toast.makeText(RegisterActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 필드 초기화 메소드
    private void clearFields() {
        editText_username.setText("");
        editText_Password.setText("");
        editText_name.setText("");
        editText_phonenumber.setText("");

        if (radioButtonOrg.isChecked()) {
            editText_Org.setText("");
            editText_Org_Number.setText("");
            editText_Org_Address.setText("");
        }

        editText_Password_Check.setText("");
        radioGroup.clearCheck();
    }
}