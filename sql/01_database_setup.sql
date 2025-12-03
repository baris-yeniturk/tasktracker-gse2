/*
 * TaskTracker Database Setup
 * MSSQL Server Script
 * Creates complete database schema for TaskTracker application
 */

PRINT '========================================';
PRINT '   TaskTracker Database Setup v1.0     ';
PRINT '========================================';
GO

-- 1. CREATE DATABASE
PRINT '1. Creating database...';
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'TaskTrackerDB')
BEGIN
    CREATE DATABASE TaskTrackerDB;
    PRINT '     Database "TaskTrackerDB" created';
END
ELSE
BEGIN
    PRINT '     Database already exists';
END
GO

USE TaskTrackerDB;
GO

-- 2. CREATE TABLES
PRINT '2. Creating tables...';

-- Tasks table (main entity)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Tasks' AND type = 'U')
BEGIN
    CREATE TABLE Tasks (
        id BIGINT IDENTITY(1,1) NOT NULL,
        title NVARCHAR(200) NOT NULL,
        description NVARCHAR(1000) NULL,
        status NVARCHAR(20) NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE() NOT NULL,
        updated_at DATETIME2 DEFAULT GETDATE() NOT NULL,
        completed_at DATETIME2 NULL,

        CONSTRAINT PK_Tasks PRIMARY KEY (id),
        CONSTRAINT CHK_Tasks_Status CHECK (status IN ('OFFEN', 'IN_ARBEIT', 'ERLEDIGT', 'ARCHIVIERT')),
        CONSTRAINT CHK_Tasks_Title CHECK (LEN(TRIM(title)) > 0)
    );
    PRINT '   ✓ Table "Tasks" created';
END
ELSE
BEGIN
    PRINT '   ⚠ Table "Tasks" already exists';
END
GO

-- Users table (for future extensions)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Users' AND type = 'U')
BEGIN
    CREATE TABLE Users (
        id INT IDENTITY(1,1) NOT NULL,
        username NVARCHAR(50) UNIQUE NOT NULL,
        display_name NVARCHAR(100) NOT NULL,
        email NVARCHAR(255) NULL,
        is_active BIT DEFAULT 1 NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE() NOT NULL,

        CONSTRAINT PK_Users PRIMARY KEY (id),
        CONSTRAINT CHK_Users_Username CHECK (LEN(TRIM(username)) > 0)
    );
    PRINT '     Table "Users" created';
END
GO

-- 3. CREATE INDEXES for performance
PRINT '3. Creating indexes...';

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Tasks_Status' AND object_id = OBJECT_ID('Tasks'))
BEGIN
    CREATE INDEX IX_Tasks_Status ON Tasks(status);
    PRINT '     Index "IX_Tasks_Status" created';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Tasks_CreatedAt' AND object_id = OBJECT_ID('Tasks'))
BEGIN
    CREATE INDEX IX_Tasks_CreatedAt ON Tasks(created_at DESC);
    PRINT '     Index "IX_Tasks_CreatedAt" created';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Tasks_UpdatedAt' AND object_id = OBJECT_ID('Tasks'))
BEGIN
    CREATE INDEX IX_Tasks_UpdatedAt ON Tasks(updated_at DESC);
    PRINT '     Index "IX_Tasks_UpdatedAt" created';
END

-- 4. CREATE VIEWS for common queries
PRINT '4. Creating views...';

IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_OpenTasks')
BEGIN
    EXEC('CREATE VIEW vw_OpenTasks AS
        SELECT
            id,
            title,
            description,
            created_at,
            updated_at
        FROM Tasks
        WHERE status IN (''OFFEN'', ''IN_ARBEIT'')
        ORDER BY created_at DESC');
    PRINT '   ✓ View "vw_OpenTasks" created';
END
GO

IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_CompletedTasks')
BEGIN
    EXEC('CREATE VIEW vw_CompletedTasks AS
        SELECT
            id,
            title,
            description,
            completed_at,
            DATEDIFF(DAY, created_at, completed_at) AS days_to_complete
        FROM Tasks
        WHERE status = ''ERLEDIGT''
        ORDER BY completed_at DESC');
    PRINT '   ✓ View "vw_CompletedTasks" created';
END
GO

-- 5. CREATE STORED PROCEDURES for business logic
PRINT '5. Creating stored procedures...';

-- Procedure: Create task
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_CreateTask')
BEGIN
    EXEC('CREATE PROCEDURE sp_CreateTask
        @title NVARCHAR(200),
        @description NVARCHAR(1000) = NULL,
        @status NVARCHAR(20) = ''OFFEN''
    AS
    BEGIN
        SET NOCOUNT ON;

        INSERT INTO Tasks (title, description, status)
        VALUES (@title, @description, @status);

        SELECT SCOPE_IDENTITY() AS id;
    END');
    PRINT '   ✓ Procedure "sp_CreateTask" created';
END
GO

-- Procedure: Update task
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_UpdateTask')
BEGIN
    EXEC('CREATE PROCEDURE sp_UpdateTask
        @id BIGINT,
        @title NVARCHAR(200) = NULL,
        @description NVARCHAR(1000) = NULL,
        @status NVARCHAR(20) = NULL
    AS
    BEGIN
        SET NOCOUNT ON;

        UPDATE Tasks
        SET
            title = ISNULL(@title, title),
            description = ISNULL(@description, description),
            status = ISNULL(@status, status),
            updated_at = GETDATE(),
            completed_at = CASE
                WHEN @status = ''ERLEDIGT'' AND status != ''ERLEDIGT''
                THEN GETDATE()
                ELSE completed_at
            END
        WHERE id = @id;

        SELECT @@ROWCOUNT AS rows_affected;
    END');
    PRINT '     Procedure "sp_UpdateTask" created';
END
GO

-- Procedure: Delete task
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_DeleteTask')
BEGIN
    EXEC('CREATE PROCEDURE sp_DeleteTask
        @id BIGINT
    AS
    BEGIN
        SET NOCOUNT ON;

        DELETE FROM Tasks WHERE id = @id;

        SELECT @@ROWCOUNT AS rows_affected;
    END');
    PRINT '     Procedure "sp_DeleteTask" created';
END
GO

-- 6. CREATE TRIGGER for updated_at automation
PRINT '6. Creating triggers...';

IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Tasks_UpdateTimestamp')
BEGIN
    EXEC('CREATE TRIGGER trg_Tasks_UpdateTimestamp
        ON Tasks
        AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;

        UPDATE t
        SET updated_at = GETDATE()
        FROM Tasks t
        INNER JOIN inserted i ON t.id = i.id;
    END');
    PRINT '     Trigger "trg_Tasks_UpdateTimestamp" created';
END
GO

PRINT '======================================';
PRINT 'Database setup completed successfully!';
PRINT '======================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Database: TaskTrackerDB';
PRINT '  - Tables: Tasks, Users';
PRINT '  - Indexes: 3';
PRINT '  - Views: 2';
PRINT '  - Stored Procedures: 3';
PRINT '  - Triggers: 1';
PRINT '';
PRINT 'Next: Run 03_sample_data.sql for test data';
PRINT '========================================';
GO