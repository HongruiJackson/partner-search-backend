package com.jackson.partnersearchbackend.controller;

import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.service.TeamService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest httpServletRequest){
        return null;
    }
}
