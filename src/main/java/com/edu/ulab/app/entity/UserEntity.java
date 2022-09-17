package com.edu.ulab.app.entity;


import lombok.Data;

@Data
public class UserEntity {
    private Long id;
    private String fullName;
    private String title;
    private int age;
}
