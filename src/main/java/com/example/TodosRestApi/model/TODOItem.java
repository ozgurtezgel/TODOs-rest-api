package com.example.TodosRestApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TODOItem {

    private Long id;
    private Long userId;
    @NotBlank(message = "Title can't be blank")
    private String title;
    private String due_on;
    @NotBlank(message = "Status can't be blank")
    @Pattern(regexp = "(completed|pending)", message = "Invalid input for status")
    private String status;

    @Override
    public String toString() {
        return "TODO{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", due_on=" + due_on +
                ", status='" + status + '\'' +
                '}';
    }
}
