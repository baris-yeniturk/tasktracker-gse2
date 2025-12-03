PRINT '========================================';
PRINT '   TaskTracker Database Cleanup        ';
PRINT '   WARNING: DELETES ALL DATA!         ';
PRINT '========================================';
GO

-- Safety check
DECLARE @databaseName NVARCHAR(100) = 'TaskTrackerDB';
DECLARE @confirm BIT = 0; -- Change to 1 to execute

IF @confirm = 1
BEGIN
    PRINT 'Executing cleanup...';

    USE master;

    -- Drop database if exists
    IF EXISTS (SELECT name FROM sys.databases WHERE name = @databaseName)
    BEGIN
        -- Kick out all connections
        ALTER DATABASE [TaskTrackerDB] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;

        -- Drop database
        DROP DATABASE [TaskTrackerDB];

        PRINT '✅ Database "' + @databaseName + '" dropped';
    END
    ELSE
    BEGIN
        PRINT '⚠ Database "' + @databaseName + '" does not exist';
    END
END
ELSE
BEGIN
    PRINT '  Cleanup aborted. Safety check failed.';
    PRINT 'To execute, change @confirm to 1 in this script.';
END
GO