package tests;

import org.json.JSONObject;
import server.SocketLogic;
import server.WebsocketServer;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import javax.websocket.Session;
import static org.junit.Assert.*;

public class ServerTest {

    private static WebsocketServer server;

    @Before
    public void before() {
        System.out.println("Starting...");
        server = new WebsocketServer(8080);
        server.start();
        System.out.println("Started");
        SocketClient.message = new String();
    }

    @Test
    public void TestLoginUser() throws Exception {
        URI uri = new URI("ws://localhost:8080/events/");
        Session s1 = SocketClient.connect(uri);
        JSONObject request = new JSONObject().put("event", "login_user");
        request.accumulate("body", new JSONObject().put("user", "test"));
        assertTrue(s1.isOpen());
        s1.getBasicRemote().sendText(request.toString());
        while(true){
            if(!SocketClient.message.isEmpty()){
                System.out.println(SocketClient.message);
                break;
            }
        }
        assertNotNull(SocketClient.message);
        JSONObject response = new JSONObject(SocketClient.message);
        assertEquals(response.getInt("status"), 0);
        assertEquals(SocketLogic.getConnectUsers().length, 1);
    }
}
