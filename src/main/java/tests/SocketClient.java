package tests;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.*;
import javax.websocket.OnMessage;
import java.net.URI;


@ClientEndpoint
public class SocketClient {

    public static Session connect(URI uri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            // Attempt Connect
            return container.connectToServer(SocketClient.class, uri);
        } finally {
            // Force lifecycle stop when done with container.
            // This is to free up threads and resources that the
            // JSR-356 container allocates. But unfortunately
            // the JSR-356 spec does not handle lifecycles (yet)
//            if (container instanceof LifeCycle) {
//                ((LifeCycle) container).stop();
//            }
        }
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        System.out.println(session.getId() + ": " + msg);
    }


}