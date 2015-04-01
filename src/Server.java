/**
 * Created by Stefan on 01.04.2015.
 */

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ServerEndpoint("/websocket")
public class Server {

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException, JSONException {
        System.out.println("User input: " + message + "; Session: " + session);
        session.getBasicRemote().sendText("Hello world Mr. " + message);
        JSONObject json_payload = new JSONObject(message);

        //Method method = getClass().getDeclaredMethod(functionName);
        //method.invoke(this);
        // Sending message to client each 1 second
        for (int i = 0; i <= 25; i++) {
            session.getBasicRemote().sendText(i + " Message from server");
            Thread.sleep(1000);
        }
        session.getBasicRemote().sendText("socket geschlossen:(");
        session.close();
    }

    @OnOpen
    public void onOpen() {
        System.out.println("Client connected");
    }

    @OnClose
    public void onClose() {
        System.out.println("Connection closed");
    }

    private void login_user(){

    }
}
