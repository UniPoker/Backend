package server;

import org.json.JSONArray;
import org.json.JSONObject;
import users.User;
import users.UserList;
import utils.Helper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class needed to perform pushnotification all users in the userlist
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.json.JSONObject
 * @see org.json.JSONArray
 * @see javax.websocket.Session
 * @see java.io.IOException
 * @see java.util.ArrayList
 * @see java.util.List
 * @see java.util.stream.Collectors
 */
public class Pusher {

    List<Session> sessions;

    public Pusher(UserList users) {
        sessions = new ArrayList<>();
        sessions.addAll(users.getUsers().stream().filter(session-> session == null).map(User::getWebsession).collect(Collectors.toList()));
    }

    /**
     * perform a push notification to all sessions.
     * the notification contains an event and a body with given data
     *
     * @param event event of the push notification
     * @param body body of the pushnotification
     */
    public void pushToAll(String event, JSONObject body) {
        try {
            for (Session session : sessions) {
                if(session != null){
                    JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", body, event);
                    session.getBasicRemote().sendText(json.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * perform a push notification to all sessions.
     * the notification contains an event and a body with given data
     *
     * @param event event of the push notification
     * @param body body of the pushnotification
     */
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

    /**
     * this method performs a push notification
     * to a single, given as parameter, session.
     *
     * @param event event of the push notification
     * @param body body of the pushnotification
     * @param session to send message to this session
     */
    public void pushToSingle(String event, JSONObject body, Session session) {
        try {
            JSONObject json = Helper.getJsonFrame(0, "Nachricht erhalten", body, event);
            session.getBasicRemote().sendText(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes a user by his session from the session list
     * @param user to remove from session list
     */
    public void removeUser(User user) {
        sessions.removeIf((p -> p == user.getWebsession()));
    }

    /**
     * add a users session to the session list
     * @param user to add his session to the list
     */
    public void addUser(User user) {
        sessions.add(user.getWebsession());
    }

    /**
     * add multiple sessions from users to the list
     * @param users to add their sessions to the list
     */
    public void addUser(UserList users) {
        sessions.addAll(users.getUsers().stream().map(User::getWebsession).collect(Collectors.toList()));
    }

}
