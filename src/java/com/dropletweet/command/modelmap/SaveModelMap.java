/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.modelmap;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Siriquelle
 */
public class SaveModelMap {
/**
     *
     * @param request
     * @param modelMap
     * @return
     */
    public static Map run(HttpServletRequest request, Map modelMap)
    {
        HttpSession session = request.getSession();
        session.setAttribute("modelMap", modelMap);
        return modelMap;
    }
}
