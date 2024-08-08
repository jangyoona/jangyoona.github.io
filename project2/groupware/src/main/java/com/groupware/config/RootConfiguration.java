package com.groupware.config;

import com.groupware.loginAttempt.LoginAttemptService;
import com.groupware.loginAttempt.LoginAttemptServiceImpl;
import com.groupware.mapper.*;
import com.groupware.service.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.groupware.mapper"})  // == <mybatis:scan base-package="com.demoweb.mapper"/>
@EnableTransactionManagement // <tx:annotation-driven 과 같은 역할
public class RootConfiguration {

	// application.properties 의 정보를 읽어서 저장하는 객체
	@Autowired
	Environment env;

	@Bean
	// Environment 객체에 저장된 속성 중에서 spring.datasource.hikari로 시작하는 속성을 적용
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean MailService mailService(MailMapper mailMapper) throws Exception {
		MailServiceImpl mailService = new MailServiceImpl();
		mailService.setMailMapper(mailMapper);
		return mailService;
	}

	@Bean NotificationService notificationService(NotificationMapper notificationMapper) throws Exception {
		NotificationServiceImpl notificationService = new NotificationServiceImpl();
		notificationService.setNotificationMapper(notificationMapper);
		return notificationService;
	}

	@Bean BoardService boardService(BoardMapper boardMapper) throws Exception {
		BoardServiceImpl boardService = new BoardServiceImpl();
		boardService.setBoardMapper(boardMapper);
		return boardService;
	}


	@Bean
	public AccountService accountService(AccountMapper accountMapper) throws Exception {
		AccountServiceImpl accountService = new AccountServiceImpl();
		accountService.setAccountMapper(accountMapper);

		return accountService;
	}

	@Bean
	public ApprovalService approvalService(ApprovalMapper approvalMapper) throws Exception {
		ApprovalServiceImpl approvalService = new ApprovalServiceImpl();
		approvalService.setApprovalMapper(approvalMapper);
		return approvalService;
	}

	// Project Bean
	@Bean
	public ProjectService projectService(ProjectMapper projectMapper) {
		ProjectServiceImpl projectService = new ProjectServiceImpl();
		projectService.setProjectMapper(projectMapper);

		return projectService;
	}

	@Bean
	public MyPageService myPageService(MyPageMapper myPageMapper, LoginAttemptMapper loginAttemptMapper, HrMapper hrMapper) throws Exception {
		MyPageServiceImpl myPageService = new MyPageServiceImpl();
		myPageService.setMyPageMapper(myPageMapper);
		myPageService.setLoginAttemptMapper(loginAttemptMapper);
		myPageService.setHrMapper(hrMapper);
		return myPageService;
	}

	@Bean
	LoginAttemptService loginAttemptService(LoginAttemptMapper loginAttemptMapper) throws Exception {
		LoginAttemptServiceImpl loginAttemptService = new LoginAttemptServiceImpl();
		loginAttemptService.setLoginAttemptMapper(loginAttemptMapper);
		return loginAttemptService;
	}

	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource(hikariConfig());
		return dataSource;
	}
	
	@Bean SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		return factoryBean.getObject();
	}
	
	@Bean PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}
	
	@Bean TransactionTemplate transactionTemplate() {
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		transactionTemplate.setTransactionManager(transactionManager());
		return transactionTemplate;
	}
	
}







