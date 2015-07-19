package server;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by loster on 20.05.2015.
 */
public class UserList {

    private List<User> users;
    public int length = 0;

    /**
     * creates an empty server.UserList
     */
    public UserList() {
        users = new ArrayList<User>();
    }

    public UserList(User user) {
        users = new ArrayList<>();
        users.add(user);
        length++;
    }

    /**
     * @param users creates server.UserList with all Elements of the given Array
     */
    public UserList(User[] users) {
        this.users = new ArrayList<User>(Arrays.asList(users));
        length += users.length;
    }

    /**
     * @param user user to add to server.UserList
     */
    public void add(User user) {
        users.add(user);
        length ++;
    }

    /**
     * @param user server.User to delete from server.UserList
     */
    public void removeUser(User user) {
        users.removeIf(p -> p == user);
    }

    /**
     * Remove server.User with param session from server.UserList
     *
     * @param session Websession of server.User
     */
    public void removeUserWithSession(Session session) {
        users.removeIf(p -> p.getWebsession() == session);
        length --;
    }

    /**
     * "delete" all Elements in server.UserList
     */
    public void clearAll() {
        users.removeAll(users);
        length = 0;
    }

    /**
     * @return the size of server.UserList
     */
//    public int length() {
//        return users.size();
//    }

    /**
     * @return server.UserList
     */
    public List<User> getUsers() {
        return users;
    }

    public User getUserByIndex(int index){
        return users.get(index);
    }

    public User getUserBySession(Session session){
        for(User user : users){
            if(user.getWebsession().equals(session)){
                return user;
            }
        }
        return null;
    }
}
