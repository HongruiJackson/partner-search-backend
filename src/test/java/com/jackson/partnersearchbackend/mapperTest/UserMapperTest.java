package com.jackson.partnersearchbackend.mapperTest;

import com.google.gson.Gson;
import com.jackson.partnersearchbackend.mapper.UserMapper;
import com.jackson.partnersearchbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void selectAllByAnyTagTest() {
        List<String> tags = new ArrayList<>();
//        tags.add("Java");
        tags.add("Python");

        Gson gson = new Gson();

        List<String> jsonTagList = tags.stream().map(gson::toJson).toList();
        System.out.println(jsonTagList);
        List<User> users = userMapper.selectAllByAnyTag(jsonTagList);
        System.out.println(users);

    }

    @Test
    public void selectAllByAllTagsTest() {
        List<String> tags = new ArrayList<>();
        tags.add("Java");
        tags.add("Python");
//        tags.add("js");
        Gson gson = new Gson();
        String json = gson.toJson(tags);
        List<User> users = userMapper.selectAllByAllTags(json);
        System.out.println(users);

    }
}
