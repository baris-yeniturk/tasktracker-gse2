package com.tasktracker.model;

public class Task{
    private long id;
    private String description;
    private TaskStatus status;

    public Task(String description){
        if (description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Leere Beschreibung");
        }
        this.description = description;
        this.status = TaskStatus.OFFEN;
    }

    public Task(long id, String description, TaskStatus status){
        if (description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Leere Beschreibung");
        }
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public void setDescription(String description){
        if (description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Leere Beschreibung");
        }
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

    public TaskStatus getTaskStatus(){
        return status;
    }

    public void setTaskStatus(TaskStatus status){
        this.status = status;
    }

    public long getTaskId(){
        return id;
    }

    public void setTaskId(long id){
        this.id = id;
    }
}
