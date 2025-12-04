/*
 * Migration: Add Priority Field
 * Example migration script for future enhancements
 * Shows how to evolve the database schema
 */

PRINT '========================================';
PRINT '   Migration: Add Priority to Tasks    ';
PRINT '   Version: 1.1                        ';
PRINT '========================================';
GO

USE TaskTrackerDB;
GO

-- 1. Add priority column
PRINT '1. Adding priority column...';
IF NOT EXISTS (SELECT * FROM sys.columns
               WHERE object_id = OBJECT_ID('Tasks') AND name = 'priority')
BEGIN
    ALTER TABLE Tasks ADD priority INT DEFAULT 3;

    -- Add constraint for valid priority range (1-5)
    ALTER TABLE Tasks ADD CONSTRAINT CHK_Tasks_Priority
        CHECK (priority BETWEEN 1 AND 5);

    PRINT '     Column "priority" added (default: 3, range: 1-5)';
END
ELSE
BEGIN
    PRINT '     Column "priority" already exists';
END
GO

-- 2. Add index for priority
PRINT '2. Adding priority index...';
IF NOT EXISTS (SELECT * FROM sys.indexes
               WHERE name = 'IX_Tasks_Priority' AND object_id = OBJECT_ID('Tasks'))
BEGIN
    CREATE INDEX IX_Tasks_Priority ON Tasks(priority);
    PRINT '     Index "IX_Tasks_Priority" created';
END
GO

-- 3. Update existing data with sensible priorities
PRINT '3. Updating existing data...';
UPDATE Tasks
SET priority =
    CASE
        WHEN status = 'ERLEDIGT' THEN 1
        WHEN title LIKE '%wichtig%' OR title LIKE '%critical%' THEN 2
        WHEN status = 'IN_ARBEIT' THEN 3
        ELSE 4
    END;
PRINT '   ✓ Updated ' + CAST(@@ROWCOUNT AS NVARCHAR) + ' tasks';
GO

-- 4. Create view for high priority tasks
PRINT '4. Creating high priority view...';
IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_HighPriorityTasks')
BEGIN
    EXEC('CREATE VIEW vw_HighPriorityTasks AS
        SELECT
            id,
            title,
            description,
            priority,
            status,
            created_at
        FROM Tasks
        WHERE priority <= 2
          AND status != ''ERLEDIGT''
        ORDER BY priority, created_at DESC');
    PRINT '   ✓ View "vw_HighPriorityTasks" created';
END
GO

PRINT '========================================';
PRINT '   Migration completed successfully!    ';
PRINT '========================================';
PRINT '';
PRINT 'Changes applied:';
PRINT '  - Added "priority" column (1-5, default 3)';
PRINT '  - Added index on priority';
PRINT '  - Updated existing data';
PRINT '  - Created view for high priority tasks';
PRINT '';
PRINT 'Run: SELECT * FROM vw_HighPriorityTasks';
PRINT '========================================';
GO