package com.groupware.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ProjectDto {

    private int scheduleNo;
    private String scheduleTitle;
    private String scheduleContent;
    private Date scheduleWriteDate;
    private Date scheduleStartDate;
    private Date scheduleEndDate;
    private boolean scheduleActive;
    private int importance;
    private String scheduleStatus;
    private int schedulePm;

    private int percent;

    // 프로젝트 관련 협업 부서
    private List<DepartmentDto> depts;

    // 프로젝트 관련 참여인원
    private List<EmployeeDto> employees;

}
