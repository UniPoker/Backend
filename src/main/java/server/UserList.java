package server;

import org.json.JSONArray;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        length++;
    }

    /**
     * @param user server.User to delete from server.UserList
     */
    public void removeUser(User user) {
        //TODO was passiert wenn wir hier einen User reinreichen der nicht in der Liste war? dann ist die length einen runter aber kein user gelöscht?!
        boolean removed = users.removeIf(p -> p == user);
        if(removed){
            length--;
        }
    }

    /**
     * Remove server.User with param session from server.UserList
     *
     * @param session Websession of server.User
     */
    public void removeUserWithSession(Session session) {
        users.removeIf(p -> p.getWebsession() == session);
        length--;
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

    public List<User> getUsersExceptUser(User user) {
        List<User> _users = users;
        _users.removeIf(p -> p == user);
        return _users;
    }

    public List<User> getUsersExceptSession(Session session) {
        List<User> _users = users;
        _users.removeIf(p -> p.getWebsession() == session);
        return _users;
    }

    public User getUserByIndex(int index) {
        return users.get(index);
    }

    public void setUserByIndex(int index, User user) {
        users.set(index, user);
    }

    public User getUserBySession(Session session) {
        for (User user : users) {
            if (user.getWebsession().getId() == session.getId()) {
                return user;
            }
        }
        return null;
    }

    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public int getIndexOfUser(User user) {
        return users.indexOf(user);
    }

    public boolean contains(User user) {
        return users.contains(user);
    }

    public JSONArray getInterfaceUserList() {
        JSONArray arr = new JSONArray();
        for (User user : users) {
            HashMap<String, String> map = new HashMap<>();
            map.put("user_name", user.getName());
            arr.put(map);
        }
        return arr;
    }
}
