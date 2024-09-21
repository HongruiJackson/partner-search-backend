package com.jackson.partnersearchbackend.managerTest;

import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class CompletedCatalogManagerTest {
    @Resource
    private CompletedCatalogManager completedCatalogManager;

    @Test
    public void getCompletedCatalogListTest() {
        List<CompletedCatalogVo> completedCatalogList = completedCatalogManager.getCompletedCatalogList();
        System.out.println(completedCatalogList);
    }
}
