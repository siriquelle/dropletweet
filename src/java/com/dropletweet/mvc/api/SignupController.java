/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc.api;

import com.dropletweet.domain.Signup;
import com.dropletweet.service.DropletService;
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
public class SignupController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Map modelMap = getModelMap(request);
        boolean success = false;
        String email = request.getParameter("email");
        String out = "";
        if (email.matches(".+@.+\\.[a-z]+"))
        {
            success = true;
            Signup signup = new Signup();
            signup.setEmail(email);
            dropletService.persistSignup(signup);
        }
        out = "{\"success\":\"" + success + "\"}";
        modelMap.put("signup", out);
        modelMap.putAll(saveModelMap(request, modelMap));
        return new ModelAndView("ajax/signup", "modelMap", modelMap);
    }
    private DropletService dropletService;

    /**
     * Set the value of rssService
     *
     * @param dropletService
     */
    public void setDropletService(DropletService dropletService)
    {
        this.dropletService = dropletService;
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
