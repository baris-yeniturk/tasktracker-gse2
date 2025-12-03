# TaskTracker Database Scripts

## Overview
This directory contains all SQL scripts for the TaskTracker application's Microsoft SQL Server database.

## Script Execution Order

### 1. Initial Setup
Run these scripts **in order**:

1. **`01_database_setup.sql`** - Creates database, tables, indexes, views, and stored procedures
2. **`03_sample_data.sql`** - Inserts sample data for testing (optional)

### 2. Maintenance & Development
- **`02_cleanup_reset.sql`** - Drops entire database (USE WITH CAUTION!)
- **`04_migration_add_priority.sql`** - Example migration script
- **`05_example_queries.sql`** - Example queries and usage patterns

## Database Schema

### Tables
- **`Tasks`** - Main task entity with status tracking
- **`Users`** - User management (for future extensions)

### Views
- **`vw_OpenTasks`** - All non-completed tasks
- **`vw_CompletedTasks`** - Completed tasks with metrics
- **`vw_HighPriorityTasks`** - High priority tasks (after migration)

### Stored Procedures
- **`sp_CreateTask`** - Creates a new task
- **`sp_UpdateTask`** - Updates an existing task
- **`sp_DeleteTask`** - Deletes a task

## Execution Methods

### Using SQL Server Management Studio (SSMS)
1. Open SSMS
2. Connect to your SQL Server instance
3. Open and execute each script in order

### Using SQLCMD (Command Line)
```bash
sqlcmd -S localhost -E -i 01_database_setup.sql
sqlcmd -S localhost -E -i 03_sample_data.sql