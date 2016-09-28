/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.markets.exceptions.user;

/**
 *
 * @author Jonny
 */
public abstract class UserException extends Exception
{
    public UserException(String message)
    {
        super(message);
    }    
}
