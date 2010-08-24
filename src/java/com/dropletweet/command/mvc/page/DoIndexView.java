/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.page;

import com.dropletweet.command.cookie.GetCookieValue;
import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.constants.AppValues;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Siriquelle
 */
public class DoIndexView {
    // <editor-fold defaultstate="collapsed" desc="DO INDEX VIEW - DOCS">

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService) throws Exception
    // <editor-fold defaultstate="collapsed" desc="DO INDEX VIEW - Controls the default entry point process">
    {
//**                                                                        **//
        Map modelMap = GetModelMap.run(request);
        HttpSession session = request.getSession();
        if (session.getAttribute(AppValues.SESSION_KEY_USER) != null)
        {
            modelMap.putAll(DoDropletView.run(request, response, dropletProperties, dropletService));
        } else
        {
            if (GetCookieValue.run(request.getCookies(), AppValues.COOKIE_KEY_ACCESS_TOKEN) != null)
            {
                modelMap.putAll(DoSigninView.run(request, response, dropletProperties, dropletService));
            } else
            {
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.VIEW_VALUE_INDEX);
            }
        }
//**                                                                        **//
        return modelMap;
    }// </editor-fold>
}
