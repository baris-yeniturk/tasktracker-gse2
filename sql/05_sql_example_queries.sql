/*
 * TaskTracker Example Queries
 * Demonstrates common use cases and SQL patterns
 */

PRINT '========================================';
PRINT '   Example Queries & Use Cases         ';
PRINT '========================================';
GO

USE TaskTrackerDB;
GO

-- 1. BASIC CRUD EXAMPLES
PRINT '';
PRINT '1. BASIC CRUD OPERATIONS:';
PRINT '-------------------------';

-- Create (using stored procedure)
PRINT '   Create new task:';
DECLARE @new_id BIGINT;
EXEC @new_id = sp_CreateTask
    @title = 'Neue Beispielaufgabe',
    @description = 'Erstellt mit Stored Procedure',
    @status = 'OFFEN';
PRINT '      New ID: ' + CAST(@new_id AS NVARCHAR);

-- Read
PRINT '';
PRINT '   Read tasks (all):';
SELECT TOP 3 id, title, status, created_at
FROM Tasks
ORDER BY created_at DESC;

-- Update
PRINT '';
PRINT '   Update task:';
EXEC sp_UpdateTask
    @id = @new_id,
    @status = 'IN_ARBEIT',
    @description = 'Aktualisierte Beschreibung';
PRINT '      Task updated';

-- Delete
PRINT '';
PRINT '   Delete task:';
EXEC sp_DeleteTask @id = @new_id;
PRINT '      Task deleted';
GO

-- 2. QUERY EXAMPLES
PRINT '';
PRINT '2. QUERY EXAMPLES:';
PRINT '------------------';

-- All open tasks
PRINT '   All open tasks:';
SELECT id, title, description, created_at
FROM vw_OpenTasks;

-- Completed this week
PRINT '';
PRINT '   Completed this week:';
SELECT
    id,
    title,
    completed_at,
    DATEDIFF(HOUR, created_at, completed_at) AS hours_to_complete
FROM Tasks
WHERE status = 'ERLEDIGT'
  AND completed_at >= DATEADD(DAY, -7, GETDATE())
ORDER BY completed_at DESC;

-- Task statistics
PRINT '';
PRINT '   Task statistics:';
SELECT
    status,
    COUNT(*) AS count,
    AVG(DATEDIFF(HOUR, created_at, ISNULL(completed_at, GETDATE()))) * 1.0 AS avg_hours
FROM Tasks
GROUP BY status
ORDER BY count DESC;
GO

-- 3. ADVANCED QUERIES
PRINT '';
PRINT '3. ADVANCED QUERIES:';
PRINT '-------------------';

-- Tasks created per day (last 7 days)
PRINT '   Daily task creation (last 7 days):';
SELECT
    CONVERT(DATE, created_at) AS creation_date,
    COUNT(*) AS tasks_created,
    SUM(CASE WHEN status = 'ERLEDIGT' THEN 1 ELSE 0 END) AS tasks_completed
FROM Tasks
WHERE created_at >= DATEADD(DAY, -7, GETDATE())
GROUP BY CONVERT(DATE, created_at)
ORDER BY creation_date DESC;

-- Long-running open tasks
PRINT '';
PRINT '   Long-running open tasks (> 3 days):';
SELECT
    id,
    title,
    status,
    created_at,
    DATEDIFF(DAY, created_at, GETDATE()) AS days_open
FROM Tasks
WHERE status IN ('OFFEN', 'IN_ARBEIT')
  AND created_at <= DATEADD(DAY, -3, GETDATE())
ORDER BY days_open DESC;
GO

-- 4. TRANSACTION EXAMPLE
PRINT '';
PRINT '4. TRANSACTION EXAMPLE:';
PRINT '----------------------';

PRINT '   Starting transaction...';
BEGIN TRANSACTION;
BEGIN TRY
    -- Multiple operations as single transaction
    DECLARE @task1_id BIGINT, @task2_id BIGINT;

    EXEC @task1_id = sp_CreateTask
        @title = 'Transaction Task 1',
        @status = 'OFFEN';

    EXEC @task2_id = sp_CreateTask
        @title = 'Transaction Task 2',
        @status = 'OFFEN';

    -- Update both
    EXEC sp_UpdateTask @id = @task1_id, @status = 'IN_ARBEIT';
    EXEC sp_UpdateTask @id = @task2_id, @status = 'ERLEDIGT';

    COMMIT TRANSACTION;
    PRINT '   ✅ Transaction committed successfully';

END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT '     Transaction rolled back: ' + ERROR_MESSAGE();
END CATCH
GO

PRINT '========================================';
PRINT '✅ Example queries completed!           ';
PRINT '========================================';
PRINT '';
PRINT 'Try these queries in your application:';
PRINT '  1. Find all open tasks';
PRINT '  2. Get task statistics';
PRINT '  3. Use transactions for data consistency';
PRINT '========================================';
GO