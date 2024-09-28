package com.jackson.partnersearchbackend.service;

import com.jackson.partnersearchbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.query.TeamQuery;
import com.jackson.partnersearchbackend.model.request.TeamAddRequest;
import com.jackson.partnersearchbackend.model.request.TeamUpdateRequest;
import com.jackson.partnersearchbackend.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 10240
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-09-24 11:59:37
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param teamAddRequest 创建的队伍信息
     * @param loginUser 前端应该确保传入到这里的时候这个值不为空
     * @return 添加成功的teamId
     */
    long addTeam(TeamAddRequest teamAddRequest, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery 队伍查询条件
     * @param isAdmin 是否是管理员
     * @return 查到的队伍以及包含的加入的成员
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新用户
     * 只允许管理员或者队伍创建者修改
     * @param teamUpdateRequest 修改的信息
     * @param loginUser 进行此操作的用户
     * @return 成功为true
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
}
