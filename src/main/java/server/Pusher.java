package server;

import org.json.JSONObject;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loster on 27.05.2015.
 */
public class Pusher {

    List<Session> sessions;

    public Pusher(UserList users) {
        sessions = new ArrayList<>();
        for (User user : users.getUsers()) {
            sessions.add(user.getWebsession());
        }
    }

    public void pushToAll(String message, String event) {
        try {
            for (Session session : sessions) {
                session.getBasicRemote().sendText(getJsonFrame(0,"Nachricht erhalten",new JSONObject().put("message", message),event).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeUser(User user){
        sessions.removeIf((p -> p == user.getWebsession()));
    }

    private JSONObject getJsonFrame(int status, String message, JSONObject body, String event) {
        JSONObject frame =  new JSONObject().put("status", status).put("message", message).put("event", event);
        frame.accumulate("body", body);
        return frame;
    }
}
