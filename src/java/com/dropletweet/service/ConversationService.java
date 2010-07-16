/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.service;

import com.dropletweet.model.Droplet;

/**
 *
 * @author Siriquelle
 */
public interface ConversationService {

    //JSON SERVICE
    public String getJsonConversation(String url);

    //DROPLET SERVICE
    public Droplet getDropletConversation(String url);

    //JIT SERVICE
    public String getJITConversation(String url);
}
