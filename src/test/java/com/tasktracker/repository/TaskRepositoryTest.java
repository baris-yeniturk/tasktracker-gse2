package com.tasktracker.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRepositoryTest {
    /*
     * Tests für TaskRepository
     * Testet CRUD Operationen
     * TDD: ROT -> GRÜN -> REFACTOR
     */

    private TaskRepository repository;

    @BeforeEach
    void setup(){
        repository = new InMemoryTaskRepository();
    }

    @Test
    void testSaveTaskAndAssignId(){
        Task task = new Task("Aufgabe");

        Task savedTask = repository.save(task);

        assertNotNull(savedTask);
        assertTrue(savedTask.getTaskId() > 0);
        assertEquals("Aufgabe", savedTask.getDescription());
    }
}
