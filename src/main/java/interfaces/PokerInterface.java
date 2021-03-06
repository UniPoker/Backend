package interfaces;

import game.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import rooms.Room;
import rooms.RoomList;
import server.Pusher;
import users.User;
import users.UserList;
import utils.DatabaseConnection;
import utils.Helper;
import utils.Mailer;
import utils.NotLoggedInException;

import javax.mail.MessagingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * This class defines the poker interface which can be accessed
 * with interface_name = "PokerInterface"
 * It contains the complete request- and response logic
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.json.JSONArray
 * @see org.json.JSONObject
 * @see javax.mail.MessagingException
 * @see java.sql.ResultSet
 * @see java.sql.SQLException
 * @see java.util.Arrays
 */
public class PokerInterface {

    private String[] do_not_authorize = new String[]{"login_user", "register_user"};
    private DatabaseConnection con = DatabaseConnection.getInstance();
    private Mailer mailer = Mailer.getInstance();

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
    public JSONObject receive(String event, JSONObject data, User current_user, UserList connected_users, RoomList all_rooms) throws NotLoggedInException, SQLException, MessagingException {
        if (!Arrays.asList(do_not_authorize).contains(event)) {
            isLoggedIn(connected_users, current_user);
        }
        switch (event) {
            case "login_user":
                return login_user(data, current_user, connected_users, all_rooms);
            case "list_rooms":
                return list_rooms(all_rooms);
            case "create_room":
                return create_room(current_user, all_rooms, connected_users);
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
                return do_bet(all_rooms, data, current_user);
            case "do_check":
                return do_check(all_rooms, current_user);
            case "do_call":
                return do_call(all_rooms, current_user);
            case "do_fold":
                return do_fold(all_rooms, current_user);
            case "do_raise":
                return do_raise(all_rooms, data, current_user);
            default:
                return Helper.getJsonFrame(99, "unbekannter RequestType", new JSONObject(), "error_response");
        }
    }

    /**
     * This method perform a bet if the user is at turn
     *
     * @param all_rooms needed to get the game of the room
     * @param data holds the information about the bet (int) at the key "bet"
     * @param user the user who wants to perform a bet
     * @return JSONObject with the response (status: 0 if OK, 1 if not, 2 bet have to be bigger than 0, 3 bet is to high)
     */
    private JSONObject do_bet(RoomList all_rooms, JSONObject data, User user) {
        int bet = data.getInt("bet");
        if (bet <= 0) {
            return Helper.getJsonFrame(2, "Bet muss größer 0 sein", new JSONObject(), "do_bet_response");
        }
        if (bet > user.getLimit()) {
            return Helper.getJsonFrame(3, "Guthaben reicht nicht aus", new JSONObject(), "do_bet_response");
        }
        Room room = all_rooms.getRoomByRoomId(user.getRoomId());
        Game game = room.getGame();
        boolean is_successfull = game.doBet(user, bet);
        if (is_successfull) {
            return Helper.getJsonFrame(0, "Bet erfolgreich", new JSONObject(), "do_bet_response");
        } else {
            return Helper.getJsonFrame(1, "Bet nicht erfolgreich", new JSONObject(), "do_bet_response");
        }
    }

    /**
     * This method perform a call if the user is at turn
     *
     * @param all_rooms needed to get the game of the room
     * @param user the user who wants to perform a call
     * @return JSONObject with the response (status: 0 if OK, 1 if not, 2 user needs more money)
     */
    private JSONObject do_call(RoomList all_rooms, User user) {
        Room room = all_rooms.getRoomByRoomId(user.getRoomId());
        Game game = room.getGame();
        if (game.getActive_players().getCallValueForUser(user) > user.getLimit()) {
            return Helper.getJsonFrame(2, "Guthaben reicht nicht aus", new JSONObject(), "do_raise_response");
        }
        boolean is_successfull = game.doCall(user);
        if (is_successfull) {
            return Helper.getJsonFrame(0, "Call erfolgreich", new JSONObject(), "do_call_response");
        } else {
            return Helper.getJsonFrame(1, "Call nicht erfolgreich", new JSONObject(), "do_call_response");
        }
    }

    /**
     * This method perform a check if the user is at turn
     *
     * @param all_rooms needed to get the game of the room
     * @param user the user who wants to perform a check
     * @return JSONObject with the response (status: 0 if OK, 1 if not)
     */
    private JSONObject do_check(RoomList all_rooms, User user) {
        Room room = all_rooms.getRoomByRoomId(user.getRoomId());
        Game game = room.getGame();
        boolean is_successfull = game.doCheck(user);
        if (is_successfull) {
            return Helper.getJsonFrame(0, "Check erfolgreich", new JSONObject(), "do_check_response");
        } else {
            return Helper.getJsonFrame(1, "Check nicht erfolgreich", new JSONObject(), "do_check_response");
        }
    }

    /**
     * This method perform a raise if the user is at turn
     *
     * @param all_rooms needed to get the game of the room
     * @param data holds the information about the raise (int) at the key "raise"
     * @param user the user who wants to perform a raise
     * @return JSONObject with the response (status: 0 if OK, 1 if not, 2 raise have to be bigger than 0, 3 bet is to high)
     */
    private JSONObject do_raise(RoomList all_rooms, JSONObject data, User user) {
        int raise = data.getInt("raise");
        if (raise <= 0) {
            return Helper.getJsonFrame(2, "Gebot muss größer 0 sein.", new JSONObject(), "do_raise_response");
        }
        Room room = all_rooms.getRoomByRoomId(user.getRoomId());
        Game game = room.getGame();
        if (game.getActive_players().getHighestBet()- user.getAlready_paid() + raise > user.getLimit()) {
            return Helper.getJsonFrame(3, "Guthaben reicht nicht aus.", new JSONObject(), "do_raise_response");
        }
        boolean is_successfull = game.doRaise(user, raise);
        if (is_successfull) {
            return Helper.getJsonFrame(0, "Raise erfolgreich", new JSONObject(), "do_raise_response");
        } else {
            return Helper.getJsonFrame(1, "Raise nicht erfolgreich", new JSONObject(), "do_raise_response");
        }
    }

    /**
     * This method perform a fold if the user is at turn
     *
     * @param all_rooms needed to get the game of the room
     * @param user the user who wants to perform a fold
     * @return JSONObject with the response (status: 0 if OK, 1 if not)
     */
    private JSONObject do_fold(RoomList all_rooms, User user) {
        Room room = all_rooms.getRoomByRoomId(user.getRoomId());
        Game game = room.getGame();
        boolean is_successfull = game.doFold(user);
        if (is_successfull) {
            return Helper.getJsonFrame(0, "Fold erfolgreich", new JSONObject(), "do_fold_response");
        } else {
            return Helper.getJsonFrame(1, "Fold nicht erfolgreich", new JSONObject(), "do_fold_response");
        }
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
    private JSONObject create_room(User current_user, RoomList all_rooms, UserList users) {
        if (current_user.getRoomId() == -1) {
            Room new_room = all_rooms.addNewRoom(current_user);
            Pusher pusher = new Pusher(users);
            JSONObject body = new JSONObject().put("room_id", new_room.getId());
            JSONArray arr = all_rooms.getInterfaceRoomList();
            pusher.pushToAll("new_rooms_notification", arr);
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
     * @return the a jsonobject with the room id if the user is joined successful
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
            return Helper.getJsonFrame(2, "User schon im Raum", new JSONObject(), "join_room_response");
        }
    }

    /**
     * possible event of PokerInterface.
     * lets the requesting user leave a room if it exists
     *
     * @param user who wants to leave the room
     * @param all_rooms list of all rooms so the room can be found by room_id
     * @return jsonobject with status 0 if successfull
     */
    private JSONObject leave_room(User user, RoomList all_rooms) {
        int room_id = user.getRoomId();
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room == null) {
            return Helper.getJsonFrame(1, "Zuvor keinem Raum beigetreten", new JSONObject(), "leave_room_response");
        }
        room.leaveRoom(user);
        return Helper.getJsonFrame(0, "Raum verlassen", new JSONObject(), "leave_room_response");
    }

    /**
     * a possible request of PokerInterface
     * push a notification to all players in a room
     * if the user is not in a room the push notification will go to each
     * logged in user.
     *
     * @param data contains the message to be sent.
     * @param all_rooms needed to get the room if the user is in a room.
     * @param connected_users a list of all logged in users
     * @param current_user the user who wants to send a message
     * @return
     */
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

    /**
     * a possible request of PokerInterface
     * this request starts a new round if there are more than
     * 2 players in this game
     *
     * @param all_rooms a list of all rooms the get the room by its id
     * @param data contains the room_id
     * @param current_user the user who wants to start a round
     * @return a jsonobject with status 0 if successfull else 1
     */
    private JSONObject start_round(RoomList all_rooms, JSONObject data, User current_user) {
        int room_id = data.getInt("room_id");
        Room room = all_rooms.getRoomByRoomId(room_id);
        if (room == null) {
            return Helper.getJsonFrame(1, "Raum nicht vorhanden", new JSONObject(), "start_round_response");
        }
        room.getGame().startRound(current_user);
        return Helper.getJsonFrame(0, "Rundenstart erfolgreich angefragt", new JSONObject(), "start_round_response");
    }


    /**
     * a possible request of PokerInterface
     * this request performs a logout out for the given user.
     * For this purpose the user will be remove from the connected_user list,
     * from the Userlist of the given room etc.
     *
     * @param user who wants to logout
     * @param all_rooms needed to get the room where the user is.
     * @param connected_users to remove the given user from this list
     * @return a logout_user_respone json with status 0
     */
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


    /**
     * a possible request of PokerInterface
     * perform a registration for a user.
     * write its data into the database
     * When succesfull it sends the user a mail with his username and password
     * @see DatabaseConnection
     * @see Mailer#sendRegistrationMail(String, String, String, String)
     *
     * @param data containing all data needed for a registration (name, password, email)
     * @return json with status 0 if correct else 1
     * @throws SQLException when insert fails
     * @throws MessagingException when email not correct
     */
    private JSONObject register_user(JSONObject data) throws SQLException, MessagingException {
        ResultSet rs = con.getUserByName(data.getString("name"));
        if (!rs.next()) {
            String name = data.getString("name");
            String password = data.getString("password");
            con.insertUser(name, password);
            mailer.sendRegistrationMail(data.getString("email"), "Registrierung", name, password);
            return Helper.getJsonFrame(0, "Registrierung erfolgreich.", new JSONObject(), "register_user_response");
        } else {
            return Helper.getJsonFrame(1, "Nutzername schon verwendet.", new JSONObject(), "register_user_response");
        }
    }
}
