/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao.impl;

import com.dropletweet.dao.TweetDao;
import com.dropletweet.domain.Tweet;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Siriquelle
 */
public class TweetDaoImpl extends HibernateDaoSupport implements TweetDao {

    @Override
    public void save(Tweet tweet)
    {
        getHibernateTemplate().saveOrUpdate(tweet);
    }

    @Override
    public void delete(Tweet tweet)
    {
        getHibernateTemplate().delete(tweet);
    }

    @Override
    public Tweet getByID(Long id)
    {
        return (Tweet) getHibernateTemplate().get(Tweet.class, id);
    }

    @Override
    public List<Tweet> getAll()
    {
        return (List<Tweet>) getHibernateTemplate().find("from Tweet");
    }

    @Override
    public List<Tweet> getAllByFromUserID(Integer id)
    {
        return (List<Tweet>) getHibernateTemplate().find("from Tweet where from_user_id = ?", id);
    }

    @Override
    public List<Tweet> getAllByToUserID(Integer id)
    {
        return (List<Tweet>) getHibernateTemplate().find("from Tweet where to_user_id = ?", id);
    }

    @Override
    public List<Tweet> getAllByToTweetID(Long id)
    {
        return (List<Tweet>) getHibernateTemplate().find("from Tweet where in_reply_to_id = ?", id);
    }
}
