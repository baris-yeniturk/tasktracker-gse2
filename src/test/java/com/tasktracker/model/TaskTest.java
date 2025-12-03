package com.tasktracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest{
    /*
     * Unit tests für die Task-Klasse
     * TDD: Rot -> Grün -> Refactor
     */

    @Test
    void testCreateTaskWithValidDescription(){
        Task task = new Task("Test");
        assertEquals("Test", task.getDescription());
    }

    @Test
    void testCreateTaskWithEmptyDescription(){
        assertThrows(IllegalArgumentException.class, () -> new Task(""));
    }

    @Test
    void testCreateTaskWithNullDescription(){
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    void testCreateTaskWithWhitespaceDescription(){
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    void testUpdateTaskDescription(){
        Task task = new Task("Alte Beschreibung");

        task.setDescription("Neue Beschreibung");

        assertEquals("Neue Beschreibung", task.getDescription());
    }

    @Test
    void testUpdateTaskWithEmptyDescription(){
        Task task = new Task("Original");
        assertThrows(IllegalArgumentException.class, () -> task.setDescription(""));
    }

    @Test
    void testUpdateTaskStatus(){
        Task task = new Task("Aufgabe");
        assertEquals(TaskStatus.OFFEN, task.getTaskStatus());
        task.setTaskStatus(TaskStatus.ERLEDIGT);
        assertEquals(TaskStatus.ERLEDIGT, task.getTaskStatus());
    }
}
