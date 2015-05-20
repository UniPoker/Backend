/**
 * Created by Stefan on 01.04.2015.
 */

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.json.*;

@ServerEndpoint("/websocket")
public class Server {

    private static List<User> connected_users = new ArrayList<>();
    private static List<Room> all_rooms = new ArrayList<>();
    private int room_index = 0;

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            System.out.println("onMessage " + message);
            System.out.println("!!!!SESSION!!!! " + session);
            JSONObject json_payload = null;
            json_payload = new JSONObject(message);
            JSONObject data = json_payload.getJSONObject("body");
            String event = json_payload.getString("event");

            Object return_value;
            if(event.equals("login_user")){
                return_value = login_user(data, session);
            }else{
                Class[] paramJSON = new Class[1];
                paramJSON[0] = JSONObject.class;
                Method method = getClass().getDeclaredMethod(event, paramJSON);
                return_value = method.invoke(this, data);
            }
            boolean status = (Boolean) return_value;
            if (status) {
                JSONObject response = null;
                response = new JSONObject().put("status", "true");
                session.getBasicRemote().sendObject(response.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText("FEHLER: " + e.getMessage());
        } catch (EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText("{message: 'invalid JSON'}");
        }

        // Sending message to client each 1 second
        // for (int i = 0; i <= 25; i++) {
        //     session.getBasicRemote().sendText(i + " Message from server");
        //     Thread.sleep(1000);
        // }
        // session.getBasicRemote().sendText("socket geschlossen:(");
        // session.close();
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Client connected");
    }

    @OnClose
    public void onClose(final Session session, CloseReason reason) {
        System.out.println("removing user with session: " + session);
        connected_users.removeIf(p -> p.getWebsession() == session);
        for (User current: connected_users) { //debug only
            System.out.println("CONNECTED_USERSWITHREMOVE: " + current);
        }
    }

    private boolean login_user(JSONObject data, Session session) throws JSONException {
        System.out.println("USER WIRD EINGELOGGT!");
        User user = new User(session, data.getString("user"));
        connected_users.add(user);
        for (User current: connected_users) {
            System.out.println("CONNECTED_USERS: " + current);
        }
        return true;
    }
}
