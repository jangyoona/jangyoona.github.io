package com.groupware.security;

import com.groupware.dto.EmployeeDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        EmployeeDto user = (EmployeeDto) request.getSession().getAttribute("loginUser");
        String uri = request.getRequestURI();
//        if(uri.contains("/")) {
//            if(user == null) {
//                log.info("미인증 사용자");
//                response.sendRedirect("/account/login");
//                return false;
//            }
//        }
        if(user ==null) {
            if (uri.contains("/account/user-check") || uri.contains("account/email-check") || uri.contains("/account/reset-password")) {
                return true;
            }
            response.sendRedirect("/account/login");
            return false;
        }

        if (uri.contains("/pages/attendance/attendance-edit") || uri.contains("/account/modify") || uri.contains("/account/register")) {
            if (user.getDeptNo() != 6) {
                response.sendRedirect("/pages/attendance/alert"); // Redirect to an unauthorized access page
                return false;
            }
        }

        return true;
    }

    // Controller를 호출한 후에, 호출되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
//		System.out.println("AuthInterceptor.postHandle");

    }


    // View를 호출한 후에, 호출되는 메서드 (모든 작업이 끝난 후에 호출)
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
//		System.out.println("AuthInterceptor.afterCompletion");

    }
}
