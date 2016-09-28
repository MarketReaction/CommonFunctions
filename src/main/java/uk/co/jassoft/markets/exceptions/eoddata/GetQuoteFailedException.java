package uk.co.jassoft.markets.exceptions.eoddata;

/**
 * Created by Jonny on 01/09/2014.
 */
public class GetQuoteFailedException extends Exception
{
    public GetQuoteFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
