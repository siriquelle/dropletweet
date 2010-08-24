/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import com.dropletweet.log.DLog;
import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.modelmap.SaveModelMap;
import com.dropletweet.command.mvc.ajax.DoAjaxStatusList;
import com.dropletweet.command.mvc.ajax.DoAjaxTweetAction;
import com.dropletweet.command.mvc.ajax.DoAjaxUser;
import com.dropletweet.command.mvc.ajax.DoAjaxUtil;
import com.dropletweet.command.mvc.page.DoDropletView;
import com.dropletweet.command.mvc.page.DoIndexView;
import com.dropletweet.command.mvc.page.DoSigninView;
import com.dropletweet.constants.AppValues;
import com.dropletweet.model.bean.DropletLoadingBean;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Siriquelle
 */
public class DropletController extends AbstractController {

    public static DropletLoadingBean dropletLoadingBean;

    // <editor-fold defaultstate="collapsed" desc="HANDLE REQUEST INTERNAL - DOCS">
    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */// </editor-fold>
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    // <editor-fold defaultstate="collapsed" desc="HANDLE REQUEST INTERNAL - Performs delegation of requests process">
    {
        request.setCharacterEncoding("UTF-8");
        dropletLoadingBean = DropletLoadingBean.getInstance(request.getSession());
//**                                                                        **//
        DLog.log("START GETTING USERS SESSION");
        Map modelMap = GetModelMap.run(request);
        DLog.log("END GETTING USERS SESSION");
//**                                                                        **//
        DLog.log("START SETTING UP DEFAULT DESTINATION");
        String view = AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX;
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, view);
        DLog.log("END SETTING UP DEFAULT DESTINATION");
//**                                                                        **//
        DLog.log("START PROCESSING REQUEST");
        String uri = request.getRequestURI();
        if (uri.contains(AppValues.VIEW_INDEX_NAME))
        {
            modelMap.putAll(DoIndexView.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.VIEW_DROPLET_NAME))
        {
            modelMap.putAll(DoDropletView.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.VIEW_SIGNIN_NAME))
        {
            modelMap.putAll(DoSigninView.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.AJAX_STATUSLIST_NAME))
        {
            modelMap.putAll(DoAjaxStatusList.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.AJAX_TWEET_NAME))
        {
            modelMap.putAll(DoAjaxTweetAction.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.AJAX_USER_NAME))
        {
            modelMap.putAll(DoAjaxUser.run(request, response, dropletProperties, dropletService));
        } else if (uri.contains(AppValues.AJAX_UTIL_NAME))
        {
            modelMap.putAll(DoAjaxUtil.run(request, response, dropletProperties, dropletService, this.getServletContext()));
        }
        DLog.log("END PROCESSING REQUEST");
//**                                                                        **//
        DLog.log("START SAVING MODEL MAP");
        modelMap.putAll(SaveModelMap.run(request, modelMap));
        DLog.log("END SAVING MODEL MAP");
//**                                                                        **//
        return new ModelAndView((String) modelMap.get(AppValues.MODELMAP_KEY_VIEW), AppValues.MODELMAP_KEY_MODELMAP, modelMap);
    }// </editor-fold>
//**                                                                        **//
    /*******************************************************************************/
    /*START DEPENDANCY INJECTION */
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DEPENDANCY INJECTION OBJECTS">
    protected DropletService dropletService;
    protected DropletProperties dropletProperties;

    /**
     * Set the value of rssService
     *
     * @param dropletService
     */
    public void setDropletService(DropletService dropletService)
    {
        this.dropletService = dropletService;
    }

    public void setDropletProperties(DropletProperties dropletProperties)
    {
        this.dropletProperties = dropletProperties;
    }
    // </editor-fold>
    /*******************************************************************************/
    /*END DEPENDANCY INJECTION */
    /*******************************************************************************/
}
