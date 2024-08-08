package com.groupware.mapper;

import com.groupware.dto.BoardAttachDto;
import com.groupware.dto.BoardDto;
import com.groupware.dto.MailAttachDto;
import com.groupware.dto.PositionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    void insertBoard(BoardDto board);
    void insertBoardAttach(BoardAttachDto attach);

    List<BoardDto> selectBoardByRange(@Param("from") int from, @Param("count") int count, @Param("searchText") String searchText);
    BoardDto selectBoardByBoardNo(@Param("boardNo") int boardNo);
    int countBoards(String searchText);
    PositionDto selectPositionNameByPositionNo(@Param("positionNo") int positionNo);
    List<String> selectAllEmail(@Param("empId") int empId);

    void updateBoardCountByBoardNo(@Param("boardNo") int boardNo);
    void updateBoardContentByBoardNo(BoardDto board);
    int updateBoardActiveByBoardNo(@Param("boardNo") Long boardNo);


    BoardAttachDto selectBoardAttachByAttachNo(int attachNo);
}
