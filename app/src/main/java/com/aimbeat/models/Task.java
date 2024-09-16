package com.aimbeat.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey
    @NonNull
    public String id; // Use String for Firebase compatibility

    public String title;
    public String description;
    public String dueDate;
    public String status;

    // Default constructor
    public Task() {}

    // Parameterized constructor
    public Task(@NonNull String id, String title, String description, String dueDate, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }
}
