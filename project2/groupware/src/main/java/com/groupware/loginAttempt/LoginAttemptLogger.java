package com.groupware.loginAttempt;

import com.groupware.dto.EmployeeDto;
import com.groupware.mapper.LoginAttemptMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class LoginAttemptLogger {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptLogger.class);
    @Setter(onMethod_ = { @Autowired })
    private LoginAttemptMapper loginAttemptMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    // 로그인 성공, 실패 구분
    @AfterReturning(pointcut = "execution(* com.groupware.controller.AccountController.login(..))", returning = "result")
    public void logSuccessfulLogin(JoinPoint joinPoint, Object result) {

        if(result.equals("redirect:/account/login?loginfail=true")) {
            saveLoginAttempt(joinPoint, false);
        } else if(result.equals("redirect:/home")){
            saveLoginAttempt(joinPoint, true);
        }
    }



    // 로그인 시도 기록 저장
    private void saveLoginAttempt(JoinPoint joinPoint, boolean isSuccess) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            EmployeeDto loginUser = (EmployeeDto) req.getSession().getAttribute("loginUser");

            LoginAttemptDto loginAttempt = new LoginAttemptDto();
            loginAttempt.setDiff(req.getRequestURI().equals("/account/login") ? "login" : "logout");
            loginAttempt.setUserName(loginUser != null ? String.valueOf(loginUser.getEmpId()) : "GUEST");
            loginAttempt.setTimestamp(Timestamp.valueOf(sdf.format(new Date())));
            loginAttempt.setIpAddress(req.getRemoteAddr());
            loginAttempt.setSuccess(isSuccess);

            // MyBatis를 이용해 Mapper를 통해 데이터베이스에 저장
            loginAttemptMapper.insertLoginAttempt(loginAttempt);
        }
    }

    // 로그아웃
    @AfterReturning(pointcut = "execution(* com.groupware.controller.AccountController.logout(..))", returning = "result")
    public void logoutLog(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();

            EmployeeDto loginUser = (EmployeeDto) req.getSession().getAttribute("loginUser");

//            System.out.println("login user? : " + loginUser.getEmpId());

            LoginAttemptDto loginAttempt = new LoginAttemptDto();
            if(loginUser != null) {
                loginAttempt.setDiff("logout");
                loginAttempt.setUserName(String.valueOf(loginUser.getEmpId()));
                loginAttempt.setTimestamp(Timestamp.valueOf(sdf.format(new Date())));
                loginAttempt.setIpAddress(req.getRemoteAddr());
                loginAttempt.setSuccess(true);
                loginAttemptMapper.insertLoginAttempt(loginAttempt);
            }

            HttpSession session = req.getSession();
            session.removeAttribute("loginUser");
        }

    }


    // 로그인 실패 시 (security?)
//    @AfterThrowing(pointcut = "execution(* com.groupware.controller.AccountController.login(..))", throwing = "error")
//    public void logFailedLogin(JoinPoint joinPoint, Throwable error) {
//        saveLoginAttempt(joinPoint, false);
//        System.out.println("login fail");
//    }
}
