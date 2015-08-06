package server;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import utils.DatabaseConnection;

import javax.websocket.server.ServerContainer;


/**
 * The server class which starts with a given port.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.eclipse.jetty.server.ServerConnector
 * @see org.eclipse.jetty.servlet.ServletContextHandler
 * @see org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
 * @see javax.websocket.server.ServerContainer
 */
public class Server {

    private org.eclipse.jetty.server.Server server;
    private int port;

    public Server(int port){
        this.port = port;
    }

    /**
     * start the server. It uses the port attribute as port to access the server.
     * It also holds the WebsocketEndpoint class.
     */
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
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    /**
     * only used to close the server.
     * @throws Exception in case of an error
     */
    public void close() throws Exception {
        server.stop();
    }

    /**
     * Main class which starts the server.
     * It also defines the port to be 8080.
     * Furthermore there is a shutdownhook added which closes the server and database connection
     * @param args not used
     */
    public static void main(String[] args)
    {
        Server websocketserver = new Server(8080);
        websocketserver.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    websocketserver.close();
                    DatabaseConnection.getInstance().closeConnection();
                    System.out.println("Alle Verbindungen geschlossen! ");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        System.out.println("Server gestartet!");
    }
}
