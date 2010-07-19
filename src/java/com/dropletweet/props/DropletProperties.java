/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.props;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Siriquelle
 */
public class DropletProperties extends Properties {

    public DropletProperties()
    {
        try
        {
            load(DropletProperties.class.getResourceAsStream("droplet.properties"));
        } catch (IOException ex)
        {
            Logger.getLogger(DropletProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
