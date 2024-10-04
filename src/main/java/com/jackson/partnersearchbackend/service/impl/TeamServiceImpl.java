package com.jackson.partnersearchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.TeamStatusEnum;
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
import com.jackson.partnersearchbackend.model.vo.UserVO;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.mapper.TeamMapper;
import com.jackson.partnersearchbackend.service.UserService;
import com.jackson.partnersearchbackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.jackson.partnersearchbackend.constant.UserConstant.ADMIN_ROLE;

/**
* @author 10240
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-09-24 11:59:37
*/
@Service
public
class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

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
        // 3.7 校验用户最多创建/加入5个队伍
        LambdaQueryWrapper<UserTeam> userJoinCountWrapper = new LambdaQueryWrapper<>();
        userJoinCountWrapper.eq(UserTeam::getUserId,loginUser.getId());
        if (userTeamService.count(userJoinCountWrapper)>=5) throw new BusinessException(ErrorCode.FORBIDDEN,"最多创建和加入5个队伍");
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

    @Override
    public Page<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin, boolean isMy) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getTeamStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                List<Integer> enumList = new ArrayList<>();
                enumList.add(TeamStatusEnum.PUBLIC.getValue());
                enumList.add(TeamStatusEnum.PRIVATE.getValue());
                enumList.add(TeamStatusEnum.SECRET.getValue());
                queryWrapper.in("team_status", enumList);
            }
            else if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            } else {
                queryWrapper.eq("team_status", statusEnum.getValue());
            }

        }
        // 不展示已过期的队伍，如果是要查询自己创建/加入的队伍，过期了也应该查询出来
        // expireTime is null or expireTime > now()
        if (!isMy) {
            queryWrapper.and(qw -> qw.gt("expire_time", new Date()).or().isNull("expire_time"));
        }
        Page<Team> OriginPage = this.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()), queryWrapper);
        Page<TeamUserVO> teamUserVOPage = new Page<>();
        BeanUtils.copyProperties(OriginPage,teamUserVOPage,"records");

        List<Team> teamList = OriginPage.getRecords();
        if (CollectionUtils.isEmpty(teamList)) {
            teamUserVOPage.setRecords(new ArrayList<>());
            return teamUserVOPage;
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        teamUserVOPage.setRecords(teamUserVOList);
        return teamUserVOPage;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        Long teamId = teamUpdateRequest.getId();
        Team team = this.getById(teamId);
        if (team == null) throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        // 只有队伍创建者或管理员才能对队伍信息进行修改
        if (!Objects.equals(team.getUserId(), loginUser.getId()) && loginUser.getUserRole()!=ADMIN_ROLE)
            throw new BusinessException(ErrorCode.NO_AUTH);
        // 修改为加密状态必须要有密码
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        if (teamStatus != null) {
            TeamStatusEnum statusEnum = Objects.requireNonNullElse(TeamStatusEnum.getEnumByValue(teamStatus),TeamStatusEnum.PUBLIC);
            if (statusEnum.equals(TeamStatusEnum.SECRET)) {
                // 如果之前不是加密的且没有传密码的话报错
                if (TeamStatusEnum.getEnumByValue(team.getTeamStatus())!=TeamStatusEnum.SECRET && StringUtils.isBlank(teamUpdateRequest.getTeamPassword())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"未填写密码");
                }
            }
        }

        BeanUtils.copyProperties(teamUpdateRequest,team);
        return this.updateById(team);

    }

    @Override
    @Transactional
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 1. 获取队伍/用户id
        Long teamId = teamJoinRequest.getId();
        Long userId = loginUser.getId();
        // 2. 队伍是否存在
        Team team = this.getById(teamId);
        if (team == null) throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");

        // 3. 用户是否创建/加入5个队伍
        LambdaQueryWrapper<UserTeam> userJoinCountWrapper = new LambdaQueryWrapper<>();
        userJoinCountWrapper.eq(UserTeam::getUserId,userId);
        if (userTeamService.count(userJoinCountWrapper)>=5) throw new BusinessException(ErrorCode.FORBIDDEN,"最多创建和加入5个队伍");

        // 4. 不允许加入已过期队伍
        if (team.getExpireTime()!=null && new Date().after(team.getExpireTime()))
            throw new BusinessException(ErrorCode.FORBIDDEN,"队伍已过期");
        // 5. 不允许加入私有队伍
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(team.getTeamStatus());
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum))
            throw new BusinessException(ErrorCode.NO_AUTH,"不允许加入私有队伍");
        // 6. 若队伍时加密的，必须密码匹配
        String teamPassword = teamJoinRequest.getTeamPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(teamPassword) || !teamPassword.equals(team.getTeamPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        // 7. 不能重复加入一队伍
        LambdaQueryWrapper<UserTeam> reCountWrapper = new LambdaQueryWrapper<>();
        reCountWrapper.eq(UserTeam::getTeamId,teamId)
                .eq(UserTeam::getUserId,userId);
        if (userTeamService.count(reCountWrapper)!=0) {
            throw new BusinessException(ErrorCode.FORBIDDEN,"禁止重复加入");
        }

        // 8. 加入人数
        LambdaQueryWrapper<UserTeam> hasJoinCountWrapper = new LambdaQueryWrapper<>();
        hasJoinCountWrapper.eq(UserTeam::getTeamId,teamId);
        long hasJoinCount = userTeamService.count(hasJoinCountWrapper);
        if (team.getMaxNum() <= hasJoinCount) {
            throw new BusinessException(ErrorCode.FORBIDDEN,"队伍已满");
        }

        // 9. 加入
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        // 1. 校验参数
        if (Objects.isNull(teamQuitRequest)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        // 2. 对id的校验，简单地防一下缓存穿透
        Long teamId = teamQuitRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 3. 数据库获取team
        Team team = this.getById(teamId);
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 4. 判断是否在队伍当中
        Long userId = loginUser.getId();
        LambdaQueryWrapper<UserTeam> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserTeam::getUserId,userId)
                .eq(UserTeam::getTeamId,teamId);
        long count = userTeamService.count(countWrapper);
        if (count != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long hasJoinCount = this.getHasJoinCount(teamId);
        if (hasJoinCount == 1) { // 解散队伍
            // 删除队伍和所有加入队伍的关系
            this.removeById(teamId);
            LambdaQueryWrapper<UserTeam> removeDeleteTeamIdWrapper = new LambdaQueryWrapper<>();
            removeDeleteTeamIdWrapper.eq(UserTeam::getTeamId,teamId);
            return userTeamService.remove(removeDeleteTeamIdWrapper);
        } else {
            // 是否为创建人
            if (Objects.equals(team.getUserId(), userId)) {
                // 是创建人顺位到下一个最早加入的人
                // id是自增的，就找两个人就好了
                LambdaQueryWrapper<UserTeam> twoPeopleWrapper = new LambdaQueryWrapper<>();
                twoPeopleWrapper.eq(UserTeam::getTeamId,teamId)
                        .orderByAsc(UserTeam::getId)
                        .last("limit 2");
                List<UserTeam> twoPeople = userTeamService.list(twoPeopleWrapper);
                if (CollectionUtils.isEmpty(twoPeople) || twoPeople.size()!=2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                // 更新当前队伍创建人
                UserTeam userTeam = twoPeople.get(1);
                Long nextUserId = userTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextUserId);
                boolean updateTeamResult = this.updateById(updateTeam);
                if (!updateTeamResult) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队伍创建人失败");
                }
            }
            // 移除关系
            LambdaQueryWrapper<UserTeam> removeDeleteTeamIdWrapper = new LambdaQueryWrapper<>();
            removeDeleteTeamIdWrapper.eq(UserTeam::getTeamId,teamId)
                    .eq(UserTeam::getUserId,userId);
            return userTeamService.remove(removeDeleteTeamIdWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long id, User loginUser) {
        if (id == null || id<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 如果不是创建者且不是管理员
        if (!loginUser.getId().equals(team.getUserId()) && loginUser.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 移除所有信息
        LambdaQueryWrapper<UserTeam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserTeam::getTeamId,id);
        boolean remove = this.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"检查数据库日志");
        }
        return userTeamService.remove(lambdaQueryWrapper);

    }


    private long getHasJoinCount(long teamId) {
        LambdaQueryWrapper<UserTeam> userTeamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userTeamLambdaQueryWrapper.eq(UserTeam::getTeamId,teamId);
        return userTeamService.count(userTeamLambdaQueryWrapper);
    }
}




