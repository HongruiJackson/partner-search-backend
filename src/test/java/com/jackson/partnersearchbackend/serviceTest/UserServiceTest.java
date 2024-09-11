package com.jackson.partnersearchbackend.serviceTest;

import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;


    @Test
    public void searchUserByTagsTest() {
        List<String> tags = new ArrayList<>();
        tags.add("Java");
        tags.add("Python");
//        tags.add("js");
        List<User> users = userService.searchUserByTags(tags);
        System.out.println(users);
    }
}
