package com.groupware.dto;

import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EmployeeDetailDto {

    private int empId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date empBirthDate;
    private String empPhone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date empInDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date empOutDate;
    private String empAddress;
    private String empAddressDetail;
    private Boolean modifyActive;
    private String modifyDetail;

}
