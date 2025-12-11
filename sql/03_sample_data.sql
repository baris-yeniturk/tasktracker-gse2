/*
 * TaskTracker Sample Data
 * Inserts realistic test data for development/testing
 */

PRINT '========================================';
PRINT '   Inserting Sample Data               ';
PRINT '========================================';
GO

USE TaskTrackerDB;
GO

-- Clear existing data (optional)
PRINT 'Clearing existing data...';
DELETE FROM Tasks;
DBCC CHECKIDENT ('Tasks', RESEED, 0);
PRINT '✓ Tables cleared';
GO

-- Insert sample tasks
PRINT 'Inserting sample tasks...';

INSERT INTO Tasks (title, description, status, created_at) VALUES
('Datenbank-Schema entwerfen', 'ER-Diagramm und Tabellen für TaskTracker erstellen', 'ERLEDIGT', DATEADD(DAY, -7, GETDATE())),
('JDBC Verbindung implementieren', 'MSSQL JDBC Driver einbinden und Connection Pool konfigurieren', 'ERLEDIGT', DATEADD(DAY, -6, GETDATE())),
('CRUD-Operationen testen', 'Unit Tests für alle Datenbank-Operationen schreiben', 'IN_ARBEIT', DATEADD(DAY, -5, GETDATE())),
('Benutzeroberfläche erstellen', 'Einfache Konsolen-UI für Task-Management', 'OFFEN', DATEADD(DAY, -4, GETDATE())),
('Dokumentation vervollständigen', 'Projektdokumentation mit Screenshots und Erklärungen', 'OFFEN', DATEADD(DAY, -3, GETDATE())),
('Performance optimieren', 'Indizes prüfen und Query-Performance analysieren', 'OFFEN', DATEADD(DAY, -2, GETDATE())),
('Integrationstests schreiben', 'End-to-End Tests mit Testcontainers', 'OFFEN', DATEADD(DAY, -1, GETDATE())),
('Code-Review durchführen', 'Pull Request erstellen und Code reviewen', 'ARCHIVIERT', DATEADD(DAY, -10, GETDATE()));

PRINT CAST(@@ROWCOUNT AS NVARCHAR) + ' sample tasks inserted';
GO

-- Insert sample users (for future features)
PRINT 'Inserting sample users...';

INSERT INTO Users (username, display_name, email) VALUES
('alice.dev', 'Alice Developer', 'alice@tasktracker.local'),
('bob.test', 'Bob Tester', 'bob@tasktracker.local'),
('charlie.doc', 'Charlie Documenter', 'charlie@tasktracker.local');

PRINT '  ' + CAST(@@ROWCOUNT AS NVARCHAR) + ' sample users inserted';
GO

-- Show statistics
PRINT '';
PRINT '  Data Statistics:';
PRINT '-------------------';

DECLARE @total_tasks INT, @open_tasks INT, @completed_tasks INT;

SELECT @total_tasks = COUNT(*) FROM Tasks;
SELECT @open_tasks = COUNT(*) FROM Tasks WHERE status IN ('OFFEN', 'IN_ARBEIT');
SELECT @completed_tasks = COUNT(*) FROM Tasks WHERE status = 'ERLEDIGT';

PRINT '  Total Tasks: ' + CAST(@total_tasks AS NVARCHAR);
PRINT '  Open Tasks: ' + CAST(@open_tasks AS NVARCHAR);
PRINT '  Completed Tasks: ' + CAST(@completed_tasks AS NVARCHAR);
PRINT '  Users: ' + CAST((SELECT COUNT(*) FROM Users) AS NVARCHAR);
PRINT '';
PRINT '  Sample data inserted successfully!';
PRINT '========================================';
GO