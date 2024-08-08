package com.groupware.dto;

import lombok.Data;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeDto {

    private int empId;
    private int deptNo;
    private String empPasswd;
    private boolean empActive;
    private Date empRegidate;
    private String empName;
    private String empEmail;
    private String empPosition;
    private int positionNo;

    // 부서명 조회를 위함
    private String deptName;
    // 직위 조회를 위함
    private String positionName;
    // 회원 상세 ( 아래 DetailDto로 어캐하는지 몰겠음 ㅠㅠ)
    private String empAddress;
    private String empPhone;
    private Date empInDate;
    private Date empOutDate;
    private Date empBirthDate;
    private String empAddressDetail;

    // 회원 첨부파일
    private List<EmployeeAttachDto> employeeAttach;
    private MultipartFile[] empFiles; // 추가된 필드

    // project 테이블과 1 : 1 관계를 구현하는 필드
    private ProjectDto project;

    // 개인 Todo percent 표시하기 위한 변수
    private int percent;

    private EmployeeDetailDto employeeDetail;



}
