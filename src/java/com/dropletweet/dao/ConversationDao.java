/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao;

import com.dropletweet.domain.Conversation;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public interface ConversationDao extends AbstractDao<Conversation> {

    public Conversation getByID(Integer id);

    public List<Conversation> getAllByUserId(Integer userId);
}
