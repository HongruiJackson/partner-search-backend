package com.jackson.partnersearchbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.SuccessCode;
import com.jackson.partnersearchbackend.exception.BusinessException;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.query.TeamQuery;
import com.jackson.partnersearchbackend.model.request.TeamAddRequest;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.service.UserService;
import com.jackson.partnersearchbackend.utils.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest httpServletRequest){
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        long teamId = teamService.addTeam(teamAddRequest,loginUser);
        return ResultUtils.success(teamId, SuccessCode.COMMON_SUCCESS);
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

    @PostMapping("/list")
    public BaseResponse<List<Team>> listTeam(@RequestBody TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(team);
        List<Team> list = teamService.list(queryWrapper);
        return ResultUtils.success(Objects.requireNonNullElseGet(list, ArrayList::new), SuccessCode.COMMON_SUCCESS);

    }

    @PostMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPage(@RequestBody TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(team);
        Page<Team> pageList = teamService.page(page,queryWrapper);
        return ResultUtils.success(pageList, SuccessCode.COMMON_SUCCESS);

    }
}
