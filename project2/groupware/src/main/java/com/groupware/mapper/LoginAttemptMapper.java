package com.groupware.mapper;

import com.groupware.loginAttempt.LoginAttemptDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginAttemptMapper {
    void insertLoginAttempt(LoginAttemptDto loginAttempt);

    List<LoginAttemptDto> findLoginAttemptsById(Map param);

    int findLoginAttemptsCountById(Map param);
}
