package users;

import cards.Card;
import org.json.JSONArray;
import utils.TypedMap;
import utils.Constants;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserList {

    private List<User> users;
    public int length = 0;

    /**
     * creates an empty users.UserList
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
     * @param users creates users.UserList with all Elements of the given Array
     */
    public UserList(User[] users) {
        this.users = new ArrayList<User>(Arrays.asList(users));
        length += users.length;
    }

    public UserList(List<User> users) {
        this.users = users;
        length = users.size();
    }

    /**
     * @param user user to add to users.UserList
     */
    public void add(User user) {
        users.add(user);
        length++;
    }

    /**
     * @param user users.User to delete from users.UserList
     */
    public void removeUser(User user) {
        boolean removed = users.removeIf(p -> p == user);
        if (removed) {
            length--;
        }
    }

    /**
     * Remove users.User with param session from users.UserList
     *
     * @param session Websession of users.User
     */
    public void removeUserWithSession(Session session) {
        users.removeIf(p -> p.getWebsession() == session);
        length--;
    }

    /**
     * "delete" all Elements in users.UserList
     */
    public void clearAll() {
        users.removeAll(users);
        length = 0;
    }

    /**
     * @return the size of users.UserList
     */
//    public int length() {
//        return users.size();
//    }

    /**
     * @return users.UserList
     */
    public List<User> getUsers() {
        List<User> _users = new ArrayList<User>(users);
        return _users;
    }

    /**
     * returns a new userlist except a single user
     *
     * @param user the be excepted from the new userlist
     * @return a new userlist
     */
    public List<User> getUsersExceptUser(User user) {
        List<User> _users = users;
        _users.removeIf(p -> p == user);
        return _users;
    }

    /**
     * @see UserList#getUsersExceptUser(User)
     * @param session session to be excepted
     * @return a new userlist
     */
    public List<User> getUsersExceptSession(Session session) {
        List<User> _users = users;
        _users.removeIf(p -> p.getWebsession() == session);
        return _users;
    }

    /**
     * This method returns a user from the userlist by its index
     *
     * @param index to be checked up
     * @return the user with the given index
     */
    public User getUserByIndex(int index) {
        return users.get(index);
    }

    /**
     * replace the user at the given index
     *
     * @param index the index  which should be replaced
     * @param user the user who should be at the index
     * @see ArrayList#set(int, Object)
     */
    public void setUserByIndex(int index, User user) {
        users.set(index, user);
    }

    /**
     * seach for a user by its session and returns it.
     *
     * @param session to check up a user
     * @return the user with the given session
     */
    public User getUserBySession(Session session) {
        for (User user : users) {
            if (user.getWebsession().getId() == session.getId()) {
                return user;
            }
        }
        return null;
    }

    /**
     * seach for a user by its name and returns it.
     * Possible because the name is unique in the database
     *
     * @param name to check up a user
     * @return the user with the given name
     */
    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * This method is used to get the index of a user in the userlist.
     *
     * @param user to check up for
     * @return the index of the user
     */
    public int getIndexOfUser(User user) {
        return users.indexOf(user);
    }

    /**
     * Check if the given user is in the userlist.
     *
     * @param user to check up for
     * @return true when user is in the userlist
     */
    public boolean contains(User user) {
        return users.contains(user);
    }

    /**
     * @return a jsonarray representation of the userlist
     */
    public JSONArray getInterfaceUserList() {
        JSONArray arr = new JSONArray();
        for (User user : users) {
            HashMap<String, String> map = new HashMap<>();
            map.put("username", user.getName());
            arr.put(map);
        }
        return arr;
    }

    /**
     * @param small_blind points out which user is small blind
     * @param big_blind points out which user is small
     * @param current_user point out who is at turn
     * @param show_player_cards point out if the player cards should be shown
     * @return a jsonarray representation of the userlist
     */
    public JSONArray getInterfaceUserList(User small_blind, User big_blind, User current_user, boolean show_player_cards) {
        JSONArray arr = new JSONArray();
        for (User user : users) {
            TypedMap map = new TypedMap();
            TypedMap.AbstractKey<String> username = TypedMap.StringKey.username;
            TypedMap.AbstractKey<Boolean> is_big_blind = TypedMap.BooleanKey.is_big_blind;
            TypedMap.AbstractKey<Boolean> is_small_blind = TypedMap.BooleanKey.is_small_blind;
            TypedMap.AbstractKey<Boolean> is_active = TypedMap.BooleanKey.is_active;
            TypedMap.AbstractKey<Boolean> show_cards = TypedMap.BooleanKey.show_cards;
            TypedMap.AbstractKey<Card[]> hand_cards = TypedMap.CardArrayKey.hand_cards;
            map.put(username, user.getName());
            map.put(is_big_blind, user == big_blind);
            map.put(is_small_blind, user == small_blind);
            map.put(is_active, current_user == user);

            Card[] player_hand_cards = new Card[2];
            if (show_player_cards) {
                player_hand_cards = user.getHandCards();
            } else {
                player_hand_cards[0] = new Card(0, "");
                player_hand_cards[1] = new Card(0, "");
            }
            map.put(hand_cards, player_hand_cards);
            map.put(show_cards, show_player_cards);

            arr.put(map.getMap());
        }
        return arr;
    }

    /**
     *
     * @param user to get the before user
     * @return the user before the given user
     */
    public User getPreviousUser(User user) {
        return this.getUserByIndex((users.indexOf(user) - 1 % length + length) % length);
    }

    /**
     * Iterate over the userlist and reinitilize the hand cards of all users.
     * @see User#resetHandCards()
     */
    public void resetAllHandCards() {
        for (User user : users) {
            user.resetHandCards();
        }
    }

    /**
     * Iterate over the userlist and reinitilize what the users already paid.
     * @see User#resetPayment() ()
     */
    public void resetAlreadyPaid() {
        for (User user : users) {
            user.resetPayment();
        }
    }

    /**
     * @return the highest already paid attribute of the users in this list.
     */
    public int getHighestBet() {
        int i = 0;
        for (User user : users) {
            int value = user.getAlready_paid();
            if (value > i) {
                i = value;
            }
        }
        return i;
    }

    /**
     * @return true when all users already_paid attribute is equal
     */
    public boolean allUsersPaidSame() {
        boolean paid_same = true;
        int first_payment = users.get(0).getAlready_paid();
        for (User user : users) {
            if (first_payment != user.getAlready_paid()) {
                paid_same = false;
            }
        }
        return paid_same;
    }

    /**
     * @param user to check his already paid attribute
     * @return the value of money the user got to call.
     */
    public int getCallValueForUser(User user) {
        int highest_bet = getHighestBet();
        int already_paid = user.getAlready_paid();
        return highest_bet - already_paid;
    }

    /**
     * @return true when all users in the list perform an action
     * @see Constants.Actions
     */
    public boolean allPlayersActionNotNull() {
        for (User user : users) {
            if (user.getLast_action().equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method reinitilize the user action to "" of all users in the userlist.
     */
    public void resetAllPlayersAction() {
        for (User user : users) {
            user.setLast_action("");
        }
    }
}
