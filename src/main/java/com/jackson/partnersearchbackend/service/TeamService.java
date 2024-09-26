package com.jackson.partnersearchbackend.service;

import com.jackson.partnersearchbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.request.TeamAddRequest;

import javax.servlet.http.HttpServletRequest;

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
}
