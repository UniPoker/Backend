package server;

//import javax.websocket.ClientEndpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//@ClientEndpoint
@ServerEndpoint("/events/")
public class WebsocketEndpoint {
    private static UserList connected_users = new UserList();
    private static RoomList all_rooms = new RoomList();
    public PokerInterface pokerInterface = new PokerInterface();


    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
    }

    @OnMessage
    public void onWebSocketMessage(String message, Session session) throws IOException {
        String event = "error"; //TODO muss rein damit auch JSON exceptions eine error_response haben
        try {
            System.out.println("onMessage " + message);
            JSONObject json_payload = new JSONObject(message);
            if (!json_payload.has("body")) {
                throw new JSONException("no Body");
            }
            JSONObject data = json_payload.getJSONObject("body");
            event = json_payload.getString("event");
            event = (event.equals("")) ? "error" : event;
            String interface_name = json_payload.getString("interface_name");
            JSONObject response;
            switch (interface_name){
                case "PokerInterface":
                    User user = connected_users.getUserBySession(session);
                    if(user == null){
                        user = new User(session);
                    }
                    response = pokerInterface.receive(event, data, user, connected_users, all_rooms);
                    break;
                default:
                    throw new JSONException("Invalid JSON");
            }
            session.getBasicRemote().sendObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(Helper.getJsonFrame(99, "Ungültiges JSON!", new JSONObject(), event + "_response").toString());
        } catch (EncodeException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(Helper.getJsonFrame(99, "Encode Exception!", new JSONObject(), event + "_response").toString());
        } catch (NotLoggedInException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(Helper.getJsonFrame(99, "Not Logged In!", new JSONObject(), event + "_response").toString());
        }catch (SQLException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(Helper.getJsonFrame(99, "Irgendein SQL Fehler", new JSONObject(), event + "_response").toString());
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(Helper.getJsonFrame(99, "Ein Fehler trat auf!", new JSONObject(), event + "_response").toString());
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason, Session session) throws NotLoggedInException, SQLException {
        pokerInterface.receive("logout_user", new JSONObject(), connected_users.getUserBySession(session), connected_users, all_rooms);
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }
}