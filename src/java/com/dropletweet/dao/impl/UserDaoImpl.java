/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao.impl;

import com.dropletweet.dao.UserDao;
import com.dropletweet.domain.User;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Siriquelle
 */
public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

    @Override
    public void save(User user)
    {
        getHibernateTemplate().saveOrUpdate(user);
    }

    @Override
    public void delete(User user)
    {
        getHibernateTemplate().delete(user);
    }

    @Override
    public User getByID(Integer id)
    {
        return (User) getHibernateTemplate().get(User.class, id);
    }

    @Override
    public User getByScreen_Name(String screen_name)
    {
        List<User> userList = (List<User>) getHibernateTemplate().find("from User where screen_name = ?", screen_name);

        return (userList.isEmpty()) ? null : userList.get(0);

    }

    @Override
    public List<User> getAll()
    {
        return (List<User>) getHibernateTemplate().find("from User");
    }
}
