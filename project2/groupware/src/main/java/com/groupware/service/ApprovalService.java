package com.groupware.service;

import com.groupware.dto.*;

import java.util.List;

public interface ApprovalService {

    void writeApprovalForm(ApprovalFormDto approvalForm);
    //ajax로 양식가져오는 메서드
    ApprovalFormDto getApprovalForm(String approvalFrom);
    // 양식버튼에 들어갈 양식 이름 불러오는 메서드
    List<ApprovalFormDto> findApprovalForm();
    //자신이 신청한 결재 진행상황 리스트
    List<ApprovalDto> findApproval(int empId);

    void writeApproval(ApprovalDto approval);

    List<EmployeeDto> findEmployee();

    ApprovalDto findApprovalByApprovalNo(ApprovalDto approvalDocumentNo);
    //결재자 정보
    List<ApproverDto> findApproverByApprovalNo(ApprovalDto approvalDocumentNo);

    void deleteApproval(int approvalDocumentNo);
    // 자신이 결자할 대기문서 조회 (결재대기문서함)
    List<ApprovalDto> findApprovalByEmpId(int empId);
    //조건 검사전 처리할 문서의 값을 가져오는 메서드
    List<ApproverDto> findApproverByApprovalDocumentNo(int approvalDocumentNo);

    void approvalActiveProcess(ApproverDto approverConverter);

    ApproverDto findApproverLastComment(ApprovalDto approvalDocumentNo);

    void updateApprovalContent(ApprovalDto Approval);
    //page
    int getApprovalCount(int empId);

    List<ApprovalDto> findApprovals(int start , int empId);
    //page
    int getApprovalWaitingCount(int empId);

    List<ApprovalDto> findApprovalWaitings(int empId, int start);

    int getApprovalFormCount();

    List<ApprovalDto> findApprovalForms(int start);


    List<ApproverDto> findApproverEmployeeByApprovalNo(ApprovalDto approvalDocumentNo);

    void updateApproval(ApprovalDto approval);


    int getApprovalSearchAllCount(int empId , String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> findApprovalSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue);

    int getApprovalWaitingSearchCount(int empId, String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> findApprovalWaitingSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue);

    int getApprovalFormSearchCount(String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> findApprovalFormSearch(int start, String approvalFormSelect, String approvalSearchValue);

    ApprovalFormDto findApprovalFormDetail(int approvalFormNo);

    void updateApprovalForm(int approvalFormNo);
    //임시저장 다시 인설트
    void writeReApproval(ApprovalDto approval);
    //결재선 저장
    void writeApproverLine(ApprovalDto approval);

    int getApprovallineCount(int empId);

    List<ApprovalDto> findApproverList(int start ,int empId);

    List<ApproverDto> findApproverSave(int saveLineNo);

    ApproverDto findApproverSaveTitle(int saveLineNo);

    List<ApproverDto> findApproverSaveList();

    List<ApproverDto>  getApprovalLine(String lineTitleId,int empId);

    List<ApproverDto> findLineTitle(int empId);

}
