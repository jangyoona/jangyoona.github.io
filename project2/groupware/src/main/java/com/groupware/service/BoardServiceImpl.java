package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.mapper.BoardMapper;
import lombok.Setter;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class BoardServiceImpl implements BoardService {

    @Setter
    private BoardMapper boardMapper;

    @Override
    public int getBoardCount(String searchText) {
        return boardMapper.countBoards(searchText);
    }
    @Override
    public List<BoardDto> findBoardByRange(int start, int count, String searchText) {
        return boardMapper.selectBoardByRange(start, count, searchText);
    }
    @Override
    public BoardDto findBoardByBoardNo(int boardNo) {
        BoardDto board = boardMapper.selectBoardByBoardNo(boardNo);
        boardMapper.updateBoardCountByBoardNo(boardNo);
        return board;
    }

    @Override
    public void updateBoard(BoardDto board) {
        boardMapper.updateBoardContentByBoardNo(board);
    }

    @Override
    public boolean deleteBoard(Long boardNo) {
        int result = boardMapper.updateBoardActiveByBoardNo(boardNo);
        return result > 0; // 성공적으로 업데이트된 행의 수가 0보다 크면 true 반환
    }

    @Override
    public PositionDto getPositionNameByPositionNo(int positionNo) {
        PositionDto position = boardMapper.selectPositionNameByPositionNo(positionNo);
        return position;
    }

    @Override
    public List<String> getAllEmail(int empId) {
        List<String> allEmail = boardMapper.selectAllEmail(empId);
        return allEmail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public void writeBoard(BoardDto board, ArrayList<BoardAttachDto> attachments) {

        boardMapper.insertBoard(board);

        // 첨부파일이 존재하는 경우에만 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (BoardAttachDto attach : attachments) {
                attach.setBoardNo(board.getBoardNo()); // 메일 번호 설정
                boardMapper.insertBoardAttach(attach);
            }
        }
    }

    @Override
    public BoardAttachDto findBoardAttachByAttachNo(int attachNo) {
        BoardAttachDto attach = boardMapper.selectBoardAttachByAttachNo(attachNo);
        return attach;
    }
}
