/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.page;

import com.dropletweet.command.cookie.DestroyCookie;
import com.dropletweet.command.cookie.GetCookieValue;
import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.modelmap.UpdateModelMap;
import com.dropletweet.command.twitter.GetAuthorizationURL;
import com.dropletweet.command.twitter.GetAuthorizedTwitter;
import com.dropletweet.command.twitter.GetAuthorizedTwitterFromKeySecret;
import com.dropletweet.constants.AppValues;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class DoSigninView {

    /**                                                                           **/
    // <editor-fold defaultstate="collapsed" desc="DO SIGN IN VIEW - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService) throws Exception
    // <editor-fold defaultstate="collapsed" desc="DO SIGN IN VIEW - Controles the signiture process">
    {
        Map modelMap = GetModelMap.run(request);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        String accessToken = AppValues.COOKIE_KEY_ACCESS_TOKEN;
        if (request.getParameter(AppValues.REQUEST_KEY_LOGOUT) == null)
        {

            accessToken = GetCookieValue.run(cookies, AppValues.COOKIE_KEY_ACCESS_TOKEN);
            accessToken = (accessToken == null) ? AppValues.COOKIE_KEY_ACCESS_TOKEN : accessToken;
            if (!accessToken.equals(AppValues.COOKIE_KEY_ACCESS_TOKEN))
            {
                String tokenKey = accessToken.substring(0, accessToken.indexOf(AppValues.VALUE_SEPARATOR_UNDERSCORE));
                String tokenSecret = accessToken.substring(accessToken.indexOf(AppValues.VALUE_SEPARATOR_UNDERSCORE) + 1, accessToken.length());
                modelMap.putAll(UpdateModelMap.run(GetAuthorizedTwitterFromKeySecret.run(request, tokenKey, tokenSecret, dropletProperties), request, dropletService));
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_DROPLET);
            } else if ((RequestToken) session.getAttribute(AppValues.SESSION_KEY_REQUEST_TOKEN) == null)
            {
                String authorizationURL = GetAuthorizationURL.run(session, dropletProperties).toExternalForm();
                if (authorizationURL != null)
                {
                    modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_PREFIX + authorizationURL);
                }
            } else
            {
                modelMap.putAll(UpdateModelMap.run(GetAuthorizedTwitter.run(request, response), request, dropletService));
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_DROPLET);
            }

        } else
        {
            session.invalidate();
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

            response.addCookie(DestroyCookie.run(cookies, AppValues.COOKIE_KEY_ACCESS_TOKEN));

            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        }
        return modelMap;
    }// </editor-fold>
}
