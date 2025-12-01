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
}
