/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc.api;

import com.dropletweet.service.ConversationService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 *
 * @author Siriquelle
 */
public class JitConversationController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Map modelMap = getModelMap(request);
        String seedURL = request.getParameter("q");
        if (seedURL != null)
        {
            if (request.getSession().getAttribute("jit") == null || !request.getParameter("q").equals(request.getSession().getAttribute("url")))
            {

                String jit = conversationService.getJITConversation(seedURL);
                modelMap.put("jit", jit);
                request.getSession().setAttribute("jit", jit);
                request.getSession().setAttribute("url", seedURL);
                this.getServletContext().setAttribute("latest_url", seedURL);
                modelMap.put("jit", request.getSession().getAttribute("jit"));
                modelMap.put("url", seedURL);
            }
        }
        modelMap.putAll(saveModelMap(request, modelMap));
        return new ModelAndView("ajax/jit", "modelMap", modelMap);
    }
    private ConversationService conversationService;

    /**
     * Set the value of rssService
     *
     * @param conversationService
     */
    public void setConversationService(ConversationService conversationService)
    {
        this.conversationService = conversationService;
    }

    private Map getModelMap(HttpServletRequest request)
    {
        HttpSession session = request.getSession();

        if (session.getAttribute("modelMap") == null)
        {
            session.setAttribute("modelMap", new HashMap(0));
        }
        return (Map) session.getAttribute("modelMap");
    }

    private Map saveModelMap(HttpServletRequest request, Map modelMap)
    {
        HttpSession session = request.getSession();
        session.setAttribute("modelMap", modelMap);
        return modelMap;
    }
   
}
