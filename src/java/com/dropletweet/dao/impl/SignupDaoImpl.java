/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao.impl;

import com.dropletweet.dao.SignupDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.domain.Signup;
import com.dropletweet.domain.User;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Siriquelle
 */
public class SignupDaoImpl extends HibernateDaoSupport implements SignupDao {

    @Override
    public void save(Signup signup)
    {
        getHibernateTemplate().saveOrUpdate(signup);
    }

    @Override
    public void delete(Signup signup)
    {
        getHibernateTemplate().delete(signup);
    }

    @Override
    public Signup getByID(Integer id)
    {
        return (Signup) getHibernateTemplate().get(Signup.class, id);
    }

    @Override
    public List<Signup> getAll()
    {
        return (List<Signup>) getHibernateTemplate().find("from Signup");
    }
}
