# Work with Classes

Now you need to implement a Maven project that interacts with classes of your application. We need to create at least
two classes, each having:

- private fields (supported types are String, Integer, Double, Boolean, Long)
- public methods
- an empty constructor
- a constructor with a parameter
- toString() method

In this task, you do not need to implement get/set methods. Newly created classes must be located in a separate *
*classes** package (this package may be located in other packages). Let's assume that the application has User and Car
classes. User class is described below:

```java
public class User {
    private String firstName;
    private String lastName;
    private int height;

    public User() {
        this.firstName = "Default first name";
        this.lastName = "Default last name";
        this.height = 0;
    }

    public User(String firstName, String lastName, int height) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
    }

    public int grow(int value) {
        this.height += value;
        return height;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("height=" + height)
                .toString();
    }
}
```

The implemented application shall operate as follows:

- Provide information about a class in classes package.
- Enable a user to create objects of a specified class with specific field values.
- Display information about the created class object.
- Call class methods.

Example of program operation:

```
Classes:
  - User
  - Car
---------------------
Enter class name:
-> User
---------------------
fields:
	String firstName
	String lastName
	int height
methods:
	int grow(int)
---------------------
Let’s create an object.
firstName:
-> UserName
lastName:
-> UserSurname
height:
-> 185
Object created: User[firstName='UserName', lastName='UserSurname', height=185]
---------------------
Enter name of the field for changing:
-> firstName
Enter String value:
-> Name
Object updated: User[firstName='Name', lastName='UserSurname', height=185]
---------------------
Enter name of the method for call:
-> grow(int)
Enter int value:
-> 10
Method returned:
195
```

- If a method contains more than one parameter, you need to set values for each one
- If the method has void type, a line with returned value information is not displayed
- In a program session, interaction only with a single class is possible; a single field of its object can be modified,
  and a single method can be called
- You may use throws operator.