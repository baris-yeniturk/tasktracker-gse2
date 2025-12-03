package com.tasktracker.model;

public class Task{
    private String description;
    private TaskStatus status;

    public Task(String description){
        if (description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Leere Beschreibung");
        }
        this.description = description;
        this.status = TaskStatus.OFFEN;
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
}
