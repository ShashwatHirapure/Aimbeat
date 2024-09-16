package com.aimbeat.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.aimbeat.models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task_table ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task_table WHERE id = :id")
    Task getTaskById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Insert
    void insertAll(Task... tasks);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table WHERE status = :status ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByStatus(String status);

    @Query("DELETE FROM task_table")
    void deleteAllTasks(); // Add this method

}
