package utils;

/**
 * This class defines a new exception, used to check
 * if a user is logged in.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see Exception
 */
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
