package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.sql.*;
import java.util.*;

public class SqlServerTaskRepository implements TaskRepository{
    private final String connectionUrl;
    private final String username;
    private final String password;

    public SqlServerTaskRepository(String connectionUrl, String username, String password){
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
        validateConnection();
    }

    public SqlServerTaskRepository(Properties properties){
        this(
                properties.getProperty("mssql.url", "jdbc:sqlserver://localhost:1433;"+
                        "databaseName=TaskTrackedDB;trustServerCertificate=true"),
                properties.getProperty("mssql.username", "sa"),
                properties.getProperty("mssql.password", ""));
    }

    public SqlServerTaskRepository(){
        this(
                "jdbc:sqlserver://localhost:1433;databaseName=TaskTrackerDB;trustServiceCertificate=true",
                "sa", ""
        );
    }

    private void validateConnection(){
        try (Connection conn = getConnection()) {
            System.out.println("MSSQL Repository: Verbindung validiert");
        }catch (SQLException e){
            System.err.println("MSSQL Repository: Verbindung fehlgeschlagen");
            System.err.println("URL: " + connectionUrl);
            System.err.println("Fehler: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException{
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }catch (ClassNotFoundException e){
            throw new SQLException("JDBC Driver nicht gefunden", e);
        }

        if (username == null || username.isEmpty()){
            return DriverManager.getConnection(connectionUrl);
        }else{
            return DriverManager.getConnection(connectionUrl, username, password);
        }
    }

    @Override
    public Task save(Task task){
        validateTask(task);
        String sql = "INSERT INTO Tasks (description, status, created_at) VALUES (?, ?, GETDATE());";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            pstmt.setString(1, task.getDescription());
            pstmt.setString(2, task.getTaskStatus().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0){
                throw new SQLException("Speichern fehlgeschlagen, keine Ziele betroffen");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    long id = generatedKeys.getLong(1);
                    task.setTaskId(id);
                    System.out.println("Aufgabe gespeichert (ID: " + id + ")");
                }else{
                    throw new SQLException("Speichern fehlgeschlagen, keine ID erhalten");
                }
            }
            return task;
        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Speichern der Aufgabe");
        }
    }

    @Override
    public Optional<Task> findById(long id){
        if (id <= 0){
            return Optional.empty();
        }

        String sql = "SELECT id, description, status, created_at FROM Tasks WHERE id = ?";

        try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapRowToTask(rs));
                }
            }
        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Finden der Aufgabe mit ID: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Task> findAll(){
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, description, status, created_at FROM Tasks ORDER BY created_at DESC";

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                tasks.add(mapRowToTask(rs));
            }

            System.out.println(tasks.size() + " Aufgaben geladen");

        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Laden aller Aufgaben", e);
        }
        return tasks;
    }

    @Override
    public Task update(Task task){
        validateTask(task);

        if (task.getTaskId() <= 0){
            throw new IllegalArgumentException("Task-ID muss positiv sein für Update");
        }

        String sql = "UPDATE Tasks SET description = ?, status = ?, updated_at = GETDATE() WHERE id = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, task.getDescription());
            pstmt.setString(2, task.getTaskStatus().name());
            pstmt.setLong(3, task.getTaskId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0){
                throw new IllegalArgumentException("Task mit ID: " + task.getTaskId() + " wurde nicht gefunden");
            }

            System.out.println("Aufgabe aktualisiert (ID: " + task.getTaskId() + ")");
            return task;

        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Aktualisieren der Aufgabe", e);
        }
    }

    @Override
    public boolean delete(long id){
        if (id <= 0){
            return false;
        }

        String sql = "DELETE FROM Tasks WHERE id = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();

            boolean deleted = affectedRows > 0;
            if (deleted){
                System.out.println("Aufgabe gelöscht (ID: " + id + ")");
            }

            return deleted;
        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Löschen der Aufgabe", e);
        }
    }

    @Override
    public boolean existsById(long id){
        if (id <= 0){
            return false;
        }

        String sql = "SELECT 1 FROM Tasks WHERE id = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()){
                return rs.next();

            }
        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Prüfen der Existenz von Aufgabe ID: " + id, e);
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status){
        if (status == null) {
            throw new IllegalArgumentException("Status darf nicht null sein");
        }

        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, description, status, created_at FROM Tasks WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

            System.out.println(tasks.size() + " Aufgaben mit Status '" + status + "' gefunden");

        } catch (SQLException e) {
            throw new PersistenceException("Fehler beim Filtern nach Status: " + status, e);
        }

        return tasks;
    }

    @Override
    public List<Task> findByDescriptionContaining(String keyword){
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Suchbegriff darf nicht leer sein");
        }

        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, description, status, created_at FROM Tasks WHERE LOWER(description) LIKE ? ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

            System.out.println(tasks.size() + " Aufgaben mit '" + keyword + "' gefunden");

        } catch (SQLException e) {
            throw new PersistenceException("Fehler bei der Suche nach: " + keyword, e);
        }

        return tasks;
    }

    @Override
    public long count(){
        String sql = "SELECT COUNT(*) FROM Tasks";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new PersistenceException("Fehler beim Zählen der Aufgaben", e);
        }
    }

    @Override
    public long countByStatus(TaskStatus status){
        if (status == null) {
            throw new IllegalArgumentException("Status darf nicht null sein");
        }

        String sql = "SELECT COUNT(*) FROM Tasks WHERE status = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            throw new PersistenceException("Fehler beim Zählen nach Status: " + status, e);
        }
    }

    @Override
    public void saveAll(List<Task> taskList){
        if (taskList == null || taskList.isEmpty()){
            return;
        }

        String sql = "INSERT INTO Tasks (description, status, created_at) VALUES (?, ?, GETDATE())";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            for (Task task : taskList){
                validateTask(task);
                pstmt.setString(1, task.getDescription());
                pstmt.setString(2, task.getTaskStatus().name());
                pstmt.addBatch();
            }

            int[] batchResults = pstmt.executeBatch();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                int i = 0;
                while (generatedKeys.next() && i < taskList.size()){
                    taskList.get(i).setTaskId(generatedKeys.getLong(1));
                    i++;
                }
            }
            System.out.println("Batch gespeichert: " + batchResults.length + " Aufgaben");

        }catch (SQLException e){
            throw new PersistenceException("Fehler beim Batch-Speichern", e);
        }
    }

    @Override
    public void deleteAll(){
        String sql = "DELETE FROM Tasks";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            int deletedRows = stmt.executeUpdate(sql);
            System.out.println("🗑️ Alle Aufgaben gelöscht: " + deletedRows + " Zeilen");

        } catch (SQLException e) {
            throw new PersistenceException("Fehler beim Löschen aller Aufgaben", e);
        }
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException{
        long id = rs.getLong("id");
        String description = rs.getString("description");
        TaskStatus status = TaskStatus.valueOf(rs.getString("status"));

        return new Task(id, description, status);
    }

    private void validateTask(Task task){
        if (task == null){
            throw new IllegalArgumentException("Task darf nicht null sein");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()){
            throw new IllegalArgumentException("Task-Beschreibung darf nicht leer sein");
        }
        if (task.getTaskStatus() == null){
            throw new IllegalArgumentException("Task-Status darf nicht null sein");
        }
    }

    public static class PersistenceException extends RuntimeException{
        public PersistenceException(String message){
            super(message);
        }

        public PersistenceException(String message, Throwable cause){
            super(message, cause);
        }
    }
}