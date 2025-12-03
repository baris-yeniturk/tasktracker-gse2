package com.tasktracker;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;
import com.tasktracker.repository.InMemoryTaskRepository;
import com.tasktracker.repository.TaskRepository;

import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TaskTracker - Aufgabenverwaltung    ");
        System.out.println("   Build: 1.0.0 | TDD | by myenituerk     ");
        System.out.println("========================================\n");

        TaskRepository repository = new InMemoryTaskRepository();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running){
            System.out.println("\n=== Hauptmenü ===");
            System.out.println("1. Neue Aufgabe erstellen");
            System.out.println("2. Alle Aufgaben anzeigen");
            System.out.println("3. Aufgabe aktualisieren");
            System.out.println("4. Aufgabe löschen");
            System.out.println("5. Beenden");
            System.out.print("Auswahl: ");

            String choice = scanner.nextLine();

            switch (choice){
                case "1":
                    createTask(scanner, repository);
                    break;
                case "2":
                    listTasks(repository);
                    break;
                case "3":
                    updateTask(scanner, repository);
                    break;
                case "4":
                    deleteTask(scanner, repository);
                    break;
                case "5":
                    running = false;
                    System.out.println("Das Program wird ausgeschaltet...");
                    break;
                default:
                    System.out.println("Ungültige Auswahl!");
            }
        }
        scanner.close();
    }

    public static void createTask(Scanner scanner, TaskRepository repository){
        System.out.print("Beschreibung der Aufgabe: ");
        String description = scanner.nextLine();

        try{
            Task task = new Task(description);
            Task saved = repository.save(task);
            System.out.println("Aufgabe erstellt (ID: " + saved.getTaskId() + ")");
        }catch (IllegalArgumentException e){
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    public static void listTasks(TaskRepository repository){
        var tasks = repository.findAll();

        if (tasks.isEmpty()){
            System.out.println("Keine Aufgaben vorhanden");
        }else{
            System.out.println("\n=== Aufgabenliste ===");
            tasks.forEach(task -> {
                System.out.printf("ID: %d | %s | Status: %s%n",
                task.getTaskId(),
                task.getDescription(),
                task.getTaskStatus());
            });
        }
    }

    public static void updateTask(Scanner scanner, TaskRepository repository){
        System.out.print("ID der zu aktualisierenden Aufgabe: ");
        try{
            long id = Long.parseLong(scanner.nextLine());

            var taskOpt = repository.findById(id);
            if (taskOpt.isEmpty()){
                System.out.println("Aufgabe nicht gefunden");
                return;
            }

            Task task = taskOpt.get();
            System.out.println("Aktuelle Beschreibung: " + task.getDescription());
            System.out.print("Neue Beschreibung (leer lassen um nicht zu ändern): ");
            String newDesc = scanner.nextLine();

            if (!newDesc.trim().isEmpty()){
                task.setDescription(newDesc);
            }

            System.out.print("Status (1=OFFEN, 2=ERLEDIGT, leer=lassen");
            String statusInput = scanner.nextLine();

            if (statusInput.equals("2")){
                task.setTaskStatus(TaskStatus.ERLEDIGT);
            }else if (statusInput.equals("1")){
                task.setTaskStatus(TaskStatus.OFFEN);
            }

            repository.update(task);
            System.out.println("Aufgabe aktualisiert!");
        }catch (NumberFormatException e){
            System.out.println("Ungültige ID!");
        }catch (IllegalArgumentException e){
            System.out.println("Fehler: " + e.getMessage());
        }
    }
}
