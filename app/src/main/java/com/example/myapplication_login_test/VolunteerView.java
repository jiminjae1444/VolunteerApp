package com.example.myapplication_login_test;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
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

        Retrofit retrofit = new Retrofit.Builder()
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
                    List<VolunteerForm> volunteerFormList = response.body();

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
            String volunteerFormTitle = VolunteerName.getText().toString();
            checkAndApplyForVolunteer(volunteerFormTitle, applicantUsername);
        });
    }

    // 이름이 일치하는 봉사폼 찾기
    private VolunteerForm findMatchingForm(List<VolunteerForm> formList, String volunteerName) {
        for (VolunteerForm form : formList) {
            if (form.getTitle().equals(volunteerName)) {
                return form;
            }
        }
        return null;
    }

    // 봉사 신청 메서드
    private void checkAndApplyForVolunteer(String volunteerFormTitle, String applicantUsername) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService volunteerService = retrofit.create(ApiService.class);

        // 봉사폼 제목을 기반으로 해당 봉사폼의 ID를 찾아오기
        Call<Long> findIdCall = volunteerService.findVolunteerFormIdByTitle(volunteerFormTitle);
        findIdCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful()) {
                    Long volunteerFormId = response.body();

                    if (volunteerFormId != null) {
                        // 찾아온 봉사폼 ID를 이용하여 봉사폼의 마감 여부 확인
                        Call<Boolean> checkClosedCall = volunteerService.checkVolunteerFormClosed(volunteerFormId);
                        checkClosedCall.enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                                if (response.isSuccessful()) {
                                    Boolean isClosed = response.body();

                                    if (isClosed != null && !isClosed) {
                                        // 마감되지 않았다면 봉사 신청 진행
                                        applyForVolunteer(volunteerFormTitle,applicantUsername);
                                    } else {
                                        // 마감된 경우 처리 (예: 메시지 출력, 버튼 비활성화 등)
                                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "이미 마감된 봉사입니다.", Toast.LENGTH_SHORT).show());
                                        // 예시: 버튼 비활성화
                                        Apply.setEnabled(false);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                                // 네트워크 오류에 대한 처리
                            }
                        });
                    } else {
                        // 봉사폼 ID가 null인 경우에 대한 처리
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "봉사폼 ID가 없습니다.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // 서버 응답이 실패한 경우에 대한 처리
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                // 네트워크 오류에 대한 처리
            }
        });
    }

    // 봉사 신청 메서드
    private void applyForVolunteer(String volunteerFormTitle, String applicantUsername) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService volunteerService = retrofit.create(ApiService.class);

        // 봉사폼 제목을 기반으로 해당 봉사폼의 ID를 찾아오기
        Call<Long> findIdCall = volunteerService.findVolunteerFormIdByTitle(volunteerFormTitle);
        findIdCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful()) {
                    Long volunteerFormId = response.body();

                    if (volunteerFormId != null) {
                        // 봉사폼 ID를 기반으로 신청서 생성
                        VolunteerApplicationRequest application = new VolunteerApplicationRequest();
                        application.setVolunteerFormId(volunteerFormId);
                        application.setApplicantUsername(applicantUsername);

                        // 봉사 신청
                        Call<Void> applyCall = volunteerService.applyForVolunteer(application);
                        applyCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "봉사 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show());
                                } else {
                                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "봉사 신청 실패. 응답 코드: " + response.code(), Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        // 봉사폼 ID가 null인 경우에 대한 처리
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "봉사폼 ID가 없습니다.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // 서버 응답이 실패한 경우에 대한 처리
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                // 네트워크 오류에 대한 처리
            }
        });
    }
}


