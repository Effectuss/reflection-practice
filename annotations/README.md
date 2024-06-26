# Annotations – SOURCE

Annotations allow to store metadata directly in the program code. Now your objective it to implement HtmlProcessor
class (derived fromAbstractProcessor) that processes classes with special @HtmlForm and @Htmlnput annotations and
generates HTML form code inside the target/classes folder after executing mvn clean compile command. Let's assume we
have UserForm class:

```java

@HtmlForm(fileName = "user_form.html", action = "/users", method = "post")
public class UserForm {
    @HtmlInput(type = "text", name = "first_name", placeholder = "Enter First Name")
    private String firstName;

    @HtmlInput(type = "text", name = "last_name", placeholder = "Enter Last Name")
    private String lastName;

    @HtmlInput(type = "password", name = "password", placeholder = "Enter Password")
    private String password;
}
```

Then, it shall be used as a base to generate "user_form.html" file with the following contents:

```HTML

<form action="/users" method="post">
    <input type="text" name="first_name" placeholder="Enter First Name">
    <input type="text" name="last_name" placeholder="Enter Last Name">
    <input type="password" name="password" placeholder="Enter Password">
    <input type="submit" value="Send">
</form>
```

- @HtmlForm and @HtmlInput annotations shall only be available during compilation.
- Project structure is at the developer's discretion.
- To handle annotations correctly, we recommend to use special settings of maven-compiler-plugin and auto-service
  dependency on com.google.auto.service.
