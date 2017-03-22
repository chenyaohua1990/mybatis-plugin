package com.github.miaoxinguo.mybatis.plugin;

import com.github.miaoxinguo.mybatis.plugin.entity.User;
import com.github.miaoxinguo.mybatis.plugin.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 */
public class TestUserMapper {
    private UserMapper userMapper;

    @Before
    public void init() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @Test
    public void testSelect() {
        User user = userMapper.selectById(1);
        System.out.println(user);
    }

    @Test
    public void testSelectByPageableQo() {
        PageableQo qo = new PageableQo();
        qo.setPageNum(1);
        qo.setPageSize(20);
        List<User> users = userMapper.selectByPageableQo(qo);

        System.out.println("total count：" + TotalCountHolder.getTotalCount());
        for (User user : users) {
            System.out.println(user);
        }
    }
}
