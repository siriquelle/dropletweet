/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.page;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.constants.AppValues;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class DoDropletView {

    /**                                                                           **/
    // <editor-fold defaultstate="collapsed" desc="DO DROPLET VIEW - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws TwitterException
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService) throws TwitterException
    // <editor-fold defaultstate="collapsed" desc="DO DROPLET VIEW - Performs post sign in processes">
    {
        Map modelMap = GetModelMap.run(request);

        HttpSession session = request.getSession();
        if ((Map) session.getAttribute(AppValues.SESSION_KEY_MODELMAP) == null || ((Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER) == null))
        {
            request.getSession().invalidate();
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        } else
        {
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.VIEW_VALUE_DROPLET);
        }

        return modelMap;
    }// </editor-fold>
}
