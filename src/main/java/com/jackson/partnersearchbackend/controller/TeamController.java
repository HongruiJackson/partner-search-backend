package com.jackson.partnersearchbackend.controller;

import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.SuccessCode;
import com.jackson.partnersearchbackend.exception.BusinessException;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.utils.ResultUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest httpServletRequest){
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean save = teamService.save(team);
        if (!save) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }
        return ResultUtils.success(team.getId(), SuccessCode.COMMON_SUCCESS);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id, HttpServletRequest httpServletRequest){
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true, SuccessCode.COMMON_SUCCESS);
    }

    @PostMapping("/update")
    public BaseResponse<Long> updateTeam(@RequestBody Team team, HttpServletRequest httpServletRequest){
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update = teamService.updateById(team);
        if (!update) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(team.getId(), SuccessCode.COMMON_SUCCESS);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id, HttpServletRequest httpServletRequest){
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw  new BusinessException(ErrorCode.NULL_ERROR,"未查询到相关信息");
        }
        return ResultUtils.success(team, SuccessCode.COMMON_SUCCESS);
    }
}
