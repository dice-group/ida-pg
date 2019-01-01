package com.poc.repositories;

import com.poc.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    private static final String USER_NAME= "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "test1";
    private static final String LAST_NAME = "test2";

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testPersistence() {
        //given
        User user = new User();
        user.setUsername(USER_NAME);
        user.setPassword(PASSWORD);
        user.setFirstname(FIRST_NAME);
        user.setLastname(LAST_NAME);

        //when
        userRepository.save(user);

        //then
        Assert.assertNotNull(user.getId());
        User newUser = userRepository.findById(user.getId()).orElse(null);
        Assert.assertEquals(USER_NAME, newUser.getUsername());
        Assert.assertEquals(PASSWORD, newUser.getPassword());
        Assert.assertEquals(FIRST_NAME, newUser.getFirstname());
        Assert.assertEquals(LAST_NAME, newUser.getLastname());
    }
}