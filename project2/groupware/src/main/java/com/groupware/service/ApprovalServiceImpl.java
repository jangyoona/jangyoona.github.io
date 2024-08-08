package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.mapper.ApprovalMapper;
import com.groupware.mapper.HrMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Setter
public class ApprovalServiceImpl implements ApprovalService {


    private ApprovalMapper approvalMapper;

    @Setter(onMethod_ = {@Autowired})
    private HrService hrService;

    @Override
    public void writeApprovalForm(ApprovalFormDto approval) {

        approvalMapper.insertApprovalForm(approval);

    }

    @Override
    public ApprovalFormDto getApprovalForm(String approvalFrom) {

       return approvalMapper.selectApprovalForm(approvalFrom);

    }

    @Override
    public List<ApprovalFormDto> findApprovalForm() {

        return approvalMapper.selectApprovalFormDocument();
    }

    @Override
    public List<ApprovalDto> findApproval(int empId) {

        return approvalMapper.selectApproval(empId);
    }


    @Override
    public void writeApproval(ApprovalDto approval) {

        // 결재 문서 삽입
            approvalMapper.insertApproval(approval);

        // approval_document_no 값 가져오기
        int approvalSequence = 1; // approvalSequence 변수 선언 및 초기화

        for (ApproverDto approver : approval.getApprovers()) {
            approver.setApprovalDocumentNo(approval.getApprovalDocumentNo()); // approvalDocumentNo 설정
            approver.setEmpId(approver.getEmpId());
            approver.setApprovalSequence(approvalSequence++); // approvalSequence 설정 및 증가

                approvalMapper.insertApprover(approver);
        }
    }


    @Override
    public List<EmployeeDto> findEmployee() {
        return approvalMapper.selectEmployee();
    }

    // 기안서 정보 조회
    @Override
    public ApprovalDto findApprovalByApprovalNo( ApprovalDto approvalDocumentNo ) {
        return approvalMapper.selectApprovalByApprovalNo( approvalDocumentNo );
    }
    // 결재자 정보 조회
    @Override
    public List<ApproverDto> findApproverByApprovalNo(ApprovalDto approvalDocumentNo) {
        return approvalMapper.selectApproverByApprovalNo( approvalDocumentNo );
    }

    @Override
    public void deleteApproval(int approvalDocumentNo) {

        approvalMapper.updateApprovalDelete( approvalDocumentNo );
    }

    @Override
    public List<ApprovalDto> findApprovalByEmpId(int empId) {

        return approvalMapper.selectApprovalByEmpId(empId);
    }

    @Override
    public List<ApproverDto> findApproverByApprovalDocumentNo(int approvalDocumentNo) {

        return approvalMapper.selectApproverByApprovalDocumentNo(approvalDocumentNo);
    }

    //결재자 상태변경 조건 검사
    @Override
    public void approvalActiveProcess(ApproverDto approverConverter) {

        List<ApproverDto> approvers = findApproverByApprovalDocumentNo(approverConverter.getApprovalDocumentNo());

        // 자신의 approvalSequence 찾기
        int mySequence = 0;
        boolean isApproverFound = false;
        for (ApproverDto approver : approvers) {
            if (approver.getEmpId() == approverConverter.getEmpId()) {
                mySequence = approver.getApprovalSequence();
                isApproverFound = true;
                break;
            }
        }

        approverConverter.setMySequence(mySequence);

        if (isApproverFound && approverConverter.getBtnType() == 1) {
                int approverCount = approvers.size();
                // 최종 결재 단계 "A"
                // Case 1: 결재자가 혼자일때 또는 마지막 결재자
                if (approverCount == 1 || mySequence == approverCount) {
                    approverConverter.setType("A");
                    approvalMapper.updateApproverActiveByApprovalNoByEmpIdByApprovalComment(approverConverter);
                    List<VacationRegistration> approvals = hrService.findAndParseApprovalLeaves();
                    for (VacationRegistration approval : approvals) {
                        hrService.insertAttendanceLeavesAuto(approval);
                    }
                    approvalMapper.updateActive(approverConverter);

                    // Case 2: 첫 번째 결재자 또는 중간 결재자
                } else if (mySequence == 1 || (mySequence > 1 && mySequence < approverCount)) {
                    approverConverter.setType("B");
                    approvalMapper.updateApproverActiveByApprovalNoByEmpIdByApprovalComment(approverConverter);
                }
            // 반려
        } else if(isApproverFound && approverConverter.getBtnType() == 2)
            approverConverter.setType("C");
            approvalMapper.updateApproverActiveByApprovalNoByEmpIdByApprovalComment(approverConverter);

    }

    @Override
    public ApproverDto findApproverLastComment(ApprovalDto approvalDocumentNo) {
      return  approvalMapper.selectApprovalCommentByApprovalNo(approvalDocumentNo);
    }

    @Override
    public void updateApprovalContent(ApprovalDto Approval) {
        approvalMapper.updateApprovalContent(Approval);
    }

    @Override
    public int getApprovalCount(int empId) {
        return approvalMapper.selectApprovalCount(empId);

    }

    @Override
    public List<ApprovalDto> findApprovals(int start, int empId) {
        return approvalMapper.selectApprovals(start , empId);
    }

    @Override
    public int getApprovalWaitingCount(int empId) {
        return approvalMapper.selectApprovalWaitingCount(empId);
    }

    @Override
    public List<ApprovalDto> findApprovalWaitings(int empId, int start) {
        return approvalMapper.selectApprovalWaitings(empId,start);
    }

    @Override
    public int getApprovalFormCount() {
        return approvalMapper.selectApprovalFormCount();
    }

    @Override
    public List<ApprovalDto> findApprovalForms(int start) {

        return approvalMapper.selectApprovalForms(start);
    }

    @Override
    public List<ApproverDto> findApproverEmployeeByApprovalNo(ApprovalDto approvalDocumentNo) {
        return approvalMapper.selectApproverEmployeeByApprovalNo(approvalDocumentNo);
    }

    @Override
    public void updateApproval(ApprovalDto approval) {

        approvalMapper.updateApprovalActive(approval);
    }

    @Override
    public int getApprovalSearchAllCount(int empId,String approvalFormSelect, String approvalSearchValue) {
        return approvalMapper.getApprovalSearchCount(empId,approvalFormSelect,approvalSearchValue);
    }

    @Override
    public List<ApprovalDto> findApprovalSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue) {

        return approvalMapper.selectApprovalSearchList(start,empId,approvalFormSelect,approvalSearchValue);
    }

    @Override
    public int getApprovalWaitingSearchCount(int empId, String approvalFormSelect, String approvalSearchValue) {
        return approvalMapper.getApprovalWaitingSearchCount(empId,approvalFormSelect,approvalSearchValue);
    }

    @Override
    public List<ApprovalDto> findApprovalWaitingSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue) {
        return approvalMapper.selectApprovalWaitingSearchList(start,empId,approvalFormSelect,approvalSearchValue);
    }

    @Override
    public int getApprovalFormSearchCount(String approvalFormSelect, String approvalSearchValue) {
        return approvalMapper.getApprovalFormCount(approvalFormSelect,approvalSearchValue);
    }

    @Override
    public List<ApprovalDto> findApprovalFormSearch(int start, String approvalFormSelect, String approvalSearchValue) {
        return approvalMapper.selectApprovalFormSearch(start,approvalFormSelect,approvalSearchValue);
    }

    @Override
    public ApprovalFormDto findApprovalFormDetail(int approvalFormNo) {
        return approvalMapper.selectApprovalFormByapprovalFormNo(approvalFormNo);
    }

    @Override
    public void updateApprovalForm(int approvalFormNo) {
        approvalMapper.updateApprovalForm(approvalFormNo);
    }

    @Override
    public void writeReApproval(ApprovalDto approval) {
        // 결재 문서 삽입
        //기존 데이터 날리기
        approvalMapper.insertApprovalAndApprover(approval);

        // approval_document_no 값 가져오기
        int approvalSequence = 1; // approvalSequence 변수 선언 및 초기화

        for (ApproverDto approver : approval.getApprovers()) {
            approver.setApprovalDocumentNo(approval.getApprovalDocumentNo()); // approvalDocumentNo 설정
            approver.setEmpId(approver.getEmpId());
            approver.setApprovalSequence(approvalSequence++); // approvalSequence 설정 및 증가

            approvalMapper.insertApprover(approver);
        }
        approvalMapper.insertApprovalAndApprover(approval);
    }

    @Override
    public void writeApproverLine(ApprovalDto approval) {

        // 결재 문서 삽입
        approvalMapper.insertApproverLine(approval);

        // approval_document_no 값 가져오기
        int approvalSequence = 1; // approvalSequence 변수 선언 및 초기화

        for (ApproverDto approver : approval.getApprovers()) {
            approver.setSaveLineNo(approval.getSaveLineNo()); // approvalDocumentNo 설정
            approver.setEmpId(approver.getEmpId());
            approver.setApprovalSequence(approvalSequence++); // approvalSequence 설정 및 증가

            approvalMapper.insertApprovers(approver);
        }
    }

    @Override
    public int getApprovallineCount(int empId) {

        return approvalMapper.getApprovalLineCount(empId);
    }

    @Override
    public List<ApprovalDto> findApproverList(int start ,int empId) {
        return approvalMapper.selectApproverListByempId(start,empId);
    }

    @Override
    public List<ApproverDto> findApproverSave(int saveLineNo) {
        return approvalMapper.selectApproverSave(saveLineNo);
    }

    @Override
    public ApproverDto findApproverSaveTitle(int saveLineNo) {
        return approvalMapper.selectApproverSaveTitle(saveLineNo);
    }

    @Override
    public List<ApproverDto> findApproverSaveList() {
        return approvalMapper.selectApproverSaveList();
    }

    @Override
    public List<ApproverDto> getApprovalLine(String lineTitleId, int empId) {

        return approvalMapper.selectApprovalList(lineTitleId,empId);
    }

    @Override
    public List<ApproverDto> findLineTitle(int empId) {

        return approvalMapper.selectLineTitleByEmpId(empId);

    }

}













