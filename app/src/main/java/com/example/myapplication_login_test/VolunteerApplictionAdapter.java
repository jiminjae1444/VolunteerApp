package com.example.myapplication_login_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerApplictionAdapter extends RecyclerView.Adapter<VolunteerApplictionAdapter.ViewHolder> {
    private List<VolunteerApplication> applications;
    private Retrofit retrofit;
    private ApiService apiService;


    public VolunteerApplictionAdapter(List<VolunteerApplication> applications) {
        this.applications = applications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_appliction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VolunteerApplication application = applications.get(holder.getAdapterPosition());

        // VolunteerApplication에서 봉사 이름과 관련된 필드를 가져오는 부분
        getVolunteerFormName(application.getVolunteerFormId(), holder);

        holder.slotTextView.setText(String.format("신청 상태 : " + application.getStatus()));
        holder.applicationDateTextView.setText("신청 날짜 : " + application.getApplicationDate());

        holder.viewCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 신청 취소 버튼 클릭 처리
                // 여기서 신청을 취소하는 논리를 구현할 수 있습니다.
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    cancelApplication(application.getId(), adapterPosition, holder.itemView.getContext());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView volunteerNameTextView;
        TextView slotTextView;
        TextView applicationDateTextView;
        Button viewCancelButton;

        ViewHolder(View itemView) {
            super(itemView);
            volunteerNameTextView = itemView.findViewById(R.id.volunteernameTextView);
            slotTextView = itemView.findViewById(R.id.slotTextView);
            applicationDateTextView = itemView.findViewById(R.id.applictionDateTextView);
            viewCancelButton = itemView.findViewById(R.id.viewCancelButton);
        }
    }

    // 서버에서 VolunteerForm 정보를 가져오는 메서드
    private void getVolunteerFormName(long volunteerFormId, ViewHolder holder) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
        Call<VolunteerForm> call = apiService.getVolunteerForm(volunteerFormId);
        call.enqueue(new Callback<VolunteerForm>() {
            @Override
            public void onResponse(Call<VolunteerForm> call, Response<VolunteerForm> response) {
                if (response.isSuccessful()) {
                    VolunteerForm volunteerForm = response.body();
                    if (volunteerForm != null) {
                        String volunteerFormName = volunteerForm.getTitle();
                        holder.volunteerNameTextView.setText(volunteerFormName);
                    } else {
                        showToast(holder.itemView.getContext(), "봉사 신청 내역이 없습니다.");
                    }
                } else {
                    showToast(holder.itemView.getContext(), "서버오류.");
                }
            }

            @Override
            public void onFailure(Call<VolunteerForm> call, Throwable t) {
                showToast(holder.itemView.getContext(), "네트워크 오류.");
            }
        });
    }
    private void cancelApplication(long applicationId, int position, Context context) {
        // 서버에서 해당 applicationId에 대한 봉사 신청을 삭제하는 요청
        Call<Void> call = apiService.cancelVolunteerApplication(applicationId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 서버에서 삭제 성공한 경우, 어댑터에서 해당 항목 제거 및 갱신
                    applications.remove(position);
                    notifyDataSetChanged();
                    showToast(context, "봉사 신청이 취소되었습니다.");
                } else {
                    // 서버에서 오류 응답이 왔을 경우에 대한 처리
                    showToast(context, "서버 오류로 봉사 신청을 취소할 수 없습니다.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류 또는 예외 발생 시에 대한 처리
                showToast(context, "네트워크 오류로 봉사 신청을 취소할 수 없습니다.");
            }
        });
    }
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}