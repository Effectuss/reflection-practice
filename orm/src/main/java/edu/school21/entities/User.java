package edu.school21.entities;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId
    private Long id;

    @OrmColumn(name = "first_name", length = 15)
    private String firstName;

    @OrmColumn(name = "last_name", length = 15)
    private String lastName;

    @OrmColumn(name = "age")
    private Integer age;

}
