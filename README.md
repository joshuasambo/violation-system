# Violation System

A Java desktop application for managing student violation reports.

## Technologies

* Java 25
* Java Swing
* Maven
* MySQL Server 8.4 LTS
* MySQL Connector/J

## Prerequisites

Before running this project, make sure you have installed:

* **JDK 25** — to compile and run the Java application.
* **Apache NetBeans or Visual Studio Code** — to open and work on the project.
* **MySQL Server 8.4 LTS** — to store the application's data.
* **MySQL Workbench** *(optional but recommended)* — to manage the MySQL database using a graphical interface.

## Database Setup

Each developer must install and configure their own MySQL Server. The database server and its data are not included in this repository.

1. Download and install MySQL Server from the [official MySQL website](https://dev.mysql.com/downloads/mysql/).

2. Start the MySQL Server service.

3. Install MySQL Workbench from the [official download page](https://dev.mysql.com/downloads/workbench/) if you want a graphical interface for managing your database.

4. Connect to your local MySQL Server.

5. Create the application database by executing:

   ```sql
   CREATE DATABASE violation_system;
   ```

6. Create a separate MySQL account for the application and grant it the necessary permissions. Do not use the MySQL `root` account in the application.

7. Configure the application's database connection settings with your own local database credentials.

**Important:** Never commit your database password or other private credentials to GitHub.

## Running the Project

1. Clone or download this repository.
2. Ensure JDK 25 and MySQL Server are installed and running.
3. Complete the database setup described above.
4. Open the project in NetBeans or Visual Studio Code.
5. Allow Maven to resolve the project's dependencies.
6. Configure the database connection settings.
7. Run the `ViolationSystem.java` main class.

## Project Status

Currently under development.

