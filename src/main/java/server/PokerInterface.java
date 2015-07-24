package server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by loster on 23.07.2015.
 */
public class PokerInterface {

    private String[] do_not_authorize = new String[]{"login_user", "register_user"};
    private DatabaseConnection con = DatabaseConnection.getInstance();

    public JSONObject receive(String event, JSONObject data, User current_user, UserList connected_users, RoomList all_rooms) throws NotLoggedInException, SQLException {
        if (!Arrays.asList(do_not_authorize).contains(event)) {
            isLoggedIn(connected_users, current_user);
        }
        switch (event) {
            case "login_user":
                return login_user(data, current_user, connected_users);
            case "list_rooms":
                return list_rooms(all_rooms);
            case "create_room":
                return create_room(current_user, all_rooms);
            case "send_message":
                return send_message(data, all_rooms);
            case "join_room":
                return join_room(data, current_user, all_rooms, connected_users);
            case "leave_room":
                return leave_room(current_user, all_rooms);
            case "logout_user":
                return logout_user(current_user, all_rooms, connected_users);
            case "register_user":
                return register_user(data);
            default:
                return Helper.getJsonFrame(99, "unbekannter RequestType", new JSONObject(), "error_response");
        }
    }


    private void isLoggedIn(UserList connected_users, User user) throws NotLoggedInException {
        if (!connected_users.contains(user)) {
            throw new NotLoggedInException();
        }
    }

    private JSONObject login_user(JSONObject data, User current_user, UserList connected_users) throws SQLException {
        String request_user = data.getString("user");
        String request_password = data.getString("password");
        ResultSet rs = con.getUserByName(request_user);
        if (rs.first()) {
            String db_password = rs.getString("password");
            if (request_password.equals(db_password)) {
                User user = new User(current_user.getWebsession(), data.getString("user"), data.getString("password"));
                User connected_user = connected_users.getUserBySession(user.getWebsession());
                if (connected_user != null) {
                    return Helper.getJsonFrame(0, "Anmeldung aktualisiert", new JSONObject(), "login_user_response");
                }
                connected_users.add(user);
                return Helper.getJsonFrame(0, "Anmeldung erfolgreich", new JSONObject(), "login_user_response");
            }
        }
        return Helper.getJsonFrame(1, "Anmeldung nicht erfolgreich", new JSONObject(), "login_user_response");
    }

    private JSONObject list_rooms(RoomList all_rooms) {
        JSONArray arr = all_rooms.getInterfaceRoomList();
        JSONObject response = Helper.getJsonFrame(0, "Anfrage erfolgreich", new JSONObject(), "list_rooms_response");
        response.put("body", arr);
        return response;
    }

    private JSONObject create_room(User current_user, RoomList all_rooms) {
        if (current_user.getRoomId() == -1) {
            Room new_room = all_rooms.addNewRoom(current_user);
            JSONObject body = new JSONObject().put("room_id", new_room.getId());
            return Helper.getJsonFrame(0, "Raum erfolgreich angelegt", body, "create_room_response");
        } else {
            return Helper.getJsonFrame(1, "Bereits in Raum", new JSONObject(), "create_room_response");
        }
    }

    private JSONObject join_room(JSONObject data, User user, RoomList all_rooms, UserList connected_users) {
        int room_id = data.getInt("room_id");
        Room current_room = all_rooms.getRoomByRoomId(room_id);
        if (current_room == null) {
            return Helper.getJsonFrame(1, "Raum existiert nicht", new JSONObject(), "join_room_response");
        }
        current_room.joinRoom(user);
        return Helper.getJsonFrame(0, "Erfolgreich Raum beigetreten", new JSONObject().put("room_id", current_room.getId()), "join_room_response");
    }

    private JSONObject leave_room(User user, RoomList all_rooms) {
        int room_id = user.getRoomId();
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room == null) {
            return Helper.getJsonFrame(1, "Zuvor keinem Raum beigetreten", new JSONObject(), "leave_room_response");
        }
        room.leaveRoom(user);
        return Helper.getJsonFrame(0, "Raum verlassen", new JSONObject(), "leave_room_response");
    }

    private JSONObject send_message(JSONObject data, RoomList all_rooms) {
        int room_id = data.getInt("room_id");
        Room room = all_rooms.getRoomByRoomId(room_id);
        if(room == null){
            return Helper.getJsonFrame(1, "Raum nicht vorhanden", new JSONObject(), "send_message_response");
        }
        UserList room_users = room.getAllUsers();
        Pusher push = new Pusher(room_users);
        String message = data.getString("message");
        push.pushToAll(message, "chat_notification");
        return Helper.getJsonFrame(0, "Nachricht erfolgreich gesendet", new JSONObject(), "send_message_response");
    }

    private JSONObject logout_user(User user, RoomList all_rooms, UserList connected_users) {
        int room_id = user.getRoomId();
        Room room = all_rooms.getRoomByRoomId(room_id);
        if(room != null){
            room.leaveRoom(user);
        }
        connected_users.removeUser(user);
        return Helper.getJsonFrame(0, "Erfolgreich abgemeldet", new JSONObject(), "logout_user_response");
    }

    private JSONObject register_user(JSONObject data) throws SQLException {
        if (data.has("name") && data.has("password")) {
            //TODO hier noch überprüfen ob schon jemand vorhanden ist
            ResultSet rs = con.getUserByName(data.getString("name"));
            if (!rs.next()) {
                con.insertUser(data.getString("name"), data.getString("password"));
                return Helper.getJsonFrame(0, "Registrierung erfolgreich.", new JSONObject(), "register_user_response");
            } else {
                return Helper.getJsonFrame(1, "Nutzername schon verwendet.", new JSONObject(), "register_user_response");
            }
        } else {
            throw new JSONException("Invalid JSON");
        }
    }
}
