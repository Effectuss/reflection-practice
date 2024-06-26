# ORM

We mentioned before that Hibernate ORM framework for databases is based on reflection. ORM concept allows to map relational links to object-oriented links automatically. This approach makes the application fully independent from DBMS. You need to implement a trivial version of such ORM framework.

Let's assume we have a set of model classes. Each class contains no dependencies on other classes, and its fields may only accept the following value types: String, Integer, Double, Boolean, Long. Let's specify a certain set of annotations for the class and its members, for example, User class:

```java
@OrmEntity(table = "simple_user")
public class User {
  @OrmColumnId
  private Long id;
  @OrmColumn(name = "first_name", length = 10)
  private String firstName;
  @OrmColumn(name = "first_name", length = 10)
  private String lastName;
  @OrmColumn(name = "age")
  private Integer age;
  
  // setters/getters
}
```

OrmManager class developed by you shall generate and execute respective SQL code during initialization of all classes marked with @OrmEntity annotation. That code will contain CREATE TABLE command for creating a table with the name specified in the annotation. Each field of the class marked with @OrmColumn annotation becomes a column in this table. The field marked with @OrmColumnId annotation indicates that an auto increment identifier must be created. OrmManager shall also support the following set of operations (the respective SQL code in Runtime is also generated for each of them):

```java
public void save(Object entity);

public void update(Object entity);

public <T> T findById(Long id, Class<T> aClass);
```

- OrmManager shall ensure the output of generated SQL onto the console during execution.
- In initialization, OrmManager shall remove created tables.
- Update method shall replace values in columns specified in the entity, even if object field value is null.
