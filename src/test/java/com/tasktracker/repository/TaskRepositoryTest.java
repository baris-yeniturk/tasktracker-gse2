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
        assertEquals(TaskStatus.OFFEN, savedTask.getTaskStatus());
    }

    @Test
    void testFindTaskById(){
        Task task = new Task("Aufgabe mit ID");
        Task savedTask = repository.save(task);
        long taskId = savedTask.getTaskId();

        Optional<Task> found = repository.findById(taskId);

        assertTrue(found.isPresent());
        assertEquals(taskId, found.get().getTaskId());
        assertEquals("Aufgabe mit ID", found.get().getDescription());
    }

    @Test
    void testReturnEmptyForNonExistingId(){
        Optional<Task> found = repository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAllTasks(){
        Task task1 = new Task("Aufgabe 1");
        Task task2 = new Task("Aufgabe 2");
        Task task3 = new Task("Aufgabe 3");

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        List<Task> allTasks = repository.findAll();

        assertEquals(3, allTasks.size());
        assertTrue(allTasks.stream().anyMatch(t -> t.getDescription().equals("Aufgabe 1")));
        assertTrue(allTasks.stream().anyMatch(t -> t.getDescription().equals("Aufgabe 2")));
        assertTrue(allTasks.stream().anyMatch(t -> t.getDescription().equals("Aufgabe 3")));
    }

    @Test
    void testFindAllWhenEmpty(){
        List<Task> allTasks = repository.findAll();

        assertNotNull(allTasks);
        assertTrue(allTasks.isEmpty());
    }
}
