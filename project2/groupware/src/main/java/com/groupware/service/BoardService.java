package com.groupware.service;

import com.groupware.dto.BoardAttachDto;
import com.groupware.dto.BoardDto;
import com.groupware.dto.MailAttachDto;
import com.groupware.dto.PositionDto;

import java.util.ArrayList;
import java.util.List;

public interface BoardService {


    int getBoardCount(String searchText);
    List<BoardDto> findBoardByRange(int start, int pageSize, String searchText);
    BoardDto findBoardByBoardNo(int boardNo);
    void updateBoard(BoardDto board);
    boolean deleteBoard( Long boardNo);
    PositionDto getPositionNameByPositionNo(int positionNo);
    List<String> getAllEmail(int EmpId);
    void writeBoard( BoardDto board, ArrayList<BoardAttachDto> attachments);


    BoardAttachDto findBoardAttachByAttachNo(int attachNo);
}
