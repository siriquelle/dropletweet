/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.ajax;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.twitter.conversation.GetDropletTweetListFromTwitter4jDirectMessages;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.dropletweet.model.bean.AjaxUserBean;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import twitter4j.DirectMessage;
import twitter4j.Paging;
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
        AjaxUserBean ajaxUserBean = new AjaxUserBean();
        twitter4j.User user = null;
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER);

        if (action.equals(AppValues.USER_ACTION_VALUE_GET_USER_INFO))
        {
            try
            {
                String userName = request.getParameter(AppValues.REQUEST_KEY_SCREEN_NAME);
                if (userName != null && userName.length() > 0)
                {
                    if (userName.equals(((User) modelMap.get(AppValues.MODELMAP_KEY_USER)).getScreen_name()))
                    {
                        user = (twitter4j.User) twitter.verifyCredentials();
                    } else
                    {
                        user = (twitter4j.User) twitter.showUser(userName);
                    }
                    ajaxUserBean.setUser(user);
                }
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
            } catch (TwitterException ex)
            {
                DLog.log(ex.getMessage());
            }
        } else if (action.equals(AppValues.USER_ACTION_VALUE_SET_USER_DESCRIPTION))
        {
            String description = request.getParameter(AppValues.REQUEST_KEY_DESCRIPTION);
            if (description != null && !description.isEmpty())
            {
                try
                {
                    twitter.updateProfile(null, null, null, null, description);
                    modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
                } catch (TwitterException ex)
                {
                    Logger.getLogger(DoAjaxUser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (action.equals(AppValues.USER_ACTION_VALUE_GET_DM_COUNT))
        {
            try
            {
                List<Tweet> t = (List<Tweet>) modelMap.get(AppValues.LIST_NAME_DM_LIST);
                Long since_id = (t).get(0).getId();
                List<Tweet> d = GetDropletTweetListFromTwitter4jDirectMessages.run(getLatestDirectMessages(twitter.getDirectMessages(), since_id));

                ajaxUserBean.setDmCount(d.size());
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
            } catch (TwitterException ex)
            {
                Logger.getLogger(DoAjaxUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (action.equals(AppValues.USER_ACTION_VALUE_GET_MENTIONS_COUNT))
        {
            try
            {
                Long since_id = ((List<Tweet>) modelMap.get(AppValues.LIST_NAME_REPLY_LIST)).get(0).getId();
                ajaxUserBean.setMentionsCount(twitter.getMentions(new Paging(since_id)).size());
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
            } catch (TwitterException ex)
            {
                Logger.getLogger(DoAjaxUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        modelMap.put(AppValues.MODELMAP_KEY_AJAX_USER_BEAN, ajaxUserBean);
        return modelMap;
    }// </editor-fold>

    /*******************************************************************************/
    /*END AJAX USER*/
    /*******************************************************************************/
    /**                                                                           **/
    private static List<DirectMessage> getLatestDirectMessages(List<DirectMessage> dmList, Long since_id)
    {
        ListIterator i = dmList.listIterator();
        while (i.hasNext())
        {
            DirectMessage dm = (DirectMessage) i.next();
            if (dm.getId() <= since_id)
            {
                i.remove();
            }
        }
        return dmList;
    }
}
