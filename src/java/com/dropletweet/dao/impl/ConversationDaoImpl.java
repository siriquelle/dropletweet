/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao.impl;

import com.dropletweet.dao.ConversationDao;
import com.dropletweet.domain.Conversation;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Siriquelle
 */
public class ConversationDaoImpl extends HibernateDaoSupport implements ConversationDao {

    @Override
    public void save(Conversation conversation)
    {
        getHibernateTemplate().saveOrUpdate(conversation);
    }

    @Override
    public void delete(Conversation conversation)
    {
        getHibernateTemplate().delete(conversation);
    }

    @Override
    public Conversation getByID(Integer id)
    {
        return (Conversation) getHibernateTemplate().get(Conversation.class, id);
    }

    @Override
    public List<Conversation> getAllByUserId(Integer userId)
    {
        List<Conversation> conversationList = (List<Conversation>) getHibernateTemplate().find("from Conversation where user_id = ?", userId);

        return (conversationList.isEmpty()) ? null : conversationList;
    }

    @Override
    public List<Conversation> getAll()
    {
        return (List<Conversation>) getHibernateTemplate().find("from Conversation");
    }
}
