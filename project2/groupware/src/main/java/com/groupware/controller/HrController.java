package com.groupware.controller;

import com.groupware.dto.EmployeeDto;
import com.groupware.service.HrService;
import com.groupware.service.MyPageService;
import com.groupware.ui.ProjectPager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HrController {

    @Setter(onMethod_ = {@Autowired})
    private HrService hrService;

    @Setter(onMethod_ = {@Autowired})
    private MyPageService myPageService;


    @GetMapping(path = "pages/human-resources/employee-list")
    public String showEmployeeList(@RequestParam("searchName") String empName, Model model, HttpSession session) throws NoSuchAlgorithmException {

        List<EmployeeDto> employee = hrService.findEmployeeByEmpName(empName);
        model.addAttribute("employee", employee);

        List<EmployeeDto> employees = hrService.findAllEmployee();
        model.addAttribute("employees", employees);

        return "pages/human-resources/employee-list";
    }


    @GetMapping("/search")
    @ResponseBody
    public List<EmployeeDto> searchEmployees(@RequestParam(name = "searchName", required = false) String searchName) {
        if (searchName != null && !searchName.isEmpty()) {
            return hrService.findEmployeeByEmpName(searchName);
        } else {
            return hrService.findAllEmployee();
        }
    }

    @GetMapping(path = "/modules/annual-insert")
    public String showEnnualInsert() {
        return "/modules/annual-insert";
    }

//    @PostMapping(path = "/modules/annual-insert")
//    @ResponseBody
//    public String ennualInsert(@RequestBody AttendanceDto attendance) {
//
//        hrService.insertAttendanceLeaves(attendance);
//
//        return "success";
//
//    }

    @GetMapping("/pages/human-resources/emp-contacts")
    public String myContacts(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");


        List<EmployeeDto> employees = hrService.findAllEmployee();

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("employees", employees);

        return "pages/human-resources/emp-contacts";
    }

    @GetMapping("/pages/human-resources/contact-module")
    public String contactModule(Model model, HttpServletRequest req,
                                @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                @RequestParam(name = "selectValue") int selectValue,
                                @RequestParam(name = "searchValue", defaultValue = "all") String searchValue,
                                HttpSession session) {

//        List<EmployeeDto> employees = hrService.findAllEmployee();

        // 페이징 처리
        int pageSize = 9;
        int pagerSize = 5;
        int dataCount = hrService.getEmployeesCount(selectValue, searchValue);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        List<EmployeeDto> employees = hrService.findEmployees(start, selectValue, searchValue);


        model.addAttribute("employees", employees);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);


        // 등록된 사원 사진이 있는지 확인
        ServletContext application = req.getServletContext();
        String realPath = application.getRealPath("/employee-photo");

        List<Boolean> photoList = new ArrayList<>();
        for(int i=0; i< employees.size(); i++){
            String photoPath = realPath + "/" + employees.get(i).getEmpId() + ".jpg";
            File file = new File(photoPath);
            boolean photoExists = file.exists();
            photoList.add(photoExists);
        }
        model.addAttribute("photoExists", photoList);

        return "pages/human-resources/contact-module";
    }

    @GetMapping("/pages/human-resources/bookmark-add")
    @ResponseBody
    public String contactsBookmarkAdd(int contactId, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        try {
            myPageService.UserBookmarkAdd(loginUser.getEmpId(), contactId);
        } catch (Exception e){
            return "error"; // 이미 등록된 사원인 경우
        }
        return "success";
    }
}
