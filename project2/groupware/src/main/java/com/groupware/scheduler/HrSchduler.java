package com.groupware.scheduler;

import com.groupware.dto.EmployeeDto;
import com.groupware.mapper.AttdMapper;
import com.groupware.mapper.HrMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class HrSchduler {

    @Setter(onMethod_ = {@Autowired})
    private HrMapper hrMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void insertannual() {

        hrMapper.updateAnnualCountPlusAllEmployee();

    }

}