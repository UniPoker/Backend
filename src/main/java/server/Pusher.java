package server;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pusher {

    List<Session> sessions;

    public Pusher(UserList users) {
        sessions = new ArrayList<>();
        sessions.addAll(users.getUsers().stream().map(User::getWebsession).collect(Collectors.toList()));
    }

    public void pushToAll(String event, JSONObject body) {
        try {
            for (Session session : sessions) {
                JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", body, event);
                session.getBasicRemote().sendText(json.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushToAll(String event, JSONArray body) {
        try {
            for (Session session : sessions) {
                JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", new JSONObject(), event);
                json.put("body",body);
                session.getBasicRemote().sendText(json.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushToSingle(String event, JSONObject body, Session session) {
        try {
            JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", body, event);
            session.getBasicRemote().sendText(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(User user) {
        sessions.removeIf((p -> p == user.getWebsession()));
    }

    public void addUser(User user) {
        sessions.add(user.getWebsession());
    }

    public void addUser(UserList users) {
        sessions.addAll(users.getUsers().stream().map(User::getWebsession).collect(Collectors.toList()));
    }

}
