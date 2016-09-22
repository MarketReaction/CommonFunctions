/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jassoft.markets.exceptions.user;

/**
 *
 * @author Jonny
 */
public class UnknownActivationCodeException extends UserException
{
    public UnknownActivationCodeException(String message)
    {
        super(message);
    }    
}
