/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.modelmap;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Siriquelle
 */
public class GetModelMap {
/**
     *
     * @param request
     * @return
     */
    public static Map run(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);

        if (session.getAttribute("modelMap") == null)
        {
            session.setAttribute("modelMap", new HashMap(0));
        }
        return (Map) session.getAttribute("modelMap");
    }
}
