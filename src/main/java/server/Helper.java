package server;

import org.json.JSONObject;

/**
 * Created by loster on 23.07.2015.
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
}
