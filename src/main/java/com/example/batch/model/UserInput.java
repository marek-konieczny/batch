package com.example.batch.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserInput {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

}