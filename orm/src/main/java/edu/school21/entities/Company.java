package edu.school21.entities;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OrmEntity(table = "company_table")
public class Company {
    @OrmColumnId
    private Long id;

    @OrmColumn(name = "company_name", length = 30)
    private String companyName;

    @OrmColumn(name = "number_of_employes")
    private Integer numberOfEmployees;

}
