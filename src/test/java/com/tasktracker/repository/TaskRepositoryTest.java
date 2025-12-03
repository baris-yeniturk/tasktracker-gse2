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
    void testFindByIdWhenIdIsNegative(){
        Optional<Task> found = repository.findById(-1L);
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

    @Test
    void testUpdateTaskDescription(){
        Task task = new Task("Aufgabe");
        Task saved = repository.save(task);

        saved.setTaskStatus(TaskStatus.ERLEDIGT);
        Task updated = repository.update(saved);

        assertEquals(TaskStatus.ERLEDIGT, updated.getTaskStatus());
    }

    @Test
    void testUpdateNonExistingTask(){
        Task nonExistingTask = new Task(999L, "Diese Aufgabe gibt es nicht", TaskStatus.OFFEN);

        assertThrows(IllegalArgumentException.class, () -> {
            repository.update(nonExistingTask);
        });
    }

    @Test
    void testDeleteTask(){
        Task task = new Task("Zu löschende Aufgabe");
        Task saved = repository.save(task);
        long taskId = saved.getTaskId();

        boolean deleted = repository.delete(taskId);

        assertTrue(deleted);
        assertFalse(repository.findById(taskId).isPresent());
    }

    @Test
    void testDeleteNonExistingTask(){
        boolean deleted = repository.delete(999L);
        assertFalse(deleted);
    }

    @Test
    void testDeleteWithNegativeId(){
        boolean deleted = repository.delete(-1L);
        assertFalse(deleted);
    }

    @Test
    void testTaskExistsById(){
        Task task = new Task("Aufgabe");
        Task saved = repository.save(task);

        assertTrue(repository.existsById(saved.getTaskId()));
    }

    @Test
    void testExistsByIdForNonExistingTask(){
        assertFalse(repository.existsById(999L));
    }

    @Test
    void testExistByIdWithNegativeId(){
        assertFalse(repository.existsById(-1L));
    }

    @Test
    void testFindByStatus() {
        Task task1 = new Task("Offene Aufgabe");
        Task task2 = new Task("Erledigte Aufgabe");
        task2.setTaskStatus(TaskStatus.ERLEDIGT);

        repository.save(task1);
        repository.save(task2);

        List<Task> openTasks = repository.findByStatus(TaskStatus.OFFEN);
        List<Task> completedTasks = repository.findByStatus(TaskStatus.ERLEDIGT);

        assertEquals(1, openTasks.size());
        assertEquals(1, completedTasks.size());
        assertEquals("Offene Aufgabe", openTasks.get(0).getDescription());
        assertEquals("Erledigte Aufgabe", completedTasks.get(0).getDescription());
    }

    @Test
    void testFindByDescriptionContaining() {
        Task task1 = new Task("Java Programmierung");
        Task task2 = new Task("Datenbank Design");
        Task task3 = new Task("Java Unit Tests");

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        List<Task> javaTasks = repository.findByDescriptionContaining("Java");
        List<Task> dbTasks = repository.findByDescriptionContaining("Datenbank");

        assertEquals(2, javaTasks.size());
        assertEquals(1, dbTasks.size());
    }

    @Test
    void testCount() {
        repository.save(new Task("Aufgabe 1"));
        repository.save(new Task("Aufgabe 2"));
        repository.save(new Task("Aufgabe 3"));

        assertEquals(3, repository.count());
    }

    @Test
    void testCountByStatus() {
        Task task1 = new Task("Offen 1");
        Task task2 = new Task("Offen 2");
        Task task3 = new Task("Erledigt 1");
        task3.setTaskStatus(TaskStatus.ERLEDIGT);

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        assertEquals(2, repository.countByStatus(TaskStatus.OFFEN));
        assertEquals(1, repository.countByStatus(TaskStatus.ERLEDIGT));
    }

    @Test
    void testSaveAll() {
        List<Task> tasks = List.of(
                new Task("Batch 1"),
                new Task("Batch 2"),
                new Task("Batch 3")
        );

        repository.saveAll(tasks);

        assertEquals(3, repository.count());
    }

    @Test
    void testDeleteAll() {
        repository.save(new Task("Aufgabe 1"));
        repository.save(new Task("Aufgabe 2"));
        assertEquals(2, repository.count());

        repository.deleteAll();

        assertEquals(0, repository.count());
    }
}
