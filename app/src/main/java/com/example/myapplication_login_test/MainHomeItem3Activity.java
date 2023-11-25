package com.example.myapplication_login_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainHomeItem3Activity extends Fragment {
    private ApiService apiService;
    private List<VolunteerForm> volunteerForms;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_mainhome_item1, container, false);
        ImageView imageView = rootView.findViewById(R.id.imageView);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // 서버에서 봉사폼 리스트 받아오기
        getAllVolunteerForms();



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VolunteerListActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // 서버에서 모든 봉사폼을 받아오는 메서드
    private void getAllVolunteerForms() {
        Call<List<VolunteerForm>> callAllForms = apiService.getVolunteerForm();
        callAllForms.enqueue(new Callback<List<VolunteerForm>>() {
            @Override
            public void onResponse(Call<List<VolunteerForm>> call, Response<List<VolunteerForm>> response) {
                if (response.isSuccessful()) {
                    volunteerForms = response.body();
                    // 로그 추가
                    Log.d("MainHomeItem1Activity", "Volunteer forms loaded successfully. Size: " + volunteerForms.size());
                    // 랜덤으로 봉사폼 선택 및 화면 업데이트
                    selectRandomVolunteerForm();
                } else {
                    // Handle unsuccessful response
                    Log.e("MainHomeItem1Activity", "Failed to load volunteer forms. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<VolunteerForm>> call, Throwable t) {
                // Handle failure
                Log.e("MainHomeItem1Activity", "Failed to load volunteer forms. Error: " + t.getMessage());
            }
        });
    }

    // 서버에서 받아온 봉사폼 리스트에서 랜덤으로 하나를 선택하는 메서드
    private void selectRandomVolunteerForm() {
        if (volunteerForms != null && !volunteerForms.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(volunteerForms.size());
            Log.d("MainHomeItem1Activity", "Random index: " + randomIndex);
            if (randomIndex >= 0 && randomIndex < volunteerForms.size()) {
                VolunteerForm selectedForm = volunteerForms.get(randomIndex);
                Log.d("MainHomeItem1Activity", "random size: " + volunteerForms.size());
                Log.d("MainHomeItem1Activity", "random index: " + randomIndex);
                // 로그로 선택된 봉사폼 ID 출력
                Log.d("MainHomeItem1Activity", "Selected VolunteerForm ID: " + selectedForm.getId());

                // 업데이트 메서드 호출하여 선택된 봉사폼의 정보를 표시
                updateTextViews(selectedForm);
            } else {
                Log.e("MainHomeItem1Activity", "Random index out of bounds: " + randomIndex);
            }
        }
    }

    // TextView 업데이트 메서드


    private void updateTextViews(VolunteerForm volunteerForm) {
        TextView volunteerNameTextView = getView().findViewById(R.id.Volunteer_title);
        TextView personsTextView = getView().findViewById(R.id.person);
        TextView priorityTextView = getView().findViewById(R.id.priority2);
        TextView durationTextView = getView().findViewById(R.id.duration3);
        TextView duration2TextView = getView().findViewById(R.id.duration4);
        TextView addressTextView = getView().findViewById(R.id.editaddress2);
        TextView onOrOffTextView = getView().findViewById(R.id.OnorOff2);

        volunteerNameTextView.setText(volunteerForm.getTitle());
        personsTextView.setText(String.valueOf(volunteerForm.getSlots()+"명"));
        priorityTextView.setText(volunteerForm.getPriority());
        durationTextView.setText(volunteerForm.getStart_date().toString());
        duration2TextView.setText(volunteerForm.getEnd_date().toString());
        addressTextView.setText(volunteerForm.getLocation());
        onOrOffTextView.setText(String.valueOf(volunteerForm.getRecruitment_hours()+"시간"));

        Log.d("MainHomeItem1Activity", "Selected VolunteerForm ID: " + volunteerForm.getId());
    }
}