/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.ajax;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.dropletweet.model.bean.AjaxUserBean;
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
public class DoAjaxUser {

    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX USER*/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO AJAX USER - DOCS">
    /**
     *
     * @param request
     * @param response
     * @return
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService)
    // <editor-fold defaultstate="collapsed" desc="DO AJAX USER - Handles ajaxian requests that relate to a user">
    {
        String action = request.getParameter(AppValues.REQUEST_KEY_ACTION);
        Map modelMap = GetModelMap.run(request);
        AjaxUserBean ajaxUserBean = null;
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER);

        if (action.equals(AppValues.USER_ACTION_VALUE_GET_USER_INFO))
        {
            String userName = request.getParameter(AppValues.REQUEST_KEY_SCREEN_NAME);
            if (userName != null && userName.length() > 0)
            {
                try
                {
                    twitter4j.User user = null;

                    if (userName.equals(((User) modelMap.get(AppValues.MODELMAP_KEY_USER)).getScreen_name()))
                    {
                        user = (twitter4j.User) twitter.verifyCredentials();
                    } else
                    {
                        user = (twitter4j.User) twitter.showUser(userName);
                    }

                    ajaxUserBean = new AjaxUserBean((twitter4j.User) user);

                } catch (TwitterException ex)
                {
                    DLog.log(ex.getMessage());
                }
            }
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
        }
        modelMap.put(AppValues.MODELMAP_KEY_AJAX_USER_BEAN, ajaxUserBean);
        return modelMap;
    }// </editor-fold>
    /*******************************************************************************/
    /*END AJAX USER*/
    /*******************************************************************************/
    /**                                                                           **/
}
