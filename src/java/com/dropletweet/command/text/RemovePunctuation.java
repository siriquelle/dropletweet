/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.text;

import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Siriquelle
 */
public class RemovePunctuation {

    public static String run(String word)
    {
        StringEscapeUtils.escapeHtml(word);
        word = word.replaceAll("[^a-zA-Z0-9#@&/;_-]", " ");
        word = word.replaceAll("([&]+[#]*([a-zA-Z0-9]*)[;]+)", " ");
        word = EncodeHTML.run(word);
        return word;
    }
}
