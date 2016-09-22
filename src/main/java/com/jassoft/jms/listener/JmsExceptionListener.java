/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jassoft.jms.listener;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jonny
 */
@Component
public class JmsExceptionListener implements ExceptionListener
{
    private static final Logger LOG = LoggerFactory.getLogger(JmsErrorHandler.class);

    @Override
    public void onException( final JMSException exception ) {
        LOG.error(exception.getMessage(), exception);
    }
}