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

    /**
     * the entry point of the PokerInterface. Is called by the WebsocketEndpoint and gets all the given information to fire the right event
     *
     * @param event           the event that needs to be triggered
     * @param data            the data that is needed for the event
     * @param current_user    the current user who is calling the interface
     * @param connected_users all users who are connected to the server
     * @param all_rooms       all rooms that are created
     * @return returns a JSONObject with the response of the event
     * @throws NotLoggedInException is thrown if the current_user ist not logged in and is not calling a method where is doesn't need to log in
     * @throws SQLException         is thrown if there are any SQL Errors
     */
    public JSONObject receive(String event, JSONObject data, User current_user, UserList connected_users, RoomList all_rooms) throws NotLoggedInException, SQLException {
        if (!Arrays.asList(do_not_authorize).contains(event)) {
            isLoggedIn(connected_users, current_user);
        }
        switch (event) {
            case "login_user":
                return login_user(data, current_user, connected_users, all_rooms);
            case "list_rooms":
                return list_rooms(all_rooms);
            case "create_room":
                return create_room(current_user, all_rooms);
            case "send_message":
                return send_message(data, all_rooms, connected_users, current_user);
            case "join_room":
                return join_room(data, current_user, all_rooms);
            case "leave_room":
                return leave_room(current_user, all_rooms);
            case "logout_user":
                return logout_user(current_user, all_rooms, connected_users);
            case "register_user":
                return register_user(data);
            case "start_round":
                return start_round(all_rooms, data, current_user);
            case "do_bet":
                return do_bet(all_rooms, data);
            case "do_check":
                return do_check(all_rooms, data);
            case "do_call":
                return do_call(all_rooms, data);
            case "do_fold":
                return do_fold(all_rooms, data);
            case "do_raise":
                return do_raise(all_rooms, data);
            default:
                return Helper.getJsonFrame(99, "unbekannter RequestType", new JSONObject(), "error_response");
        }
    }

    private JSONObject do_bet(RoomList all_rooms, JSONObject data) {
        return Helper.getJsonFrame(0, "Irgendeine MESSAGE", new JSONObject(), "do_bet_response");
    }

    private JSONObject do_call(RoomList all_rooms, JSONObject data) {
        return Helper.getJsonFrame(0, "Irgendeine MESSAGE", new JSONObject(), "do_call_response");
    }

    private JSONObject do_check(RoomList all_rooms, JSONObject data) {
        return Helper.getJsonFrame(0, "Irgendeine MESSAGE", new JSONObject(), "do_check_response");
    }

    private JSONObject do_raise(RoomList all_rooms, JSONObject data) {
        return Helper.getJsonFrame(0, "unbekannter RequestType", new JSONObject(), "do_raise_response");
    }

    private JSONObject do_fold(RoomList all_rooms, JSONObject data) {
        return Helper.getJsonFrame(0, "Irgendeine MESSAGE", new JSONObject(), "do_fold_response");
    }

    /**
     * checks if the given user is already logged in.
     *
     * @param connected_users all user who are already logged_in
     * @param user            the user who needs to be checked if he is logged in
     * @throws NotLoggedInException if the user is not logged in
     */
    private void isLoggedIn(UserList connected_users, User user) throws NotLoggedInException {
        if (!connected_users.contains(user)) {
            throw new NotLoggedInException();
        }
    }

    /**
     * possible event of the PokerInterface.
     * Logs the requesting user in or renews his web session if already logged in
     *
     * @param data            the data containing all information (need to contain "user" and "password")
     * @param current_user    the requesting user
     * @param connected_users all connected users
     * @return JSONObject with the response (status: 0 if OK, 1 if not)
     * @throws SQLException
     */
    private JSONObject login_user(JSONObject data, User current_user, UserList connected_users, RoomList all_rooms) throws SQLException {
        String request_user = data.getString("user");
        String request_password = data.getString("password");
        ResultSet rs = con.getUserByName(request_user);
        if (rs.first()) {
            String db_password = rs.getString("password");
            if (request_password.equals(db_password)) {
                User user = new User(current_user.getWebsession(), data.getString("user"), data.getString("password"));
                User connected_user = connected_users.getUserByName(user.getName());
                if (connected_user != null) {
                    logoutUser(connected_user, all_rooms, connected_users);
                    connected_users.add(user);
                    return Helper.getJsonFrame(0, "Anmeldung aktualisiert", new JSONObject(), "login_user_response");
                }
                connected_users.add(user);
                return Helper.getJsonFrame(0, "Anmeldung erfolgreich", new JSONObject(), "login_user_response");
            }
        }
        return Helper.getJsonFrame(1, "Anmeldung nicht erfolgreich", new JSONObject(), "login_user_response");
    }

    /**
     * possible event of PokerInterface.
     * creates a JSONObject with the details of every room
     *
     * @param all_rooms every room that is created
     * @return JSONObject of the detailed room list (e.g. {body: {[room_id: 1, seats: 0], [room_id:2, seats: 1}}
     */
    private JSONObject list_rooms(RoomList all_rooms) {
        JSONArray arr = all_rooms.getInterfaceRoomList();
        JSONObject response = Helper.getJsonFrame(0, "Anfrage erfolgreich", new JSONObject(), "list_rooms_response");
        response.put("body", arr);
        return response;
    }

    /**
     * possible event of PokerInterface.
     * creates a new room and lets the requesting user join it
     *
     * @param current_user requesting user
     * @param all_rooms    list of all rooms so the room can be added
     * @return the id of the new room or the message that the user is already in a room
     */
    private JSONObject create_room(User current_user, RoomList all_rooms) {
        if (current_user.getRoomId() == -1) {
            Room new_room = all_rooms.addNewRoom(current_user);
            JSONObject body = new JSONObject().put("room_id", new_room.getId());
            return Helper.getJsonFrame(0, "Raum erfolgreich angelegt", body, "create_room_response");
        } else {
            return Helper.getJsonFrame(1, "Bereits in Raum", new JSONObject(), "create_room_response");
        }
    }

    /**
     * possible event of PokerInterface.
     * lets the requesting user join a room if it exists
     *
     * @param data         the data so we can get "room_id"
     * @param current_user the requesting user
     * @param all_rooms    list of all rooms so the room can be found by room_id
     * @return the room id if the user is joined successful
     */
    private JSONObject join_room(JSONObject data, User current_user, RoomList all_rooms) {
        int room_id = data.getInt("room_id");
        int old_room_id = current_user.getRoomId();

        Room joining_room = all_rooms.getRoomByRoomId(room_id);
        if (joining_room == null) {
            return Helper.getJsonFrame(1, "Raum existiert nicht", new JSONObject(), "join_room_response");
        }

        boolean is_joined = joining_room.joinRoom(current_user);
        if (is_joined) {
            Room current_room = all_rooms.getRoomByRoomId(old_room_id);
            if (current_room != null) {
                current_room.leaveRoom(current_user);
            }
            JSONObject body = new JSONObject();
            body.put("room_id", joining_room.getId());
            return Helper.getJsonFrame(0, "Erfolgreich Raum beigetreten", body, "join_room_response");
        } else {
            return Helper.getJsonFrame(1, "User schon im Raum", new JSONObject(), "join_room_response");
        }
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

    private JSONObject send_message(JSONObject data, RoomList all_rooms, UserList connected_users, User current_user) {
        int room_id = data.getInt("room_id");
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room == null && room_id != -1) {
            return Helper.getJsonFrame(1, "Raum nicht vorhanden", new JSONObject(), "send_message_response");
        }
        UserList room_users = (room == null) ? connected_users : room.getAllUsers();
        String message = data.getString("message");
        JSONObject body = new JSONObject();
        body.put("message", message);
        body.put("room_id", room_id);
        body.put("sender", current_user.getName());
        Pusher push = new Pusher(room_users);
        push.pushToAll("chat_notification", body);
        return Helper.getJsonFrame(0, "Nachricht erfolgreich gesendet", new JSONObject(), "send_message_response");
    }

    private JSONObject start_round(RoomList all_rooms, JSONObject data, User current_user) {
        int room_id = data.getInt("room_id");
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room == null) {
            return Helper.getJsonFrame(1, "Raum nicht vorhanden", new JSONObject(), "start_round_response");
        }
        room.getGame().startRound(current_user);
        return Helper.getJsonFrame(0, "Rundenstart erfolgreich angefragt", new JSONObject(), "start_round_response");
    }

    private JSONObject logout_user(User user, RoomList all_rooms, UserList connected_users) {
        logoutUser(user, all_rooms, connected_users);
        return Helper.getJsonFrame(0, "Erfolgreich abgemeldet", new JSONObject(), "logout_user_response");
    }

    //doppelt damit auch andere methoden ausloggen können
    private void logoutUser(User user, RoomList all_rooms, UserList connected_users) {
        int room_id = user.getRoomId();
        System.out.println("USER WIRD AUSGELOGGT MIT ROOM ID: " + room_id);
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room != null) {
            room.leaveRoom(user);
        }
        connected_users.removeUser(user);
    }

    private JSONObject register_user(JSONObject data) throws SQLException {
        if (data.has("name") && data.has("password")) {
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
