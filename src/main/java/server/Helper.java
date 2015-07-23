package server;

import org.json.JSONObject;

/**
 * Created by loster on 23.07.2015.
 */
public class Helper {

    public static JSONObject getJsonFrame(int status, String message, JSONObject body, String event) {
        JSONObject frame = new JSONObject().put("status", status).put("message", message).put("event", event);
        frame.accumulate("body", body);
        return frame;
    }
}
