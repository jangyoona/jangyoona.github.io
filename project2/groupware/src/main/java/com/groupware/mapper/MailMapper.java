package com.groupware.mapper;

import com.groupware.dto.Mail;
import com.groupware.dto.MailAttachDto;
import com.groupware.dto.MailDto;
import com.groupware.dto.PositionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MailMapper {

    void insertMail(MailDto mail);
    void insertMailAttach(MailAttachDto attach);

    Map<String, Integer> countMailsByStatus(String empEmail);
    int countMails(@Param("empEmail")String empEmail, @Param("type")String type, @Param("searchText")String searchText);
    List<MailDto> selectMailByRange(@Param("from") int from, @Param("count") int count, @Param("empEmail") String empEmail, @Param("type") String type, @Param("searchText") String searchText);
    MailDto selectMailByMailNo(@Param("emailNo") int emailNo, @Param("empEmail")String empEmail);
    MailDto selecDetailMailByMailNo(@Param("emailNo") int emailNo, @Param("empEmail")String empEmail, @Param("type")String type);
    MailDto selecDetailMailBySameMailNo(@Param("emailNo") int emailNo, @Param("empEmail")String empEmail, @Param("type")String type);

    void updateFromEmails(@Param("fromEmailNos") List<Long> fromEmailNos, @Param("type")String type);
    void updateToEmails(@Param("toEmailNos")List<Long> toEmailNos, @Param("type")String type);

    MailAttachDto selectMailAttachByAttachNo(int attachNo);

    PositionDto selectPositionNameByPositionNo(@Param("positionNo") int positionNo);

    List<MailDto> getToDeletedEmailList();

    List<MailDto> getFromDeletedEmailList();


}
