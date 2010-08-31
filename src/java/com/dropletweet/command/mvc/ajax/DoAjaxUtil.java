/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.ajax;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.User;
import com.dropletweet.log.DConsole;
import com.dropletweet.log.DLog;
import com.dropletweet.model.bean.AjaxUtilBean;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Siriquelle
 */
public class DoAjaxUtil {
    // <editor-fold defaultstate="collapsed" desc="DO AJAX UTIL - DOCS">

    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */// </editor-fold>
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX UTIL*/
    /*******************************************************************************/
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService, ServletContext servletContext)
    // <editor-fold defaultstate="collapsed" desc="DO AJAX UTIL - Handles ajaxian requests that seek utilities">
    {
        String action = request.getParameter(AppValues.REQUEST_KEY_ACTION);
        Map modelMap = GetModelMap.run(request);
        AjaxUtilBean ajaxUtilBean = new AjaxUtilBean();

        if (action.equals(AppValues.UTIL_ACTION_VALUE_GET_LATEST_URL))
        {
            String url = dropletProperties.getProperty("droplet.ajax.util.default.url");
            if ((User) modelMap.get(AppValues.MODELMAP_KEY_USER) != null)
            {
                User user = (User) modelMap.get(AppValues.MODELMAP_KEY_USER);
                Conversation latestConversation = (dropletService.getAllConversationsByUserId(user.getId())!= null && dropletService.getAllConversationsByUserId(user.getId()).size() > 0)
                        ? dropletService.getAllConversationsByUserId(user.getId()).get(dropletService.getAllConversationsByUserId(user.getId()).size() - 1)
                        : null;

                if (latestConversation != null)
                {
                    url = "http://twitter.com/" + latestConversation.getTweet().getFrom_user() + "/status/" + latestConversation.getTweet().getId();
                }
            } else
            {
                if ((String) servletContext.getAttribute(AppValues.SERVLET_CONTEXT_KEY_LATEST_URL) != null)
                {
                    url = (String) servletContext.getAttribute(AppValues.SERVLET_CONTEXT_KEY_LATEST_URL);
                }
            }
            ajaxUtilBean.setMessage(url);
        } else if (action.equals(AppValues.UTIL_ACTION_VALUE_GET_LOADING_STATUS))
        {
            ajaxUtilBean.setMessage(DConsole.read());
            DLog.log(DConsole.read());
        }

        modelMap.put(AppValues.MODELMAP_KEY_AJAX_UTIL_BEAN, ajaxUtilBean);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_UTIL);
        return modelMap;
    }// </editor-fold>
    /*******************************************************************************/
    /*END AJAX UTIL*/
    /*******************************************************************************/
    /**                                                                           **/
}
