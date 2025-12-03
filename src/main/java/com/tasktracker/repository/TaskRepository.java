package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/*
 * Repository Interface für CRUD-Operationen
 */

public interface TaskRepository{

    Task save(Task task);
    Optional<Task> findById(long id);
    List<Task> findAll();
    Task update(Task task);
    boolean delete(long id);
    boolean existsById(long id);

    // Neue Methoden für CRUD-Operationen

    List<Task> findByStatus(TaskStatus status);
    List<Task> findByDescriptionContaining(String keyword);
    long count();
    long countByStatus(TaskStatus status);
    void saveAll(List<Task> taskList);
    void deleteAll();
}
