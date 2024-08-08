package com.groupware.mapper;

import com.groupware.dto.MinigameDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MinigameMapper {

    void insertMinigameInfo(int empId, int score);

    void updateMinigameInfo(int empId, int score);

    void updateCountOnly(int empId);

    MinigameDto findMinigameInfoByEmpId(Integer empId);

    List<MinigameDto> findAllMinigameInfo();

}
