package com.jackson.partnersearchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.TeamStatusEnum;
import com.jackson.partnersearchbackend.exception.BusinessException;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.domain.UserTeam;
import com.jackson.partnersearchbackend.model.request.TeamAddRequest;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.mapper.TeamMapper;
import com.jackson.partnersearchbackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
* @author 10240
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-09-24 11:59:37
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(TeamAddRequest teamAddRequest, User loginUser) {
        // 1. 请求参数为空
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        // 2. 校验创建team的人和cookie是否一致，拦截器校验了登录情况
//        if (team.getUserId() == null || !team.getUserId().equals(loginUser.getId())) {
//            throw new BusinessException(ErrorCode.FORBIDDEN);
//        }
        // 3. 校验信息
        // 3.1 队伍人数 > 1 || <=20
        int maxNum = Objects.requireNonNullElse(teamAddRequest.getMaxNum(),0);
        if (maxNum <=1 || maxNum >20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数只能在2~20之间");
        }
        // 3.2 队伍标题<=20 && >=1
        String teamName = teamAddRequest.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题要在1~20之间");
        }

        // 3.3 描述<=512
        String description = teamAddRequest.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不超过512");
        }
        // 3.4 status是否公开（int）,不传默认为0（公开）
        int status = Objects.requireNonNullElse(teamAddRequest.getTeamStatus(),0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        // 3.5 如果status是加密状态，一定要有密码，且密码 <= 32
        String teamPassword = teamAddRequest.getTeamPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET) && (StringUtils.isBlank(teamPassword) || teamPassword.length()>32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
        }
        // 3.6 超时校验，超时时间>当前时间
        Date expireTime = teamAddRequest.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间 > 当前时间");
        }
        // 3.7 校验用户最多创建5个队伍
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId,teamAddRequest.getUserId());
        Long count = teamMapper.selectCount(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }
        // 4 插入队伍信息到队伍表
        teamAddRequest.setUserId(loginUser.getId());
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        boolean save = this.save(team);
        if (!save || team.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        // 5 插入信息到队伍用户关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }


        return team.getId();
    }
}




