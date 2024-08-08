package com.groupware.dto;


import lombok.Data;

@Data
public class MinigameDto {

    private int minigameId;
    private int empId;
    private int score;
    private int attempts;

    private String empName;
}
