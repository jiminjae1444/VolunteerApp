package com.example.myapplication_login_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerListActivity extends Activity {
    private RecyclerView volunteerRecyclerView;
    private VolunteerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteerlist);
        Button logout = findViewById(R.id.logoutButton);
        volunteerRecyclerView = findViewById(R.id.volunteerRecyclerView);
        adapter = new VolunteerListAdapter(new ArrayList<>());
        volunteerRecyclerView.setAdapter(adapter);
        Button main = findViewById(R.id.buttonToMain);
        volunteerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchVolunteerList();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        adapter.setOnItemClickListener(new VolunteerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VolunteerEvent clickedEvent = adapter.getVolunteerEvents().get(position);
                String username = getIntent().getStringExtra("username");
                Intent intent = new Intent(VolunteerListActivity.this, VolunteerView.class);
                intent.putExtra("volunteerName", clickedEvent.getVolunteerName());
                intent.putExtra("username",username);
                System.out.println(clickedEvent.getVolunteerName());
                startActivity(intent);
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerListActivity.this, MainHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchVolunteerList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040/") // 실제 서버 주소로 변경
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        retrofit2.Call<List<VolunteerList>> call = apiService.getVolunteerList();

        call.enqueue(new Callback<List<VolunteerList>>() {
            @Override
            public void onResponse(retrofit2.Call<List<VolunteerList>> call, Response<List<VolunteerList>> response) {
                if (response.isSuccessful()) {
                    List<VolunteerList> volunteerLists = response.body();

                    // VolunteerList를 VolunteerEvent로 변환
                    List<VolunteerEvent> volunteerEvents = convertToVolunteerEvents(volunteerLists);

                    // 받아온 목록을 어댑터에 추가하고 갱신
                    adapter.setVolunteerEvents(volunteerEvents);
                    adapter.notifyDataSetChanged();
                } else {
                    // 서버로부터 오류 응답을 받은 경우 처리
                    // 예: Toast 메시지를 통한 사용자 알림
                    Toast.makeText(VolunteerListActivity.this, "서버에서 목록을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<VolunteerList>> call, Throwable t) {
                // 통신 실패 시 호출되는 메서드
                // 예: Toast 메시지를 통한 사용자 알림
                Toast.makeText(VolunteerListActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<VolunteerEvent> convertToVolunteerEvents(List<VolunteerList> volunteerLists) {
        List<VolunteerEvent> volunteerEvents = new ArrayList<>();

        for (VolunteerList volunteerList : volunteerLists) {
            VolunteerEvent volunteerEvent = new VolunteerEvent(
                    volunteerList.getVolunteerName(),
                    volunteerList.getVolunteerHour(),
                    volunteerList.getVolunteerPersons()
            );
            volunteerEvents.add(volunteerEvent);
        }

        return volunteerEvents;
    }
}