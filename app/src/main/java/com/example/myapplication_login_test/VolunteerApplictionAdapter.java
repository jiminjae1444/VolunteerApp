package com.example.myapplication_login_test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerApplictionAdapter extends RecyclerView.Adapter<VolunteerApplictionAdapter.ViewHolder> {
    private List<VolunteerApplicationDTO> applications;
    private ApiService apiService;

    public VolunteerApplictionAdapter(List<VolunteerApplicationDTO> applications) {
        this.applications = applications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_appliction_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VolunteerApplicationDTO applicationDTO = applications.get(holder.getAdapterPosition());

        String title = applicationDTO.getVolunteerFormTitle();
        holder.volunteerNameTextView.setText(title);
        holder.slotTextView.setText(applicationDTO.getStatus());
        holder.applicationDateTextView.setText(applicationDTO.getApplicationDate().toString());

        holder.viewCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    cancelApplication(applicationDTO.getId(), adapterPosition, holder.itemView.getContext());
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

    private void cancelApplication(long applicationId, int position, Context context) {
        // 서버에서 해당 applicationId에 대한 봉사 신청을 삭제하는 요청
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.45.93:8040")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
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