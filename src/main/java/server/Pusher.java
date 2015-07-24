package server;

import org.json.JSONObject;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by loster on 27.05.2015.
 */
public class Pusher {

    List<Session> sessions;
    String sender_name;
    int room_id;

    public Pusher(UserList users, String sender_name, int room_id) {
        sessions = new ArrayList<>();
        sessions.addAll(users.getUsers().stream().map(User::getWebsession).collect(Collectors.toList()));
        this.sender_name = sender_name;
        this.room_id = room_id;
    }

    public void pushToAll(String message, String event) {
        try {
            for (Session session : sessions) {
                JSONObject body = new JSONObject();
                body.put("message", message);
                body.put("room_id", room_id);
                body.put("sender", sender_name);
                JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", body, event);
                session.getBasicRemote().sendText(json.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeUser(User user){
        sessions.removeIf((p -> p == user.getWebsession()));
    }

}
