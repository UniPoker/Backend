package server;

/**
 * Created by loster on 19.07.2015.
 */
public class NotLoggedInException extends Exception {

    public NotLoggedInException() {}

    //Constructor that accepts a message
    public NotLoggedInException(String message)
    {
        super(message);
    }

}
