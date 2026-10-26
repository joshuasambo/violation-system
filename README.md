# Violation System

A Java desktop application for managing student violation reports.

## Technologies

* Java 25
* Java Swing
* Maven
* MySQL Server 8.4 LTS
* MySQL Connector/J
* Flyway

## Prerequisites

Before running this project, make sure you have installed:

* **JDK 25** — to compile and run the Java application.
* **Apache NetBeans or Visual Studio Code** — to open and work on the project.
* **MySQL Server 8.4 LTS** — to store the application's data.
* **MySQL Workbench** *(optional)* — to manage the MySQL database using a graphical interface.

## Database Setup

Each developer must install and configure their own MySQL Server. The database server and its data are not included in this repository.

1. Download and install MySQL Server from the [official MySQL website](https://dev.mysql.com/downloads/mysql/).

2. Start the MySQL Server service.

3. Install MySQL Workbench from the [official download page](https://dev.mysql.com/downloads/workbench/) if you want a graphical interface for managing your database.

4. Connect to your local MySQL Server.

5. Create the empty application database:

   ```sql
   CREATE DATABASE violation_system;
   ```

   The database itself must exist before the application starts. The application automatically creates missing tables when it launches, so you do not need to run a table script or open Workbench for table setup.

6. Create a separate MySQL account for the application and grant it permission to create tables, foreign keys, and manage Flyway's migration-history table in `violation_system`. Do not use the MySQL `root` account in the application. An administrator can grant the required database privileges in Workbench:

   ```sql
   GRANT CREATE, ALTER, INDEX, SELECT, INSERT, UPDATE, DELETE, REFERENCES
   ON violation_system.*
   TO 'violation_app'@'localhost';
   ```

7. Copy `src/main/resources/config.properties.example` to `config.properties` in the project root. Set `db.user` and `db.password` to your local MySQL account credentials. The local `config.properties` is excluded from Git. You can alternatively set `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables; environment variables take precedence.

8. Start the application once. Flyway applies the migrations before showing the login screen. They create only missing tables with `CREATE TABLE IF NOT EXISTS`; they do not overwrite existing tables or their data, and they do not repair the structure of a table that already exists. The sample-data migration adds fictional grades, sections, students, guardians, and violation types only when matching records are not already present. It uses the active academic year if one exists, or creates `2026-2027` as active if no academic year is active and that year name is unused. Flyway records each migration and does not reapply it on later starts. Add future schema changes as new versioned files in `src/main/resources/db/migration/`.

9. On the first launch when no users exist, the application displays a setup screen to create the administrator account. Public signup is disabled after setup; only an active administrator can create teacher accounts from the dashboard. Passwords are stored as salted PBKDF2 hashes.

**Important:** Never commit your database password or other private credentials to GitHub.

## Running the Project

1. Clone or download this repository.
2. Ensure JDK 25 and MySQL Server are installed and running, and create the database and application account as described above.
3. Open the project in NetBeans or Visual Studio Code.
4. Allow Maven to resolve the project's dependencies.
5. Configure the database connection settings.
6. Run `ViolationSystem.java`. It initializes missing tables, then opens first-time setup if no user accounts exist; otherwise it opens the login screen.

Authentication loads the user's role for the session. Teacher account creation is restricted to administrators and checked in the service and database layers; hiding a screen or button alone is not an authorization check.

## Project Status

Currently under development.

When making changes in the sql query, run the main in terminal with this command
**Important:** File path may vary

```powershell
& 'C:\Program Files\Java\jdk-25.0.5.0.2\bin\java.exe' '@C:\Users\joshu\AppData\Local\Temp\cp_6lgjxwy11nntqo0skndy3x2gs.argfile' 'com.school.violationsystem.ViolationSystem' '--repair-flyway-checksums'
```
