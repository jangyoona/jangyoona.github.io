package com.groupware.loginAttempt;

import com.groupware.mapper.LoginAttemptMapper;
import lombok.Setter;

public class LoginAttemptServiceImpl implements LoginAttemptService {

    @Setter
    private LoginAttemptMapper loginAttemptMapper;

    @Override
    public void saveLoginAttempt(LoginAttemptDto loginAttempt) {
        loginAttemptMapper.insertLoginAttempt(loginAttempt);
    }
}