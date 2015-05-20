import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by loster on 20.05.2015.
 */
public class UserList {

    private List<User> users;

    /**
     * creates an empty UserList
     */
    public UserList() {
        users = new ArrayList<User>();
    }

    public UserList(User user) {
        users = new ArrayList<>();
        users.add(user);
    }

    /**
     * @param users creates UserList with all Elements of the given Array
     */
    public UserList(User[] users) {
        this.users = new ArrayList<User>(Arrays.asList(users));
    }

    /**
     * @param user user to add to UserList
     */
    public void add(User user) {
        users.add(user);
    }

    /**
     * @param user User to delete from UserList
     */
    public void removeUser(User user) {
        users.removeIf(p -> p == user);
    }

    /**
     * Remove User with param session from UserList
     *
     * @param session Websession of User
     */
    public void removeUserWithSession(Session session) {
        users.removeIf(p -> p.getWebsession() == session);
    }

    /**
     * "delete" all Elements in UserList
     */
    public void clearAll() {
        users.removeAll(users);
    }

    /**
     * @return the size of UserList
     */
    public int length() {
        return users.size();
    }

    /**
     * @return UserList
     */
    public List<User> getUsers() {
        return users;
    }


}
