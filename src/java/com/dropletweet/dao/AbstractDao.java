/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.dao;

import java.util.List;

/**
 *
 * @param <T>
 * @author Siriquelle
 */
public interface AbstractDao<T> {

    public void save(T o);

    public void delete(T o);
    
    public List<T> getAll();

    

}
