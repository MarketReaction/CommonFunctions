package uk.co.jassoft.jms.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonny
 */
public class JmsErrorHandler implements org.springframework.util.ErrorHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(JmsErrorHandler.class);

    @Override
    public void handleError(Throwable thrwbl) {
        LOG.error(thrwbl.getMessage(), thrwbl);
    }
    
}
