package com.aimbeat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aimbeat.R;
import com.aimbeat.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskInteractionListener onTaskInteractionListener;

    public interface OnTaskInteractionListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(OnTaskInteractionListener listener) {
        this.onTaskInteractionListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.titleTextView.setText("Title: "+currentTask.title);
        holder.descriptionTextView.setText("Description: "+currentTask.description);
        holder.dueDateTextView.setText("Due Date: "+currentTask.dueDate);
        holder.statusTextView.setText("Status: "+currentTask.status);

        holder.editButton.setOnClickListener(v -> {
            if (onTaskInteractionListener != null) {
                onTaskInteractionListener.onEditClick(currentTask);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onTaskInteractionListener != null) {
                onTaskInteractionListener.onDeleteClick(currentTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dueDateTextView;
        private final TextView statusTextView;
        private final Button editButton;
        private final Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            descriptionTextView = itemView.findViewById(R.id.task_description);
            dueDateTextView = itemView.findViewById(R.id.task_due_date);
            statusTextView = itemView.findViewById(R.id.task_status);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
