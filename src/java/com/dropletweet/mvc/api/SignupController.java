/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc.api;

import com.dropletweet.domain.Signup;
import com.dropletweet.service.DropletService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        return new ModelAndView("ajax/signup", "modelMap", modelMap);
    }
    private Map modelMap;
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
