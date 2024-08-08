package com.groupware.controller;

import com.groupware.dto.*;
import com.groupware.service.ApprovalService;
import com.groupware.ui.ProjectPager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = { "/forms/approval" })
public class ApprovalDocumentController {

    @Setter(onMethod_ = {@Autowired})
    private ApprovalService approvalService;

    //(GET) approval-before-detail, approval-reject-detail 에서 사용중
    private void approvalDetails(ApprovalDto approvalDocumentNo, Model model) {
        // 기안서 정보
        ApprovalDto approvalDocument = approvalService.findApprovalByApprovalNo(approvalDocumentNo);
        // 결재자들 정보
        List<ApproverDto> approvers = approvalService.findApproverByApprovalNo(approvalDocumentNo);
        // 결재선 정보
        List<EmployeeDto> employee = approvalService.findEmployee();

        // 저장된 결재자 + 사원정보
        List<ApproverDto> saveEmployee = approvalService.findApproverEmployeeByApprovalNo(approvalDocumentNo);

        model.addAttribute("approvalDocument", approvalDocument);
        model.addAttribute("approvers", approvers);
        model.addAttribute("employees", employee);
        model.addAttribute("saveEmployees", saveEmployee);
    }

    //(POST) approval-before-detail, approval-reject-detail 에서 사용중
    private void approvalModify(ApprovalDto Approval,
                                @RequestParam("approvalDocumentNo") int approvalDocumentNo,
                                HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        Approval.setApprovalDocumentNo(approvalDocumentNo);
        Approval.setEmpId(empId);

        approvalService.updateApprovalContent(Approval);

    }

    @GetMapping(path = {"/approval-write"})
    public String approvalWrite(Model model, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<EmployeeDto> employee = approvalService.findEmployee();
        List<ApprovalFormDto> approvalForm = approvalService.findApprovalForm();
        List<ApproverDto> lineTitle = approvalService.findLineTitle(empId);

        model.addAttribute("lineTitle", lineTitle);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("approvalForm", approvalForm);
        model.addAttribute("employees", employee);

        return "/pages/approval/approval-write";
    }



    @PostMapping(path = {"/approval-write"})
    public String approvalWriteForm(ApprovalDto approval,
                                    ApprovalFormDto approvalFormTitle,
                                    @RequestParam("line[]") List<Integer> lines,
                                    String approvalActive,
                                    @RequestParam("empId") Integer empId) {

        List<ApproverDto> approvers = new ArrayList<>();

        int approvalSequence = 1;
        for (Integer line : lines) {
            if (line != null && line != 0) {
                ApproverDto approver = new ApproverDto();
                approver.setEmpId(line);
                approver.setApprovalSequence(approvalSequence++);
                approver.setApprovalActive(approvalActive);
                approvers.add(approver);
            }
        }
        approval.setApprovers(approvers);
        approval.setEmpId(empId);
        approval.setApprovalForm(approvalFormTitle.getApprovalFormTitle());
        approval.setApprovalActive(approvalActive);

        approvalService.writeApproval(approval);

        return "/pages/approval/approval-list";
    }

    @GetMapping(path = {"/approval-write-save"})
    public String approvalSaveDetail(ApprovalDto approvalDocumentNo, Model model) {

        List<ApprovalFormDto> approvalForm = approvalService.findApprovalForm();
        approvalDetails(approvalDocumentNo, model);
        model.addAttribute("approvalForm", approvalForm);

        return "/pages/approval/approval-write-save";
    }

    @PostMapping(path = {"/approval-write-save"})
    public String approvalWriteSave(ApprovalDto approval,
                                    @RequestParam("approvalDocumentNo") int approvalDocumentNo,
                                    @RequestParam("line[]") List<Integer> lines,
                                    String approvalActive,
                                    @RequestParam("empId") Integer empId
    ) {

        List<ApproverDto> approvers = new ArrayList<>();

        int approvalSequence = 1;
        for (Integer line : lines) {
            if (line != null && line != 0) {
                ApproverDto approver = new ApproverDto();
                approver.setEmpId(line);
                approver.setApprovalSequence(approvalSequence++);
                approver.setApprovalActive(approvalActive);
                approvers.add(approver);
            }
        }
        approval.setApprovers(approvers);
        approval.setApprovalDocumentNo(approvalDocumentNo);
        approval.setEmpId(empId);
        approval.setApprovalActive(approvalActive);
        // update 가 아닌 insert 사용
        approvalService.updateApproval(approval);

        return "/pages/approval/approval-list";
    }

    @GetMapping("/get-document")
    @ResponseBody
    public ApprovalFormDto getDocument(@RequestParam("approvalFormTitleId") String approvalFormTitleId) {

        return approvalService.getApprovalForm(approvalFormTitleId);
    }

    @GetMapping("/get-approver-list")
    @ResponseBody
    public List<ApproverDto> getApproverList(@RequestParam("lineTitleId") String lineTitleId, HttpSession session, Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        return approvalService.getApprovalLine(lineTitleId,empId);
    }

    @GetMapping("/approval-list")
    public String approvalList(HttpSession session, Model model) {

        List<ApprovalFormDto> approvalForms = approvalService.findApprovalForm();

        model.addAttribute("approvalForms", approvalForms);

        return "/pages/approval/approval-list";
    }

    @GetMapping(path = {"/approval-content"})
    public String approvalContent(Model model,
                                  HttpSession session,
                                  @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                  HttpServletRequest req) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();
        List<ApprovalDto> approvals = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalCount(empId);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvals = approvalService.findApprovals(start, empId);

        model.addAttribute("approvals", approvals);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        return "/pages/approval/approval-content-module";
    }

    @GetMapping(path = {"/approval-line-write"})
    public String approvalLineWrite(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        List<EmployeeDto> employee = approvalService.findEmployee();
        List<ApprovalFormDto> approvalForm = approvalService.findApprovalForm();

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("approvalForm", approvalForm);
        model.addAttribute("employees", employee);

        return "/pages/approval/approval-line-write";
    }

    @PostMapping(path = {"/approval-line-write"})
    public String approvalLineSave(HttpSession session,
                                   ApprovalDto approval,
                                   @RequestParam("line[]") List<Integer> lines
    ) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApproverDto> approvers = new ArrayList<>();

        int approvalSequence = 1;
        for (Integer line : lines) {
            if (line != null && line != 0) {
                ApproverDto approver = new ApproverDto();
                approver.setEmpId(line);
                approver.setApprovalSequence(approvalSequence++);
                approvers.add(approver);
            }
        }

        approval.setApprovers(approvers);
        approval.setEmpId(empId);
        approvalService.writeApproverLine(approval);

        return "redirect:approval-line-list";
    }


    @PostMapping(path = {"/approvalSearch"})
    public String approvalSearch(HttpSession session,
                                 HttpServletRequest req,
                                 @RequestParam("approvalFormSelect") String approvalFormSelect,
                                 @RequestParam("approvalSearchValue") String approvalSearchValue,
                                 @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                 Model model
    ) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApprovalDto> approvals = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalSearchAllCount(empId, approvalFormSelect, approvalSearchValue);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvals = approvalService.findApprovalSearchList(start, empId, approvalFormSelect, approvalSearchValue);

        model.addAttribute("approvals", approvals);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        return "/pages/approval/approval-content-module";
    }


    @GetMapping("approval-before-detail")
    public String approvalBeforeDetail(ApprovalDto approvalDocumentNo, Model model) {

        approvalDetails(approvalDocumentNo, model);

        return "/pages/approval/approval-before-detail";

    }

    @PostMapping("approval-before-detail")
    public String approvalBeforeWrite(ApprovalDto Approval,
                                      @RequestParam("approvalDocumentNo") int approvalDocumentNo,
                                      HttpSession session) {

        approvalModify(Approval, approvalDocumentNo, session);

        return "redirect:approval-list";
    }

    @GetMapping("approval-after-detail")
    public String approvalAfterDetail(ApprovalDto approvalDocumentNo, Model model) {

        approvalDetails(approvalDocumentNo, model);

        return "/pages/approval/approval-after-detail";

    }

    @GetMapping(path = {"approval-form-detail"})
    public String approvalFormDetail(@RequestParam("approvalFormNo") int approvalFormNo,
                                     Model model) {

        ApprovalFormDto approvalFrom = approvalService.findApprovalFormDetail(approvalFormNo);
        model.addAttribute("approvalFrom", approvalFrom);
        return "/pages/approval/approval-form-detail";
    }

    @PostMapping(path = {"approval-form-detail"})
    public String approvalFormDetailModify(@RequestParam("approvalFormNo") int approvalFormNo) {
        approvalService.updateApprovalForm(approvalFormNo);

        return "redirect:/forms/approval/approval-form-list";
    }


    @GetMapping("approval-last-detail")
    public String approvalLastDetail(ApprovalDto approvalDocumentNo, Model model) {

        approvalDetails(approvalDocumentNo, model);

        return "/pages/approval/approval-last-detail";

    }

    @GetMapping(path = {"/approval-delete/{approvalDocumentNo}"})
    public String approvalDelete(@PathVariable("approvalDocumentNo") int approvalDocumentNo) {

        approvalService.deleteApproval(approvalDocumentNo);

        return "redirect:/forms/approval/approval-list";
    }

    @GetMapping(path = {"/approval-form-write"})
    public String approvalFormWrite(Model model) {

        List<ApprovalFormDto> approvalForm = approvalService.findApprovalForm();

        model.addAttribute("approvalForm", approvalForm);


        return "/pages/approval/approval-form-write";
    }

    @PostMapping(path = {"/approval-form-write"})
    public String approvalFormWrite(ApprovalFormDto approvalForm) {

        approvalService.writeApprovalForm(approvalForm);

        return "redirect:approval-form-list";
    }

    @GetMapping(path = {"/approval-form-list"})
    public String approvalCreate(Model model) {
        List<ApprovalFormDto> approvalForms = approvalService.findApprovalForm();

        model.addAttribute("approvalForms", approvalForms);
        return "/pages/approval/approval-form-list";
    }

    @PostMapping(path = {"approvalFormSearch"})
    public String ApprovalFormSearch(Model model,
                                     HttpSession session,
                                     @RequestParam("approvalFormSelect") String approvalFormSelect,
                                     @RequestParam("approvalSearchValue") String approvalSearchValue,
                                     @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                     HttpServletRequest req) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApprovalDto> approvalFormList = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalFormSearchCount(approvalFormSelect, approvalSearchValue);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvalFormList = approvalService.findApprovalFormSearch(start, approvalFormSelect, approvalSearchValue);

        model.addAttribute("approvalFormList", approvalFormList);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);

        return "/pages/approval/approval-form-module";
    }

    @GetMapping(path = {"/approval-form-content"})
    public String approvalFormContent(Model model,
                                      HttpSession session,
                                      @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                      HttpServletRequest req) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApprovalDto> approvalFormList = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalFormCount();
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvalFormList = approvalService.findApprovalForms(start);


        model.addAttribute("approvalFormList", approvalFormList);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);

        return "/pages/approval/approval-form-module";
    }

    //    결재 대기 문서함
    @GetMapping("/approval-waiting-list")
    public String approvalAwaiting(Model model) {

        List<ApprovalFormDto> approvalForms = approvalService.findApprovalForm();

        model.addAttribute("approvalForms", approvalForms);

        return "/pages/approval/approval-waiting-list";
    }

    @GetMapping("/approval-waiting-content")
    public String approvalWaitiingContent(Model model,
                                          HttpSession session,
                                          @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                          HttpServletRequest req) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApprovalDto> approvals = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalWaitingCount(empId);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvals = approvalService.findApprovalWaitings(empId, start);

//        List<ApprovalDto> approval = approvalService.findApproval(empId);
        model.addAttribute("approvals", approvals);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        return "/pages/approval/approval-waiting-content-module";
    }


    @PostMapping(path = {"/approvalWaitingSearch"})
    public String approvalWaitingSearch(HttpSession session,
                                        HttpServletRequest req,
                                        @RequestParam("approvalFormSelect") String approvalFormSelect,
                                        @RequestParam("approvalSearchValue") String approvalSearchValue,
                                        @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                        Model model
    ) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        List<ApprovalDto> approvals = new ArrayList<>();
        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = approvalService.getApprovalWaitingSearchCount(empId, approvalFormSelect, approvalSearchValue);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);
        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        approvals = approvalService.findApprovalWaitingSearchList(start, empId, approvalFormSelect, approvalSearchValue);


//        List<ApprovalDto> approval = approvalService.findApproval(empId);
        model.addAttribute("approvals", approvals);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);
        return "/pages/approval/approval-content-module";
    }

    // 결재 반려 또는 승인 권한 조건 검사
    @GetMapping("/approval-waiting-detail")
    public String approvalAwaitingDetail(ApprovalDto approvalDocumentNo, Model model, HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();
        // 기안서 정보 , 결재자들 정보
        approvalDetails(approvalDocumentNo, model);
        // 기안서 정보
        ApprovalDto approvalDocument = approvalService.findApprovalByApprovalNo(approvalDocumentNo);
        // 결재자들 정보
        List<ApproverDto> approvers = approvalService.findApproverByApprovalNo(approvalDocumentNo);

        boolean hasAuthority = false;
        int mySequence = 0;
        // 결재자가 자신 첫 번쨰 일경우
        for (ApproverDto approver : approvers) {
            if (approver.getEmpId() == empId) {
                mySequence = approver.getApprovalSequence();
                if (mySequence == 1) {
                    hasAuthority = true;
                    break;
                }
            }
        }
        // 결재자가 여러명일 경우 앞 사람이 결재 승인 상태인지
        if (!hasAuthority) {
            for (ApproverDto approver : approvers) {
                if (approver.getEmpId() != empId && approver.getApprovalSequence() < mySequence) {
                    if (!approver.getApprovalActive().equals("결재승인")) {
                        hasAuthority = false;
                        break;
                    }
                    hasAuthority = true;
                }
            }
        }

        String authorityStatus = hasAuthority ? "결재 권한있음" : "결재 권한없음";

        model.addAttribute("approvalDocument", approvalDocument);
        model.addAttribute("approvers", approvers);
        model.addAttribute("authorityStatus", authorityStatus);

        return "/pages/approval/approval-waiting-detail";
    }

    @PostMapping(path = {"/approval-confirm"})
    public String approverConfirm(int approvalDocumentNo,
                                  String approvalComment,
                                  int btnType,
                                  HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();

        ApproverDto approverConverter = new ApproverDto();
        approverConverter.setApprovalDocumentNo(approvalDocumentNo);
        approverConverter.setApprovalComment(approvalComment);
        approverConverter.setEmpId(empId);
        approverConverter.setBtnType(btnType);

        approvalService.approvalActiveProcess(approverConverter);

        return "redirect:approval-waiting-list";
    }

    @GetMapping("/approval-reject-detail")
    public String approvalRejectDetail(ApprovalDto approvalDocumentNo, Model model) {

        ApproverDto approverReject = approvalService.findApproverLastComment(approvalDocumentNo);

        approvalDetails(approvalDocumentNo, model);
        model.addAttribute("approverReject", approverReject);

        return "/pages/approval/approval-reject-detail";
    }

    @PostMapping("/approval-reject-confirm")
    public String approvalRejectComfirm(ApprovalDto Approval,
                                        @RequestParam("approvalDocumentNo") int approvalDocumentNo,
                                        HttpSession session) {

        approvalModify(Approval, approvalDocumentNo, session);

        return "redirect:/forms/approval/approval-list";
    }

    @GetMapping(path = {"/approval-write-test"})
    public String approvalWrite2(Model model, HttpSession session) {

        List<EmployeeDto> employee = approvalService.findEmployee();
        List<ApprovalFormDto> approvalForm = approvalService.findApprovalForm();
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("approvalForm", approvalForm);
        model.addAttribute("employees", employee);

        return "/pages/approval/approval-write-test";
    }

    @GetMapping(path = {"/approval-line-list"})
    public String approvalLineSave(HttpSession session, Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        int empId = loginUser.getEmpId();


        return "/pages/approval/approval-line-list";

    }

   @GetMapping("/approval-line-content")
   public String approvalLineContent(HttpSession session,
                                     HttpServletRequest req ,
                                     @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                     Model model){

       EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
       int empId  = loginUser.getEmpId();
       List<ApprovalDto> approvals = new ArrayList<>();
       // 페이징 처리
       int pageSize = 5;
       int pagerSize = 5;
       int dataCount = approvalService.getApprovallineCount(empId);
       String uri = req.getRequestURI();
       String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
       String queryString = req.getQueryString();

       int start = pageSize * (pageNo - 1);
       ProjectPager pager = new ProjectPager(dataCount, pageNo , pageSize, pagerSize, linkUrl, queryString );

       approvals = approvalService.findApproverList(start,empId);

       model.addAttribute("approvals", approvals);
       model.addAttribute("pager", pager);
       model.addAttribute("pageNo", pageNo);
       model.addAttribute("dataCount", dataCount);
       return "/pages/approval/approval-line-content-module";

    }

   @GetMapping(path = {"/approval-list-detail"})
   public String approvalListDetail(@RequestParam("saveLineNo") int saveLineNo , Model model){

        ApproverDto approverTitle = approvalService.findApproverSaveTitle(saveLineNo);
        //   결재자 정보 불러오기
        List<ApproverDto> approverSave = approvalService.findApproverSave(saveLineNo);

        model.addAttribute("approverSave",approverSave);
        model.addAttribute("approverTitle",approverTitle);

        return "/pages/approval/approval-line-Detail";
        }


}














