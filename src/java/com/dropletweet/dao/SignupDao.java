/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao;

import com.dropletweet.domain.Signup;

/**
 *
 * @author Siriquelle
 */
public interface SignupDao extends AbstractDao<Signup> {

    public Signup getByID(Integer id);
}
