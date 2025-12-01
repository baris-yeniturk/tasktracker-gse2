package com.tasktracker.model;

public class Task{
    private String description;

    public Task(String description){
        if (description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("No description found");
        }
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
