//package com.groupware.loginAttempt;
//
//import com.groupware.mapper.LoginAttemptMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.Setter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
//import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//@Component
//public class AuthenticationEventListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
//
//    @Setter(onMethod_ = @Autowired)
//    private LoginAttemptMapper loginAttemptMapper;
//    private final HttpServletRequest request;
//    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);
//
//
//
//    public AuthenticationEventListener(LoginAttemptMapper loginAttemptMapper, HttpServletRequest request) {
//        this.loginAttemptMapper = loginAttemptMapper;
//        this.request = request;
//    }
//
//    @Override
//    @Transactional
//    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
//        String ipAddress = request.getRemoteAddr();
//        Authentication authentication = event.getAuthentication();
//        String userName = authentication.getName();
//
//        LocalDateTime now = LocalDateTime.now();
//        Timestamp timestamp = Timestamp.valueOf(now);
//
//        logger.info("Successful login attempt. Username: {}, Timestamp: {}, IP Address: {}", userName, timestamp, ipAddress);
//
//        LoginAttemptDto loginAttempt = new LoginAttemptDto(userName, timestamp, ipAddress, true);
//        loginAttemptMapper.insertLoginAttempt(loginAttempt);
//    }
//
//    // Capture failed login attempts
//    @Component
//    public static class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
//
//        private final LoginAttemptMapper loginAttemptMapper;
//        private final HttpServletRequest request;
//
//        public AuthenticationFailureListener(LoginAttemptMapper loginAttemptMapper, HttpServletRequest request) {
//            this.loginAttemptMapper = loginAttemptMapper;
//            this.request = request;
//        }
//
//        @Override
//        @Transactional
//        public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
//            String ipAddress = request.getRemoteAddr();
//            String userName = event.getAuthentication().getPrincipal();
//
//            LocalDateTime now = LocalDateTime.now();
//            Timestamp timestamp = Timestamp.valueOf(now);
//
//            logger.info("Failed login attempt. Username: {}, Timestamp: {}, IP Address: {}", userName, timestamp, ipAddress);
//            try {
//                LoginAttemptDto loginAttempt = new LoginAttemptDto(userName, timestamp, ipAddress, false);
//                loginAttemptMapper.insertLoginAttempt(loginAttempt);
//                logger.info("Saved failed login attempt to database.");
//                System.out.println("리스너 안 입니다" + loginAttempt);
//            } catch (Exception e) {
//                logger.error("Failed to save failed login attempt to database.", e);
//            }
//
//            LoginAttemptDto loginAttempt = new LoginAttemptDto(userName, timestamp, ipAddress, false);
//            loginAttemptMapper.insertLoginAttempt(loginAttempt);
//        }
//    }
//}