package tests;

import org.json.JSONObject;
import org.junit.*;
import server.SocketLogic;
import server.WebsocketServer;

import java.net.URI;
import javax.websocket.Session;
import static org.junit.Assert.*;

public class RoomTest {

    private static WebsocketServer server;
    public static Session session;

    @BeforeClass
    public static void setup() throws Exception {
        System.out.println("Starting...");
        server = new WebsocketServer(8080);
        server.start();
        URI uri = new URI("ws://localhost:8080/events/");
        session = SocketClient.connect(uri);
        System.out.println("before");
    }

    @AfterClass
    public static void clearUp() throws Exception {
        System.out.println("clearup");
        server.close();
    }

    @Before
    public void before() throws Exception {
        System.out.println("before");
        SocketClient.message = new String();
        JSONObject request = getRequestFrame("create_room", new JSONObject());
        session.getBasicRemote().sendText(request.toString());
    }

    @Test
    public void TestGetRooms() throws Exception {
        assertTrue(session.isOpen());
        JSONObject request = getRequestFrame("list_rooms", new JSONObject());
        session.getBasicRemote().sendText(request.toString());
        String message;
        while(true){
            if(!SocketClient.message.isEmpty()){
                System.out.println(SocketClient.message);
                message = SocketClient.message;
                System.out.println("in while schleife");
                break;
            }
        }
        assertNotNull(message);
        System.out.println("bevor assert");
        JSONObject response = new JSONObject(message);
        System.out.println("bevor assert");
        assertEquals(0, response.getInt("status"));
    }

    private JSONObject getRequestFrame(String event,JSONObject body) {
        JSONObject request = new JSONObject().put("event",event);
        request.accumulate("body", body);
        return request;
    }

}
