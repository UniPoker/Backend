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
    private static List<Room> all_rooms = new ArrayList<>();
    private static int room_index = 0;
    private String[] do_not_authorize = new String[]{"login_user"};

    public static UserList getConnectUsers() {
        return connected_users;
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
    }

    @OnMessage
    public void onWebSocketMessage(String message, Session session) throws IOException {
        String event = "";
        try {
            System.out.println("onMessage " + message);
            System.out.println("!!!!SESSION!!!! " + session);
            JSONObject json_payload = new JSONObject(message);
            if (json_payload.isNull("body")) {
                throw new JSONException("no Body");
            }
            JSONObject data = json_payload.getJSONObject("body");
            event = json_payload.getString("event");
            event = (event.equals("")) ? "error" : event;
            JSONObject response;
            if (!Arrays.asList(do_not_authorize).contains(event)) {
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
                case "send_message":
                    response = send_message(data);
                    break;
                case "join_room":
                    response = join_room(data, session);
                    break;
                case "leave_room":
                    response = leave_room(session);
                    break;
                case "logout_user":
                response = logout_user(session);
                break;
                default:
                    response = getJsonFrame(99, "unbekannter RequestType", new JSONObject(), "error_response");
            }
            session.getBasicRemote().sendObject(response.toString());
        } catch (JSONException e) {

            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ungültiges JSON!", new JSONObject(), event + "_response").toString());
        } catch (EncodeException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Encode Exception!", new JSONObject(), event + "_response").toString());
        } catch (NotLoggedInException e) {
            System.out.println("DAS WICHTIGE EVENT: " + event);
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Not Logged In!", new JSONObject(), event + "_response").toString());
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(getJsonFrame(99, "Ein Fehler trat auf!", new JSONObject(), event + "_response").toString());
        }
    }


    @OnClose
    public void onWebSocketClose(CloseReason reason, Session session) {
        logout(session);
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    private JSONObject getJsonFrame(int status, String message, JSONObject body, String event) {
        JSONObject frame = new JSONObject().put("status", status).put("message", message).put("event", event);
        frame.accumulate("body", body);
        return frame;
    }

    private void isLoggedIn(Session session) throws NotLoggedInException {
        if (connected_users.getUserBySession(session) == null) {
            throw new NotLoggedInException();
        }
    }

    private JSONObject login_user(JSONObject data, Session session) {
        System.out.println("USER WIRD EINGELOGGT!");
        if (!(data.getString("user").equals("mustermann") && data.getString("password").equals("123456"))) {
            return getJsonFrame(1, "Anmeldung nicht erfolgreich", new JSONObject(), "login_user_response");
        }
        User user = new User(session, data.getString("user"));
        connected_users.add(user);
        for (User current : connected_users.getUsers()) {
            System.out.println("CONNECTED_USERS: " + current);
        }
        return getJsonFrame(0, "Anmeldung erfolgreich", new JSONObject(), "login_user_response");
    }

    private JSONObject list_rooms() {
        System.out.println("LIST ALL ROOMS");
        JSONArray arr = new JSONArray();
        for (int i = 0; i < all_rooms.size(); i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put("room_id", all_rooms.get(i).getId());
            map.put("room_seats", all_rooms.get(i).userSize());
            arr.put(i, map);
        }
        JSONObject response = getJsonFrame(0, "Anfrage erfolgreich", new JSONObject(), "list_rooms_response");
        response.put("body", arr);
        return response;
    }

    private JSONObject create_room(Session session) {
        User current_user = connected_users.getUserBySession(session);
        if (current_user.getRoomIndex() == -1) {
            int id = room_index++;
            Room room = new Room(current_user, id);
            all_rooms.add(room);
            current_user.setRoomIndex(id);
            JSONObject body = new JSONObject().put("room_id", id);
            return getJsonFrame(0, "Raum erfolgreich angelegt", new JSONObject().accumulate("body", body), "create_room_response");
        } else {
            return getJsonFrame(1, "Bereits in Raum", new JSONObject(), "create_room_response");
        }
    }

    private JSONObject join_room(JSONObject data, Session session) {
        if (data.has("room_id")) {
            int room_id = data.getInt("room_id");
            Room current_room = null;
            for (Room room : all_rooms) {
                if (room.getId() == room_id) {
                    current_room = room;
                }
            }
            if (current_room == null) {
                return getJsonFrame(1, "Raum existiert nicht", new JSONObject(), "join_room_response");
            }
            User user = connected_users.getUserBySession(session);
            current_room.joinRoom(user);
            return getJsonFrame(1, "Erfolgreich Raum beigetreten", new JSONObject().put("room_id", current_room.getId()), "join_room_response");
        } else {
            throw new JSONException("no room_id");
        }
    }

    private JSONObject leave_room(Session session) {
        User current_user = connected_users.getUserBySession(session);
        for (Room room : all_rooms) {
            if (room.getId() == current_user.getRoomIndex()) {
                room.leaveRoom(current_user);
                return getJsonFrame(0, "Raum verlassen", new JSONObject(), "leave_room_response");
            }
        }
        return getJsonFrame(1, "Zuvor keinem Raum beigetreten", new JSONObject(), "leave_room_response");
    }

    private JSONObject send_message(JSONObject data) {
        int room_id = data.getInt("room_id");
        Room room = all_rooms.get(room_id);
        UserList room_users = room.getAllUsers();
        Pusher push = new Pusher(room_users);
        String message = data.getString("message");
        push.pushToAll(message, "chat_notification");
        return getJsonFrame(0, "Nachricht erfolgreich gesendet", new JSONObject(), "send_message_response");
    }

    private JSONObject logout_user(Session session) {
        logout(session);
        return getJsonFrame(0, "Erfolgreich abgemeldet", new JSONObject(), "logout_user_response");
    }

    private void logout(Session session) {
        System.out.println("removing user with session: " + session);
        User user = connected_users.getUserBySession(session);
        for (Room room : all_rooms) {
            if (room.getId() == user.getRoomIndex()) {
                room.leaveRoom(user);
            }
        }
        connected_users.removeUserWithSession(session);
        for (server.User current : connected_users.getUsers()) { //debug only
            System.out.println("CONNECTED_USERSWITHREMOVE: " + current);
        }
    }
}