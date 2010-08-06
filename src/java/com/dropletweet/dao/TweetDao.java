/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.dao;

import com.dropletweet.domain.Tweet;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public interface TweetDao extends AbstractDao<Tweet>{

    public Tweet getByID(Long id);

    public List<Tweet> getAllByFromUserID(Integer id);

    public List<Tweet> getAllByToTweetID(Long id);
}
