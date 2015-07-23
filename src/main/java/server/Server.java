package server; /**
 * Created by Stefan on 01.04.2015.
 */

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;


//@ServerEndpoint("/websocket")
public class Server {

    private org.eclipse.jetty.server.Server server;
    private int port;

    public Server(int port){
        this.port = port;
    }

    public void start(){
        server = new org.eclipse.jetty.server.Server(this.port);
        ServerConnector connector = new ServerConnector(server);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        try
        {
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
            wscontainer.addEndpoint(WebsocketEndpoint.class);
            server.start();
//            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    public void close() throws Exception {
        server.stop();
    }

    public static void main(String[] args)
    {
        Server websocketserver = new Server(8080);
        websocketserver.start();
    }
}
