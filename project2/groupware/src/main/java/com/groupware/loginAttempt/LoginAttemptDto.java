package com.groupware.loginAttempt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttemptDto {

//    private Long id; // Entity 생성시 사용할 필드
    private String diff;
    private String userName; // 로그인 유저아이디
    private Timestamp timestamp;
    private String ipAddress;
    private boolean success;


}
