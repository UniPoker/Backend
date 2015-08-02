package server;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public static JSONObject getWinnerJSON(User user, Card[] cards, int value) {
        JSONObject obj = new JSONObject();
        obj.put("value", value);
        obj.put("user", user);
        obj.put("cards", cards);
        return obj;
    }
}
