/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc.api;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.modelmap.SaveModelMap;
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
public class JitConversationController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Map modelMap = GetModelMap.run(request);

        String seedURL = request.getParameter("q");

        if (seedURL != null)
        {
            String jit = conversationService.getJITConversation(seedURL);
            modelMap.put("jit", jit);
            modelMap.put("url", seedURL);
            this.getServletContext().setAttribute("latest_url", seedURL);
        }
        modelMap.putAll(SaveModelMap.run(request, modelMap));
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



}
