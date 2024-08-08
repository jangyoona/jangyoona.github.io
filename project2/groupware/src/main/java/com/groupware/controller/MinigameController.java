package com.groupware.controller;

import com.groupware.dto.EmployeeDto;
import com.groupware.dto.MinigameDto;
import com.groupware.mapper.MinigameMapper;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MinigameController {

    @Setter(onMethod_ = {@Autowired})
    private MinigameMapper minigameMapper;

    @GetMapping(path = "/modules/minigame")
    public String showMinigame(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        MinigameDto minigameInfo = minigameMapper.findMinigameInfoByEmpId(loginUser.getEmpId());
        model.addAttribute("minigameInfo", minigameInfo);

        List<MinigameDto> allMinigameInfo = minigameMapper.findAllMinigameInfo();
        model.addAttribute("allMinigameInfo", allMinigameInfo);

        System.out.println(allMinigameInfo);

        return "/modules/minigame";
    }

    @PostMapping(path = "/insert-or-update-score")
    @ResponseBody
    public String getscore(
            @RequestBody MinigameDto minigame,
            HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        int score = minigame.getScore();

        int empId = loginUser.getEmpId();

        MinigameDto minigameInfo = minigameMapper.findMinigameInfoByEmpId(loginUser.getEmpId());

        if(minigameInfo == null) {
            minigameMapper.insertMinigameInfo(empId, score);
        } else if (minigameInfo.getScore() < score) {
            minigameMapper.updateMinigameInfo(empId, score);
        } else {
            minigameMapper.updateCountOnly(empId);
        }

        return "success";
    }

}
