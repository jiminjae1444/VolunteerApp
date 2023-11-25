package com.example.myapplication_login_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VolunteerListAdapter extends RecyclerView.Adapter<VolunteerListAdapter.ViewHolder> {

    private List<VolunteerEvent> volunteerEvents;
    private OnItemClickListener listener;

    public void addVolunteerEvent(VolunteerEvent volunteerEvent) {
        volunteerEvents.add(volunteerEvent);
        notifyDataSetChanged(); // 변경된 데이터를 화면에 갱신
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public VolunteerListAdapter(List<VolunteerEvent> volunteerEvents) {
        this.volunteerEvents = volunteerEvents;
    }
    public void setVolunteerEvents(List<VolunteerEvent> volunteerEvents) {
        this.volunteerEvents = volunteerEvents;
    }

    public List<VolunteerEvent> getVolunteerEvents() {
        return volunteerEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.volunteer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VolunteerEvent event = volunteerEvents.get(position);
        holder.nameTextView.setText(event.getVolunteerName());
        holder.timeTextView.setText(event.getVolunteerHour()+"시간");
        holder.availableSlotsTextView.setText(event.getVolunteerPersons() + "명");
        holder.viewDetailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return volunteerEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView timeTextView;
        public TextView availableSlotsTextView;
        public Button viewDetailsButton;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            availableSlotsTextView = view.findViewById(R.id.availableSlotsTextView);
            viewDetailsButton = view.findViewById(R.id.viewDetailsButton);
        }
    }
}
