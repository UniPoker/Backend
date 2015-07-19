package tests;

import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.*;
import javax.websocket.OnMessage;
import java.net.URI;


@ClientEndpoint
public class SocketClient {
    public static String message;

    public static Session connect(URI uri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        return container.connectToServer(SocketClient.class, uri);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        message = msg;
        System.out.println(session.getId() + ": " + msg);
    }


}