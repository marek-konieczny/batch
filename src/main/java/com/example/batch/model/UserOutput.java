package com.example.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutput {

    private String firstName;
    private String lastName;
    private int birthYear;
}
