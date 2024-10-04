package com.jackson.partnersearchbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.SuccessCode;
import com.jackson.partnersearchbackend.exception.BusinessException;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.domain.UserTeam;
import com.jackson.partnersearchbackend.model.query.TeamQuery;
import com.jackson.partnersearchbackend.model.request.TeamAddRequest;
import com.jackson.partnersearchbackend.model.request.TeamJoinRequest;
import com.jackson.partnersearchbackend.model.request.TeamQuitRequest;
import com.jackson.partnersearchbackend.model.request.TeamUpdateRequest;
import com.jackson.partnersearchbackend.model.vo.TeamUserVO;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.service.UserService;
import com.jackson.partnersearchbackend.service.UserTeamService;
import com.jackson.partnersearchbackend.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.jackson.partnersearchbackend.constant.UserConstant.ADMIN_ROLE;

@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param teamAddRequest 创建队伍请求体
     * @param httpServletRequest http请求体
     * @return 创建成功为新增队伍的id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest httpServletRequest){
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        long teamId = teamService.addTeam(teamAddRequest,loginUser);
        return ResultUtils.success(teamId, SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 解除队伍
     * @param id 队伍id
     * @param httpServletRequest 请求体
     * @return 返回删除结果，返回即为true，否则报错
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id, HttpServletRequest httpServletRequest){
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean remove = teamService.deleteTeam(id,loginUser);
        if (!remove) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true, SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 更新队伍信息
     * @param teamUpdateRequest 队伍更新信息
     * @param httpServletRequest 请求体
     * @return 更新结果，成功为true，否则为false
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest httpServletRequest){
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);

        // 实际更新操作
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!update) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true, SuccessCode.COMMON_SUCCESS);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id, HttpServletRequest httpServletRequest){
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        Team team = teamService.getById(id);
        if (team == null) {
            throw  new BusinessException(ErrorCode.NULL_ERROR,"未查询到相关信息");
        }
        if (!Objects.equals(loginUser.getId(), team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return ResultUtils.success(team, SuccessCode.COMMON_SUCCESS);
    }


    private Page<TeamUserVO> queryTeamList(TeamQuery teamQuery, HttpServletRequest httpServletRequest,boolean isAdmin, boolean isMy) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (teamQuery.getPageNum() == null || teamQuery.getPageSize() == null)throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Page<TeamUserVO> teamUserVOPage = teamService.listTeams(teamQuery, isAdmin,isMy);
        List<TeamUserVO> teamList = teamUserVOPage.getRecords();
        // 原本就没有任何队伍直接返回空列表
        if (CollectionUtils.isEmpty(teamList)) return teamUserVOPage;

        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        User loginUser = userService.getLoginUser(httpServletRequest);
        userTeamQueryWrapper.eq("user_id", loginUser.getId());
        userTeamQueryWrapper.in("team_id", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        // 已加入的队伍 id 集合
        Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team -> {
            boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
            team.setHasJoin(hasJoin);
        });

        // 3、查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("team_id", teamIdList);
        List<UserTeam> teams = userTeamService.list(userTeamJoinQueryWrapper);
        // 队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = teams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        teamUserVOPage.setRecords(teamList);
        return teamUserVOPage;
        
    }

    /**
     * 查询队伍
     * @param teamQuery 查询条件
     * @param httpServletRequest 请求体
     * @return 队伍列表
     */
    @PostMapping("/list")
    public BaseResponse<Page<TeamUserVO>> listTeam(@RequestBody TeamQuery teamQuery,HttpServletRequest httpServletRequest) {
        Integer userRole = userService.getLoginUser(httpServletRequest).getUserRole();
        boolean isAdmin = userRole == ADMIN_ROLE;
        Page<TeamUserVO> teamUserVOPage = queryTeamList(teamQuery, httpServletRequest,isAdmin,false);
        return ResultUtils.success(teamUserVOPage, SuccessCode.COMMON_SUCCESS);

    }

    /**
     * 获取加入但并非创建的队伍
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listJoinedTeam(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();

        // 用户创建的队伍
        LambdaQueryWrapper<Team> createdTeamQueryWrapper = new LambdaQueryWrapper<>();
        createdTeamQueryWrapper.eq(Team::getUserId,userId);
        List<Team> createdTeamList = teamService.list(createdTeamQueryWrapper);
        Set<Long> createdListId = createdTeamList.stream().map(Team::getId).collect(Collectors.toSet());

        // 用户加入和创建的队伍
        LambdaQueryWrapper<UserTeam> joinedTeamQueryWrapper = new LambdaQueryWrapper<>();
        joinedTeamQueryWrapper.eq(UserTeam::getUserId,userId);
        List<UserTeam> joinedTeamList = userTeamService.list(joinedTeamQueryWrapper);
        List<Long> joinedlist = joinedTeamList.stream().map(UserTeam::getTeamId).toList();

        if (joinedlist.isEmpty()) return ResultUtils.success(new ArrayList<>(), SuccessCode.COMMON_SUCCESS);

        TeamQuery teamQuery = new TeamQuery();
        if (createdListId.isEmpty()) { // 没有创建队伍
            teamQuery.setIdList(joinedlist);
        } else { // 有创建队伍进行剔除
            List<Long> idList = joinedlist.stream().filter(id -> !createdListId.contains(id)).toList();
            teamQuery.setIdList(idList);
        }
        Page<TeamUserVO> teamUserVOPage = this.queryTeamList(teamQuery, httpServletRequest,true,true);
        List<TeamUserVO> list = teamUserVOPage.getRecords();
        return ResultUtils.success(list, SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 获取创建但并非加入的队伍
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listCreatedTeam(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setUserId(userId);
        Page<TeamUserVO> teamUserVOPage = this.queryTeamList(teamQuery, httpServletRequest,true,true);
        List<TeamUserVO> list = teamUserVOPage.getRecords();
        return ResultUtils.success(list, SuccessCode.COMMON_SUCCESS);
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

    /**
     * 加入队伍
     * @param teamJoinRequest 队伍加入请求体
     * @param httpServletRequest 请求体
     * @return 里面包含的数据是加入的情况
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest httpServletRequest) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtils.success(result,SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest 队伍退出请求体
     * @param httpServletRequest 请求体
     * @return 里面包含的数据为退出结果
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest httpServletRequest) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(result,SuccessCode.COMMON_SUCCESS);
    }




}
