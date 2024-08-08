package com.groupware.dto;

import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Date;
import java.util.List;

@Data
public class AttendanceDto {

    private int empId;
    private int attdNo;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date attdIn;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date attdOut;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date attdDate;
    private String attdStatus;
    private String attdDetail;

    private String attdInStr;  // 문자열로 수신하기 위한 필드 추가
    private String attdOutStr; // 문자열로 수신하기 위한 필드 추가
    private String attdDateStr; // 문자열로 수신하기 위한 필드 추가

    private int deptNo; // 부서별 조회를 위함
    private String empName; // 조회 목록에 이름을 추가하기 위함

    private String positionName; // 직위를 조회하기 위함

    // 휴가관련
    private List<String> dates;
}
