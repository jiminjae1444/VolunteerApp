package com.example.myapplication_login_test;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerFormActivity extends AppCompatActivity {
    private EditText Volunteer_name, persons, description, hours, start_date, end_date;
    EditText editaddress;
    Button button_map, button_form;
    double latitude = 37.51231313, longitude = 127.12316546;
    String[][] priority = { { null } };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteerform);
        Button logout = findViewById(R.id.logoutButton);
        initView();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerFormActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        Spinner spinner = findViewById(R.id.spinner);
        Volunteer_name = findViewById(R.id.Volunteer_name);
        persons = findViewById(R.id.persons);
        start_date = findViewById(R.id.editTextStartDate);
        end_date = findViewById(R.id.editTextEndDate);
        description = findViewById(R.id.description);
        hours = findViewById(R.id.hours);
        editaddress = findViewById(R.id.editaddress);
        button_map = findViewById(R.id.button_map);
        button_form = findViewById(R.id.button_form);

        // 스피너에 표시할 데이터 준비 (문자열 배열)
        String[] items = {"선착순", "경력자만"};

        // ArrayAdapter를 사용하여 데이터를 스피너에 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        // 스피너 드롭다운 목록의 스타일 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 어댑터 설정
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 스피너에서 항목을 선택했을 때 실행할 코드 작성
                String selectedValue = (String) parentView.getItemAtPosition(position);
                button_form.setEnabled(true);
                // 선택된 항목 처리
                if ("선착순".equals(selectedValue)) {
                    priority[0][0] = "선착순";
                } else if ("경력자만".equals(selectedValue)) {
                    priority[0][0] = "경력자만";
                } else {
                    priority[0][0] = null; // 아무 항목도 선택하지 않았을 때 null로 설정
                    // 아무 항목도 선택하지 않았을 때 경고 메시지 표시 (예를 들어, Toast 메시지 사용)
                    Toast.makeText(getApplicationContext(), "항목을 선택하십시오.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무 항목도 선택하지 않았을 때 실행할 코드 작성
                priority[0] = null; // null로 설정
                button_form.setEnabled(false);
                // 아무 항목도 선택하지 않았을 때 경고 메시지 표시 (예를 들어, Toast 메시지 사용)
                Toast.makeText(getApplicationContext(), "항목을 선택하십시오.", Toast.LENGTH_SHORT).show();
            }
        });

        button_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMapButtonClick();
            }
        });

        button_form.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                handleFormButtonClick();
            }
        });
    }

    private void handleMapButtonClick() {
        String et = editaddress.getText().toString();

        Geocoder geocoder = new Geocoder(VolunteerFormActivity.this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocationName(et, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "&z=10");
                intent.setData(uri);
                startActivity(intent);
            } else {
                // Handle the case when no address is found
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception here
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleFormButtonClick() {
        if (isFormValid()) {
            String volunteerName = Volunteer_name.getText().toString();
            String volunteerPersons = persons.getText().toString().replaceAll("[^0-9]", "");
            String volunteerHour = hours.getText().toString().replaceAll("[^0-9]", "");
            String volunteerLocation = editaddress.getText().toString();
            String volunteerDescription = description.getText().toString();
            String selectedPriority = priority[0][0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = start_date.getText().toString();
            String endDate = end_date.getText().toString();
            try {
                LocalDate startDateTxt = LocalDate.parse(start_date.getText().toString(), formatter);
                LocalDate endDateTxt =  LocalDate.parse(end_date.getText().toString(), formatter);
            } catch (DateTimeParseException e) {
                // 날짜 형식이 올바르지 않을 때 사용자에게 메시지 표시
                Toast.makeText(getApplicationContext(), "올바른 날짜 및 시간 형식을 입력하십시오.", Toast.LENGTH_SHORT).show();
                Log.e("VolunteerFormActivity", "Error parsing date: " + e.getMessage());
                return;
            }

            sendVolunteerForm(volunteerName, volunteerLocation, volunteerDescription, startDate, endDate, volunteerPersons, volunteerHour, selectedPriority);
        } else {
            // 폼이 유효하지 않을 때 수행할 로직을 여기에 추가하세요.
            // 예를 들어, Toast 메시지를 표시하거나 사용자에게 알림을 주는 등의 처리를 할 수 있습니다.
            Toast.makeText(getApplicationContext(), "입력 필드를 모두 작성하고 항목을 선택하십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFormValid() {
        return !Volunteer_name.getText().toString().isEmpty() &&
                !persons.getText().toString().isEmpty() &&
                !start_date.getText().toString().isEmpty() &&
                !end_date.getText().toString().isEmpty() &&
                !hours.getText().toString().isEmpty() &&
                !editaddress.getText().toString().isEmpty() &&
                !description.getText().toString().isEmpty() &&
                priority[0][0] != null;
    }

    public void sendVolunteerForm(String volunteerName, String volunteerLocation, String volunteerDescription, String startDate, String endDate, String volunteerPersons, String volunteerHour, String selectedPriority) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        // Retrofit을 사용하여 서버로 POST 요청을 보내 모집 정보 전송
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 서버 API 엔드포인트 URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        VolunteerForminfo volunteerForminfo = new VolunteerForminfo(volunteerName, volunteerLocation, volunteerDescription, startDate, endDate, volunteerPersons, volunteerHour, selectedPriority);

        Call<Void> call = apiService.createVolunteer(volunteerForminfo);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulFormSubmission();
                } else {
                    handleFormSubmissionFailure(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleFormSubmissionFailure(t);
            }
        });
    }

    private void handleSuccessfulFormSubmission() {
        Toast.makeText(getApplicationContext(), "모집글이 생성되었습니다!!", Toast.LENGTH_SHORT).show();
        String volunteerName = Volunteer_name.getText().toString();
        String volunteerHour = hours.getText().toString();
        String volunteerPersons = persons.getText().toString();
        // VolunteerEvent 객체 생성
        resetFormFields();
        //모집글 등록 성공시
        // 스피너 초기 선택 항목으로 설정
        Intent intent = new Intent(VolunteerFormActivity.this,VolunteerListActivity.class);
        startActivity(intent);
        Log.e("MyApp", "앱이 강제로 종료됨");
        finish();
    }

        // 비동기적으로 실행
    private void handleFormSubmissionFailure(Object errorObject) {
        Log.e("VolunteerFormActivity", "HTTP 요청 실패: " + errorObject.toString());
        Toast.makeText(getApplicationContext(), "모집글 등록 실패!!", Toast.LENGTH_SHORT).show();
    }

    private void resetFormFields() {
        Volunteer_name.setText(""); // 봉사 이름 초기화
        persons.setText(""); // 모집 인원 초기화
        start_date.setText(""); // 시작일 초기화
        end_date.setText(""); // 마감일 초기화
        hours.setText(""); // 봉사 시간 초기화
        editaddress.setText(""); // 봉사 장소 초기화
        description.setText(""); // 주요 내용 초기화
    }
}

