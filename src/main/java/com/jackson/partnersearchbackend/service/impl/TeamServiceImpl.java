package com.jackson.partnersearchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.partnersearchbackend.model.domain.Team;
import com.jackson.partnersearchbackend.service.TeamService;
import com.jackson.partnersearchbackend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 10240
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-09-24 11:59:37
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




