package com.example.TodosRestApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    @NotBlank(message = "Name can't be blank")
    private String name;
    @NotBlank(message = "Email can't be blank")
    @Pattern(regexp = "^(.+)@([a-zA-Z0-9]+|[a-zA-Z0-9]+.[a-zA-Z0-9]+)$", message = "Invalid email")
    // ^[a-zA-Z0-9]+@.[a-zA-Z0-9]+
    // ^(.+)@([a-zA-Z0-9]+|[a-zA-Z0-9]+.[a-zA-Z0-9]+)  working
    // ^(.+)@(.+)$
    private String email;
    @NotBlank(message = "Gender can't be blank")
    @Pattern(regexp = "(male|female)", message = "Invalid input for gender")
    private String gender;
    @NotBlank(message = "Status can't be blank")
    @Pattern(regexp = "(active|inactive)", message = "Invalid input for status")
    private String status;

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
