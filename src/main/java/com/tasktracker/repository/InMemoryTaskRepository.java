package com.tasktracker.repository;

import com.tasktracker.model.Task;
import java.util.*;

public class InMemoryTaskRepository implements TaskRepository{

    private final Map<Long, Task> tasks = new HashMap<>();
    private long nextId = 1;

    @Override
    public Task save(Task task){
        if (task.getTaskId() == 0){
            task.setTaskId(nextId);
            nextId++;
        }
        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(long id){
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task update(Task task){
        if (!tasks.containsKey(task.getTaskId())){
            throw new IllegalArgumentException("Task nicht gefunden");
        }
        tasks.put(task.getTaskId(), task);
        return task;
    }
}
