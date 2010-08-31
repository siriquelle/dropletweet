/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model.bean;

import com.dropletweet.constants.AppValues;
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
        if (message.equals("Charging..."))
        {
            percent = 1.0;
            return message;
        } else
        {
            Double p = (100 - (100 / percent));
            return message + " | " + (p.intValue()) + "%";
        }
    }

    public void setMessage(String message)
    {
        this.percent += .025;
        this.message = message;
    }

    public static DropletLoadingBean getInstance(HttpSession session)
    {
        DropletLoadingBean dropletLoading = (DropletLoadingBean) session.getAttribute(AppValues.SESSION_KEY_DROPLET_LOADING);

        if (dropletLoading == null)
        {
            session.setAttribute(AppValues.SESSION_KEY_DROPLET_LOADING, new DropletLoadingBean());
            dropletLoading = (DropletLoadingBean) session.getAttribute(AppValues.SESSION_KEY_DROPLET_LOADING);
        }

        return dropletLoading;
    }
    private String message;
    private Double percent = 1.0;
}
