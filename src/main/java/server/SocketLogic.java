package server;

//import javax.websocket.ClientEndpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@ClientEndpoint
@ServerEndpoint("/events/")
public class SocketLogic {

    private static UserList connected_users = new UserList();
    private static List<server.Room> all_rooms = new ArrayList<>();
    private int room_index = 0;

    public static UserList getConnectUsers(){
        return connected_users;
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
    }

    @OnMessage
    public void onWebSocketMessage(String message, Session session) throws IOException {
        try {
            System.out.println("onMessage " + message);
            System.out.println("!!!!SESSION!!!! " + session);
            JSONObject json_payload = new JSONObject(message);
            JSONObject data = json_payload.getJSONObject("body");
            String event = json_payload.getString("event");
            JSONObject response;
            switch (event) {
                case "login_user":
                    response = login_user(data, session);
                    break;
                default:
                    response = getJsonFrame(99, "unbekannter RequestType");
            }
            session.getBasicRemote().sendObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ungültiges JSON!").toString());
        } catch (EncodeException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Encode Exception!").toString());
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ein Fehler trat auf!").toString());
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason, Session session) {
        System.out.println("removing user with session: " + session);
        connected_users.removeUserWithSession(session);
        for (server.User current : connected_users.getUsers()) { //debug only
            System.out.println("CONNECTED_USERSWITHREMOVE: " + current);
        }
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    private JSONObject getJsonFrame(int status, String message) {
        return new JSONObject().put("status", status).put("message", message);
    }

    private JSONObject login_user(JSONObject data, Session session){
        System.out.println("USER WIRD EINGELOGGT!");
        User user = new User(session, data.getString("user"));
        connected_users.add(user);
        for (User current : connected_users.getUsers()) {
            System.out.println("CONNECTED_USERS: " + current);
        }
        return getJsonFrame(00, "Anmeldung erfolgreich");
    }
}