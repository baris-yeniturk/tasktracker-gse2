package com.tasktracker.repository;

import com.tasktracker.model.Task;
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
}
