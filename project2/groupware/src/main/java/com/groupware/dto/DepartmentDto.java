package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

@Data
public class DepartmentDto {

    private int deptNo;
    private String deptName;

    // 부서 Todo percent 표시하기 위한 변수
    private int percent;

    private ProjectDto project;

}
