package server;

import org.json.JSONArray;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public UserList(List<User> users){
        this.users = users;
        length = users.size();
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
            map.put("username", user.getName());
            arr.put(map);
        }
        return arr;
    }

    public JSONArray getInterfaceUserList(User smallblind, User bigblind) {
        JSONArray arr = new JSONArray();
        for (User user : users) {
            TypedMap map = new TypedMap();
            TypedMap.AbstractKey<String> username = TypedMap.StringKey.username;
            TypedMap.AbstractKey<Boolean> is_big_blind = TypedMap.BooleanKey.is_big_blind;
            TypedMap.AbstractKey<Boolean> is_small_blind = TypedMap.BooleanKey.is_small_blind;
            map.put(username, user.getName());
            map.put(is_big_blind, user == bigblind);
            map.put(is_small_blind, user == smallblind);
            arr.put(map.getMap());
        }
        return arr;
    }

    public User getPreviousUser(User user) {
        return this.getUserByIndex((users.indexOf(user) - 1 % length + length) % length) ;
    }

    public void resetAllHandCards(){
        for(User user : users){
            user.resetHandCards();
        }
    }

    public void resetAlreadyPaid(){
        for(User user : users){
            user.resetPayment();
        }
    }

    public boolean allUsersPaidSame() {
        boolean paid_same = true;
        int first_payment = users.get(0).getAlready_paid();
        for(User user: users){
            if(first_payment != user.getAlready_paid()){
                paid_same = false;
            }
        }
        return paid_same;
    }

    public int getHighestBet(){
        int i = 0;
        for(User user: users){
            int value = user.getAlready_paid();
            if(value> i){
                i = value;
            }
        }return i;
    }

    public boolean allPlayersActionNotNull(){
        for(User user: users){
            if(user.getLast_action().equals("")){
                return false;
            }
        }
        return true;
    }

    public void resetAllPlayersAction(){
        for(User user: users){
            user.setLast_action("");
        }
    }
}
