package server;

public class NotLoggedInException extends Exception {

    /**
     * creates a new NotLoggedInException.
     */
    public NotLoggedInException() {}

    /**
     * Constructor that accepts a message
     * @param message the message to display in the Exception
     */
    public NotLoggedInException(String message)
    {
        super(message);
    }

}
