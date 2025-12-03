package com.tasktracker.repository;

import com.tasktracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RepositoryFactory{
    private static final String CONFIG_FILE = "application.properties";
    private static final String REPOSITORY_TYPE_KEY = "repository.type";
    private static final String DEFAULT_REPOSITORY_TYPE = "AUTO_DETECT";

    private static final String TYPE_MSSQL = "MSSQL";
    private static final String TYPE_IN_MEMORY = "IN_MEMORY";
    private static final String TYPE_AUTO_DETECT = "AUTO_DETECT";

    private static RepositoryFactory instance;
    private final Properties properties;

    private RepositoryFactory(){
        this.properties = loadProperties();
    }

    public static synchronized RepositoryFactory getInstance(){
        if (instance == null){
            instance = new RepositoryFactory();
        }
        return instance;
    }

    public TaskRepository createRepository(){
        String repositoryType = properties.getProperty(REPOSITORY_TYPE_KEY, DEFAULT_REPOSITORY_TYPE);

        System.out.println("Repository Factory: Konfiguriert für '" + repositoryType + "'");

        switch (repositoryType.toUpperCase()){
            case TYPE_MSSQL:
                return createMssqlRepository();
            case TYPE_IN_MEMORY:
                return createInMemoryRepository();
            case TYPE_AUTO_DETECT:
            default:
                return autoDetectRepository();
        }
    }

    public TaskRepository createInMemoryRepository(){
        System.out.println("Erstelle In-Memory Repository (für Entwicklung/Tests)");
        return new InMemoryTaskRepository();
    }

    public TaskRepository createMssqlRepository(){
        System.out.println("Erstelle MSSQL Repository");
        return new SqlServerTaskRepository();
    }

    public TaskRepository createMssqlRepository(String url, String username, String password) {
        System.out.println("Erstelle MSSQL Repository mit benutzerdefinierter Verbindung");
        return new SqlServerTaskRepository(url, username, password);
    }

    private TaskRepository autoDetectRepository(){
        System.out.println("🔍 Auto-Detect: Prüfe MSSQL-Verfügbarkeit...");

        try {
            TaskRepository mssqlRepo = new SqlServerTaskRepository(properties);

            mssqlRepo.count();
            System.out.println("✅ MSSQL verfügbar - verwende Datenbank");
            return mssqlRepo;

        } catch (Exception e) {
            System.out.println("⚠ MSSQL nicht verfügbar: " + e.getMessage());
            System.out.println("🔄 Fallback zu In-Memory Repository");
            return createInMemoryRepository();
        }
    }

    private Properties loadProperties(){
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
                System.out.println("⚙️ Konfiguration geladen aus: " + CONFIG_FILE);
            } else {
                System.out.println("⚠ " + CONFIG_FILE + " nicht gefunden, verwende Standardwerte");
                setDefaultProperties(props);
            }
        } catch (IOException e) {
            System.err.println("⚠ Fehler beim Laden von " + CONFIG_FILE + ": " + e.getMessage());
            setDefaultProperties(props);
        }
        return props;
    }

    private void setDefaultProperties(Properties props) {
        props.setProperty(REPOSITORY_TYPE_KEY, DEFAULT_REPOSITORY_TYPE);
        props.setProperty("mssql.url", "jdbc:sqlserver://localhost:1433;databaseName=TaskTrackerDB;trustServerCertificate=true");
        props.setProperty("mssql.username", "sa");
        props.setProperty("mssql.password", "");
        props.setProperty("mssql.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public static TaskRepository getDefaultRepository() {
        return getInstance().createRepository();
    }

    public static boolean isMssqlAvailable() {
        try {
            SqlServerTaskRepository repo = new SqlServerTaskRepository();
            repo.count(); // Test-Query
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
