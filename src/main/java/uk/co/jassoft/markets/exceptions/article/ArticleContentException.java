package uk.co.jassoft.markets.exceptions.article;

/**
 * Created by jonshaw on 15/02/2016.
 */
public class ArticleContentException extends Exception {

    public ArticleContentException(String message) {
        super(message);
    }

    public ArticleContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
