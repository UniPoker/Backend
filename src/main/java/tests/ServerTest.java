//package tests;
//
//import org.json.JSONObject;
//import org.junit.After;
//import server.WebsocketEndpoint;
//import server.Server;
//import org.junit.Before;
//import org.junit.Test;
//import java.net.URI;
//import javax.websocket.Session;
//import static org.junit.Assert.*;
//
//public class ServerTest {
//
//    private static Server server;
//    public Session session;
//
//    @Before
//    public void before() throws Exception {
//        System.out.println("Starting...");
//        server = new Server(8080);
//        server.start();
//        System.out.println("Started");
//        URI uri = new URI("ws://localhost:8080/events/");
//        session = SocketClient.connect(uri);
//        SocketClient.message = new String();
//    }
//
//    @After
//    public void after() throws Exception {
//        server.close();
//        SocketClient.message = new String();
//    }
//
//    @Test
//    public void TestLoginUser() throws Exception {
//        assertTrue(session.isOpen());
//        JSONObject request = getRequestFrame("login_user", new JSONObject().put("user", "test"));
//        session.getBasicRemote().sendText(request.toString());
//        String message;
//        while(true){
//            if(!SocketClient.message.isEmpty()){
//                System.out.println(SocketClient.message);
//                message = SocketClient.message;
//                break;
//            }
//        }
//        assertNotNull(message);
//        JSONObject response = new JSONObject(message );
//        assertEquals(0, response.getInt("status"));
//        assertEquals(1, WebsocketEndpoint.getConnectUsers().length);
//    }
//
//    @Test
//    public void TestCreateRoom() throws Exception {
//        assertTrue(session.isOpen());
//        JSONObject request = getRequestFrame("create_room", new JSONObject());
//        session.getBasicRemote().sendText(request.toString());
//        String message;
//        while(true){
//            if(!SocketClient.message.isEmpty()){
//                System.out.println(SocketClient.message);
//                message = SocketClient.message;
//                break;
//            }
//        }
//        assertNotNull(message);
//        System.out.println("nach assert");
//        JSONObject response = new JSONObject(message);
//        assertEquals(0, response.getInt("status"));
//        //create a second room
//        System.out.println("2 Test anlegen");
//        SocketClient.message = new String();
//        session.getBasicRemote().sendText(request.toString());
//        String response_message;
//        System.out.println("vor while");
//        while(true){
//            if(!SocketClient.message.isEmpty()){
//                System.out.println(SocketClient.message);
//                response_message = SocketClient.message;
//                break;
//            }
//        }
//        assertNotNull(response_message);
//        System.out.println("nach assert");
//    }
//
//    private JSONObject getRequestFrame(String event,JSONObject body) {
//        JSONObject request = new JSONObject().put("event",event);
//        request.accumulate("body", body);
//        return request;
//    }
//}
