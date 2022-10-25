package com.rg.demo.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("t_student")
public class Student {

    @Id
    private Long id;
    private String name;
    private String address;

    @Version
    private Long version;
}
