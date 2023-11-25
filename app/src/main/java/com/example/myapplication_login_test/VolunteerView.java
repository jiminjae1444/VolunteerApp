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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteerview);
        Button logout = findViewById(R.id.logoutButton);

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerView.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        String volunteerName = intent.getStringExtra("volunteerName");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Step 1: 전체 봉사폼 리스트를 받아오기
        Call<List<com.example.myapplication_login_test.VolunteerForm>> callAllForms = apiService.getVolunteerForm();
        callAllForms.enqueue(new Callback<List<VolunteerForm>>() {
            @Override
            public void onResponse(@NonNull Call<List<VolunteerForm>> call, @NonNull Response<List<VolunteerForm>> response) {
                if (response.isSuccessful()) {
                    List<VolunteerForm> volunteerFormList = response.body();

                    runOnUiThread(() -> {
                        if (volunteerFormList != null && !volunteerFormList.isEmpty()) {
                            // Step 2: 전체 봉사폼 리스트 중에서 이름이 일치하는 봉사폼 찾기
                            VolunteerForm matchingForm = findMatchingForm(volunteerFormList, volunteerName);

                            if (matchingForm != null) {
                                // 봉사 정보를 텍스트 뷰에 표시
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

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String et = Location.getText().toString();

                Geocoder geocoder = new Geocoder(VolunteerView.this, Locale.KOREA);
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
        });
        buttonlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerView.this, VolunteerListActivity.class);
                startActivity(intent);
            }
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
}
