package com.jackson.partnersearchbackend.controller;

import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.SuccessCode;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import com.jackson.partnersearchbackend.service.CompletedCatalogService;
import com.jackson.partnersearchbackend.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class CompletedCatalogController {
    @Resource
    CompletedCatalogService completedCatalogService;

    /**
     * 获取完整的父子列表
     * @return 返回的为列表，没值的时候返回空列表
     */
    @GetMapping("/completedList")
    public BaseResponse<List<CompletedCatalogVo>> completedTagList() {
        return ResultUtils.success(completedCatalogService.getCompletedCatalogList(), SuccessCode.COMMON_SUCCESS);
    }

}
