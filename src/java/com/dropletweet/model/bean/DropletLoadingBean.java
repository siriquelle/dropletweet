/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model.bean;

import javax.servlet.http.HttpSession;

/**
 *
 * @author Siriquelle
 */
public class DropletLoadingBean {

    private DropletLoadingBean()
    {
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public static DropletLoadingBean getInstance(HttpSession session)
    {
        DropletLoadingBean dropletLoading = (DropletLoadingBean) session.getAttribute("dropletLoading");

        if (dropletLoading == null)
        {
            session.setAttribute("dropletLoading", new DropletLoadingBean());
            dropletLoading = (DropletLoadingBean) session.getAttribute("dropletLoading");
        }

        return dropletLoading;
    }
    private String message;
}
