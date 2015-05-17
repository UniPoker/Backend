/**
 * Created by Stefan on 01.04.2015.
 */

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.json.*;

@ServerEndpoint("/websocket")
public class Server {



    @OnMessage
    public void onMessage(String message, Session session) throws IOException{
        try {
            System.out.println("onMessage " + message);
            JSONObject json_payload = null;
            json_payload = new JSONObject(message);
            JSONObject data = json_payload.getJSONObject("body");
            String event = json_payload.getString("event");
            session.getBasicRemote().sendObject(new JSONObject().put("message", "invalid JSON"));
            Class[] paramJSON = new Class[1];
            paramJSON[0] = JSONObject.class;
            Method method = getClass().getDeclaredMethod(event, paramJSON);
            Object return_value = method.invoke(this, data);
            boolean status = (Boolean) return_value;
            if (status) {
                JSONObject response = null;
                response = new JSONObject().put("status", "true");
                session.getBasicRemote().sendObject(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onOpen() {
        System.out.println("Client connected");
    }

    @OnClose
    public void onClose() {
        System.out.println("Connection closed");
    }

    private boolean login_user(JSONObject data) {
        System.out.println("USER WIRD EINGELOGGT!");
        return true;
    }
}
