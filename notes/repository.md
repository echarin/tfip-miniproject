# Repository/Backend Database Stuff

- Let's graduate from the `spring-jdbc` module and start using Spring Data JPA

## Dependencies

- No need to specify version for Starter Data JPA, as Spring Boot will automatically manage it

```xml
<!-- Spring Boot Starter Data JPA -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL JDBC driver -->
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.32</version>
</dependency>

<!-- Hibernate -->
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>6.2.4.Final</version>
</dependency>
```

## Configuration

- In `application.properties`

```text
# Database Connection Properties
spring.datasource.url=jdbc:mysql://localhost:3306/tfip-miniproject
spring.datasource.username=root
spring.datasource.password=root

# JPA Properties
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

- Explanations
  - `spring.datasource.url`: The JDBC URL for the database
    - Connecting to a MySQL database running on `localhost` on port `3306`, accessing a database named `mydatabase`
    - May have to set `allowPublicKeyRetrieval=true` and `useSSL=false`, but this poses a security risk due to allowing transmission of passwords over an unencrypted connection
  - `spring.datasource.username` and `spring.datasource.password`
  - `spring.jpa.show-sql`: If set to true, this property causes Hibernate to print out the SQL it's sending to the database
  - `spring.jpa.hibernate.ddl-auto`: Tells Hibernate to automatically create, update or validate the database schema on startup
    - "update" means that Hibernate will make any necessary changes to the schema to reflect the current state of your entities
      - `create` will create the schema, destroying previous data
      - `create-drop` is the same except it also drops the schema when the `SessionFactory` is closed, meaning that when the application stops, all data will be lost
      - `validate` will validate the schema and make no changes to the database
      - `update` updates the schema if necessary; creates new tables if they don't exist, and add new columns to existing tables if needed
        - It will not remove a column from a table if that column is no longer present in the entity
    - In a real-world application, use `spring.jpa.hibernate.ddl-auto=none` and manage the database schema changes through a database migration tool
  - `spring.jpa.properties.hibenate.dialect`: Sets the SQL dialect that Hibernate should use; the dialect tells Hibernate how to generate SQL that is compatible with the type of database you ae using
    - Since we are using MySQL version 8.0.32 as of 6 June 2023, we use `org.hibernate.dialect.MySQLDialect`
    - Hibernate will say that `MySQL8Dialect` is deprecated

### Encrypted Connections to MySQL

- `caching_sha2_password` is the default authentication plugin rather than `mysql_native_password`
- To set up an account that uses the `caching_sha2_password` plugin for SHA-256 password hashing, use this statement
  - `CREATE USER 'sha2user'@'localhost' IDENTIFIED WITH caching_sha2_password BY '<password>'`
  - Then allow this user access to your schemas
- In `application.properties`, set `spring.datasource.username=sha2user` and `spring.datasource.password=<password>` accordingly

## The `JpaRepository` Interface

- In Spring Data JPA, a repository is an interface that extends one of the repository interfaces provided by Spring Data
  - `CrudRepository` is the base interface for Spring Data repos, and provides methods for CRUD operations such as `save`, `findOne`, `findAll`, `delete` and so on
  - `PagingAndSortingRepository`: this interface extends `CrudRepository` and adds additional methods to ease paginated access to entities and sorting
  - `JpaRepository` is an extension of PASRepo and adds JPA-specific functionality; it is specifically tailored for use with JPA
- These interfaces provide a range of CRUD methods to use
- `JpaRepository` is a generic type and you must specify the entity class that the repository works with, and the type of the entity's ID
  - `JpaRepository<Employee, UUID>`
- You do not need to provide an implementation for this interface; at runtime, Spring Data JPA will automatically generate an implementation that includes all of the inherited methods, saving lots of time from writing boilerplate data access code

### Custom Queries

- `findBy`
  - `findBy<property>And<property>`

## Entity Mapping

### Annotations

#### @MappedSuperclass

- A JPA annotation to denote classes whose mapping information is applied to the entities that inherit from them
- There is no separate table defined for a mapped superclass
- Its mapping information is absorbed by the entities that inherit from it
- Allows you to define mappings for common fields such as `createdAt`, `updatedAt`, `deletedAt` in one place, then extend this class in each entity
- Great for avoiding code duplication
- You can create an abstract class with this annotation
  - `abstract` means that the class cannot be instantiated, only subclassed
  - i.e. you can create objects of classes that extend this abstract class

#### @JoinColumn

- Marks a column as a join column for an entity association or an element collection

#### @OneToMany

- `orphanRemoval` instructs the JPA provider to trigger a `remove` entity state transition when a child entity is no longer referenced by its parent entity

## Exceptions

### "detached entity passed to persist"

- The error message "detached entity passed to persist" is related to how Hibernate manages entity states.
  - **Transient**: The entity is not associated with any Hibernate `Session`.
  - **Persistent**: The entity is associated with a Hibernate `Session`.
  - **Detached**: The entity was associated with an active Hibernate `Session`, but that `Session` was closed.
  - **Removed**: The entity is associated with a `Session`, but it is marked for deletion.
- When a `detached entity passed to persist` error occurs, it typically means that an object that's already in the session or that has an ID (and is therefore considered persistent) is being used in a way Hibernate doesn't expect.

#### Solving the issue

- The solution is to make sure all database operations within the scope are part of the same database transaction. In Spring Boot, this can be achieved by using the `@Transactional` annotation.
  - By using `@Transactional`, you ensure that all operations are part of the same `Session` and transaction. This allows Hibernate to correctly manage the entities' states, and prevents issues like the one you experienced.

#### Understanding the `@Transactional` annotation

- The `@Transactional` annotation tells Spring to start a new transaction before executing the method and commit it after the method has finished.
  - This means all operations in the method share the same `Session` and are part of the same transaction.
  - So, when you save an entity, it becomes persistent, but the `Session` is still open. Then, when you later add that entity to another entity and save the second entity, Hibernate recognizes the first entity as a persistent entity associated with the current `Session`, not a detached one.

#### Understanding Bi-directional relationships

- When managing relationships between entities, especially bi-directional relationships, it's important to keep the entities' state in sync.
  - In a bi-directional relationship, one side is the owning side (the one Hibernate uses to check what needs to be persisted in the database).
  - It's important that you set the owning side in the non-owning side, this helps to keep the state of the entities in sync and ensures that Hibernate works correctly.
