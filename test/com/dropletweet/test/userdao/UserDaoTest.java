/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.userdao;

import com.dropletweet.dao.UserDao;
import com.dropletweet.test.StartUp;
import com.dropletweet.domain.User;
import org.junit.Test;

/**
 *
 * @author Siriquelle
 */
public class UserDaoTest {

    public UserDaoTest()
    {
        StartUp.startUp();
        userDao = (UserDao) StartUp.ctx.getBean("userDao");
    }
    //
    private static final String random = "RANDOM1";
    private static final Integer user_id = 5;
    private UserDao userDao;
    private User user;

    /**
     *
     */
    @Test
    public void TestTweetDaoSave()
    {
        System.out.println("------------------");
        System.out.println("Begin Insert Tweet");
        System.out.println("------------------");
        user = new User();

        user.setId(user_id);
        user.setName(random);
        user.setLocation(random);

        userDao.save(user);

        System.out.println("------------------");
        System.out.println("End Insert Tweet");
        System.out.println("------------------");
    }

    @Test
    public void TestTweetDaoGetById()
    {
        System.out.println("------------------");
        System.out.println("Begin Get Tweet");
        System.out.println("------------------");
        user = userDao.getByID(user_id);
        System.out.println("Name: " + user.getName());
        System.out.println("------------------");
        System.out.println("End Get Tweet");
        System.out.println("------------------");
    }

    @Test
    public void TestTweetDaoDelete()
    {
        System.out.println("------------------");
        System.out.println("Begin Delete Tweet");
        System.out.println("------------------");
        System.out.println("Size Before Delete: " + userDao.getAll().size());
        user = userDao.getByID(user_id);
        userDao.delete(user);
        System.out.println("Size After Delete: " + userDao.getAll().size());
        System.out.println("------------------");
        System.out.println("End Get Tweet");
        System.out.println("------------------");
    }
}
