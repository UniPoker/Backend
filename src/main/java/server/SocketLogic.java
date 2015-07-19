package server;

//import javax.websocket.ClientEndpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

import java.io.IOException;
import java.util.*;

//@ClientEndpoint
@ServerEndpoint("/events/")
public class SocketLogic {

    private static UserList connected_users = new UserList();
    private static List<server.Room> all_rooms = new ArrayList<>();
    private int room_index = 0;
    private String [] do_not_authorize = new String[]{"login_user"};

    public static UserList getConnectUsers(){
        return connected_users;
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
    }

    @OnMessage
    public void onWebSocketMessage(String message, Session session) throws IOException {
        String event = "error";
        try {
            System.out.println("onMessage " + message);
            System.out.println("!!!!SESSION!!!! " + session);
            JSONObject json_payload = new JSONObject(message);
            if(json_payload.isNull("body")){
                throw new JSONException("no Body");
            }
            JSONObject data = json_payload.getJSONObject("body");
            event = json_payload.getString("event");
            JSONObject response;
            if(!Arrays.asList(do_not_authorize).contains(event)){
                isLoggedIn(session);
            }
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
                    response = getJsonFrame(99, "unbekannter RequestType", new JSONObject(), event+"_response");
            }
            session.getBasicRemote().sendObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ungültiges JSON!",new JSONObject(), event+"_response").toString());
        } catch (EncodeException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Encode Exception!", new JSONObject(), event+"_response" ).toString());
        } catch(NotLoggedInException e){
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Not Logged In!", new JSONObject(), event+"_response" ).toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ein Fehler trat auf!", new JSONObject(), event+"_response").toString());
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

    private JSONObject getJsonFrame(int status, String message, JSONObject body, String event) {
        JSONObject frame =  new JSONObject().put("status", status).put("message", message).put("event", event);
        frame.accumulate("body", body);
        return frame;
    }

    private void isLoggedIn(Session session) throws NotLoggedInException {
        if(connected_users.getUserBySession(session) == null){
            throw new NotLoggedInException();
        }
    }

    private JSONObject login_user(JSONObject data, Session session){
        System.out.println("USER WIRD EINGELOGGT!");
        User user = new User(session, data.getString("user"));
        connected_users.add(user);
        for (User current : connected_users.getUsers()) {
            System.out.println("CONNECTED_USERS: " + current);
        }
        return getJsonFrame(0, "Anmeldung erfolgreich", new JSONObject(), "login_user_response");
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
        JSONObject response = getJsonFrame(0,"Anfrage erfolgreich",new JSONObject(), "list_rooms_response");
        response.put("body", arr);
        return response;
    }

    private JSONObject create_room(Session session) {
        int id = room_index++;
        Room room = new Room(new User(session,"test1234"),id);
        all_rooms.add(room);
        JSONObject body = new JSONObject().put("room_id",id);
        return getJsonFrame(0,"Raum erfolgreich angelegt", new JSONObject().accumulate("body",body), "create_room_response");
    }



}