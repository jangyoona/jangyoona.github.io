package com.groupware.mapper;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.groupware.dto.ApprovalDto;
import com.groupware.dto.ApprovalFormDto;
import com.groupware.dto.ApproverDto;
import com.groupware.dto.EmployeeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {

    void insertApprovalForm(ApprovalFormDto approvalFrom);

    ApprovalFormDto selectApprovalForm(String approvalFrom);

    List<ApprovalFormDto> selectApprovalFormDocument();

    List<ApprovalDto> selectApproval(int empId);

    void insertApproval(ApprovalDto approval);

    void insertApprover(ApproverDto approver);

    List<EmployeeDto> selectEmployee();

    ApprovalDto selectApprovalByApprovalNo(ApprovalDto approvalDocumentNo);

    List<ApproverDto> selectApproverByApprovalNo(ApprovalDto approvalDocumentNo);

    void updateApprovalDelete(int approvalDocumentNo);

    List<ApprovalDto> selectApprovalByEmpId(int empId);

    List<ApproverDto> selectApproverByApprovalDocumentNo(int approvalDocumentNo);

    void updateApproverActiveByApprovalNoByEmpIdByApprovalComment(ApproverDto approverConverter);

    ApproverDto selectApprovalCommentByApprovalNo(ApprovalDto approvalDocumentNo);

    void updateApprovalContent(ApprovalDto Approval);

    int selectApprovalCount(int empId);

    List<ApprovalDto> selectApprovals(int start, int empId);

    int selectApprovalWaitingCount(int empId);

    List<ApprovalDto> selectApprovalWaitings(int empId, int start);

    int selectApprovalFormCount();

    List<ApprovalDto> selectApprovalForms(int start);

    List<ApproverDto> selectApproverEmployeeByApprovalNo(ApprovalDto approvalDocumentNo);

    void updateApprovalActive(ApprovalDto approval);

    int getApprovalSearchCount(int empId, String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> selectApprovalSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue);

    int getApprovalWaitingSearchCount(int empId, String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> selectApprovalWaitingSearchList(int start, int empId, String approvalFormSelect, String approvalSearchValue);

    int getApprovalFormCount(String approvalFormSelect, String approvalSearchValue);

    List<ApprovalDto> selectApprovalFormSearch(int start, String approvalFormSelect, String approvalSearchValue);

    ApprovalFormDto selectApprovalFormByapprovalFormNo(int approvalFormNo);

    void updateApprovalForm(int approvalFormNo);

    void insertApprovalAndApprover(ApprovalDto approval);

    //  ---------- 결재선 추가
    //결재선 등록
    void insertApproverLine(ApprovalDto approval);
    // 결재자 추가
    void insertApprovers(ApproverDto approver);
    // ----------- end 결재선 추가
    //결재선 카운트
    int getApprovalLineCount(int empId);
    //결재선 불러오기
    List<ApprovalDto> selectApproverListByempId(int start ,int empId);

    void selectApprovalSave(int saveLineNo);

    List<ApproverDto> selectApproverSave(int saveLineNo);

    ApproverDto selectApproverSaveTitle(int saveLineNo);

    void updateActive(ApproverDto approver);

    List<ApproverDto> selectApproverSaveList();

    List<ApproverDto> selectApprovalList(String lineTitleId, int empId);

    List<ApproverDto> selectLineTitleByEmpId(int empId);

}
