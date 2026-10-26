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

6. Create a separate MySQL account for the application. Do not use the MySQL `root` account in the application. As a MySQL administrator, grant the application account the privileges needed to create and migrate tables and run the demo seed procedure:

   ```sql
   GRANT
       CREATE,
       ALTER,
       INDEX,
       SELECT,
       INSERT,
       UPDATE,
       DELETE,
       REFERENCES,
       CREATE TEMPORARY TABLES,
       CREATE ROUTINE,
       ALTER ROUTINE,
       EXECUTE
   ON `violation_system`.*
   TO 'violation_app'@'localhost';
   ```

   Replace the account name and host if yours differ. `CREATE TEMPORARY TABLES` is used for the seed procedure's temporary name pools; `CREATE ROUTINE`, `ALTER ROUTINE`, and `EXECUTE` allow Flyway to create, clean up, and call the procedure.

7. Copy `src/main/resources/config.properties.example` to `config.properties` in the project root. Set `db.user` and `db.password` to the application account credentials. The local `config.properties` is excluded from Git. Alternatively, set the `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables; environment variables take precedence.

8. Start the application. Flyway applies pending migrations before showing the login screen. The migrations create missing tables; they do not overwrite existing tables or repair their structure. The sample-data migration makes `2026-2027` the active academic year, adds Grades 9–12 and twelve named sections, then generates 28–38 fictional students and guardians per section from distinct name pools. Generated guardian emails use the reserved `.invalid` domain, and phone values are marked `TEST-PHONE`; both are unique in the seed data and are not real contact details. It also adds sample violation categories and types. Flyway records each migration and does not reapply it on later starts. Add future schema changes as new versioned files in `src/main/resources/db/migration/`; do not edit migrations that have already been applied.

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

## Database Migrations

Run the application normally to apply pending Flyway migrations. Do not run `repair` every time a migration file changes: it updates Flyway's recorded checksum but does not rerun the migration. Once a migration has been applied, make database changes in a new versioned migration.

### Repairing Flyway Migration History

Use `repair` only after investigating a failed migration or confirming that an applied migration's contents changed without changing its database effects. Repair updates Flyway's schema history; it does not rerun a migration or undo partial database changes. Back up important data and resolve any partial migration effects before repairing.

For this project, the application supports a one-time repair option. Run the Java launch command with `--repair-flyway-checksums` after the main class:

```powershell
& 'C:\Path\To\jdk-25\bin\java.exe' '@C:\Path\To\VSCode\classpath.argfile' 'com.school.violationsystem.ViolationSystem' '--repair-flyway-checksums'
```

mine is:
```powershell
& 'C:\Program Files\Java\jdk-25.0.5.0.2\bin\java.exe' '@C:\Users\joshu\AppData\Local\Temp\cp_6lgjxwy11nntqo0skndy3x2gs.argfile' 'com.school.violationsystem.ViolationSystem' '--repair-flyway-checksums'
```

Replace both paths with the Java executable and classpath argfile from your own VS Code launch command; the argfile is generated and its path may change. After repair and any pending migration succeed, launch the application normally without the flag.
