/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.dao;

import com.dropletweet.domain.User;



/**
 *
 * @author Siriquelle
 */
public interface UserDao extends AbstractDao<User>{

    public User getByID(Integer id);
    public User getByScreen_Name(String screen_name);
}
