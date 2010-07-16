/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc.api;

import com.dropletweet.service.ConversationService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 *
 * @author Siriquelle
 */
public class JsonConversationController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String seedURL = request.getParameter("q");
        if (seedURL != null)
        {
            String json = conversationService.getJsonConversation(seedURL);
            modelMap.put("json", json);
        }
        return new ModelAndView("ajax/json", "modelMap", modelMap);
    }
    private Map modelMap;
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

    /**
     * Set the value of modelMap
     *
     * @param modelMap new value of modelMap
     */
    public void setModelMap(Map modelMap)
    {
        this.modelMap = modelMap;
    }
}
