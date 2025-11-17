Spring Boot backend skeleton for Movie Ticket System.

Features:
- Basic project structure with Promotion and Announcement entities, repositories, controllers.
- Simple AuthController that returns a fake token (replace with real auth).
- SecurityConfig placeholder (permits all for development).
- application.properties configured for SQL Server (replace credentials).

How to run:
- Requires JDK 17+ and Maven.
- Edit src/main/resources/application.properties to set SQL Server credentials.
- Run: mvn spring-boot:run
