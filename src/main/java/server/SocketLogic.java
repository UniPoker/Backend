package server;

//import javax.websocket.ClientEndpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                case "list_rooms":
                    response = list_rooms();
                    break;
                case "create_room":
                    response = create_room(session);
                    break;
                default:
                    response = getJsonFrame(99, "unbekannter RequestType", new JSONObject());
            }
            session.getBasicRemote().sendObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ungültiges JSON!",new JSONObject()).toString());
        } catch (EncodeException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Encode Exception!", new JSONObject()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ein Fehler trat auf!", new JSONObject()).toString());
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

    private JSONObject getJsonFrame(int status, String message, JSONObject body) {
        JSONObject frame =  new JSONObject().put("status", status).put("message", message);
        frame.accumulate("body", body);
        return frame;
    }

    private JSONObject login_user(JSONObject data, Session session){
        System.out.println("USER WIRD EINGELOGGT!");
        User user = new User(session, data.getString("user"));
        connected_users.add(user);
        for (User current : connected_users.getUsers()) {
            System.out.println("CONNECTED_USERS: " + current);
        }
        return getJsonFrame(0, "Anmeldung erfolgreich", new JSONObject());
    }

    private JSONObject list_rooms(){
        System.out.println("LIST ALL ROOMS");
        JSONArray arr = new JSONArray();
        for(int i = 0; i < all_rooms.size();i++){
            HashMap<String,Integer> map = new HashMap<String,Integer>();
            map.put("room_id", all_rooms.get(i).getId());
            map.put("room_seats", all_rooms.get(i).userSize());
            arr.put(i,map);
        }
        return getJsonFrame(0,"Anfrage erfolgreich", new JSONObject().accumulate("body",arr));
    }

    private JSONObject create_room(Session session) {
        int id = all_rooms.size() == 0 ? 0 : all_rooms.get(all_rooms.size()-1).getId()+1;
        Room room = new Room(new User(session,"test1234"),id);
        all_rooms.add(room);
        JSONObject body = new JSONObject().put("room_id",room.getId());
        return getJsonFrame(0,"Raum erfolgreich angelegt", new JSONObject().accumulate("body",body));
    }


}