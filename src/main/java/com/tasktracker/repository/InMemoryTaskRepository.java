package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public boolean delete(long id){
        if (id <= 0){
            return false;
        }
        return tasks.remove(id) != null;
    }

    @Override
    public boolean existsById(long id){
        if (id <= 0){
            return false;
        }
        return tasks.containsKey(id);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status){
        return tasks.values().stream()
                .filter(task -> task.getTaskStatus() == status)
                .sorted(Comparator.comparing(Task::getTaskId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByDescriptionContaining(String keyword){
        if (keyword == null || keyword.trim().isEmpty()){
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase();
        return tasks.values().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .sorted(Comparator.comparing(Task::getTaskId))
                .collect(Collectors.toList());
    }

    @Override
    public long count(){
        return tasks.size();
    }

    @Override
    public long countByStatus(TaskStatus status){
        return tasks.values().stream()
                .filter(task -> task.getTaskStatus() == status)
                .count();
    }

    @Override
    public void saveAll(List<Task> taskList){
        if (taskList == null) return;

        for (Task task : taskList){
            save(task);
        }
    }

    @Override
    public void deleteAll(){
        tasks.clear();
        nextId = 1;
    }
}
