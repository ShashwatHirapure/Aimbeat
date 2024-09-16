package com.aimbeat.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.aimbeat.AppDatabase;
import com.aimbeat.daos.TaskDao;
import com.aimbeat.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;

public class TaskRepository {
    private final TaskDao taskDao;
    private final DatabaseReference firebaseDatabaseReference;
    private final LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("tasks").child(""+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        allTasks = taskDao.getAllTasks();

        syncWithFirebase();
    }

    private void syncWithFirebase() {
        firebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Task> tasksFromFirebase = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && task.id != null) { // Ensure task and task.id are not null
                        tasksFromFirebase.add(task);
                    }
                }
                // Insert or update tasks in Room Database
                new Thread(() -> {
                    for (Task task : tasksFromFirebase) {
                        if (task.id != null) { // Ensure task.id is not null
                            Task existingTask = taskDao.getTaskById(task.id);
                            if (existingTask != null) {
                                taskDao.update(task); // Update existing task
                            } else {
                                taskDao.insert(task); // Insert new task
                            }
                        } else {
                            Log.e("TaskRepository", "Task ID is null: " + task);
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TaskRepository", "Firebase sync cancelled: " + databaseError.getMessage());
            }
        });
    }


    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        if (task.id == null) {
            task.id = firebaseDatabaseReference.push().getKey();
        }

        new Thread(() -> {
            // Insert or update in Room Database
            if (taskDao.getTaskById(task.id) != null) {
                taskDao.update(task);
            } else {
                taskDao.insert(task);
            }
            if (task.id != null) {
                firebaseDatabaseReference.child(task.id).setValue(task); // Add to Firebase
            }
        }).start();
    }

    public void insertAll(List<Task> tasks) {
        new Thread(() -> {
            for (Task task : tasks) {
                if (task.id == null) {
                    task.id = firebaseDatabaseReference.push().getKey();
                }
                if (taskDao.getTaskById(task.id) != null) {
                    taskDao.update(task);
                } else {
                    taskDao.insert(task);
                }
                if (task.id != null) {
                    firebaseDatabaseReference.child(task.id).setValue(task); // Add to Firebase
                }
            }
        }).start();
    }

    public void update(Task task) {
        new Thread(() -> {
            taskDao.update(task);
            if (task.id != null) {
                firebaseDatabaseReference.child(task.id).setValue(task); // Update in Firebase
            }
        }).start();
    }

    public void delete(Task task) {
        new Thread(() -> {
            taskDao.delete(task);
            if (task.id != null) {
                firebaseDatabaseReference.child(task.id).removeValue(); // Remove from Firebase
            }
        }).start();
    }
    public void clearAllTasks() {
        new Thread(() -> taskDao.deleteAllTasks()).start(); // Clear Room DB
    }


    public LiveData<List<Task>> getTasksByStatus(String status) {
        return taskDao.getTasksByStatus(status);
    }
}
