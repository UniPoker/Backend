package utils;

import cards.Card;
import org.json.JSONObject;
import users.User;

/**
 * A class only with static method.
 * This methods should return a json frame which is needed all over the project
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 */
public class Helper {

    /**
     * creates and returns a JSONObject of the given parameters
     * e.g. {"status": 0, "message": "ok", "event": "login_response", "body": {}}
     *
     * @param status  the status of the JSON (Integer)
     * @param message the message of the JSON (String)
     * @param body    the body of the JSON (JSONObject)
     * @param event   the event of the JSON (String)
     * @return returns the created JSONObject
     */
    public static JSONObject getJsonFrame(int status, String message, JSONObject body, String event) {
        JSONObject frame = new JSONObject().put("status", status).put("message", message).put("event", event);
        frame.accumulate("body", body);
        return frame;
    }

    /**
     * Creates and returns a JSONObject with the given parameters
     *  e.g. {"value" : 1, "user": USEROBJECT, "cards": CARDSOBJECT}
     *
     * @param user the user put into the jsonobject
     * @param cards the card put into the jsonobject
     * @param value the value put into the jsonobject
     * @return a jsonobject representing the winner of a game
     */
    public static JSONObject getWinnerJSON(User user, Card[] cards, int value) {
        JSONObject obj = new JSONObject();
        obj.put("value", value);
        obj.put("user", user);
        obj.put("cards", cards);
        return obj;
    }
}
