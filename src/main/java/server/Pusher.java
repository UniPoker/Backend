package server;

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

    public void pushToAll(String message) {
        try {
            for (Session session : sessions) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeUser(User user){
        sessions.removeIf((p -> p == user.getWebsession()));
    }

}
