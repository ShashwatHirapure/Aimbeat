package com.aimbeat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aimbeat.adapters.TaskAdapter;
import com.aimbeat.databinding.ActivityTaskManagementBinding;
import com.aimbeat.models.Task;
import com.aimbeat.repositories.TaskRepository;
import com.aimbeat.viewmodels.TaskViewModel;
import com.aimbeat.viewmodels.TaskViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private ActivityTaskManagementBinding binding;
    private FirebaseAuth mAuth;
    private TaskRepository taskRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        taskRepository = new TaskRepository(getApplication());

        setupStatusSpinner();
        setupRecyclerView();

        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication())).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> taskAdapter.setTasks(tasks));

        binding.addTaskButton.setOnClickListener(v -> showAddTaskDialog());
        binding.logoutButton.setOnClickListener(v->signOut());
        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String status = binding.statusSpinner.getSelectedItem().toString();
                if ("All".equals(status)) {
                    taskViewModel.getAllTasks().observe(TaskManagementActivity.this, tasks -> taskAdapter.setTasks(tasks));
                } else {
                    taskViewModel.getTasksByStatus(status).observe(TaskManagementActivity.this, tasks -> taskAdapter.setTasks(tasks));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        saveTokenToDatabase(token);
                    } else {
                        Toast.makeText(TaskManagementActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void signOut() {
        taskRepository.clearAllTasks();

        mAuth.signOut();
        Toast.makeText(TaskManagementActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        // Redirect to Login Activity
        Intent intent = new Intent(TaskManagementActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        taskAdapter = new TaskAdapter(this);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupStatusSpinner() {
        List<String> statuses = Arrays.asList("All", "Pending", "In Progress", "Completed");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusSpinner.setAdapter(adapter);
    }

    private void showAddTaskDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText titleInput = dialogView.findViewById(R.id.edit_task_title);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_task_description);
        TextView dueDateInput = dialogView.findViewById(R.id.edit_task_due_date);
        Spinner statusSpinner = dialogView.findViewById(R.id.spinner_task_status);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskManagementActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dueDateInput.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dueDateInput.setOnClickListener(v -> {
            datePickerDialog.show();
        });
        builder.setTitle("Add New Task").setView(dialogView).setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();
            String status = statusSpinner.getSelectedItem().toString();

            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(TaskManagementActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task();
            newTask.title = title;
            newTask.description = description;
            newTask.dueDate = dueDate;
            newTask.status = status;

            taskViewModel.insert(newTask);
            Toast.makeText(TaskManagementActivity.this, "Task added", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void showEditTaskDialog(Task task) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        EditText titleInput = dialogView.findViewById(R.id.edit_task_title);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_task_description);
        TextView dueDateInput = dialogView.findViewById(R.id.edit_task_due_date);
        Spinner statusSpinner = dialogView.findViewById(R.id.spinner_task_status);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskManagementActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dueDateInput.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dueDateInput.setOnClickListener(v -> {
            datePickerDialog.show();
        });
        titleInput.setText(task.title);
        descriptionInput.setText(task.description);
        dueDateInput.setText(task.dueDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        int statusPosition = adapter.getPosition(task.status);
        statusSpinner.setSelection(statusPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task").setView(dialogView).setPositiveButton("Save", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();
            String status = statusSpinner.getSelectedItem().toString();

            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(TaskManagementActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            task.title = title;
            task.description = description;
            task.dueDate = dueDate;
            task.status = status;

            taskViewModel.update(task);
            Toast.makeText(TaskManagementActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
    }

    @Override
    public void onEditClick(Task task) {
        showEditTaskDialog(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        new AlertDialog.Builder(this).setTitle("Delete Task").setMessage("Are you sure you want to delete this task?").setPositiveButton("Yes", (dialog, which) -> {
            taskViewModel.delete(task);
            Toast.makeText(TaskManagementActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create().show();
    }
    private void saveTokenToDatabase(String token) {
        String Uid = FirebaseAuth.getInstance().getUid();
        if (Uid != null) {
            // Replace . with , in email to create a valid Firebase key
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            HashMap hashMap = new HashMap();
            hashMap.put("deviceToken", token);
            hashMap.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            databaseReference.child("users").child(Uid).setValue(hashMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Task", "Token saved successfully");
                        } else {
                            Log.e("Task", "Failed to save token", task.getException());
                        }
                    });
        } else {
            Log.e("Task", "UID is null");
        }
    }
}
