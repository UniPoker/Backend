import javax.websocket.Session;

/**
 * Created by Stefan on 17.05.2015.
 */
public class User {

    private Session websession;
    private String name;

    private double limit = 1000.00;

    User(Session websession, String name) {
        this.websession = websession;
        this.name = name;
    }

    public Session getWebsession() {
        return websession;
    }

    public void setWebsession(Session websession) {
        this.websession = websession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}