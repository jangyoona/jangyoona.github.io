package com.groupware.controller;

import com.groupware.common.Util;
import com.groupware.dto.*;
import com.groupware.loginAttempt.LoginAttemptDto;
import com.groupware.service.*;
import com.groupware.ui.ProjectPager;
import com.groupware.view.UserFileDownloadView;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/myPage")
public class MyPageController {

    @Setter(onMethod_ = { @Autowired })
    private MyPageService myPageService;

    @Setter(onMethod_ = { @Autowired })
    private AttdService attdService;

    @Setter(onMethod_ = { @Autowired})
    private ProjectService projectService;

    @Setter(onMethod_ = { @Autowired })
    private CalendarService calendarService;

    @Setter(onMethod_ = { @Autowired })
    private ApprovalService approvalService; // file test


    @GetMapping("/my-page")
    public String myPageTestForm(HttpSession session, Model model, String keyWord, @RequestParam(name = "tab", required = false) Integer tab) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        List<UserScheduleDto> scheduleList =  myPageService.getUserSchedule(employee.getEmpId(), "null");
        List<AttendanceDto> empAttendance = attdService.findAttendanceByEmpId(employee.getEmpId());

        if(scheduleList == null || empAttendance == null) {
        } else {
            LocalDate today = LocalDate.now();
            model.addAttribute("today", today);
            model.addAttribute("empAttendance", empAttendance);
            model.addAttribute("scheduleList", scheduleList);
        }

        // empId로 개인 캘린더 조회
        CalendarDto personalCalendar = calendarService.findCalendarByEmpId(employee.getEmpId(), 1); // calendarNo는 예시로 1을 사용

        if (personalCalendar == null) {
            personalCalendar = new CalendarDto();
            personalCalendar.setEvents(new ArrayList<>()); // 빈 이벤트 리스트 설정
        }
        // 모델에 개인 캘린더 정보 추가
        model.addAttribute("calendar", personalCalendar);
        model.addAttribute("loginUser", employee);

        // 근태 탭 유저 연차 갯수 표시
        Integer annualCount = myPageService.getAnnualCount(employee.getEmpId());
        model.addAttribute("annualCount", annualCount);

        // 근태 탭 status 상태 불러오기
        List<String> attendanceStatus = myPageService.getAttendanceStatus();
        model.addAttribute("attdStatus", attendanceStatus);

        return "myPage/my-page";
    }


    @GetMapping("/todo-add")
    @ResponseBody
    public String myTodoAdd(@RequestParam("personalScheduleStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date personalScheduleStartDate,
                                @RequestParam("personalScheduleEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date personalScheduleEndDate,
                                String personalScheduleContent, Integer empId) {

        if(empId != null) {
            UserScheduleDto schedule = new UserScheduleDto();
            schedule.setEmpId(empId);
            schedule.setPersonalScheduleStartDate(personalScheduleStartDate);
            schedule.setPersonalScheduleEndDate(personalScheduleEndDate);
            schedule.setPersonalScheduleContent(personalScheduleContent);
            myPageService.insertUserSchedule(schedule);
            return "success";
        } else {
            return "false";
        }
    }

    @GetMapping("/todo-delete")
    @ResponseBody
    public String todoListDelete(Integer scheduleNo) {

        if (scheduleNo == null) {
            return "false";
        }
        myPageService.deleteTodoList(scheduleNo);
        return "success";
    }

    @GetMapping("/my-information")
    public String myInformationForm(HttpSession session, Model model, HttpServletRequest req) {

        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        // 사원 이미지 등록되어 있는지 확인

        ServletContext application = req.getServletContext();
        String realPath = application.getRealPath("/employee-photo");
        String photoPath = realPath + "/" + employee.getEmpId() + ".jpg";

        File file = new File(photoPath);
        boolean photoExists = file.exists();

        model.addAttribute("photoExists", photoExists);
        model.addAttribute("loginUser", employee);
        return "myPage/my-information";
    }
    

    @PostMapping("/my-information-edit")
    @ResponseBody
    public String myInformation(EmployeeDetailDto employeeDetail, String empEmail,
                                @RequestParam(name = "attach", required = false) MultipartFile attach,
                                HttpServletRequest req) {
        myPageService.editEmpEmail(employeeDetail.getEmpId(),empEmail);
        myPageService.editEmpInformationDetail(employeeDetail);

        // attach null -> 에러떠서 바로 return
        if(attach == null) {
            return "success";
        }

        if (!attach.isEmpty() && attach.getOriginalFilename().length() > 0) {

            // 이미 등록된 사원 사진이 있는지 확인
            ServletContext application = req.getServletContext();
            String dir = application.getRealPath("/employee-photo");
            String saveName = dir + "/" + employeeDetail.getEmpId() + ".jpg";

            File file = new File(saveName);

            try {
                boolean deleted = file.delete();
                File newFile = new File(saveName);
                attach.transferTo(newFile);
                return "success";
            } catch (IOException e){
                e.printStackTrace(); // Log the exception
                System.out.println("파일처리 오류");
                return "false";
            }
        }
        return "success";
    }


    @GetMapping("/todo-list")
    public String todoList(HttpSession session, @RequestParam(required = false) String sortKeyword, Model model) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        List<UserScheduleDto> scheduleList =  myPageService.getUserSchedule(employee.getEmpId(), sortKeyword);

        model.addAttribute("scheduleList", scheduleList);
        return "myPage/todo-list";
    }

    @GetMapping("/login-attepmt-view")
    @ResponseBody
    public String loginAttemptView() {
        return "success";
    }

    @GetMapping("/login-attempt") // 로그인 조회 이게 찐 -test
    public String loginAttempt2Form(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                    @RequestParam(name="keyword", defaultValue = "all") String keyword,
                                    String startDate, String endDate,
                                    HttpSession session, Model model, HttpServletRequest req) {
        if(startDate == null || endDate == null) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.format(now);

            startDate = sdf.format(now);
            endDate = sdf.format(now);
        }

        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");

        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = myPageService.getUserAttemptsCount(employee.getEmpId(), keyword, startDate, endDate);

        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        List<LoginAttemptDto> loginAttempts = myPageService.getUserLoginAttemptLog(employee.getEmpId(), start, keyword, startDate, endDate);
        model.addAttribute("loginAttempt", loginAttempts);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        model.addAttribute("loginKeyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "myPage/login-attempt";
    }

    // test - 유저 근태조회
    @GetMapping("/my-attendance")
//    public String myAttendanceForm(@RequestParam(required = false) Integer pageNum, HttpSession session, Model model) {
    public String myAttendanceForm(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                   @RequestParam(name = "keyword", defaultValue = "all") String keyword,
                                   String startDate, String endDate,
                                   HttpSession session, Model model, HttpServletRequest req) {
        if(startDate == null || endDate == null) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.format(now);

            startDate = sdf.format(now);
            endDate = sdf.format(now);
        }

        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = myPageService.getUserAttendanceCount(employee.getEmpId(), keyword, startDate, endDate);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        List<AttendanceDto> empAttendance = myPageService.findAttendanceByEmpId(employee.getEmpId(), start, keyword, startDate, endDate);

        model.addAttribute("empAttendance", empAttendance);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        model.addAttribute("attdKeyword", keyword);

        return "myPage/my-attendance";

    }

    @GetMapping("/my-file-box") // 메일함 view
    public String fileBoxForm(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                              @RequestParam(name="keyword", defaultValue = "all") String keyword,
                              HttpSession session, Model model, HttpServletRequest req) {
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");

        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = myPageService.getUserFileCount(employee.getEmpId(), keyword);

        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        List<UserFileBoxDto> userFileList = myPageService.getUserFileList(employee.getEmpId(), start, keyword);
        model.addAttribute("userFileList", userFileList);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        model.addAttribute("fileKeyword", keyword);

        return "myPage/my-file-box";

    }

    @GetMapping("my-file-create")
    public String fileCreateForm(){

        return "myPage/my-file-create";
    }

    @PostMapping("my-file-save")
    @ResponseBody
    public String fileCreate(Integer empId, String note, @RequestParam(defaultValue = "개인") String fileSort, MultipartFile attach, HttpServletRequest req){

        UserFileBoxDto userFile = new UserFileBoxDto();
        userFile.setEmpId(empId);
        userFile.setNote(note);
        userFile.setFileSort(fileSort);

        if (!attach.isEmpty() && attach.getOriginalFilename().length() > 0) {
            try {
                String userFileName = attach.getOriginalFilename();
                String savedFileName = Util.makeUniqueFileName(userFileName);

                String dir = req.getServletContext().getRealPath("/user-filebox");
                attach.transferTo(new File(dir, savedFileName)); // 파일 저장

                userFile.setUserFileName(userFileName);
                userFile.setSavedFileName(savedFileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            myPageService.saveUserFile(userFile);
        } catch (Exception ex) {
            System.out.println("파일 등록 실패");
            ex.printStackTrace();
            return "error";
        }
        return "success";
    }

    @GetMapping(path = {"/userFile/download"})
    public View userFileDownload(int fileNo, Model model, HttpServletRequest req) {

        UserFileBoxDto attach = myPageService.getUserFileByFileNo(fileNo);

        ServletContext application = req.getServletContext();
        String path = application.getRealPath("/user-filebox/" + attach.getSavedFileName());

        model.addAttribute("attach", attach);
        model.addAttribute("uploadPath", path);

        return new UserFileDownloadView();
    }

    @GetMapping("/userFile-remove")
    @ResponseBody
    public String userFileRemove(@RequestParam(required = false) Integer fileNo, HttpServletRequest req){

        if(fileNo == null) {
            return "error";
        }

        UserFileBoxDto attach = myPageService.getUserFileByFileNo(fileNo);
        String dirPath = req.getServletContext().getRealPath("/user-filebox");
        File file = new File(dirPath, attach.getSavedFileName());

        if(file.exists()){
            file.delete();
        }
        myPageService.deleteUserFile(fileNo);
        return "success";

    }


    @GetMapping("/my-bookmark")
    public String contactBookmark(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                  @RequestParam(name = "option", defaultValue = "all") String option,
                                  @RequestParam(name = "keyword", defaultValue = "all") String keyword,

                                  @RequestParam(name = "sortName", defaultValue = "all") String sortName,
                                  @RequestParam(name = "sortValue", defaultValue = "false") String sortValue,
                                  HttpSession session,Model model, HttpServletRequest req) {


        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("employee", employee);

        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = myPageService.getUserBookmarkCount(employee.getEmpId(), option, keyword);

        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
//        List<UserBookmarkDto> bookmarkList = myPageService.getUserBookmarkByEmpId(employee.getEmpId(), start, option, keyword);
        List<UserBookmarkDto> bookmarkList = myPageService.getUserBookmarkByEmpId(employee.getEmpId(), start, option, keyword, sortName, sortValue);

        if (sortName.equals("name")) {
            model.addAttribute("nameSortValue", sortValue);
            model.addAttribute("deptSortValue", false);
        } else {
            model.addAttribute("deptSortValue", sortValue);
            model.addAttribute("nameSortValue", false);
        }

        model.addAttribute("bookmarks", bookmarkList);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        model.addAttribute("bookmarkKeyword", keyword);
        model.addAttribute("sortName", sortName);

        return "myPage/my-bookmark";
    }

    @PostMapping("/my-bookmark-delete")
    @ResponseBody
    public String contactDelete(@RequestBody List<UserBookmarkDto> bookmarks, HttpSession session) {

        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        myPageService.deleteUserBookmark(employee.getEmpId(), bookmarks);

        return "success";
    }

    @PostMapping("/my-bookmark-set")
    @ResponseBody
    public String contactBookmarkSet(Boolean bookmarkActive, int contactId, HttpSession session){
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        myPageService.updateUserBookmarkSet(employee.getEmpId(), contactId, bookmarkActive);

        return "success";
    }

}




