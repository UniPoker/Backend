package tests;

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
    }

    @Test
    public void openWebsocket() throws Exception {
        URI uri = new URI("ws://localhost:8080/events/");
        Session s1 = SocketClient.connect(uri);
        assertTrue(s1.isOpen());
    }
}
