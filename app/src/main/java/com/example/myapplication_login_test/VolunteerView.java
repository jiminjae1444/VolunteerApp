package com.example.myapplication_login_test;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerView extends AppCompatActivity {
    double latitude = 37.51231313, longitude = 127.12316546;
    Button Apply;
    private Retrofit retrofit;
    private List<VolunteerForm> volunteerFormList; // 클래스 레벨에서 선언

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteerview);

        Button logout = findViewById(R.id.logoutButton);
        Button Apply = findViewById(R.id.button_Apply);
        Button buttonlist = findViewById(R.id.button_Tolist);
        TextView VolunteerName = findViewById(R.id.Volunteer_name);
        TextView Slots = findViewById(R.id.persons);
        TextView StartDate = findViewById(R.id.duration2);
        TextView EndDate = findViewById(R.id.duration);
        TextView Descrpition = findViewById(R.id.content);
        TextView Location = findViewById(R.id.editaddress);
        TextView Hours = findViewById(R.id.OnorOff);
        TextView Priority = findViewById(R.id.priority);
        Button buttonMap = findViewById(R.id.button_map2);

        logout.setOnClickListener(view -> {
            Intent intent = new Intent(VolunteerView.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        String volunteerName = intent.getStringExtra("volunteerName");

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Step 1: 전체 봉사폼 리스트를 가져오기
        Call<List<VolunteerForm>> callAllForms = apiService.getVolunteerForm();
        callAllForms.enqueue(new Callback<List<VolunteerForm>>() {
            @Override
            public void onResponse(@NonNull Call<List<VolunteerForm>> call, @NonNull Response<List<VolunteerForm>> response) {
                if (response.isSuccessful()) {
                    volunteerFormList = response.body();

                    runOnUiThread(() -> {
                        if (volunteerFormList != null && !volunteerFormList.isEmpty()) {
                            // Step 2: 이름이 일치하는 봉사폼 찾기
                            VolunteerForm matchingForm = findMatchingForm(volunteerFormList, volunteerName);

                            if (matchingForm != null) {
                                // 봉사 정보를 TextView에 표시
                                try {
                                    VolunteerName.setText(matchingForm.getTitle());
                                    Slots.setText(matchingForm.getSlots() + "명");
                                    StartDate.setText(matchingForm.getStart_date().toString());
                                    EndDate.setText(matchingForm.getEnd_date().toString());
                                    Descrpition.setText(matchingForm.getDescription());
                                    Location.setText(matchingForm.getLocation());
                                    Priority.setText(matchingForm.getPriority());
                                    Hours.setText(matchingForm.getRecruitment_hours() + "시간");
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "NullPointerException 발생", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "일치하는 봉사폼이 없습니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "봉사폼이 비어 있습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "서버 응답 실패. 응답 코드: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<List<VolunteerForm>> call, Throwable t) {
                Log.e("VolunteerView", "Network error: " + t.getMessage(), t);
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show());
            }
        });

        buttonMap.setOnClickListener(view -> {
            String et = Location.getText().toString();

            Geocoder geocoder = new Geocoder(VolunteerView.this, Locale.KOREA);
            try {
                List<Address> addresses = geocoder.getFromLocationName(et, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();

                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "&z=10");
                    intent1.setData(uri);
                    startActivity(intent1);
                } else {
                    // 주소를 찾지 못한 경우 처리
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 예외 처리
            }
        });

        buttonlist.setOnClickListener(view -> {
            Intent intent1 = new Intent(VolunteerView.this, VolunteerListActivity.class);
            startActivity(intent1);
        });

        // Volunteer 신청 버튼 클릭 리스너
        Intent intent2 = getIntent();
        String applicantUsername = intent2.getStringExtra("username");
        Apply.setOnClickListener(view -> {
            VolunteerForm matchingForm = findMatchingForm(volunteerFormList, volunteerName);
            if (matchingForm != null) {
                // 시작일이 현재 날짜보다 이전인지 확인
                String startDate = matchingForm.getStart_date();
                if (LocalDate.now().isBefore(LocalDate.parse(startDate))) {
                    sendVolunteerApplication(matchingForm.getTitle(), applicantUsername);
                } else {
                    // 시작일이 현재 날짜보다 이후인 경우
                    Toast.makeText(getApplicationContext(), "봉사 신청이 마감되었습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "일치하는 봉사폼이 없습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendVolunteerApplication(String volunteerName, String applicantUsername) {
        VolunteerApplicationRequest applicationRequest = new VolunteerApplicationRequest();
        applicationRequest.setVolunteerFormName(volunteerName);  // 봉사 폼 이름 설정
        applicationRequest.setApplicantUsername(applicantUsername); // 사용자명 설정

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040") // 서버 API 엔드포인트 URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService volunteerService = retrofit.create(ApiService.class);

        Call<Void> call = volunteerService.applyForVolunteer(applicationRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "봉사 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "봉사 신청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private VolunteerForm findMatchingForm(List<VolunteerForm> formList, String volunteerName) {
        for (VolunteerForm form : formList) {
            if (form.getTitle().equals(volunteerName)) {
                return form;
            }
        }
        return null;
    }
}
