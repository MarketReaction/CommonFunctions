/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jassoft.markets.exceptions.email;

/**
 *
 * @author Jonny
 */
public class EmailSendException extends Exception
{
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
