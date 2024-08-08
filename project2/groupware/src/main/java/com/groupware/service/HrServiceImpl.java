package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.mapper.AttdMapper;
import com.groupware.mapper.HrMapper;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HrServiceImpl implements HrService{

    @Setter
    private HrMapper hrMapper;

    @Autowired
    public HrServiceImpl(HrMapper hrMapper) {
        this.hrMapper = hrMapper;
    }

    @Override
    public List<EmployeeDto> findAllEmployee() {
        return hrMapper.findAllEmployee();
    }

    @Override
    public List<EmployeeDto> findEmployeeByEmpName(String empName) { return hrMapper.findEmployeeByEmpName(empName);};

//    @Override
//    public void insertAttendanceLeaves(AttendanceDto attendance) {
//            for (String date : attendance.getDates()) {
//                hrMapper.insertAttendanceLeaves(Integer.valueOf(attendance.getEmpId()), date, attendance.getAttdStatus(), attendance.getAttdDetail());
//                hrMapper.updateAnnualCountMinus(Integer.valueOf(attendance.getEmpId()));
//            }
//        }
    @Override
    public void insertAttendanceLeavesAuto(VacationRegistration vacationRegistration) {

        LocalDate startDate = LocalDate.parse(vacationRegistration.getStartDate());
        LocalDate endDate = LocalDate.parse(vacationRegistration.getEndDate());

        List<String> dates = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.toString());
        }

        for (String date : dates) {
            hrMapper.insertAttendanceLeaves(Integer.valueOf(vacationRegistration.getEmpId()), date, vacationRegistration.getDetail());
            hrMapper.updateAnnualCountMinus(Integer.valueOf(vacationRegistration.getEmpId()));
        }
    }

    public List<VacationRegistration> findAndParseApprovalLeaves() {
        List<ApprovalDto> approvals = hrMapper.findApprovalLeavesByActive();
        List<VacationRegistration> vacationRegistrations = new ArrayList<>();
        System.out.println(approvals);

        for (ApprovalDto approval : approvals) {
            String approvalContent = approval.getApprovalContent();
            Document doc = Jsoup.parse(approvalContent);

            String startDate = extractValue(doc, "출발일");
            String endDate = extractValue(doc, "복귀일");
            String detail = extractValue(doc, "휴가 사유");

            VacationRegistration registration = new VacationRegistration();
            registration.setEmpId(String.valueOf(approval.getEmpId()));
            registration.setStartDate(startDate);
            registration.setEndDate(endDate);
            registration.setDetail(detail);

            vacationRegistrations.add(registration);
        }

        return vacationRegistrations;
    }
    public String extractValue(Document doc, String headerText) {
        Elements rows = doc.select("tr");
        for (Element row : rows) {
            Elements headers = row.select("td");
            if (headers.size() >= 2 && headers.get(0).text().contains(headerText)) {
                String value = headers.get(1).text().trim();
                System.out.println("Extracted value for " + headerText + ": " + value); // Debug statement
                return value;
            }
        }
        return null;
    }

    // 사원 총 수
    @Override
    public int getEmployeesCount(int selectValue, String searchValue) {
        return hrMapper.selectEmployeesCount(selectValue, searchValue);
    }

    @Override
    public List<EmployeeDto> findEmployees(int start, int selectValue, String searchValue) {
        return hrMapper.selectEmployees(start, selectValue, searchValue);
    }
}
